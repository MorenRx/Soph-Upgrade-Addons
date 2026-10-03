package top.morenrx.sua.mixin.common.tomstorage;

import com.tom.storagemod.block.entity.StorageTerminalBlockEntity;
import com.tom.storagemod.inventory.NetworkInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;

@Mixin(value = StorageTerminalBlockEntity.class, remap = false)
public class MixinStorageTerminalBlockEntity implements IStorageTerminalBlockEntityAccess {

    @Shadow
    private NetworkInventory itemCache;

    @Override
    public NetworkInventory sua$getItemCache() {
        return this.itemCache;
    }
}
