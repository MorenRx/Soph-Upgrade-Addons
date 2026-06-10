package top.morenrx.sbua.upgrades.rs_pickup;

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
import top.morenrx.sbua.util.SBUAUtils;

import java.util.function.Consumer;

public class RSPickupUpgradeWrapper extends UpgradeWrapperBase<RSPickupUpgradeWrapper, RSPickupUpgrade>
        implements IPickupResponseUpgrade, IContentsFilteredUpgrade {
    private final ContentsFilterLogic filterLogic;
    private INetwork networkCache = null;
    private Player playerCache = null;

    public RSPickupUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, stack -> save(), upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler, storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class));
    }

    @Override
    public @NotNull ItemStack pickup(@NotNull Level world, @NotNull ItemStack stack, boolean simulate) {
        if (!filterLogic.matchesFilter(stack)) return stack;
        if (!(world instanceof ServerLevel level)) return stack;

        if (SBUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) return ItemStack.EMPTY;

        if (this.playerCache == null) this.playerCache = SBUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));

        if (networkCache == null || !networkCache.canRun()) {
            CompoundTag tag = upgrade.getOrCreateTag();
            if ((networkCache = SBUAUtils.RS.getRSNetwork(level, tag.getLong("pos"), tag.getString("dim"))) == null) {
                return stack;
            }
        }

        return SBUAUtils.RS.insertItemToRS(networkCache, stack, playerCache, simulate);
    }

    @Override
    public @NotNull ContentsFilterLogic getFilterLogic() {
        return filterLogic;
    }
}
