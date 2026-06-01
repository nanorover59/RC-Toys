package rctoys.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rctoys.client.RCToysModClient;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /**
     * Redirect the camera entity.
     */
    @Redirect(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;entity()Lnet/minecraft/world/entity/Entity;"))
    private Entity redirectEntity(Camera camera) {
        if(RCToysModClient.fpvUUID != null && !camera.isDetached()) {
            Minecraft client = Minecraft.getInstance();
            Entity fpvEntity = client.level.getEntity(RCToysModClient.fpvUUID);

            if(fpvEntity != null)
                return fpvEntity;
        }

        return camera.entity();
    }
}