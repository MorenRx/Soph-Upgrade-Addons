package top.morenrx.sua.access.tomstorage;

import net.minecraft.world.item.ItemStack;

public interface IStorageTerminalBlockEntityAccess {

    ItemStack sua$pushStack(ItemStack itemstack, boolean simulate);
}
