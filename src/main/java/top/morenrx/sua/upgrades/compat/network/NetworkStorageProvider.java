package top.morenrx.sua.upgrades.compat.network;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.capabilities.Capabilities;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import com.tom.storagemod.tile.StorageTerminalBlockEntity;
import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.storage.key.KeyAmount;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import com.wintercogs.beyonddimensions.common.block.entity.NetedBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import top.morenrx.sua.access.tomstorage.IStorageTerminalBlockEntityAccess;
import top.morenrx.sua.init.SUACompat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NetworkStorageProvider {

    public static class Type {
        public static final String RS = "rs";
        public static final String AE = "ae";
        public static final String TOM = "tom";
        public static final String BD = "bd";
    }

    private final Map<String, NetworkStorageHandler> networkStorageHandlers = new LinkedHashMap<>();
    private static final NetworkStorageProvider serverProvider = new NetworkStorageProvider();
    private static final NetworkStorageProvider clientProvider = new NetworkStorageProvider();

    public static NetworkStorageProvider get() {
        return FMLEnvironment.dist == Dist.CLIENT ? clientProvider : serverProvider;
    }

    private NetworkStorageProvider() {
        if (SUACompat.REFINED_STORAGE.getAsBoolean()) this.networkStorageHandlers.put(Type.RS, RSHandler.get());
        if (SUACompat.APPLIED_ENERGISTICS.getAsBoolean()) this.networkStorageHandlers.put(Type.AE, AEHandler.get());
        if (SUACompat.TOMS_STORAGE.getAsBoolean()) this.networkStorageHandlers.put(Type.TOM, TOMHandler.get());
        if (SUACompat.BEYOND_DIMENSIONS.getAsBoolean()) this.networkStorageHandlers.put(Type.BD, BDHandler.get());
    }


    public String nextNetworkType(String current) {
        List<String> types = networkStorageHandlers.keySet().stream().toList();
        if (types.isEmpty()) return Type.RS;

        int index = types.indexOf(current);
        if (index == -1) return types.get(0);
        return types.get((index + 1) % types.size());
    }

    public Map<String, NetworkStorageHandler> getNetworkStorageHandlers() {
        return this.networkStorageHandlers;
    }

    private static class RSHandler {
        private static NetworkStorageHandler get() {
            Function<BlockEntity, Boolean> blockValidGetter = (blockEntity) ->
                    blockEntity instanceof INetworkNodeProxy<?>;
            Function<BlockEntity, NetworkStorageHandler.InsertHandler> insertHandlerGetter = (blockEntity) -> {
                if (!(blockEntity instanceof INetworkNodeProxy<?> networkNodeProxy)) return null;
                INetwork network = networkNodeProxy.getNode().getNetwork();
                if (network == null || !network.canRun()) return null;
                return (stack, player, simulate) -> {
                    ItemStack remaining = network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
                    if (!simulate) network.getItemStorageTracker().changed(player, stack.copy());
                    return remaining;
                };
            };
            return new NetworkStorageHandler(NetworkStorageProvider.Type.RS, blockValidGetter, insertHandlerGetter);
        }
    }

    private static class AEHandler {
        private static NetworkStorageHandler get() {
            Function<BlockEntity, Boolean> blockValidGetter = (blockEntity) -> {
                if (blockEntity == null) return false;
                if (blockEntity instanceof IInWorldGridNodeHost) return true;
                return blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).isPresent();
            };
            Function<BlockEntity, NetworkStorageHandler.InsertHandler> insertHandlerGetter = (blockEntity) -> {
                if (blockEntity == null) return null;
                IInWorldGridNodeHost host = (blockEntity instanceof IInWorldGridNodeHost h) ? h : blockEntity.getCapability(Capabilities.IN_WORLD_GRID_NODE_HOST).orElse(null);
                if (host == null) return null;
                IGridNode gridNode = host.getGridNode(Direction.UP);
                if (gridNode == null) return null;
                IGrid grid = gridNode.getGrid();
                MEStorage inventory = grid.getStorageService().getInventory();
                return (stack, player, simulate) -> {
                    long amount = inventory.insert(AEItemKey.of(stack), stack.getCount(), simulate ? Actionable.SIMULATE : Actionable.MODULATE, IActionSource.ofPlayer(player));
                    if (amount == stack.getCount()) return ItemStack.EMPTY;
                    if (amount == 0) return stack;
                    ItemStack copy = stack.copy();
                    copy.setCount(copy.getCount() - (int) amount);
                    return copy;
                };
            };
            return new NetworkStorageHandler(NetworkStorageProvider.Type.AE, blockValidGetter, insertHandlerGetter);
        }
    }

    private static class TOMHandler {

        private static NetworkStorageHandler get() {
            Function<BlockEntity, Boolean> blockValidGetter = (blockEntity) ->
                    blockEntity instanceof StorageTerminalBlockEntity;
            Function<BlockEntity, NetworkStorageHandler.InsertHandler> insertHandlerGetter = (blockEntity) -> {
                if (!(blockEntity instanceof StorageTerminalBlockEntity storageTerminal)) return null;

                return (stack, player, simulate) -> {
                    if (!storageTerminal.canInteractWith(player)) return stack;
                    return ((IStorageTerminalBlockEntityAccess) storageTerminal).sua$pushStack(stack, simulate);
                };
            };
            return new NetworkStorageHandler(NetworkStorageProvider.Type.TOM, blockValidGetter, insertHandlerGetter);
        }
    }

    private static class BDHandler {
        private static NetworkStorageHandler get() {
            Function<BlockEntity, Boolean> blockValidGetter = (blockEntity) ->
                    blockEntity instanceof NetedBlockEntity;
            Function<BlockEntity, NetworkStorageHandler.InsertHandler> insertHandlerGetter = (blockEntity) -> {
                if (!(blockEntity instanceof NetedBlockEntity networkBlock)) return null;
                DimensionsNet net = networkBlock.getNet();
                if (net == null) return null;
                return (stack, player, simulate) -> {
                    net.getUnifiedStorage().setSlotCapacity(64);
                    KeyAmount remaining = net.getUnifiedStorage().insert(new ItemStackKey(stack), stack.getCount(), simulate);
                    if (remaining.isEmpty()) return ItemStack.EMPTY;
                    ItemStack copy = stack.copy();
                    copy.setCount((int) remaining.amount());
                    return copy;
                };
            };
            return new NetworkStorageHandler(NetworkStorageProvider.Type.BD, blockValidGetter, insertHandlerGetter);
        }
    }
}
