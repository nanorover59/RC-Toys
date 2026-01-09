package rctoys.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.entity.AbstractRCEntity;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public class ChunkMapTrackedEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "removePlayer", at = @At("HEAD"), cancellable = true)
    public void removePlayerInject(ServerPlayer serverPlayer, CallbackInfo info) {
        if(this.entity instanceof AbstractRCEntity) {
            AbstractRCEntity rcEntity = (AbstractRCEntity) this.entity;

            if(rcEntity.trackingPlayer != null && rcEntity.trackingPlayer.getUUID().equals(serverPlayer.getUUID()))
                info.cancel();
        }
    }
}