package top.morenrx.sua.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import top.morenrx.sua.SophUpgradeAddons;

public record NetworkLocation(Integer id, ServerLevel dim, BlockPos pos) {

    public static NetworkLocation create(ServerLevel level, Integer id, String dim, Long pos) {
        if (id != null) return new NetworkLocation(id, null, null);
        if (pos == null || dim.isEmpty()) return null;
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, SophUpgradeAddons.parse(dim));

        ServerLevel rsLevel = level.getServer().getLevel(dimensionKey);
        if (rsLevel == null) return null;

        return new NetworkLocation(null, rsLevel, BlockPos.of(pos));
    }

    public static BlockEntity getBlockEntity(NetworkLocation location) {
        return location.dim().getBlockEntity(location.pos());
    }
}
