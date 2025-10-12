package rctoys.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import rctoys.RCToysMod;
import rctoys.item.RemoteItem;
import rctoys.item.RemoteLinkComponent;
import rctoys.network.c2s.MotorSoundS2CPacket;
import rctoys.network.c2s.RemoteControlC2SPacket;

import java.util.UUID;

public abstract class AbstractRCEntity extends Entity
{
	private static final TrackedData<Integer> COLOR = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> ENABLED = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Float> QX = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> QY = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> QZ = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> QW = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> THROTTLE = DataTracker.registerData(AbstractRCEntity.class, TrackedDataHandlerRegistry.FLOAT);

	private final PositionInterpolator interpolator = new PositionInterpolator(this, 3);
	public Quaternionf clientQuaternion;
	public Quaternionf clientQuaternionPrevious;

	public AbstractRCEntity(EntityType<?> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(COLOR, Integer.valueOf(getDefaultColor()));
		builder.add(ENABLED, false);
		builder.add(QX, Float.valueOf(0.0f));
		builder.add(QY, Float.valueOf(0.0f));
		builder.add(QZ, Float.valueOf(0.0f));
		builder.add(QW, Float.valueOf(1.0f));
		builder.add(THROTTLE, Float.valueOf(0.0f));
	}

	public void setColor(int color)
	{
		this.dataTracker.set(COLOR, color);
	}

	public int getColor()
	{
		return this.dataTracker.get(COLOR).intValue();
	}
	
	public abstract int getDefaultColor();

	public void setEnabled(boolean enabled)
	{
		if(enabled && !isEnabled())
			this.playSound(RCToysMod.REMOTE_LINK_SOUND, 2.0f, 0.7f);
		
		this.dataTracker.set(ENABLED, enabled);
	}

	public boolean isEnabled()
	{
		return this.dataTracker.get(ENABLED).booleanValue();
	}

	public void setQuaternion(Quaternionf quaternion)
	{
		this.dataTracker.set(QX, quaternion.x());
		this.dataTracker.set(QY, quaternion.y());
		this.dataTracker.set(QZ, quaternion.z());
		this.dataTracker.set(QW, quaternion.w());
	}

	public Quaternionf getQuaternion()
	{
		return new Quaternionf(this.dataTracker.get(QX).floatValue(), this.dataTracker.get(QY).floatValue(), this.dataTracker.get(QZ).floatValue(), this.dataTracker.get(QW).floatValue());
	}
	
	public Quaternionf getLerpedQuaternion(float tickProgress)
	{
		if(this.clientQuaternion == null)
			return new Quaternionf();
		else if(tickProgress == 1.0f || this.clientQuaternionPrevious == null)
			return this.clientQuaternion;
		
		return new Quaternionf(this.clientQuaternionPrevious).slerp(this.clientQuaternion, tickProgress);
	}
	
	public void setThrottle(float throttle)
	{
		this.dataTracker.set(THROTTLE, throttle);
	}

	public float getThrottle()
	{
		return this.dataTracker.get(THROTTLE).floatValue();
	}

	@Override
	public PositionInterpolator getInterpolator()
	{
		return this.interpolator;
	}

	@Override
	public void tick()
	{
		super.tick();
		this.interpolator.tick();
		World world = getEntityWorld();

		if(world.isClient())
		{
			if(this.clientQuaternion != null)
			{
				this.clientQuaternionPrevious = new Quaternionf(this.clientQuaternion);
				this.clientQuaternion = new Quaternionf(this.clientQuaternion).slerp(getQuaternion(), 0.4f);
			}
			else
				this.clientQuaternion = getQuaternion();
		}
		else
		{
			tickPhysics();
			setQuaternion(updateQuaternion());

			for(Entity other : this.getEntityWorld().getOtherEntities(this, this.getBoundingBox()))
		        this.pushAwayFrom(other);

			this.velocityModified = true;
			this.velocityDirty = true;
		}
	}

	public abstract void tickPhysics();

	public abstract Quaternionf updateQuaternion();

	public abstract void remoteControlInput(boolean[] inputArray);

	public abstract Item asItem();

	@Override
	protected double getGravity()
	{
		return 0.08;
	}

	@Override
	public boolean isPushable()
	{
		return true;
	}

