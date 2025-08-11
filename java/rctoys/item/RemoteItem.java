package rctoys.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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
		
		if(rcEntity != null)
		{
			boolean enable = slot == EquipmentSlot.MAINHAND;
			
			if(enable && !rcEntity.isEnabled())
				entity.playSound(RCToysMod.REMOTE_LINK_SOUND, 2.0f, 0.7f);
			
			rcEntity.setEnabled(enable);
		}
	}
	
	public static AbstractRCEntity getRCEntity(ItemStack stack, ServerWorld world)
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