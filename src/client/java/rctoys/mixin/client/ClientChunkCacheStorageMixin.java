package rctoys.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rctoys.client.RCToysModClient;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientChunkCache$Storage")
public abstract class ClientChunkCacheStorageMixin {
    @Inject(method = "inRange", at = @At("RETURN"), cancellable = true)
    private void inRangeInject(int i, int j, CallbackInfoReturnable<Boolean> info) {
        if(RCToysModClient.fpvUUID != null)
            info.setReturnValue(true);
    }
}