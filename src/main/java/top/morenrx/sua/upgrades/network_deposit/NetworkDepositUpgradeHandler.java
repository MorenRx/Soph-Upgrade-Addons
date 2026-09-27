package top.morenrx.sua.upgrades.network_deposit;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.List;

public class NetworkDepositUpgradeHandler {

    public static boolean tryDeposit(Direction face, Player player, ItemStack backpack, BlockEntity te) {
        INetworkStorage storage = findDepositStorage(te);
        if (storage == null)
            return false;

        if (player.level().isClientSide())
            return true;

        return backpack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
                .map(wrapper -> {
                    List<DepositUpgradeWrapper> upgradeWrappers = wrapper.getUpgradeHandler().getTypeWrappers(NetworkDepositUpgrade.TYPE);
                    if (upgradeWrappers.isEmpty()) return false;

                    INetworkStorage.NetworkInsertHandler insertHandler = storage.getDepositInsertHandler(te);
                    if (insertHandler == null) return false;

                    return processBackpackDeposit(wrapper, upgradeWrappers, player, insertHandler);
                })
                .orElse(false);
    }

    private static @Nullable INetworkStorage findDepositStorage(BlockEntity te) {
        for (INetworkStorage storage : NetworkStorageProvider.get().getStorages().values()) {
            if (storage.isValidDepositBlock(te))
                return storage;
        }
        return null;
    }

    private static boolean processBackpackDeposit(IBackpackWrapper wrapper, List<DepositUpgradeWrapper> upgradeWrappers, Player player, INetworkStorage.NetworkInsertHandler insertHandler) {
        InventoryHandler inventoryHandler = wrapper.getInventoryHandler();
        for (DepositUpgradeWrapper upgradeWrapper : upgradeWrappers) {
            int depositedCount = processDepositUpgrade(upgradeWrapper, inventoryHandler, player, insertHandler);
            notifyDepositResult(player, depositedCount);
        }
        return true;
    }

    private static int processDepositUpgrade(DepositUpgradeWrapper upgradeWrapper, InventoryHandler inventoryHandler, Player player, INetworkStorage.NetworkInsertHandler insertHandler) {
        int depositedCount = 0;
        for (int i = 0; i < inventoryHandler.getSlots(); i++) {
            if (depositSlot(inventoryHandler, i, upgradeWrapper, player, insertHandler))
                depositedCount++;
        }
        return depositedCount;
    }

    private static boolean depositSlot(InventoryHandler inventoryHandler, int slot, DepositUpgradeWrapper upgradeWrapper, Player player, INetworkStorage.NetworkInsertHandler insertHandler) {
        ItemStack slotStack = inventoryHandler.getSlotStack(slot);
        if (slotStack.isEmpty() || !upgradeWrapper.getFilterLogic().matchesFilter(slotStack))
            return false;

        ItemStack remainingSimulated = insertHandler.insert(slotStack, player, true);
        int canAccept = slotStack.getCount() - remainingSimulated.getCount();
        if (canAccept <= 0)
            return false;

        ItemStack remaining = insertHandler.insert(slotStack, player, false);
        int accepted = slotStack.getCount() - remaining.getCount();
        if (accepted > 0) {
            inventoryHandler.extractItem(slot, accepted, false);
            return true;
        }
        return false;
    }

    private static void notifyDepositResult(Player player, int depositedCount) {
        String translKey = depositedCount > 0
                ? "gui.sophisticatedbackpacks.status.stacks_deposited"
                : "gui.sophisticatedbackpacks.status.nothing_to_deposit";
        player.displayClientMessage(Component.translatable(translKey, depositedCount), true);
    }
}
