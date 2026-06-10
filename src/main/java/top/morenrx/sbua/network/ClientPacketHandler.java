package top.morenrx.sbua.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandler {

    public static void handleEnderChestSync(S2CEnderChestSyncMessage msg) {
        Player player = Minecraft.getInstance().player;
        if (player != null && msg.items() != null) {
            if (msg.items().contains(S2CEnderChestSyncMessage.EnderChestNbtKey, Tag.TAG_LIST)) {
                ListTag listTag = msg.items().getList(S2CEnderChestSyncMessage.EnderChestNbtKey, Tag.TAG_COMPOUND);
                player.getEnderChestInventory().fromTag(listTag);
            }
        }
    }

}
