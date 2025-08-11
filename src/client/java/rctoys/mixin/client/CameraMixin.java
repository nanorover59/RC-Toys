package rctoys.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import rctoys.client.RCToysModClient;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow private boolean thirdPerson;
	
	/**
	 * Track the RC entity being controlled.
	 */
	@ModifyVariable(method = "update", at = @At("HEAD"), argsOnly = true)
	private Entity trackRCEntity(Entity original)
	{
		MinecraftClient client = MinecraftClient.getInstance();

		if(RCToysModClient.fpvUUID != null && client.world != null)
		{
			for(Entity entity : client.world.getEntities())
			{
				if(entity.getUuid().equals(RCToysModClient.fpvUUID))
					return entity;
			}
		}

		return original;
	}
	
	/**
	 * Allow for camera panning while viewing the RC entity in third person.
	 */
	@ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 1))
	private void modifyRotations(Args args)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		
		if(thirdPerson && RCToysModClient.fpvUUID != null && client.world != null)
		{
			float partialTicks = client.getRenderTickCounter().getTickProgress(false);
			args.set(0, client.player.getYaw(partialTicks));
			args.set(1, client.player.getPitch(partialTicks));
		}
	}
}