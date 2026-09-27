package top.morenrx.sua.mixin.common.sophisticatedbackpacks;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.morenrx.sua.upgrades.base.UpgradeSlotHelper;

@Mixin(value = StorageContainerMenuBase.StorageUpgradeSlot.class)
public class MixinStorageUpgradeSlot {

    @Redirect(
            method = "mayPlace",
            at = @At(
                    remap = false,
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/items/IItemHandler;isItemValid(ILnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean redirectIsItemValid(IItemHandler itemHandler, int slotIndex, ItemStack stack) {
        return UpgradeSlotHelper.isItemValid(itemHandler, slotIndex, stack);
    }
}
