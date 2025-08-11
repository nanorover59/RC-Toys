package rctoys.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import rctoys.entity.AbstractRCEntity;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	/**
	 * Continue to render the player while tracking an RC entity.
	 */
	@Inject(method = "getEntitiesToRender", at = @At("RETURN"))
	private void injected(Camera camera, Frustum frustum, List<Entity> output, CallbackInfoReturnable<Boolean> info)
	{
		MinecraftClient client = MinecraftClient.getInstance();

		if(camera.getFocusedEntity() instanceof AbstractRCEntity)
			output.add(client.player);
	}
}