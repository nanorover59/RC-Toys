package rctoys.entity;

import org.joml.Quaternionf;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rctoys.RCToysMod;

public class CarEntity extends AbstractRCEntity
{
	private int throttle;
	private int steering;
	
	public CarEntity(EntityType<?> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	public Item asItem()
	{
		return RCToysMod.CAR_ITEM;
	}
	
	@Override
	public int getDefaultColor()
	{
		return -48340;
	}

	@Override
	public void tickPhysics()
	{
		if(!isEnabled())
		{
			throttle = 0;
			steering = 0;
		}
		
		if(isOnGround())
		{
			setVelocity(getVelocity().multiply(throttle == 0 ? 0.9 : 0.99));
			Vec3d velocity = getVelocity();
			Vec3d horizontalVelocity = new Vec3d(velocity.getX(), 0.0, velocity.getZ());
			Vec3d forward = new Vec3d(0.0, 0.0, 1.0).rotateY(-getYaw() * MathHelper.RADIANS_PER_DEGREE);
			double forwardMagnitude = horizontalVelocity.dotProduct(forward);
			Vec3d forwardVelocity = forward.multiply(forwardMagnitude);
			Vec3d lateralVelocity = horizontalVelocity.subtract(forwardVelocity);
			
			// Forward Acceleration
			double acc = (double) throttle * 0.02;
			forwardVelocity = forwardVelocity.add(forward.multiply(acc));
			
			// Lateral Friction
			double lateralFriction = 0.6;
			lateralVelocity = lateralVelocity.multiply(lateralFriction);
			
			// New Velocity
			Vec3d newVelocity = forwardVelocity.add(lateralVelocity);
			setVelocity(newVelocity.getX(), velocity.getY(), newVelocity.getZ());
			
			// Steering
		    double turnSpeed = -16.0 / (1.0 + forwardMagnitude * 2.0);
		    setYaw(getYaw() + (float) steering * (float) turnSpeed);
		}
		else
		{
			// Pitch with vertical velocity.
		    setPitch((float) (-getVelocity().getY() * 100.0));
		    
		    // Apply Gravity
			applyGravity();
		}
		
		// Extra drag in water.
		if(isTouchingWater())
			setVelocity(getVelocity().multiply(0.8f, 0.5f, 0.8f));
		
		// Move and Jump
		double previousY = getY();
		move(MovementType.SELF, getVelocity());
		double deltaY = getY() - previousY;
		
		if(deltaY > 0.1 && verticalCollision)
		{
			double speed = Math.hypot(getVelocity().getX(), getVelocity().getZ());
			double jump = 0.1 + Math.min(speed, 1.0);
			addVelocity(0.0, jump, 0.0);
		}
		
		// Update Rotation Quaternion
		Quaternionf quaternion = new Quaternionf();
		quaternion.rotateY(getYaw() * MathHelper.RADIANS_PER_DEGREE);
		quaternion.rotateX(getPitch() * MathHelper.RADIANS_PER_DEGREE);
		setQuaternion(quaternion);
	}

	@Override
	public void remoteControlInput(boolean[] inputArray)
	{
		throttle = 0;
		steering = 0;
		
		// Accelerate Forwards
		if(inputArray[0])
			throttle++;
		
		// Accelerate Backwards
		if(inputArray[1])
			throttle--;
		
		// Turn Left
		if(inputArray[2])
			steering++;
		
		// Turn Right
		if(inputArray[3])
			steering--;
		
		// Boost
		if(inputArray[4])
			throttle *= 2;
		
		setThrottle(throttle);
	}
	
	@Override
	public boolean shouldSpawnSprintingParticles()
	{
		return getVelocity().length() > 0.25;
	}
	
}