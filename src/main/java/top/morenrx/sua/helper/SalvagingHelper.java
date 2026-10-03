package top.morenrx.sua.helper;

import dev.shadowsoffire.apotheosis.affix.salvaging.SalvagingMenu;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgrade;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SalvagingHelper {
    private static WeakReference<Level> levelRef = new WeakReference<>(null);

    public static void init() {
        NeoForge.EVENT_BUS.addListener(SalvagingHelper::onLevelLoad);
    }

    private static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.dimension().equals(Level.OVERWORLD)) {
            levelRef = new WeakReference<>(serverLevel);
        }
    }

    public static void setLevel(Level level) {
        levelRef = new WeakReference<>(level);
    }

    public static @Nullable Level getLevel() {
        return levelRef.get();
    }

    @Nullable
    public static SalvagingUpgradeWrapper shouldSalvaging(IStorageWrapper storageWrapper, ItemStack stack) {
        List<SalvagingUpgradeWrapper> wrappers = storageWrapper.getUpgradeHandler().getTypeWrappers(SalvagingUpgrade.TYPE);
        for (SalvagingUpgradeWrapper upgradeWrapper : wrappers) {
            if (upgradeWrapper.getFilterLogic().matchesFilter(stack) && upgradeWrapper.canSalvaging(stack)) {
                return upgradeWrapper;
            }
        }
        return null;
    }

    public static List<ItemStack> getSalvagingResult(ItemStack stack) {
        Level l = levelRef.get();
        List<ItemStack> stacks = new ArrayList<>();
        if (l == null) return stacks;
        stacks.addAll(SalvagingMenu.getSalvageResults(l, stack));
        return stacks;
    }

    public static boolean findMatchSalvaging(ItemStack stack) {
        Level l = levelRef.get();
        if (l == null) return false;
        return !SalvagingMenu.findMatch(l, stack).isEmpty();
    }
}
