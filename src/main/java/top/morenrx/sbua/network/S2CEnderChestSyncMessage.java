package top.morenrx.sbua.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import top.morenrx.sbua.init.SBUANetwork;

import java.util.function.Supplier;

public record S2CEnderChestSyncMessage(CompoundTag items) {
    public final static String EnderChestNbtKey = "ender_chest";

    public static void encode(S2CEnderChestSyncMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.items);
    }

    public static S2CEnderChestSyncMessage decode(FriendlyByteBuf buffer) {
        CompoundTag items = buffer.readNbt();
        return new S2CEnderChestSyncMessage(items);
    }

    public static void sync(ServerPlayer player) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = player.getEnderChestInventory().createTag();
        nbt.put(EnderChestNbtKey, listTag);

        SBUANetwork.NETWORK_HANDLER.sendToTrackingAndSelf(new S2CEnderChestSyncMessage(nbt), player);
    }

    public static void handle(S2CEnderChestSyncMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketHandler.handleEnderChestSync(msg));
        context.setPacketHandled(true);
    }
}
