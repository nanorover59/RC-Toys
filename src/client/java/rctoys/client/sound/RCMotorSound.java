package rctoys.client.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import rctoys.entity.AbstractRCEntity;

public class RCMotorSound extends MovingSoundInstance
{
	private final AbstractRCEntity entity;

	public RCMotorSound(AbstractRCEntity entity, SoundEvent motorSound)
	{
		super(motorSound, SoundCategory.NEUTRAL, SoundInstance.createRandom());
		this.entity = entity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.0f;
	}

	@Override
	public void tick()
	{
		if(entity.isRemoved())
		{
			this.setDone();
			return;
		}
		
		this.x = entity.getX();
		this.y = entity.getY();
		this.z = entity.getZ();
		
		if(Math.abs(entity.getThrottle()) > 0.0f)
			this.volume = MathHelper.clamp(this.volume + 0.05f, 0.0f, 1.0f);
		else
			this.volume = MathHelper.clamp(this.volume - 0.05f, 0.0f, 1.0f);

		float speedFactor = (float) (entity.getVelocity().length() * 2.0);
		this.pitch = 0.6f + MathHelper.clamp(speedFactor, 0.0f, 0.8f);
	}
	
	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}
}