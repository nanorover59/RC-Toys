package rctoys.item;

import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import rctoys.entity.AbstractRCEntity;

public class RCToyItem extends Item
{
	private final EntityType<? extends AbstractRCEntity> type;

	public RCToyItem(EntityType<? extends AbstractRCEntity> type, Item.Settings settings)
	{
		super(settings.maxCount(1));
		this.type = type;
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		
		if(!world.isClient && blockHitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos2;
			
			if(world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty())
				blockPos2 = blockPos;
			else
				blockPos2 = blockPos.offset(direction);

			if(createEntity(world, blockPos2, itemStack, user) != null)
			{
				if(!user.isCreative())
					itemStack.decrement(1);
				
				world.emitGameEvent(user, GameEvent.ENTITY_PLACE, blockPos);
			}

			return ActionResult.SUCCESS;
		}
		
		return ActionResult.PASS;
	}

	@Nullable
	private AbstractRCEntity createEntity(World world, BlockPos pos, ItemStack stack, PlayerEntity player)
	{
		AbstractRCEntity abstractRCEntity = type.create(world, SpawnReason.SPAWN_ITEM_USE);
		
		if(abstractRCEntity != null)
		{
			Vec3d vec3d = pos.toCenterPos();
			abstractRCEntity.setPosition(vec3d.x, vec3d.y, vec3d.z);
			abstractRCEntity.setYaw(player.getYaw());
			abstractRCEntity.setQuaternion(new Quaternionf(new AxisAngle4f(abstractRCEntity.getYaw() * -MathHelper.RADIANS_PER_DEGREE + MathHelper.PI, 0.0f, 1.0f, 0.0f)));
			
			if(stack.contains(DataComponentTypes.DYED_COLOR))
				abstractRCEntity.setColor(stack.get(DataComponentTypes.DYED_COLOR).rgb());
			
			world.spawnEntity(abstractRCEntity);
			
			if(world instanceof ServerWorld serverWorld)
				EntityType.copier(serverWorld, stack, player).accept(abstractRCEntity);
		}
		
		return abstractRCEntity;
	}
}