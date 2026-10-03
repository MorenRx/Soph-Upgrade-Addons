package top.morenrx.sua.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.FakePlayer;
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

import java.util.List;
import java.util.UUID;

public class SUAUtils {

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

            for (ServerPlayer serverplayer : level.getServer().getPlayerList().getPlayers()) {
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
}
