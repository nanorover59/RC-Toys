package rctoys.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import rctoys.entity.AbstractRCEntity;

public class RCMotorSound extends AbstractTickableSoundInstance
{
	private final AbstractRCEntity entity;

	public RCMotorSound(AbstractRCEntity entity, SoundEvent motorSound)
	{
		super(motorSound, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
		this.volume = 0.0f;
	}

	@Override
	public void tick() {
        if (entity.isRemoved()) {
            this.stop();
            return;
        }

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();

        if (entity.isEnabled() && Math.abs(entity.getThrottle()) > 0.0f) {
            this.volume = Mth.clamp(this.volume + 0.05f, 0.0f, 1.0f);
            this.pitch = Mth.clamp(this.pitch + 0.01f, 0.7f, 1.0f);
        } else {
            this.volume = Mth.clamp(this.volume - 0.05f, 0.0f, 1.0f);
            this.pitch = Mth.clamp(this.pitch - 0.01f, 0.7f, 1.0f);
        }
    }
}