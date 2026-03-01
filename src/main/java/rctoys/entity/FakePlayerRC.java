package rctoys.entity;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public class FakePlayerRC extends FakePlayer {
    public AbstractRCEntity rcEntity;

    protected FakePlayerRC(ServerLevel world, GameProfile profile, AbstractRCEntity rcEntity) {
        super(world, profile);
        this.rcEntity = rcEntity;
        this.refreshDimensions();
    }

    @Override
    public int requestedViewDistance() {
        if(rcEntity.trackingPlayer != null)
            return rcEntity.trackingPlayer.requestedViewDistance();
        else
            return 2;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return EntityDimensions.fixed(0, 0);
    }
}