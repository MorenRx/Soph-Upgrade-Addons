package top.morenrx.sbua.upgrades.drink;

import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedcore.init.ModCompat;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.init.SBUACompat;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;

import java.util.List;
import java.util.function.IntSupplier;

public class DrinkUpgrade extends UpgradeItemBase<DrinkUpgradeWrapper> implements ISBUAItemConfig {
    public static final UpgradeType<DrinkUpgradeWrapper> TYPE = new UpgradeType<>(DrinkUpgradeWrapper::new);
    private final DrinkUpgradeConfig config;

    public DrinkUpgrade(DrinkUpgradeConfig config) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.config = config;
    }

    public int getFilterSlotCount() {
        return config.filterSlots.get();
    }

    public @NotNull UpgradeType<DrinkUpgradeWrapper> getType() {
        return TYPE;
    }

    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return List.of();
    }

    @Override
    public boolean isEnable() {
        return false;
//        return SBUACompat.THIRST && config.enable.get();
    }
}
