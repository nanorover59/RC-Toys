package rctoys.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.client.RCToysModClient;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    @Shadow protected abstract EntityRenderState extractEntity(Entity entity, float partialTickTime);

    /**
     * Skip rendering the RC toy being controlled.
     */
    @Redirect(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z"))
    private boolean redirectShouldRender(EntityRenderDispatcher entityRenderDispatcher, Entity entity, Frustum culler, double camX, double camY, double camZ, @Local Camera camera) {
        if(RCToysModClient.fpvUUID != null && entity.getUUID().equals(RCToysModClient.fpvUUID) && !camera.isDetached())
            return false;

        return entityRenderDispatcher.shouldRender(entity, culler, camX, camY, camZ);
    }

    /**
     * Ensure the controlling player is rendered.
     */
    @Inject(method = "extractVisibleEntities", at = @At(value = "TAIL"))
    private void extractVisibleEntitiesInject(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState output, CallbackInfo info) {
        if(RCToysModClient.fpvUUID != null && !camera.isDetached()) {
            Minecraft client = Minecraft.getInstance();
            TickRateManager tickRateManager = client.level.tickRateManager();
            LocalPlayer player = client.player;

            if(player != null) {
                float partialEntity = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(player));
                EntityRenderState state = extractEntity(player, partialEntity);
                output.entityRenderStates.add(state);
                output.lastEntityRenderStateCount++;
            }
        }
    }
}