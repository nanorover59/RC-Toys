package rctoys.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rctoys.RCToysMod;

public class CarEntity extends AbstractRCEntity
{
	private int jumpTimer;

	public CarEntity(EntityType<?> entityType, Level world)
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
		if(!isEnabled()) {
			pitch = 0;
			yaw = 0;
			jumpTimer = 0;
		}

		double speed = Math.hypot(getDeltaMovement().x(), getDeltaMovement().z());
		int maxJump = Mth.clamp((int) Mth.map(speed, 0.2, 0.8, 0, 40), 0, 40);
		
		if(onGround()) {
			setDeltaMovement(getDeltaMovement().scale(pitch == 0 ? 0.9 : 0.99));
            Vector3f velocity = getDeltaMovement().toVector3f();
            Vector3f horizontalVelocity = new Vector3f(velocity.x(), 0.0f, velocity.z());
            Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f).rotateY(getYRot() * -Mth.DEG_TO_RAD + Mth.PI);
			float forwardMagnitude = horizontalVelocity.dot(forward);
            Vector3f forwardVelocity = new Vector3f(forward).mul(forwardMagnitude);
            Vector3f lateralVelocity = new Vector3f(horizontalVelocity).sub(forwardVelocity);
			
			// Forward Acceleration
			float acc = pitch * -0.02f;
			forwardVelocity.add(new Vector3f(forward).mul(acc));
			
			// Lateral Friction
			float lateralFriction = 0.6f;
			lateralVelocity.mul(lateralFriction);
			
			// New Velocity
            Vector3f newVelocity = new Vector3f(forwardVelocity).add(lateralVelocity);
			setDeltaMovement(newVelocity.x(), velocity.y(), newVelocity.z());
			
			// Steering
		    float turnSpeed = -12.0f / (1.0f + forwardMagnitude * 2.0f);
		    setYRot(getYRot() - yaw * turnSpeed);

			// Jump Timer
			if(throttle > 0.0f) {
				if(jumpTimer < maxJump)
					jumpTimer++;
			}
			else if (jumpTimer > 0)
				jumpTimer--;
		}
		else {
			// Pitch with vertical velocity.
		    setXRot((float) (-getDeltaMovement().y() * 100.0));
		    
		    // Apply Gravity
			applyGravity();
		}
		
		// Extra drag in water.
		if(isInWater())
			setDeltaMovement(getDeltaMovement().multiply(0.8f, 0.5f, 0.8f));
		
		// Move
		double previousY = getY();
		move(MoverType.SELF, getDeltaMovement());
		double deltaY = getY() - previousY;

		// Jump on space bar release or step height.
		if((jumpTimer > 0 && throttle <= 0.0f) || (deltaY > 0.1 && verticalCollision)) {
			double impulse = Math.min(speed * 2.0, 1.5);

			if(jumpTimer > 0 && deltaY == 0)
				impulse *= (double) jumpTimer / 40.0;

			push(0.0, impulse, 0.0);
			jumpTimer = 0;
		}
	}
	
	@Override
	public Quaternionf updateQuaternion() {
		Quaternionf quaternion = new Quaternionf();
		quaternion.rotateY(getYRot() * -Mth.DEG_TO_RAD + Mth.PI);
		quaternion.rotateX(getXRot() * -Mth.DEG_TO_RAD);
		return quaternion;
	}
	
	@Override
	public boolean canSpawnSprintParticle()
	{
		return getDeltaMovement().length() > 0.25;
	}
}