	@Override
	public float getStepHeight()
	{
		return 0.5f;
	}

	@Override
	public boolean canHit()
	{
		return !this.isRemoved();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state)
	{
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getStackInHand(hand);

		if(!stack.isOf(RCToysMod.REMOTE))
		{
			// Miniature players can ride RC toys :)
			if(player.getScale() <= 0.31)
			{
				player.startRiding(this);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		}

		World world = getEntityWorld();

		if(!world.isClient())
		{
			AbstractRCEntity previousRCEntity = RemoteItem.getRCEntity(stack, (ServerWorld) world);

			if(previousRCEntity != null)
			{
				cleanRemoteLinks(previousRCEntity.getUuid());
				previousRCEntity.setEnabled(false);
			}

			cleanRemoteLinks(getUuid());
			setEnabled(true);
			stack.set(RCToysMod.REMOTE_LINK, new RemoteLinkComponent(getUuid(), getName().getString()));
			player.sendMessage(Text.translatable("entity.rctoys.remote_linked"), false);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		this.kill(world);

		if(world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
		{
			ItemStack itemStack = new ItemStack(asItem());
			itemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());

			if(getColor() != getDefaultColor())
				itemStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(getColor()));

			this.dropStack(world, itemStack);
		}

		return true;
	}

	@Override
	public void onRemove(Entity.RemovalReason reason)
	{
		World world = getEntityWorld();

		if(world.isClient())
			return;

		cleanRemoteLinks(getUuid());
	}

    public SoundEvent getSoundLoop()
    {
        return RCToysMod.CAR_LOOP_SOUND;
    }

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player)
	{
		ServerPlayNetworking.send(player, new MotorSoundS2CPacket(getId(), true, getSoundLoop().id()));
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player)
	{
		ServerPlayNetworking.send(player, new MotorSoundS2CPacket(getId(), false, getSoundLoop().id()));
	}

	private void cleanRemoteLinks(UUID rcUUID)
	{
		for(ServerPlayerEntity player : ((ServerWorld) getEntityWorld()).getPlayers())
		{
			for(ItemStack stack : player.getInventory())
			{
				if(stack.contains(RCToysMod.REMOTE_LINK))
				{
					UUID foundUUID = stack.get(RCToysMod.REMOTE_LINK).uuid();

					if(rcUUID.equals(foundUUID))
						stack.set(RCToysMod.REMOTE_LINK, null);
				}
			}
		}
	}

	@Override
	protected void readCustomData(ReadView view)
	{
		setColor(view.getInt("color", Colors.WHITE));
		setQuaternion(new Quaternionf(view.getFloat("qx", 0.0f), view.getFloat("qy", 0.0f), view.getFloat("qz", 0.0f), view.getFloat("qw", 0.0f)).normalize());
	}

	@Override
	protected void writeCustomData(WriteView view)
	{
		view.putInt("color", this.dataTracker.get(COLOR).intValue());
		view.putFloat("qx", this.dataTracker.get(QX).floatValue());
		view.putFloat("qy", this.dataTracker.get(QY).floatValue());
		view.putFloat("qz", this.dataTracker.get(QZ).floatValue());
		view.putFloat("qw", this.dataTracker.get(QW).floatValue());
	}

	public static void receiveControl(RemoteControlC2SPacket payload, ServerPlayNetworking.Context context)
	{
		int input = payload.input();

		context.server().execute(() -> {

			ServerPlayerEntity player = context.player();
			ItemStack stack = player.getMainHandStack();

			if(stack.isOf(RCToysMod.REMOTE) && stack.contains(RCToysMod.REMOTE_LINK))
			{
				UUID rcUUID = stack.get(RCToysMod.REMOTE_LINK).uuid();
				Entity entity = player.getEntityWorld().getEntity(rcUUID);

				if(entity != null && entity instanceof AbstractRCEntity)
					((AbstractRCEntity) entity).remoteControlInput(unpackInput(input));
			}
		});
	}
	
	public static boolean[] unpackInput(int input)
	{
		boolean[] inputArray = new boolean[6];

		for(int i = 0; i < 6; i++)
			inputArray[i] = ((input >> i) & 1) == 1;
		
		return inputArray;
	}
}