package top.morenrx.sua.mixin.common.tomstorage;

import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;

@Mixin(value = StorageTerminalBlockEntity.class, remap = false)
public class MixinStorageTerminalBlockEntity implements IStorageTerminalBlockEntityAccess {

    @Shadow
    private IItemHandler itemHandler;
    @Shadow
    private boolean updateItems;

    @Override
    public IItemHandler sua$getItemHandler() {
        return itemHandler;
    }

    @Override
    public void sua$setUpdateItems(boolean update) {
        this.updateItems = update;
    }
}
