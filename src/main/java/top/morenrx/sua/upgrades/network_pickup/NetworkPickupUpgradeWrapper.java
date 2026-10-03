package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IContentsFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IPickupResponseUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.helper.SalvagingHelper;
import top.morenrx.sua.init.SUADataComponents;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.util.SUAUtils;

import java.util.function.Consumer;

public class NetworkPickupUpgradeWrapper extends UpgradeWrapperBase<NetworkPickupUpgradeWrapper, NetworkPickupUpgrade>
        implements IPickupResponseUpgrade, IContentsFilteredUpgrade {
    private final ContentsFilterLogic filterLogic;
    private Player playerCache = null;
    private NetworkLocation locationCache = null;

    public NetworkPickupUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, stack -> save(), upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler,
                storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class), ModCoreDataComponents.FILTER_ATTRIBUTES);
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

        if (playerCache == null || playerCache.isRemoved()) {
            playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));
            if (playerCache instanceof FakePlayer) {
                playerCache = null;
            }
        }

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
        upgrade.set(SUADataComponents.ENABLE_VOID, enableVoid);
        save();
    }

    public boolean shouldEnableVoid() {
        return upgrade.getOrDefault(SUADataComponents.ENABLE_VOID, true);
    }

    public void setNetworkType(String networkType) {
        upgrade.set(SUADataComponents.NETWORK_TYPE, networkType);
        locationCache = null;
        save();
    }

    public String shouldNetworkType() {
        return getNetworkLocation().storageType();
    }
}
