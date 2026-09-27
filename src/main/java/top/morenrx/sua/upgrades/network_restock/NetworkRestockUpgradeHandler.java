package top.morenrx.sua.upgrades.network_restock;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.restock.RestockUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.compat.network.INetworkStorage;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;

import java.util.List;
import java.util.function.BiFunction;

public class NetworkRestockUpgradeHandler {

    public static boolean tryRestock(Direction face, Player player, ItemStack backpack, BlockEntity te) {
        INetworkStorage storage = findRestockStorage(te);
        if (storage == null)
            return false;

        if (player.level().isClientSide())
            return true;

        return backpack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
                .map(wrapper -> {
                    List<RestockUpgradeWrapper> upgradeWrappers = wrapper.getUpgradeHandler().getTypeWrappers(NetworkRestockUpgrade.TYPE);
                    if (upgradeWrappers.isEmpty()) return false;

                    INetworkStorage.NetworkExtractHandler extractHandler = storage.getRestockExtractHandler(te);
                    if (extractHandler == null) return false;

                    InventoryHandler inventoryHandler = wrapper.getInventoryHandler();
                    inventoryHandler.getSlotTracker();

                    RestockContext ctx = new RestockContext(player, wrapper, inventoryHandler, storage, te, extractHandler);

                    return processBackpackRestock(ctx, upgradeWrappers);
                })
                .orElse(false);
    }

    private static @Nullable INetworkStorage findRestockStorage(BlockEntity te) {
        for (INetworkStorage storage : NetworkStorageProvider.get().getStorages().values()) {
            if (storage.isValidRestockBlock(te)) return storage;
        }
        return null;
    }

    private static boolean processBackpackRestock(RestockContext ctx, List<RestockUpgradeWrapper> upgradeWrappers) {
        for (RestockUpgradeWrapper upgradeWrapper : upgradeWrappers) {
            int restockedCount = processRestockUpgrade(ctx, upgradeWrapper);
            notifyRestockResult(ctx.player(), restockedCount);
        }
        return true;
    }

    private static int processRestockUpgrade(RestockContext ctx, RestockUpgradeWrapper upgradeWrapper) {
        int[] restockedCount = new int[1];

        ctx.storage().forEachStoredItem(ctx.targetBlockEntity(), sampleStack -> {
            if (!hasAcceptingSlot(ctx.inventoryHandler(), sampleStack))
                return hasAnySpace(ctx.inventoryHandler());

            if (upgradeWrapper.getFilterLogic().matchesFilter(sampleStack))
                if (restockItem(ctx, sampleStack) > 0)
                    restockedCount[0]++;

            return hasAnySpace(ctx.inventoryHandler());
        });

        return restockedCount[0];
    }

    private static int restockItem(RestockContext ctx, ItemStack targetStack) {
        int totalExtracted = 0;

        while (hasAcceptingSlot(ctx.inventoryHandler(), targetStack)) {
            int maxBatch = Math.max(1, targetStack.getMaxStackSize());
            int accepted = transferBatch(ctx, targetStack, maxBatch,
                    (stack, simulate) -> ctx.inventoryHandler().insertItem(stack, simulate));

            if (accepted <= 0) break;
            totalExtracted += accepted;
            if (accepted < maxBatch) break;
        }

        return totalExtracted;
    }

    private static int transferBatch(RestockContext ctx, ItemStack targetStack, int requestedAmount, BiFunction<ItemStack, Boolean, ItemStack> inserter) {
        // Phase 1: 模拟入库
        ItemStack testStack = targetStack.copyWithCount(requestedAmount);
        ItemStack simRemainder = inserter.apply(testStack, true);
        int canAccept = requestedAmount - simRemainder.getCount();
        if (canAccept <= 0) return 0;

        ItemStack extracted = ctx.extractHandler().extract(targetStack, canAccept, ctx.player(), false);
        if (extracted.isEmpty()) return 0;

        ItemStack realRemainder = inserter.apply(extracted, false);
        int accepted = extracted.getCount() - realRemainder.getCount();

        if (!realRemainder.isEmpty()) ctx.inventoryHandler().insertItem(realRemainder, false);

        return accepted;
    }

    private static boolean hasAcceptingSlot(InventoryHandler handler, ItemStack stack) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (inSlot.isEmpty())
                return true;
            if (ItemHandlerHelper.canItemStacksStack(inSlot, stack) && inSlot.getCount() < handler.getStackLimit(i, inSlot))
                return true;
        }
        return false;
    }

    private static boolean hasAnySpace(InventoryHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < handler.getStackLimit(i, stack))
                return true;
        }
        return false;
    }

    private static void notifyRestockResult(Player player, int restockedCount) {
        String translKey = restockedCount > 0
                ? "gui.sophisticatedbackpacks.status.stacks_restocked"
                : "gui.sophisticatedbackpacks.status.nothing_to_restock";
        player.displayClientMessage(Component.translatable(translKey, restockedCount), true);
    }

    public record RestockContext(Player player, IBackpackWrapper wrapper, InventoryHandler inventoryHandler,
                                 INetworkStorage storage, BlockEntity targetBlockEntity,
                                 INetworkStorage.NetworkExtractHandler extractHandler) {
    }
}
