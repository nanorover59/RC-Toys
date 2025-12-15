package rctoys.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rctoys.RCToysMod;

public class PlaneEntity extends AbstractRCEntity
{
	private int pitch;
	private int roll;
	private int throttleControl;
	private float throttle;
	
	public PlaneEntity(EntityType<?> entityType, Level world)
	{
		super(entityType, world);
	}
	
	@Override
	public Item asItem()
	{
		return RCToysMod.PLANE_ITEM;
	}
	
	@Override
	public int getDefaultColor()
	{
		return -16201290;
	}

    @Override
    public SoundEvent getSoundLoop()
    {
        return RCToysMod.PLANE_LOOP_SOUND;
    }
	
	public float getMaximumThrust()
	{
		return 0.05f;
	}
	
	@Override
	protected double getDefaultGravity()
	{
		return 0.05;
	}

	@Override
	public void tickPhysics()
	{
		if(!isEnabled())
		{
			pitch = 0;
			roll = 0;
			throttleControl = 0;
			throttle = 0;
		}
		
		throttle = Math.clamp(throttle + throttleControl * 0.1f, 0.0f, 1.0f);
		setThrottle(throttle);
		Quaternionf quaternion = getQuaternion();
		Quaternionf invQuaternion = getQuaternion().invert();
		Vector3f acc = new Vector3f(0.0f, 0.0f, -1.0f).rotate(quaternion).mul(getMaximumThrust() * throttle);
		Vector3f right = new Vector3f(-1.0f, 0.0f, 0.0f).rotate(quaternion);
		float wingSpan = 0.8f;
		float wingArea = 0.5f;
		float aspectRatio = (wingSpan * wingSpan) / wingArea;
		Vector3f velocity = getDeltaMovement().toVector3f();
		Vector3f localVelocity = getDeltaMovement().toVector3f().rotate(invQuaternion);
		float stallAngle = 0.25f;
		float angleOfAttack = (float) -Math.atan2(localVelocity.y(), -localVelocity.z());
		float inducedLift = angleOfAttack * (aspectRatio / (aspectRatio + 2.0f)) * Mth.PI * 2.0f;
		
		if(Math.abs(angleOfAttack) > stallAngle)
			inducedLift *= (float) Math.cos((Math.abs(angleOfAttack) - stallAngle) * (Mth.PI / 4.0f));
		
		float inducedDrag = (inducedLift * inducedLift) / (aspectRatio * Mth.PI);
		
		if(Math.abs(angleOfAttack) > stallAngle)
			inducedDrag += (Math.abs(angleOfAttack) - stallAngle) * 0.5f;
		
		if(velocity.lengthSquared() > 1e-8f)
		{
			float dynamicPressure = velocity.lengthSquared() * wingArea * 1.225f * 0.5f;
			Vector3f lift = new Vector3f(velocity).normalize().cross(right).mul(Math.clamp(inducedLift * dynamicPressure, 0.0f, 16.0f));
			Vector3f drag = new Vector3f(velocity).normalize((inducedDrag + 0.1f) * dynamicPressure);
			
			if(lift.isFinite())
				acc.add(lift);
			
			if(drag.isFinite())
				acc.sub(drag);
		}
		
		push(acc.x(), acc.y(), acc.z());
		
		// Apply Gravity
		applyGravity();
		
		// Extra Drag
		if(isInWater() || (onGround() && throttle == 0.0f))
			setDeltaMovement(getDeltaMovement().multiply(0.8f, 0.5f, 0.8f));
		
		// Move
		move(MoverType.SELF, getDeltaMovement());
	}
	
	@Override
	public Quaternionf updateQuaternion()
	{
		Quaternionf quaternion = getQuaternion();
		Vector3f velocity = getDeltaMovement().toVector3f();
		
		if(velocity.lengthSquared() > 1e-8f)
		{
			Quaternionf invQuaternion = getQuaternion().invert();
			Vector3f localVelocity = new Vector3f(velocity).rotate(invQuaternion).normalize();
			quaternion.rotateX(localVelocity.y() * 0.5f);
			quaternion.rotateY(localVelocity.x()  * -0.5f);
			
			if(!onGround())
				quaternion.rotateZ(roll * Math.clamp(velocity.length() * 0.1f, 0.0f, 0.1f));
			
			quaternion.rotateX(pitch * Math.clamp(velocity.length() * 0.1f, 0.0f, 0.1f));
		}
		
		if(onGround())
			quaternion.rotateY(roll * 0.1f);
		
		return quaternion.normalize();
	}

	@Override
	public void remoteControlInput(boolean[] inputArray)
	{
		pitch = 0;
		roll = 0;
		throttleControl = 0;
		
		// Pitch Down
		if(inputArray[0])
			pitch--;
		
		// Pitch Up
		if(inputArray[1])
			pitch++;
		
		// Roll Left
		if(inputArray[2])
			roll++;
		
		// Roll Right
		if(inputArray[3])
			roll--;
		
		// Throttle Up
		if(inputArray[4])
			throttleControl++;
				
		// Throttle Down
		if(inputArray[5])
			throttleControl--;
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return getDeltaMovement().length() > 0.25;
	}
}