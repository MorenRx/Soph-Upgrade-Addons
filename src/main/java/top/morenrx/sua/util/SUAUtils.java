package top.morenrx.sua.util;

import com.mojang.authlib.GameProfile;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.api.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
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
import top.morenrx.sua.data.RSLocation;

import java.util.List;
import java.util.UUID;


public class SUAUtils {
    public static class Data {
        public static final String KEY_NBT_DIM = "dim";
        public static final String KEY_NBT_POS = "pos";
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
    }


    public static class RS {
        public static INetwork getRSNetwork(RSLocation rsLocation) {
            BlockEntity blockEntity = rsLocation.dim().getBlockEntity(rsLocation.pos());
            if (!(blockEntity instanceof INetworkNodeProxy<?> networkNode)) return null;
            return networkNode.getNode().getNetwork();
        }

        public static ItemStack insertItemToRS(INetwork network, ItemStack stack, Player player, boolean simulate) {
            ItemStack remaining = network.insertItem(stack, stack.getCount(), simulate ? Action.SIMULATE : Action.PERFORM);
            if (!simulate) network.getItemStorageTracker().changed(player, stack.copy());
            return remaining;
        }
    }

    public static class Gui {
        private final static ResourceLocation ICONS = ResourceLocation.fromNamespaceAndPath(SophUpgradeAddons.MODID, "textures/gui/icons.png");

        public static ToggleButton.StateData getButtonStateData(UV uv, String tooltip, Dimension dimension, Position offset) {
            return new ToggleButton.StateData(new TextureBlitData(ICONS, offset, Dimension.SQUARE_256, uv, dimension), Component.translatable(tooltip));
        }
    }
}
