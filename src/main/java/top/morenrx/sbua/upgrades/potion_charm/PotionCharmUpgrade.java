package top.morenrx.sbua.upgrades.potion_charm;

import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.init.SBUACompat;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;

import java.util.List;
import java.util.function.IntSupplier;

public class PotionCharmUpgrade extends UpgradeItemBase<PotionCharmUpgradeWrapper> implements ISBUAItemConfig {
    private static final UpgradeType<PotionCharmUpgradeWrapper> TYPE = new UpgradeType<>(PotionCharmUpgradeWrapper::new);
    private final PotionCharmUpgradeConfig config;

    public PotionCharmUpgrade(PotionCharmUpgradeConfig config) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.config = config;
    }

    public @NotNull UpgradeType<PotionCharmUpgradeWrapper> getType() {
        return TYPE;
    }

    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public boolean isEnable() {
        return false;
//        return SBUACompat.APOTHEOSIS && config.enable.get();
    }
}

