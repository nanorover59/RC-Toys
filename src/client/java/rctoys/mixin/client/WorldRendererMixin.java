package rctoys.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.world.tick.TickManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.entity.AbstractRCEntity;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow protected abstract EntityRenderState getAndUpdateRenderState(Entity entity, float tickProgress);

    /**
	 * Continue to render the player while tracking an RC entity.
	 */
    @Inject(method = "fillEntityRenderStates", at = @At("RETURN"))
    private void injected(Camera camera, Frustum frustum, RenderTickCounter tickCounter, WorldRenderState renderStates, CallbackInfo ci)
    {
        MinecraftClient client = MinecraftClient.getInstance();

        if(camera.getFocusedEntity() instanceof AbstractRCEntity)
        {
            TickManager tickManager = client.world.getTickManager();
            ClientPlayerEntity player = client.player;
            float g = tickCounter.getTickProgress(!tickManager.shouldSkipTick(player));
            EntityRenderState entityRenderState = this.getAndUpdateRenderState(player, g);
            renderStates.entityRenderStates.add(entityRenderState);
        }
    }
}