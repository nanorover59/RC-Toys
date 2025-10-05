package rctoys.client;

import java.util.UUID;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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

public class RCToysModClient implements ClientModInitializer
{
	public static final EntityModelLayer MODEL_CAR_LAYER = new EntityModelLayer(Identifier.of(RCToysMod.MOD_ID, "rc_car"), "main");
	public static final EntityModelLayer MODEL_PLANE_LAYER = new EntityModelLayer(Identifier.of(RCToysMod.MOD_ID, "rc_plane"), "main");
	
	private static KeyBinding[] inputKeys;
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
				lines.add(Text.translatable("Linked to %s", link.name()).formatted(Formatting.GRAY));
		});
		
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if(client.player != null && client.world != null && client.player.getMainHandStack().contains(RCToysMod.REMOTE_LINK))
			{
				AbstractRCEntity entity = (AbstractRCEntity) client.world.getEntity(client.player.getMainHandStack().get(RCToysMod.REMOTE_LINK).uuid());
				
				if(entity != null && entity.isEnabled())
				{
					// Initialize the input key array.
					if(inputKeys == null)
						inputKeys = new KeyBinding[] {
							client.options.forwardKey,
							client.options.backKey,
							client.options.leftKey,
							client.options.rightKey,
							client.options.jumpKey,
							client.options.sneakKey
						};
					
					// Force a refresh of keys pressed.
					KeyBinding.updatePressedStates();
					int input = 0;
					
					for(int i = 0; i < inputKeys.length; i++)
					{
						KeyBinding key = inputKeys[i];
						
						// Pack pressed keys into an integer.
						if(key.isPressed())
							input |= (1 << i);
						
						// Block player movement input while holding a remote.
						key.setPressed(false);
					}
					
					if(lastInput != input)
						ClientPlayNetworking.send(new RemoteControlC2SPacket(input));
					
					lastInput = input;
					
					// Toggle camera tracking entity.
					if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_V))
					{
						if(!trackingEntityKeyPressed)
						{
							trackingEntityKeyPressed = true;
							
							if(fpvUUID == null)
								fpvUUID = client.player.getMainHandStack().get(RCToysMod.REMOTE_LINK).uuid();
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