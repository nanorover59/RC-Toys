package rctoys.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import rctoys.RCToysMod;
import rctoys.entity.AbstractRCEntity;

public class RemoteItem extends Item
{
	public RemoteItem(Properties settings)
	{
		super(settings.stacksTo(1));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot)
	{
		AbstractRCEntity rcEntity = getRCEntity(stack, world);
		
		if(rcEntity != null && rcEntity.isEnabled() && slot != EquipmentSlot.MAINHAND)
		{
			rcEntity.setEnabled(false);
			
			if(entity instanceof Player)
				((Player) entity).displayClientMessage(Component.translatable("item.rctoys.stopped_controlling", rcEntity.getName().getString()), false);
		}
	}
	
	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand)
	{
		if(world.isClientSide())
			return InteractionResult.PASS;
		
		ItemStack stack = user.getItemInHand(hand);
		AbstractRCEntity rcEntity = getRCEntity(stack, world);
		
		if(rcEntity != null)
		{
			boolean enabled = rcEntity.isEnabled();
			rcEntity.setEnabled(!enabled);
			user.displayClientMessage(Component.translatable(enabled ? "item.rctoys.stopped_controlling" : "item.rctoys.started_controlling", rcEntity.getName().getString()), false);
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	public static AbstractRCEntity getRCEntity(ItemStack stack, Level world)
	{
		if(stack.has(RCToysMod.REMOTE_LINK))
		{
			Entity entityByUUID = world.getEntity(stack.get(RCToysMod.REMOTE_LINK).uuid());
			
			if(entityByUUID != null && entityByUUID instanceof AbstractRCEntity)
				return (AbstractRCEntity) entityByUUID;
		}
		
		return null;
	}
}