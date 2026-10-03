package top.morenrx.sua.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.SophUpgradeAddons;

public record S2CEnderChestSyncMessage(CompoundTag items) implements CustomPacketPayload {
    public static final String EnderChestNbtKey = "ender_chest";
    public static final Type<S2CEnderChestSyncMessage> TYPE = new Type<>(SophUpgradeAddons.id("ender_chest_sync"));

    public static final StreamCodec<ByteBuf, S2CEnderChestSyncMessage> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(S2CEnderChestSyncMessage::new, S2CEnderChestSyncMessage::items);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sync(ServerPlayer player) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = player.getEnderChestInventory().createTag(player.registryAccess());
        nbt.put(EnderChestNbtKey, listTag);

        PacketDistributor.sendToPlayer(player, new S2CEnderChestSyncMessage(nbt));
    }

    public static void handle(S2CEnderChestSyncMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleEnderChestSync(msg));
    }
}
