package rctoys.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.lwjgl.glfw.GLFW;
import rctoys.RCToysMod;
import rctoys.client.render.entity.CarEntityRenderer;
import rctoys.client.render.entity.PlaneEntityRenderer;
import rctoys.client.render.entity.SubmarineEntityRenderer;
import rctoys.client.render.entity.model.CarEntityModel;
import rctoys.client.render.entity.model.PlaneEntityModel;
import rctoys.client.render.entity.model.SubmarineEntityModel;
import rctoys.client.sound.DynamicSoundManager;
import rctoys.entity.AbstractRCEntity;
import rctoys.item.RemoteLinkComponent;
import rctoys.network.c2s.TrackingPlayerC2SPacket;
import rctoys.network.c2s.MotorSoundS2CPacket;

import java.util.UUID;

public class RCToysModClient implements ClientModInitializer
{
	public static final ModelLayerLocation MODEL_CAR_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "rc_car"), "main");
	public static final ModelLayerLocation MODEL_PLANE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "rc_plane"), "main");
	public static final ModelLayerLocation MODEL_SUBMARINE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "rc_submarine"), "main");

	public static UUID fpvUUID;
	public boolean trackingEntityKeyPressed;
	
	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(MotorSoundS2CPacket.ID, (payload, context) -> DynamicSoundManager.receiveSoundPacket(payload, context));
		EntityRendererRegistry.register(RCToysMod.CAR, context -> new CarEntityRenderer(context));
		EntityRendererRegistry.register(RCToysMod.PLANE, context -> new PlaneEntityRenderer(context));
		EntityRendererRegistry.register(RCToysMod.SUBMARINE, context -> new SubmarineEntityRenderer(context));
		ModelLayerRegistry.registerModelLayer(MODEL_CAR_LAYER, CarEntityModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MODEL_PLANE_LAYER, PlaneEntityModel::getTexturedModelData);
		ModelLayerRegistry.registerModelLayer(MODEL_SUBMARINE_LAYER, SubmarineEntityModel::getTexturedModelData);
		
		ItemTooltipCallback.EVENT.register((stack, world, ctx, lines) -> {
			RemoteLinkComponent link = stack.get(RCToysMod.REMOTE_LINK);
			
			if(link != null && !link.name().isEmpty())
				lines.add(Component.translatable("Linked to %s", link.name()).withStyle(ChatFormatting.GRAY));
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if(client.player != null && client.level != null && client.player.getMainHandItem().getComponents().has(RCToysMod.REMOTE_LINK)) {
				AbstractRCEntity entity = (AbstractRCEntity) client.level.getEntity(client.player.getMainHandItem().getComponents().get(RCToysMod.REMOTE_LINK).uuid());
				
				if(entity != null && entity.isEnabled()) {

					RemoteControlUtil.control(client.options, entity);
					
					// Toggle camera tracking entity.
					if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_V)) {
						if(!trackingEntityKeyPressed) {
							trackingEntityKeyPressed = true;
							
							if(fpvUUID == null)
								fpvUUID = client.player.getMainHandItem().get(RCToysMod.REMOTE_LINK).uuid();
							else
								fpvUUID = null;

                            ClientPlayNetworking.send(new TrackingPlayerC2SPacket(entity.getId(), fpvUUID != null));
						}
					}
					else
						trackingEntityKeyPressed = false;
                    
					return;
				}
			}

            if(fpvUUID != null && client.level != null) {
                Entity fpvEntity = client.level.getEntity(fpvUUID);

                if(fpvEntity != null)
                    ClientPlayNetworking.send(new TrackingPlayerC2SPacket(fpvEntity.getId(), false));
            }

            fpvUUID = null;
		});
	}
}