package top.morenrx.sua.upgrades.rs_magnet;

import net.minecraftforge.common.ForgeConfigSpec;
import top.morenrx.sua.upgrades.base.FilteredUpgradeConfig;

public class RSMagnetUpgradeConfig extends FilteredUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;
    public final ForgeConfigSpec.IntValue magnetRange;

    public RSMagnetUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow, int defaultMagnetRange) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        magnetRange = builder.comment("磁铁吸取的范围").defineInRange("magnetRange", defaultMagnetRange, 1, 20);
        builder.pop();
    }
}
