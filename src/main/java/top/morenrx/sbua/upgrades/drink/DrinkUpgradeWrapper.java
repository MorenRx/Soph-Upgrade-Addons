package top.morenrx.sbua.upgrades.drink;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DrinkUpgradeWrapper extends UpgradeWrapperBase<DrinkUpgradeWrapper, DrinkUpgrade> implements ITickableUpgrade, IFilteredUpgrade {

    private final FilterLogic filterLogic;

    public DrinkUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount(), ItemStack::isEdible);
    }

    @Override
    public void tick(@Nullable Entity entity, @NotNull Level level, @NotNull BlockPos pos) {

    }

    @Override
    public @NotNull FilterLogic getFilterLogic() {
        return filterLogic;
    }
}
