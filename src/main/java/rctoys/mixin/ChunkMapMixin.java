package rctoys.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.entity.FakePlayerRC;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
    @Inject(method = "markChunkPendingToSend(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/chunk/LevelChunk;)V", at = @At("HEAD"), cancellable = true)
    private static void markChunkPendingToSendInject(ServerPlayer player, LevelChunk chunk, CallbackInfo info) {
        if(player instanceof FakePlayerRC fakePlayer && fakePlayer.rcEntity.trackingPlayer != null) {
            fakePlayer.rcEntity.trackingPlayer.connection.chunkSender.markChunkPendingToSend(chunk);
            info.cancel();
        }
    }

    @Inject(method = "dropChunk", at = @At("HEAD"), cancellable = true)
    private static void dropChunkInject(ServerPlayer player, ChunkPos pos, CallbackInfo info) {
        if(player instanceof FakePlayerRC fakePlayer && fakePlayer.rcEntity.trackingPlayer != null) {
            fakePlayer.rcEntity.trackingPlayer.connection.chunkSender.dropChunk(fakePlayer.rcEntity.trackingPlayer, pos);
            info.cancel();
        }
    }
}