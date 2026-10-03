package top.morenrx.sua.upgrades.ender_chest;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EnderChestUpgradeConfig {
    public final ModConfigSpec.BooleanValue enable;

    public EnderChestUpgradeConfig(ModConfigSpec.Builder builder, String name, String path) {
        builder.comment(name + " 设置").push(path);
        enable = builder.comment("是否启用").define("enable", true);
        builder.pop();
    }
}
