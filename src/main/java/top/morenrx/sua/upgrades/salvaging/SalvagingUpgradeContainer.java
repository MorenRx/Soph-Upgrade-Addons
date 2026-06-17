package top.morenrx.sua.upgrades.salvaging;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;

public class SalvagingUpgradeContainer extends UpgradeContainerBase<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> {

    private final FilterLogicContainer<FilterLogic> filterLogicContainer;

    public SalvagingUpgradeContainer(Player player, int upgradeContainerId, SalvagingUpgradeWrapper upgradeWrapper, UpgradeContainerType<SalvagingUpgradeWrapper, SalvagingUpgradeContainer> type) {
        super(player, upgradeContainerId, upgradeWrapper, type);
        filterLogicContainer = new FilterLogicContainer<>(upgradeWrapper::getFilterLogic, this, slots::add);
    }


    @Override
    public void handleMessage(@NotNull CompoundTag data) {
        if (data.contains(SalvagingUpgrade.Data.KEY_WORK_IN_GUI)) {
            setWorkInGUI(data.getBoolean(SalvagingUpgrade.Data.KEY_WORK_IN_GUI));
        } else if (data.contains(SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY)) {
            setEquipmentRarity(data.getInt(SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY));
        } else if (data.contains(SalvagingUpgrade.Data.KEY_GEM_RARITY)) {
            setGemRarity(data.getInt(SalvagingUpgrade.Data.KEY_GEM_RARITY));
        } else if (data.contains(SalvagingUpgrade.Data.KEY_SALVAGING_OTHER)) {
            setSalvagingOther(data.getBoolean(SalvagingUpgrade.Data.KEY_SALVAGING_OTHER));
        }
        filterLogicContainer.handleMessage(data);
    }

    public FilterLogicContainer<FilterLogic> getFilterLogicContainer() {
        return filterLogicContainer;
    }

    public void setWorkInGUI(boolean workInGUI) {
        upgradeWrapper.setWorkInGUI(workInGUI);
        sendBooleanToServer(SalvagingUpgrade.Data.KEY_WORK_IN_GUI, workInGUI);
    }

    public boolean shouldWorkInGUI() {
        return upgradeWrapper.shouldWorkInGUI();
    }

    public void setEquipmentRarity(int equipmentRarity) {
        upgradeWrapper.setEquipmentRarity(equipmentRarity);
        sendDataToServer(() -> NBTHelper.putInt(new CompoundTag(), SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY, equipmentRarity));
    }

    public int shouldEquipmentRarity() {
        return upgradeWrapper.shouldEquipmentRarity();
    }

    public void setGemRarity(int gemRarity) {
        upgradeWrapper.setGemRarity(gemRarity);
        sendDataToServer(() -> NBTHelper.putInt(new CompoundTag(), SalvagingUpgrade.Data.KEY_GEM_RARITY, gemRarity));
    }

    public int shouldGemRarity() {
        return upgradeWrapper.shouldGemRarity();
    }

    public void setSalvagingOther(boolean salvagingOther) {
        upgradeWrapper.setSalvagingOther(salvagingOther);
        sendBooleanToServer(SalvagingUpgrade.Data.KEY_SALVAGING_OTHER, salvagingOther);
    }

    public boolean shouldSalvagingOther() {
        return upgradeWrapper.shouldSalvagingOther();
    }
}
