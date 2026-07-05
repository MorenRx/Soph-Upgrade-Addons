package top.morenrx.sua.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.morenrx.sua.SophUpgradeAddons;

public record NetworkLocation(ServerLevel dim, BlockPos pos) {

    public static NetworkLocation create(ServerLevel level, String dim, long pos) {
        if (pos == 0 || dim.isEmpty()) return null;
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, SophUpgradeAddons.parse(dim));

        ServerLevel rsLevel = level.getServer().getLevel(dimensionKey);
        if (rsLevel == null) return null;

        return new NetworkLocation(rsLevel, BlockPos.of(pos));
    }

    public static BlockEntity getBlockEntity(NetworkLocation location) {
        return location.dim().getBlockEntity(location.pos());
    }
}
