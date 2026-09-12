package rctoys.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rctoys.RCToysMod;

public class SubmarineEntity extends AbstractRCEntity {

    public SubmarineEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Item asItem() {
        return RCToysMod.SUBMARINE_ITEM;
    }

    @Override
    public int getDefaultColor() {
        return -48340;
    }

    @Override
    public void tickPhysics() {
        if(!isEnabled()) {
            pitch = 0;
            yaw = 0;
            throttle = 0;
        }

        if(isInWater()) {
            setDeltaMovement(getDeltaMovement().scale(pitch == 0 ? 0.9 : 0.95));
            Vector3f velocity = getDeltaMovement().toVector3f();
            Vector3f horizontalVelocity = new Vector3f(velocity.x(), 0.0f, velocity.z());
            Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f).rotateY(getYRot() * -Mth.DEG_TO_RAD);
            float forwardMagnitude = horizontalVelocity.dot(forward);
            Vector3f forwardVelocity = new Vector3f(forward).mul(forwardMagnitude);
            Vector3f lateralVelocity = new Vector3f(horizontalVelocity).sub(forwardVelocity);

            // Forward Acceleration
            float acc = pitch * -0.02f;
            forwardVelocity.add(new Vector3f(forward).mul(acc));

            // Lateral Friction
            float lateralFriction = 0.6f;
            lateralVelocity.mul(lateralFriction);

            // Ascend and Descend
            float vertical = throttle * 0.03f;

            // New Velocity
            Vector3f newVelocity = new Vector3f(forwardVelocity).add(lateralVelocity);
            setDeltaMovement(newVelocity.x(), velocity.y() + vertical, newVelocity.z());

            // Steering
            float turnSpeed = -8.0f / (1.0f + forwardMagnitude * 2.0f);
            setYRot(getYRot() - yaw * turnSpeed);

            setXRot((float) (-getDeltaMovement().y() * 100.0));
        }
        else
            applyGravity();

        // Move
        move(MoverType.SELF, getDeltaMovement());
    }

    @Override
    public Quaternionf updateQuaternion() {
        Quaternionf quaternion = new Quaternionf();
        quaternion.rotateY(getYRot() * -Mth.DEG_TO_RAD + Mth.PI);
        quaternion.rotateX(getXRot() * -Mth.DEG_TO_RAD);
        return quaternion;
    }
}