package top.morenrx.sua.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import top.morenrx.sua.SophUpgradeAddons;

public record RSLocation(ServerLevel dim, BlockPos pos) {

    public static RSLocation create(Level level, long pos, String dim) {
        if (pos == 0 || dim.isEmpty()) return null;
        if (!(level instanceof ServerLevel serverLevel)) return null;
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, SophUpgradeAddons.parse(dim));

        ServerLevel rsLevel = serverLevel.getServer().getLevel(dimensionKey);
        if (rsLevel == null) return null;

        return new RSLocation(rsLevel, BlockPos.of(pos));
    }
}
