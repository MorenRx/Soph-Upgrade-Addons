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
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.helper.NetworkStorageHelper;
import top.morenrx.sua.init.SUADataComponents;
import top.morenrx.sua.upgrades.compat.network.BDNetworkStorage;
import top.morenrx.sua.upgrades.compat.network.BackpackNetworkStorage;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

public record NetworkLocation(
        String storageType,
        @Nullable INetworkStorage storage,
        boolean hasBinding,
        @Nullable ResourceKey<Level> dimension,
        @Nullable BlockPos pos,
        int netId
) {

    public static NetworkLocation fromUpgrade(ItemStack upgrade) {
        String type = upgrade.getOrDefault(SUADataComponents.NETWORK_TYPE, NetworkStorageProvider.Data.BACKPACK);

        if (type.isEmpty())
            type = NetworkStorageProvider.Data.BACKPACK;

        if (!NetworkStorageProvider.get().hasStorage(type))
            type = NetworkStorageProvider.get().getDefaultStorageType();

        INetworkStorage storage = NetworkStorageProvider.get().getStorage(type);

        switch (storage) {
            case null -> {
                return new NetworkLocation(type, null, false, null, null, -1);
            }
            case BackpackNetworkStorage ignored -> {
                return new NetworkLocation(type, storage, true, null, null, -1);
            }
            case BDNetworkStorage ignored -> {
                CompoundTag tag = NetworkStorageHelper.getStorageTag(upgrade, storage.getName());
                int id = (tag != null && tag.contains(BDNetworkStorage.Data.KEY_ID)) ? tag.getInt(BDNetworkStorage.Data.KEY_ID) : DimensionsNet.NO_PRIMARY_NET_ID;
                boolean bound = id != DimensionsNet.NO_PRIMARY_NET_ID;
                return new NetworkLocation(type, storage, bound, null, null, id);
            }
            default -> {
            }
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
