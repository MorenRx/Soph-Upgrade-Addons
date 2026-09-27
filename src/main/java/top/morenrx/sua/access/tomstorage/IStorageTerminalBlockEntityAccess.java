package top.morenrx.sua.access.tomstorage;

import net.minecraftforge.items.IItemHandler;

public interface IStorageTerminalBlockEntityAccess {

    IItemHandler sua$getItemHandler();

    void sua$setUpdateItems(boolean updateItems);
}
