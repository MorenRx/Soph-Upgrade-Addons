package top.morenrx.sua.upgrades.network_pickup;

import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.nbt.CompoundTag;
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
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.data.NetworkLocation;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;
import top.morenrx.sua.util.SUAUtils;

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

        if (networkLocationCache == null) {
            CompoundTag tag = upgrade.getOrCreateTag();
            if ((networkLocationCache = NetworkLocation.create(level, tag.getLong(SUAUtils.RS.Data.KEY_NBT_POS), tag.getString(SUAUtils.RS.Data.KEY_NBT_DIM))) == null) {
                return stack;
            }
        }

        INetwork network = SUAUtils.RS.getNetwork(networkLocationCache);
        if (network == null || !network.canRun()) return stack;

        SalvagingUpgradeWrapper wrapper;
        if (!simulate && (wrapper = SUAUtils.Backpack.shouldSalvaging(storageWrapper, stack)) != null) {
            int consumeCount = wrapper.trySalvagingAndInsertItem(stack, (tempStack, tempSimulate) ->
                    SUAUtils.RS.insertItem(network, tempStack, playerCache, tempSimulate));
            if (consumeCount <= 0) return stack;
            if (consumeCount == stack.getCount()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack copy = stack.copy();
                copy.setCount(copy.getCount() - consumeCount);
                return copy;
            }
        }

        if (SUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) return ItemStack.EMPTY;

        return SUAUtils.RS.insertItem(network, stack, playerCache, simulate);
    }
}
