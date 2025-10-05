package rctoys.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
            Vector3f velocity = getVelocity().toVector3f();
            Vector3f horizontalVelocity = new Vector3f(velocity.x(), 0.0f, velocity.z());
            Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f).rotateY(getYaw() * -MathHelper.RADIANS_PER_DEGREE + MathHelper.PI);
			float forwardMagnitude = horizontalVelocity.dot(forward);
            Vector3f forwardVelocity = new Vector3f(forward).mul(forwardMagnitude);
            Vector3f lateralVelocity = new Vector3f(horizontalVelocity).sub(forwardVelocity);
			
			// Forward Acceleration
			float acc = throttle * 0.02f;
			forwardVelocity.add(new Vector3f(forward).mul(acc));
			
			// Lateral Friction
			float lateralFriction = 0.6f;
			lateralVelocity.mul(lateralFriction);
			
			// New Velocity
            Vector3f newVelocity = new Vector3f(forwardVelocity).add(lateralVelocity);
			setVelocity(newVelocity.x(), velocity.y(), newVelocity.z());
			
			// Steering
		    float turnSpeed = -12.0f / (1.0f + forwardMagnitude * 2.0f);
		    setYaw(getYaw() + steering * turnSpeed);
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
	}
	
	@Override
	public Quaternionf updateQuaternion()
	{
		Quaternionf quaternion = new Quaternionf();
		quaternion.rotateY(getYaw() * -MathHelper.RADIANS_PER_DEGREE + MathHelper.PI);
		quaternion.rotateX(getPitch() * -MathHelper.RADIANS_PER_DEGREE);
		return quaternion;
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