package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IContentsFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IPickupResponseUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.helper.SalvagingHelper;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.util.SUAUtils;

import java.util.function.Consumer;

public class NetworkPickupUpgradeWrapper extends UpgradeWrapperBase<NetworkPickupUpgradeWrapper, NetworkPickupUpgrade>
        implements IPickupResponseUpgrade, IContentsFilteredUpgrade {
    private final ContentsFilterLogic filterLogic;
    private Player playerCache = null;
    private Boolean enableVoidCache = null;
    private NetworkLocation locationCache = null;

    public NetworkPickupUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, stack -> save(), upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler, storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class));
    }

    @Override
    public @NotNull ContentsFilterLogic getFilterLogic() {
        return filterLogic;
    }

    public NetworkLocation getNetworkLocation() {
        if (locationCache == null)
            locationCache = NetworkLocation.fromUpgrade(upgrade);
        return locationCache;
    }

    @Override
    public @NotNull ItemStack pickup(@NotNull Level world, @NotNull ItemStack stack, boolean simulate) {
        if (!filterLogic.matchesFilter(stack))
            return stack;
        if (!(world instanceof ServerLevel level))
            return stack;

        if (playerCache == null)
            playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));

        NetworkLocation location = getNetworkLocation();
        INetworkStorage storage = location.storage();
        if (storage == null || !location.hasBinding())
            return stack;

        SalvagingUpgradeWrapper wrapper;
        if (!simulate && (wrapper = SalvagingHelper.shouldSalvaging(storageWrapper, stack)) != null) {
            int consumeCount = wrapper.trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    storage.insert(storageWrapper, upgrade, level, playerCache, tempStack, tempSimulate, location));
            if (consumeCount <= 0)
                return stack;
            if (consumeCount == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack copy = stack.copy();
            copy.setCount(copy.getCount() - consumeCount);
            return copy;
        }

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack))
            return ItemStack.EMPTY;

        return storage.insert(storageWrapper, upgrade, level, playerCache, stack, simulate, location);
    }

    public void setEnableVoid(boolean enableVoid) {
        enableVoidCache = enableVoid;
        NBTHelper.setBoolean(upgrade, NetworkPickupUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
        save();
    }

    public boolean shouldEnableVoid() {
        if (enableVoidCache == null)
            enableVoidCache = NBTHelper.getBoolean(upgrade, NetworkPickupUpgrade.Data.KEY_ENABLE_VOID).orElse(true);
        return enableVoidCache;
    }

    public void setNetworkType(String networkType) {
        NBTHelper.putString(upgrade.getOrCreateTag(), NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE, networkType);
        locationCache = null;
        save();
    }

    public String shouldNetworkType() {
        return getNetworkLocation().storageType();
    }
}
