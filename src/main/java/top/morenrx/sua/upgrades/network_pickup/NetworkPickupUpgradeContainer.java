package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogicContainer;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;

public class NetworkPickupUpgradeContainer extends UpgradeContainerBase<NetworkPickupUpgradeWrapper, NetworkPickupUpgradeContainer> {
    private final ContentsFilterLogicContainer filterLogicContainer;

    public NetworkPickupUpgradeContainer(Player player, int containerId, NetworkPickupUpgradeWrapper wrapper, UpgradeContainerType<NetworkPickupUpgradeWrapper, NetworkPickupUpgradeContainer> type) {
        super(player, containerId, wrapper, type);
        filterLogicContainer = new ContentsFilterLogicContainer(() -> upgradeWrapper.getFilterLogic(), this, slots::add);
    }

    @Override
    public void handleMessage(CompoundTag data) {
        if (data.contains(NetworkPickupUpgrade.Data.KEY_ENABLE_VOID)) {
            setEnableVoid(data.getBoolean(NetworkPickupUpgrade.Data.KEY_ENABLE_VOID));
        } else if (data.contains(NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE)) {
            setNetworkType(data.getString(NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE));
        }
        filterLogicContainer.handleMessage(data);
    }

    public ContentsFilterLogicContainer getFilterLogicContainer() {
        return filterLogicContainer;
    }

    public void setEnableVoid(boolean enableVoid) {
        upgradeWrapper.setEnableVoid(enableVoid);
        sendBooleanToServer(NetworkPickupUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
    }

    public boolean shouldEnableVoid() {
        return upgradeWrapper.shouldEnableVoid();
    }

    public void setNetworkType(String networkType) {
        upgradeWrapper.setNetworkType(networkType);
        sendDataToServer(() -> NBTHelper.putString(new CompoundTag(), NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE, networkType));
    }

    public String shouldNetworkType() {
        return upgradeWrapper.shouldNetworkType();
    }
}
