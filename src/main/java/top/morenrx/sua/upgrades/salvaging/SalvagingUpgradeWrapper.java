package top.morenrx.sua.upgrades.salvaging;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.p3pp3rf1y.sophisticatedcore.api.ISlotChangeResponseUpgrade;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SalvagingUpgradeWrapper extends UpgradeWrapperBase<SalvagingUpgradeWrapper, SalvagingUpgrade> implements IInsertResponseUpgrade, IFilteredUpgrade, ISlotChangeResponseUpgrade, ITickableUpgrade, IOverflowResponseUpgrade {
    private final FilterLogic filterLogic;

    public SalvagingUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        this.filterLogic = new FilterLogic(upgrade, upgradeSaveHandler, this.upgradeItem.getFilterSlotCount());
        this.filterLogic.setAllowByDefault(true);
    }

    @Override
    public void onSlotChange(@NotNull IItemHandler iItemHandler, int i) {

    }

    @Override
    public @NotNull FilterLogic getFilterLogic() {
        return filterLogic;
    }

    @Override
    public boolean worksInGui() {
        return false;
    }

    @Override
    public @NotNull ItemStack onSlotOverflow(@NotNull ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public @NotNull ItemStack onStorageOverflow(@NotNull ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public boolean stackMatchesFilter(@NotNull ItemStack itemStack) {
        return false;
    }


    public void setWorkInGUI(boolean workInGUI) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_WORK_IN_GUI, workInGUI);
        this.save();
    }

    public boolean shouldWorkInGUI() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_WORK_IN_GUI).orElse(false);
    }

    public void setEquipmentRarity(int equipmentRarity) {
        NBTHelper.setInteger(this.upgrade, SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY, equipmentRarity);
        this.save();
    }

    public int shouldEquipmentRarity() {
        return NBTHelper.getInt(this.upgrade, SalvagingUpgrade.Data.KEY_EQUIPMENT_RARITY).orElse(0);
    }

    public void setGemRarity(int gemRarity) {
        NBTHelper.setInteger(this.upgrade, SalvagingUpgrade.Data.KEY_GEM_RARITY, gemRarity);
        this.save();
    }

    public int shouldGemRarity() {
        return NBTHelper.getInt(this.upgrade, SalvagingUpgrade.Data.KEY_GEM_RARITY).orElse(0);
    }

    public void setSalvagingOther(boolean salvagingOther) {
        NBTHelper.setBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_OTHER, salvagingOther);
        this.save();
    }

    public boolean shouldSalvagingOther() {
        return NBTHelper.getBoolean(this.upgrade, SalvagingUpgrade.Data.KEY_SALVAGING_OTHER).orElse(false);
    }
}
