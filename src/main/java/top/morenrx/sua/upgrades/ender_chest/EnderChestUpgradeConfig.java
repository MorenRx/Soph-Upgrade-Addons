package top.morenrx.sua.upgrades.ender_chest;

import net.minecraftforge.common.ForgeConfigSpec;

public class EnderChestUpgradeConfig {
    public final ForgeConfigSpec.BooleanValue enable;

    public EnderChestUpgradeConfig(ForgeConfigSpec.Builder builder, String name, String path) {
        builder.comment(name + " 设置").push(path);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}
