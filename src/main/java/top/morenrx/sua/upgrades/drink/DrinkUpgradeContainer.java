package top.morenrx.sua.upgrades.drink;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

public class DrinkUpgradeContainer extends UpgradeContainerBase<DrinkUpgradeWrapper, DrinkUpgradeContainer> {

    private final FilterLogicContainer<FilterLogic> filterLogicContainer;

    public DrinkUpgradeContainer(Player player, int containerId, DrinkUpgradeWrapper wrapper, UpgradeContainerType<DrinkUpgradeWrapper, DrinkUpgradeContainer> type) {
        super(player, containerId, wrapper, type);
        filterLogicContainer = new FilterLogicContainer<>(() -> upgradeWrapper.getFilterLogic(), this, slots::add);
    }

    @Override
    public void handleMessage(CompoundTag data) {
        if (data.contains(DrinkUpgrade.Data.DATA_THIRST_LEVEL)) {
            setDrinkAtThirstLevel(data.getInt(DrinkUpgrade.Data.DATA_THIRST_LEVEL));
        } else if (data.contains(DrinkUpgrade.Data.DATA_DRINK_FOR_HURT)) {
            setDrinkForHurt(data.getBoolean(DrinkUpgrade.Data.DATA_DRINK_FOR_HURT));
        } else if (data.contains(DrinkUpgrade.Data.DATA_PURITY)) {
            setPurity(data.getInt(DrinkUpgrade.Data.DATA_PURITY));
        }
        filterLogicContainer.handleMessage(data);
    }

    public FilterLogicContainer<FilterLogic> getFilterLogicContainer() {
        return filterLogicContainer;
    }

    public void setDrinkAtThirstLevel(int thirstLevel) {
        upgradeWrapper.setDrinkAtThirstLevel(thirstLevel);
        sendDataToServer(() -> NBTHelper.putInt(new CompoundTag(), DrinkUpgrade.Data.DATA_THIRST_LEVEL, thirstLevel));
    }

    public int getDrinkAtThirstLevel() {
        return upgradeWrapper.getDrinkAtThirstLevel();
    }

    public void setDrinkForHurt(boolean drinkForHurt) {
        upgradeWrapper.setDrinkForHurt(drinkForHurt);
        sendBooleanToServer(DrinkUpgrade.Data.DATA_DRINK_FOR_HURT, drinkForHurt);
    }

    public boolean shouldDrinkForHurt() {
        return upgradeWrapper.shouldDrinkForHurt();
    }

    public void setPurity(int purity) {
        upgradeWrapper.setPurity(purity);
        sendDataToServer(() -> NBTHelper.putInt(new CompoundTag(), DrinkUpgrade.Data.DATA_PURITY, purity));
    }

    public int shouldPurity() {
        return upgradeWrapper.shouldPurity();
    }
}
