package top.morenrx.sua.upgrades.network_magnet;

import net.neoforged.neoforge.common.ModConfigSpec;
import top.morenrx.sua.upgrades.base.FilteredUpgradeConfig;

public class NetworkMagnetUpgradeConfig extends FilteredUpgradeConfig {
    public final ModConfigSpec.BooleanValue enable;
    public final ModConfigSpec.IntValue magnetRange;

    public NetworkMagnetUpgradeConfig(ModConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow, int defaultMagnetRange) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        magnetRange = builder.comment("磁铁吸取的范围").defineInRange("magnetRange", defaultMagnetRange, 1, 20);
        builder.pop();
    }
}
