package top.morenrx.sua.util;

import com.mojang.authlib.GameProfile;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.LevelEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.AccessLogRecord;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TextureBlitData;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.UV;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgrade;
import top.morenrx.sua.upgrades.salvaging.SalvagingUpgradeWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SUAUtils {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(SUAUtils::overworldLoaded);
    }

    public static void overworldLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension().equals(Level.OVERWORLD)) {
            Recipe.level = new WeakReference<>(level);
        }
    }

    public static class Backpack {
        private final static UUID FAKE_PLAYER_UUID = UUID.fromString("61664b79-57e6-4174-b4c1-7e1b8e4486da");
        private final static String FAKE_PLAYER_NAME = "精妙背包";
        private static FakePlayer fakePlayer = null;

        public static @NotNull ServerPlayer getFakePlayer(@NotNull ServerLevel level) {
            if (fakePlayer != null) return fakePlayer;
            return fakePlayer = new FakePlayer(level, new GameProfile(FAKE_PLAYER_UUID, FAKE_PLAYER_NAME));
        }

        public static @NotNull ServerPlayer getBackpackOwner(@NotNull ServerLevel level, @Nullable UUID backpackUUID) {
            if (backpackUUID == null) return getFakePlayer(level);

            AccessLogRecord accessLogRecord = BackpackStorage.get().getAccessLogs().get(backpackUUID);
            if (accessLogRecord == null) return getFakePlayer(level);

            for(ServerPlayer serverplayer : level.getServer().getPlayerList().getPlayers()) {
                if (serverplayer.getDisplayName().getString().equalsIgnoreCase(accessLogRecord.getPlayerName())) {
                    return serverplayer;
                }
            }
            return getFakePlayer(level);
        }

        public static boolean shouldDestroy(IStorageWrapper storageWrapper, ItemStack stack) {
            List<VoidUpgradeWrapper> wrappers = storageWrapper.getUpgradeHandler().getTypeWrappers(VoidUpgradeItem.TYPE);
            for (VoidUpgradeWrapper voidUpgradeWrapper : wrappers) {
                if (voidUpgradeWrapper.getFilterLogic().matchesFilter(stack)) {
                    return true;
                }
            }
            return false;
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
    }

    public static class Gui {
        private final static ResourceLocation ICONS = SophUpgradeAddons.id("textures/gui/icons.png");

        public static ToggleButton.StateData getButtonStateData(UV uv, String tooltip, Dimension dimension, Position offset) {
            return getButtonStateData(uv, Component.translatable(tooltip), dimension, offset);
        }
        public static ToggleButton.StateData getButtonStateData(UV uv, Component tooltip, Dimension dimension, Position offset) {
            return new ToggleButton.StateData(getTextureBlitData(uv, dimension, offset), tooltip);
        }

        public static TextureBlitData getTextureBlitData(UV uv, Dimension dimension, Position offset) {
            return new TextureBlitData(ICONS, offset, Dimension.SQUARE_256, uv, dimension);
        }
    }

    public static class Recipe {
        public static WeakReference<Level> level;

        public static List<ItemStack> getSalvagingResult(ItemStack stack) {
            Level l = level.get();
            List<ItemStack> stacks = new ArrayList<>();
            if (l == null) return stacks;
            stacks.addAll(SalvagingMenu.salvageItem(l, stack));
            return stacks;
        }

        public static boolean findMatchSalvaging(ItemStack stack) {
            Level l = level.get();
            if (l == null) return false;
            return SalvagingMenu.findMatch(l, stack) != null;
        }
    }
}
