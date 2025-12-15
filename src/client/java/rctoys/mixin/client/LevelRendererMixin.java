package rctoys.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.entity.AbstractRCEntity;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    @Shadow protected abstract EntityRenderState extractEntity(Entity entity, float tickProgress);

    /**
	 * Continue to render the player while tracking an RC entity.
	 */
    @Inject(method = "extractVisibleEntities", at = @At("RETURN"))
    private void injected(Camera camera, Frustum frustum, DeltaTracker tickCounter, LevelRenderState renderStates, CallbackInfo ci)
    {
        Minecraft client = Minecraft.getInstance();

        if(camera.entity() instanceof AbstractRCEntity)
        {
            TickRateManager tickManager = client.level.tickRateManager();
            LocalPlayer player = client.player;
            float g = tickCounter.getGameTimeDeltaPartialTick(!tickManager.isEntityFrozen(player));
            EntityRenderState entityRenderState = this.extractEntity(player, g);
            renderStates.entityRenderStates.add(entityRenderState);
        }
    }
}