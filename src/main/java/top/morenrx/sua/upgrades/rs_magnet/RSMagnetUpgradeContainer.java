package top.morenrx.sua.upgrades.rs_magnet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogicContainer;

public class RSMagnetUpgradeContainer extends UpgradeContainerBase<RSMagnetUpgradeWrapper, RSMagnetUpgradeContainer> {
    private final ContentsFilterLogicContainer filterLogicContainer;

    public RSMagnetUpgradeContainer(Player player, int containerId, RSMagnetUpgradeWrapper wrapper, UpgradeContainerType<RSMagnetUpgradeWrapper, RSMagnetUpgradeContainer> type) {
        super(player, containerId, wrapper, type);

        filterLogicContainer = new ContentsFilterLogicContainer(() -> upgradeWrapper.getFilterLogic(), this, slots::add);
    }

    @Override
    public void handleMessage(CompoundTag data) {
        if (data.contains(RSMagnetUpgrade.Data.KEY_PICKUP_ITEMS)) {
            setPickupItems(data.getBoolean(RSMagnetUpgrade.Data.KEY_PICKUP_ITEMS));
        } else if (data.contains(RSMagnetUpgrade.Data.KEY_PICKUP_XP)) {
            setPickupXp(data.getBoolean(RSMagnetUpgrade.Data.KEY_PICKUP_XP));
        } else if (data.contains(RSMagnetUpgrade.Data.KEY_ENABLE_VOID)) {
            setEnableVoid(data.getBoolean(RSMagnetUpgrade.Data.KEY_ENABLE_VOID));
        }
        filterLogicContainer.handleMessage(data);
    }

    public ContentsFilterLogicContainer getFilterLogicContainer() {
        return filterLogicContainer;
    }

    public void setPickupItems(boolean pickupItems) {
        upgradeWrapper.setPickupItems(pickupItems);
        sendBooleanToServer(RSMagnetUpgrade.Data.KEY_PICKUP_ITEMS, pickupItems);
    }

    public boolean shouldPickupItems() {
        return upgradeWrapper.shouldPickupItems();
    }

    public void setPickupXp(boolean pickupXp) {
        upgradeWrapper.setPickupXp(pickupXp);
        sendBooleanToServer(RSMagnetUpgrade.Data.KEY_PICKUP_XP, pickupXp);
    }

    public boolean shouldPickupXp() {
        return upgradeWrapper.shouldPickupXp();
    }

    public void setEnableVoid(boolean enableVoid) {
        upgradeWrapper.setEnableVoid(enableVoid);
        sendBooleanToServer(RSMagnetUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
    }

    public boolean shouldEnableVoid() {
        return upgradeWrapper.shouldEnableVoid();
    }
}