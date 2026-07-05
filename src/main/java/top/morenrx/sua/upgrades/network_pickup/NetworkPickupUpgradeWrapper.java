package top.morenrx.sua.upgrades.network_pickup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IContentsFilteredUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.IPickupResponseUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageHandler;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.util.SUAUtils;

import java.util.Map;
import java.util.function.Consumer;

public class NetworkPickupUpgradeWrapper extends UpgradeWrapperBase<NetworkPickupUpgradeWrapper, NetworkPickupUpgrade>
        implements IPickupResponseUpgrade, IContentsFilteredUpgrade {
    private final ContentsFilterLogic filterLogic;
    private NetworkLocation networkLocationCache = null;
    private Player playerCache = null;

    public NetworkPickupUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, stack -> save(), upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler, storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class));
    }

    @Override
    public @NotNull ContentsFilterLogic getFilterLogic() {
        return filterLogic;
    }

    @Override
    public @NotNull ItemStack pickup(@NotNull Level world, @NotNull ItemStack stack, boolean simulate) {
        if (!filterLogic.matchesFilter(stack)) return stack;
        if (!(world instanceof ServerLevel level)) return stack;

        if (playerCache == null) playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));

        String typeName = shouldNetworkType();
        NetworkStorageHandler networkStorageHandler = NetworkStorageProvider.get().getNetworkStorageHandlers().get(typeName);

        if (networkLocationCache == null && (networkLocationCache = networkStorageHandler.getNetworkLocation(level, upgrade)) == null) {
            return stack;
        }

        BlockEntity blockEntity = NetworkLocation.getBlockEntity(networkLocationCache);
        NetworkStorageHandler.InsertHandler insertHandler = networkStorageHandler.insertHandlerGetter().apply(blockEntity);
        if (insertHandler == null) return stack;

        SalvagingUpgradeWrapper wrapper;
        if (!simulate && (wrapper = SUAUtils.Backpack.shouldSalvaging(storageWrapper, stack)) != null) {
            int consumeCount = wrapper.trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    insertHandler.insert(tempStack, playerCache, tempSimulate));
            if (consumeCount <= 0) return stack;
            if (consumeCount == stack.getCount()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack copy = stack.copy();
                copy.setCount(copy.getCount() - consumeCount);
                return copy;
            }
        }

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) return ItemStack.EMPTY;

        return insertHandler.insert(stack, playerCache, simulate);
    }

    public void setEnableVoid(boolean enableVoid) {
        NBTHelper.setBoolean(upgrade, NetworkPickupUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
        save();
    }

    public boolean shouldEnableVoid() {
        return NBTHelper.getBoolean(upgrade, NetworkPickupUpgrade.Data.KEY_ENABLE_VOID).orElse(true);
    }

    public void setNetworkType(String networkType) {
        NBTHelper.putString(upgrade.getOrCreateTag(), NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE, networkType);
        save();
    }

    public String shouldNetworkType() {
        String type = NBTHelper.getString(upgrade, NetworkPickupUpgrade.Data.KEY_NETWORK_TYPE).orElse(NetworkStorageProvider.Type.RS);
        Map<String, NetworkStorageHandler> networkStorageHandlers = NetworkStorageProvider.get().getNetworkStorageHandlers();
        NetworkStorageHandler networkStorageHandler = networkStorageHandlers.get(type);
        if (networkStorageHandler == null) {
            type = networkStorageHandlers.keySet().iterator().next();
            setNetworkType(type);
        }
        return type;
    }
}
