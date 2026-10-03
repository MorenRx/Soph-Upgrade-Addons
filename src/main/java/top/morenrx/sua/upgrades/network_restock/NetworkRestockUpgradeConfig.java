package top.morenrx.sua.upgrades.network_restock;

import net.neoforged.neoforge.common.ModConfigSpec;
import top.morenrx.sua.upgrades.base.FilteredUpgradeConfig;

public class NetworkRestockUpgradeConfig extends FilteredUpgradeConfig {
    public final ModConfigSpec.BooleanValue enable;

    public NetworkRestockUpgradeConfig(ModConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}
