package top.morenrx.sua.upgrades.compat.network;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.init.SUACompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NetworkStorageProvider {
    public static class Data {
        public static final String BACKPACK = "backpack";
        public static final String RS = "rs";
        public static final String AE = "ae";
        public static final String TOM = "tom";
        public static final String BD = "bd";
    }

    private static final NetworkStorageProvider INSTANCE = new NetworkStorageProvider();
    private final Map<String, INetworkStorage> storages = new LinkedHashMap<>();

    public static NetworkStorageProvider get() {
        return INSTANCE;
    }

    private NetworkStorageProvider() {
        this.storages.put(Data.BACKPACK, new BackpackNetworkStorage());

        if (SUACompat.REFINED_STORAGE.getAsBoolean()) {
            this.storages.put(Data.RS, new RSNetworkStorage());
        }
        if (SUACompat.APPLIED_ENERGISTICS.getAsBoolean()) {
            this.storages.put(Data.AE, new AENetworkStorage());
        }
        if (SUACompat.TOMS_STORAGE.getAsBoolean()) {
            this.storages.put(Data.TOM, new TomNetworkStorage());
        }
        if (SUACompat.BEYOND_DIMENSIONS.getAsBoolean()) {
            this.storages.put(Data.BD, new BDNetworkStorage());
        }
    }

    public boolean hasExternalStorages() {
        return this.storages.size() > 1;
    }

    public Map<String, INetworkStorage> getStorages() {
        return this.storages;
    }

    public @Nullable INetworkStorage getStorage(String name) {
        return this.storages.get(name);
    }

    public boolean hasStorage(String name) {
        return this.storages.containsKey(name);
    }

    public String getDefaultStorageType() {
        for (String key : this.storages.keySet()) {
            if (!key.equals(Data.BACKPACK)) return key;
        }
        return Data.BACKPACK;
    }

    public String nextNetworkType(String current) {
        List<String> types = new ArrayList<>(this.storages.keySet());
        if (types.isEmpty()) return Data.BACKPACK;

        int index = types.indexOf(current);
        if (index == -1) return types.get(0);
        return types.get((index + 1) % types.size());
    }

    public InteractionResultHolder<ItemStack> onBindAir(Level level, Player player, InteractionHand hand, ItemStack upgradeStack) {
        for (INetworkStorage storage : this.storages.values()) {
            InteractionResultHolder<ItemStack> result = storage.onBindAir(level, player, hand, upgradeStack);
            if (result.getResult().consumesAction()) {
                return result;
            }
        }
        return InteractionResultHolder.pass(upgradeStack);
    }

    public InteractionResult onBindBlock(UseOnContext context, BlockEntity blockEntity, ItemStack upgradeStack) {
        for (INetworkStorage storage : this.storages.values()) {
            if (storage.canBindBlock(blockEntity)) {
                return storage.onBindBlock(context, blockEntity, upgradeStack);
            }
        }
        return InteractionResult.PASS;
    }

    public void clearBindings(ItemStack upgradeStack) {
        for (INetworkStorage storage : this.storages.values()) {
            storage.clearBinding(upgradeStack);
        }
    }

    public void appendTooltips(ItemStack upgradeStack, List<Component> tooltip) {
        for (INetworkStorage storage : this.storages.values()) {
            storage.appendTooltip(upgradeStack, tooltip);
        }
    }
}
