package rctoys.mixin.client;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import rctoys.client.RCToysModClient;
import rctoys.entity.AbstractRCEntity;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow private boolean detached;

    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow @Final private Quaternionf rotation;
    @Shadow @Final private static Vector3f FORWARDS;
    @Shadow @Final private Vector3f forwards;
    @Shadow @Final private static Vector3f UP;
    @Shadow @Final private Vector3f up;
    @Shadow @Final private static Vector3f LEFT;
    @Shadow @Final private Vector3f left;

    /**
	 * Track the RC entity being controlled.
	 */
	@ModifyVariable(method = "setup", at = @At("HEAD"), argsOnly = true)
	private Entity trackRCEntity(Entity original)
	{
		Minecraft client = Minecraft.getInstance();

		if(RCToysModClient.fpvUUID != null && client.level != null)
		{
			for(Entity entity : client.level.entitiesForRendering())
			{
				if(entity.getUUID().equals(RCToysModClient.fpvUUID))
					return entity;
			}
		}

		return original;
	}
	
	/**
	 * Allow for camera panning while viewing the RC entity in third person.
	 */
	@ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
	private void modifyRotations(Args args)
	{
		Minecraft client = Minecraft.getInstance();
		
		if(detached && RCToysModClient.fpvUUID != null && client.level != null)
		{
			float partialTicks = client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
			args.set(0, client.player.getViewYRot(partialTicks));
			args.set(1, client.player.getViewXRot(partialTicks));
		}
	}

    /**
     * Point the camera in the forward direction while in FPV mode.
     */
    @Inject(method = "setRotation", at = @At("HEAD"), cancellable = true)
    protected void setRotation(float yaw, float pitch, CallbackInfo info)
    {
        Minecraft client = Minecraft.getInstance();

        if(!detached && RCToysModClient.fpvUUID != null && client.level != null)
        {
            for(Entity entity : client.level.entitiesForRendering())
            {
                if(entity.getUUID().equals(RCToysModClient.fpvUUID))
                {
                    float partialTicks = client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                    Quaternionf quaternion = ((AbstractRCEntity) entity).getLerpedQuaternion(partialTicks);
                    this.rotation.set(quaternion);
                    FORWARDS.rotate(this.rotation, this.forwards);
                    UP.rotate(this.rotation, this.up);
                    LEFT.rotate(this.rotation, this.left);
                    this.xRot = pitch;
                    this.yRot = yaw;
                    info.cancel();
                }
            }
        }
    }
}