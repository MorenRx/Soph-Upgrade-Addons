package top.morenrx.sua.upgrades.network_magnet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

public class NetworkMagnetUpgradeContainer extends UpgradeContainerBase<NetworkMagnetUpgradeWrapper, NetworkMagnetUpgradeContainer> {
    private final ContentsFilterLogicContainer filterLogicContainer;

    public NetworkMagnetUpgradeContainer(Player player, int containerId, NetworkMagnetUpgradeWrapper wrapper, UpgradeContainerType<NetworkMagnetUpgradeWrapper, NetworkMagnetUpgradeContainer> type) {
        super(player, containerId, wrapper, type);

        filterLogicContainer = new ContentsFilterLogicContainer(() -> upgradeWrapper.getFilterLogic(), this, slots::add);
    }

    @Override
    public void handleMessage(CompoundTag data) {
        if (data.contains(NetworkMagnetUpgrade.Data.KEY_PICKUP_ITEMS)) {
            setPickupItems(data.getBoolean(NetworkMagnetUpgrade.Data.KEY_PICKUP_ITEMS));
        } else if (data.contains(NetworkMagnetUpgrade.Data.KEY_PICKUP_XP)) {
            setPickupXp(data.getBoolean(NetworkMagnetUpgrade.Data.KEY_PICKUP_XP));
        } else if (data.contains(NetworkMagnetUpgrade.Data.KEY_ENABLE_VOID)) {
            setEnableVoid(data.getBoolean(NetworkMagnetUpgrade.Data.KEY_ENABLE_VOID));
        } else if (data.contains(NetworkMagnetUpgrade.Data.KEY_NETWORK_TYPE)) {
            setNetworkType(data.getString(NetworkMagnetUpgrade.Data.KEY_NETWORK_TYPE));
        }
        filterLogicContainer.handleMessage(data);
    }

    public ContentsFilterLogicContainer getFilterLogicContainer() {
        return filterLogicContainer;
    }

    public void setPickupItems(boolean pickupItems) {
        upgradeWrapper.setPickupItems(pickupItems);
        sendBooleanToServer(NetworkMagnetUpgrade.Data.KEY_PICKUP_ITEMS, pickupItems);
    }

    public boolean shouldPickupItems() {
        return upgradeWrapper.shouldPickupItems();
    }

    public void setPickupXp(boolean pickupXp) {
        upgradeWrapper.setPickupXp(pickupXp);
        sendBooleanToServer(NetworkMagnetUpgrade.Data.KEY_PICKUP_XP, pickupXp);
    }

    public boolean shouldPickupXp() {
        return upgradeWrapper.shouldPickupXp();
    }

    public void setEnableVoid(boolean enableVoid) {
        upgradeWrapper.setEnableVoid(enableVoid);
        sendBooleanToServer(NetworkMagnetUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
    }

    public boolean shouldEnableVoid() {
        return upgradeWrapper.shouldEnableVoid();
    }

    public void setNetworkType(String networkType) {
        upgradeWrapper.setNetworkType(networkType);
        sendDataToServer(() -> NBTHelper.putString(new CompoundTag(), NetworkMagnetUpgrade.Data.KEY_NETWORK_TYPE, networkType));
    }

    public String shouldNetworkType() {
        return upgradeWrapper.shouldNetworkType();
    }
}