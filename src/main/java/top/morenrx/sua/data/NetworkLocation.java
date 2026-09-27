package top.morenrx.sua.data;

import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.helper.NetworkStorageHelper;
import top.morenrx.sua.upgrades.compat.network.BDNetworkStorage;
import top.morenrx.sua.upgrades.compat.network.BackpackNetworkStorage;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;
import top.morenrx.sua.upgrades.network_magnet.NetworkMagnetUpgrade;

public record NetworkLocation(
        String storageType,
        @Nullable INetworkStorage storage,
        boolean hasBinding,
        @Nullable ResourceKey<Level> dimension,
        @Nullable BlockPos pos,
        int netId
) {
    public static final NetworkLocation UNBOUND = new NetworkLocation("", null, false, null, null, -1);

    public static NetworkLocation fromUpgrade(ItemStack upgrade) {
        String type = NBTHelper.getString(upgrade, NetworkMagnetUpgrade.Data.KEY_NETWORK_TYPE).orElse(NetworkStorageProvider.Data.BACKPACK);

        if (!NetworkStorageProvider.get().hasStorage(type))
            type = NetworkStorageProvider.get().getDefaultStorageType();

        INetworkStorage storage = NetworkStorageProvider.get().getStorage(type);
        if (storage == null) return new NetworkLocation(type, null, false, null, null, -1);

        if (storage instanceof BackpackNetworkStorage) return new NetworkLocation(type, storage, true, null, null, -1);

        if (storage instanceof BDNetworkStorage) {
            CompoundTag tag = NetworkStorageHelper.getStorageTag(upgrade, storage.getName());
            int id = (tag != null && tag.contains(BDNetworkStorage.Data.KEY_ID)) ? tag.getInt(BDNetworkStorage.Data.KEY_ID) : DimensionsNet.NO_PRIMARY_NET_ID;
            boolean bound = id != DimensionsNet.NO_PRIMARY_NET_ID;
            return new NetworkLocation(type, storage, bound, null, null, id);
        }

        CompoundTag tag = NetworkStorageHelper.getStorageTag(upgrade, storage.getName());
        if (tag == null || !tag.contains(INetworkStorage.Data.KEY_POS) || tag.getString(INetworkStorage.Data.KEY_DIM).isEmpty())
            return new NetworkLocation(type, storage, false, null, null, -1);

        long posLong = tag.getLong(INetworkStorage.Data.KEY_POS);
        String dimStr = tag.getString(INetworkStorage.Data.KEY_DIM);
        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, SophUpgradeAddons.parse(dimStr));
        BlockPos blockPos = BlockPos.of(posLong);
        return new NetworkLocation(type, storage, true, dimKey, blockPos, -1);
    }

    public @Nullable BlockEntity getBlockEntity(ServerLevel currentLevel) {
        if (dimension == null || pos == null) return null;
        ServerLevel targetLevel = currentLevel.getServer().getLevel(dimension);
        if (targetLevel == null) return null;
        return targetLevel.getBlockEntity(pos);
    }
}
