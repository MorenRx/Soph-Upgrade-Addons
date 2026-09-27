package top.morenrx.sua.helper.client;

import net.minecraft.world.level.block.entity.BlockEntity;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

public class NetworkKeybindHelper {

    public static boolean isNetworkStorageBlock(BlockEntity te) {
        if (te == null) return false;
        for (INetworkStorage storage : NetworkStorageProvider.get().getStorages().values()) {
            if (storage.isValidDepositBlock(te) || storage.isValidRestockBlock(te))
                return true;
        }
        return false;
    }
}
