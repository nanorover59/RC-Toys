package rctoys.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		
		if(world.isClient)
			return ActionResult.SUCCESS;
		else
		{
			ItemStack itemStack = context.getStack();
			BlockPos blockPos = context.getBlockPos();
			Direction direction = context.getSide();
			BlockState blockState = world.getBlockState(blockPos);

			BlockPos blockPos2;
			
			if(blockState.getCollisionShape(world, blockPos).isEmpty())
				blockPos2 = blockPos;
			else
				blockPos2 = blockPos.offset(direction);

			if(createEntity(world, blockPos2, itemStack, context.getPlayer()) != null)
			{
				if(!context.getPlayer().isCreative())
					itemStack.decrement(1);
				
				world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
			}

			return ActionResult.SUCCESS;
		}
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
			
			if(stack.contains(DataComponentTypes.DYED_COLOR))
				abstractRCEntity.setColor(stack.get(DataComponentTypes.DYED_COLOR).rgb());
			
			world.spawnEntity(abstractRCEntity);
			
			if(world instanceof ServerWorld serverWorld)
				EntityType.copier(serverWorld, stack, player).accept(abstractRCEntity);
		}
		
		return abstractRCEntity;
	}
}