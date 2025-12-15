package rctoys.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.ClientInput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import rctoys.RCToysMod;
import rctoys.client.render.entity.CarEntityRenderer;
import rctoys.client.render.entity.PlaneEntityRenderer;
import rctoys.client.render.entity.model.CarEntityModel;
import rctoys.client.render.entity.model.PlaneEntityModel;
import rctoys.client.sound.DynamicSoundManager;
import rctoys.entity.AbstractRCEntity;
import rctoys.item.RemoteLinkComponent;
import rctoys.network.c2s.MotorSoundS2CPacket;
import rctoys.network.c2s.RemoteControlC2SPacket;

import java.util.UUID;

public class RCToysModClient implements ClientModInitializer
{
	public static final ModelLayerLocation MODEL_CAR_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "rc_car"), "main");
	public static final ModelLayerLocation MODEL_PLANE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(RCToysMod.MOD_ID, "rc_plane"), "main");
	
	private static KeyMapping[] inputKeys;
	private static int lastInput = -1;
	public static UUID fpvUUID;
	public boolean trackingEntityKeyPressed;
	
	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(MotorSoundS2CPacket.ID, (payload, context) -> DynamicSoundManager.receiveSoundPacket(payload, context));
		EntityRendererRegistry.register(RCToysMod.CAR, (context) -> new CarEntityRenderer(context));
		EntityRendererRegistry.register(RCToysMod.PLANE, (context) -> new PlaneEntityRenderer(context));
		EntityModelLayerRegistry.registerModelLayer(MODEL_CAR_LAYER, CarEntityModel::getTexturedModelData);
		EntityModelLayerRegistry.registerModelLayer(MODEL_PLANE_LAYER, PlaneEntityModel::getTexturedModelData);
		
		ItemTooltipCallback.EVENT.register((stack, world, ctx, lines) -> {
			RemoteLinkComponent link = stack.get(RCToysMod.REMOTE_LINK);
			
			if(link != null && !link.name().isEmpty())
				lines.add(Component.translatable("Linked to %s", link.name()).withStyle(ChatFormatting.GRAY));
		});
		
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if(client.player != null && client.level != null && client.player.getMainHandItem().getComponents().has(RCToysMod.REMOTE_LINK))
			{
				AbstractRCEntity entity = (AbstractRCEntity) client.level.getEntity(client.player.getMainHandItem().getComponents().get(RCToysMod.REMOTE_LINK).uuid());
				
				if(entity != null && entity.isEnabled())
				{
					// Initialize the input key array.
					if(inputKeys == null)
						inputKeys = new KeyMapping[] {
							client.options.keyUp,
							client.options.keyDown,
							client.options.keyLeft,
							client.options.keyRight,
							client.options.keyJump,
							client.options.keyShift
						};
					
					// Force a refresh of keys pressed.
                    KeyMapping.setAll();
					int input = 0;
					
					for(int i = 0; i < inputKeys.length; i++)
					{
						KeyMapping key = inputKeys[i];
						
						// Pack pressed keys into an integer.
						if(key.isDown())
							input |= (1 << i);
						
						// Block player movement input while holding a remote.
						key.setDown(false);
					}
					
					if(lastInput != input)
						ClientPlayNetworking.send(new RemoteControlC2SPacket(input));
					
					lastInput = input;
					
					// Toggle camera tracking entity.
					if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_V))
					{
						if(!trackingEntityKeyPressed)
						{
							trackingEntityKeyPressed = true;
							
							if(fpvUUID == null)
								fpvUUID = client.player.getMainHandItem().get(RCToysMod.REMOTE_LINK).uuid();
							else
								fpvUUID = null;
						}
					}
					else
						trackingEntityKeyPressed = false;
					
					return;
				}
			}
			
			lastInput = -1;
			fpvUUID = null;
		});
	}
}