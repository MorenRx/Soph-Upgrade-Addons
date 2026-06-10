package top.morenrx.sbua.upgrades.rs_deposit;

import net.minecraftforge.common.ForgeConfigSpec;
import top.morenrx.sbua.upgrades.base.FilteredUpgradeConfig;

public class RSDepositUpgradeConfig extends FilteredUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public RSDepositUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}