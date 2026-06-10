package top.morenrx.sbua.upgrades.end_chest;

import net.minecraftforge.common.ForgeConfigSpec;

public class EndChestUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public EndChestUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path) {
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}
