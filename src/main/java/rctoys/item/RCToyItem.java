package rctoys.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import rctoys.entity.AbstractRCEntity;

public class RCToyItem extends Item
{
	private final EntityType<? extends AbstractRCEntity> type;

	public RCToyItem(EntityType<? extends AbstractRCEntity> type, Item.Properties settings)
	{
		super(settings.stacksTo(1));
		this.type = type;
	}
	
	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand)
	{
		ItemStack itemStack = user.getItemInHand(hand);
		BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
		
		if(!world.isClientSide() && blockHitResult.getType() == HitResult.Type.BLOCK)
		{
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getDirection();
			BlockPos blockPos2;
			
			if(world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty())
				blockPos2 = blockPos;
			else
				blockPos2 = blockPos.relative(direction);

			if(createEntity(world, blockPos2, itemStack, user) != null)
			{
				if(!user.isCreative())
					itemStack.shrink(1);
				
				world.gameEvent(user, GameEvent.ENTITY_PLACE, blockPos);
			}

			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}

	@Nullable
	private AbstractRCEntity createEntity(Level world, BlockPos pos, ItemStack stack, Player player)
	{
		AbstractRCEntity abstractRCEntity = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
		
		if(abstractRCEntity != null)
		{
			Vec3 vec3d = pos.getCenter();
			abstractRCEntity.setPos(vec3d.x, vec3d.y, vec3d.z);
			abstractRCEntity.setYRot(player.getYRot());
			abstractRCEntity.setQuaternion(new Quaternionf(new AxisAngle4f(abstractRCEntity.getYRot() * -Mth.DEG_TO_RAD + Mth.PI, 0.0f, 1.0f, 0.0f)));
			
			if(stack.has(DataComponents.DYED_COLOR))
				abstractRCEntity.setColor(stack.get(DataComponents.DYED_COLOR).rgb());
			
			world.addFreshEntity(abstractRCEntity);
			
			if(world instanceof ServerLevel serverWorld)
				EntityType.createDefaultStackConfig(serverWorld, stack, player).accept(abstractRCEntity);
		}
		
		return abstractRCEntity;
	}
}