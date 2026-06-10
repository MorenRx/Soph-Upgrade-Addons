package top.morenrx.sbua.upgrades.voiding;

import net.minecraftforge.common.ForgeConfigSpec;
import top.morenrx.sbua.upgrades.base.FilteredUpgradeConfig;

public class SuperVoidUpgradeConfig extends FilteredUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public SuperVoidUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path, int defaultFilterSlots, int defaultSlotsInRow) {
        super(builder, name, path, defaultFilterSlots, defaultSlotsInRow);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}
