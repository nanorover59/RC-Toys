package rctoys.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rctoys.RCToysMod;
import rctoys.entity.AbstractRCEntity;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Inject(method = "setSelectedSlot", at = @At(value = "HEAD"), cancellable = true)
    public void setSelectedSlotInject(int selected, CallbackInfo info) {
        Inventory inventory = (Inventory) (Object) this;
        ItemStack stack = inventory.getSelectedItem();

        if(stack.has(RCToysMod.REMOTE_LINK)) {
            Entity entityByUUID = inventory.player.level().getEntity(stack.get(RCToysMod.REMOTE_LINK).uuid());

            if(entityByUUID != null && entityByUUID instanceof AbstractRCEntity rcEntity && rcEntity.isEnabled())
                info.cancel();
        }
    }
}