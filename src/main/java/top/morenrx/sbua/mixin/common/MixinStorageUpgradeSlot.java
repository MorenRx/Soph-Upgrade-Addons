package top.morenrx.sbua.mixin.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.common.gui.StorageContainerMenuBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;

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
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (!(item instanceof IUpgradeItem)) return false;
        return !(item instanceof ISBUAItemConfig config) || config.isEnable();
    }

}
