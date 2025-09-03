package rctoys.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import rctoys.RCToysMod;
import rctoys.entity.AbstractRCEntity;

public class RemoteItem extends Item
{
	public RemoteItem(Settings settings)
	{
		super(settings.maxCount(1));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot)
	{
		AbstractRCEntity rcEntity = getRCEntity(stack, world);
		
		if(rcEntity != null && rcEntity.isEnabled() && slot != EquipmentSlot.MAINHAND)
		{
			rcEntity.setEnabled(false);
			
			if(entity instanceof PlayerEntity)
				((PlayerEntity) entity).sendMessage(Text.translatable("item.rctoys.stopped_controlling", rcEntity.getName().getString()), false);
		}
	}
	
	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand)
	{
		if(world.isClient)
			return ActionResult.PASS;
		
		ItemStack stack = user.getStackInHand(hand);
		AbstractRCEntity rcEntity = getRCEntity(stack, world);
		
		if(rcEntity != null)
		{
			boolean enabled = rcEntity.isEnabled();
			rcEntity.setEnabled(!enabled);
			user.sendMessage(Text.translatable(enabled ? "item.rctoys.stopped_controlling" : "item.rctoys.started_controlling", rcEntity.getName().getString()), false);
			return ActionResult.SUCCESS;
		}
		
		return ActionResult.PASS;
	}
	
	public static AbstractRCEntity getRCEntity(ItemStack stack, World world)
	{
		if(stack.contains(RCToysMod.REMOTE_LINK))
		{
			Entity entityByUUID = world.getEntity(stack.get(RCToysMod.REMOTE_LINK).uuid());
			
			if(entityByUUID != null && entityByUUID instanceof AbstractRCEntity)
				return (AbstractRCEntity) entityByUUID;
		}
		
		return null;
	}
}