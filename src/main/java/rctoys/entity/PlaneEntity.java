package rctoys.entity;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rctoys.RCToysMod;

public class PlaneEntity extends AbstractRCEntity
{
	private int pitch;
	private int roll;
	private int throttleControl;
	private float throttle;
	
	public PlaneEntity(EntityType<?> entityType, World world)
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
	
	public double getWingArea()
	{
		return 0.5;
	}
	
	public float getStallAngle()
	{
		return 0.2f;
	}
	
	public float getMaximumThrust()
	{
		return 0.05f;
	}
	
	@Override
	protected double getGravity()
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
		Vector3f velocity = getVelocity().toVector3f();
		Vector3f localVelocity = getVelocity().toVector3f().rotate(invQuaternion);
		float stallAngle = 0.25f;
		float angleOfAttack = (float) -Math.atan2(localVelocity.y(), -localVelocity.z());
		float inducedLift = angleOfAttack * (aspectRatio / (aspectRatio + 2.0f)) * MathHelper.PI * 2.0f;
		
		if(Math.abs(angleOfAttack) > stallAngle)
			inducedLift *= Math.cos((Math.abs(angleOfAttack) - stallAngle) * (MathHelper.PI / 4.0f));
		
		float inducedDrag = (inducedLift * inducedLift) / (aspectRatio * MathHelper.PI);
		
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
		
		addVelocity(acc.x(), acc.y(), acc.z());
		
		// Apply Gravity
		applyGravity();
		
		// Extra Drag
		if(isTouchingWater() || (isOnGround() && throttle == 0.0f))
			setVelocity(getVelocity().multiply(0.8f, 0.5f, 0.8f));
		
		// Move
		move(MovementType.SELF, getVelocity());
	}
	
	@Override
	public Quaternionf updateQuaternion()
	{
		Quaternionf quaternion = getQuaternion();
		Vector3f velocity = getVelocity().toVector3f();
		
		if(velocity.lengthSquared() > 1e-8f)
		{
			Quaternionf invQuaternion = getQuaternion().invert();
			Vector3f localVelocity = new Vector3f(velocity).rotate(invQuaternion).normalize();
			quaternion.rotateX(localVelocity.y() * 0.5f);
			quaternion.rotateY(localVelocity.x()  * -0.5f);
			
			if(!isOnGround())
				quaternion.rotateZ(roll * Math.clamp(velocity.length() * 0.1f, 0.0f, 0.1f));
			
			quaternion.rotateX(pitch * -Math.clamp(velocity.length() * 0.1f, 0.0f, 0.1f));
		}
		
		if(isOnGround())
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
			pitch++;
		
		// Pitch Up
		if(inputArray[1])
			pitch--;
		
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
	public boolean shouldSpawnSprintingParticles()
	{
		return getVelocity().length() > 0.25;
	}
}