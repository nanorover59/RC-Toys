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
		{
			this.volume = MathHelper.clamp(this.volume + 0.05f, 0.0f, 1.0f);
			this.pitch = MathHelper.clamp(this.pitch + 0.01f, 0.7f, 1.0f);
		}
		else
		{
			this.volume = MathHelper.clamp(this.volume - 0.05f, 0.0f, 1.0f);
			this.pitch = MathHelper.clamp(this.pitch - 0.01f, 0.7f, 1.0f);
		}
	}
	
	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}
}