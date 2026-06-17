package rctoys.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.client.RCToysModClient;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", at = @At("HEAD"), cancellable = true)
    public void extractRenderStateInject(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo info) {
        if(RCToysModClient.fpvUUID != null)
            info.cancel();
    }
}