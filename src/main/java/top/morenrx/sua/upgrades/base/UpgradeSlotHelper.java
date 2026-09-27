package top.morenrx.sua.upgrades.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IUpgradeItem;

public class UpgradeSlotHelper {

    public static boolean isItemValid(IItemHandler itemHandler, int slotIndex, ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (!(item instanceof IUpgradeItem)) return false;
        return !(item instanceof ISUAItemConfig config) || config.isEnable();
    }
}
