package top.morenrx.sua.upgrades.network_pickup;

import net.minecraftforge.common.ForgeConfigSpec;
import top.morenrx.sua.upgrades.base.FilteredUpgradeConfig;

public class NetworkPickupUpgradeConfig extends FilteredUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public NetworkPickupUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}