package top.morenrx.sbua.upgrades.rs_pickup;

import net.minecraftforge.common.ForgeConfigSpec;
import top.morenrx.sbua.upgrades.base.FilteredUpgradeConfig;

public class RSPickupUpgradeConfig extends FilteredUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public RSPickupUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}