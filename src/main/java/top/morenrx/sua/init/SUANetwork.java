package top.morenrx.sua.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.network.S2CEnderChestSyncMessage;

public class SUANetwork {
    public static final SUANetwork NETWORK_HANDLER = new SUANetwork();
    private final String protocolVersion = Integer.toString(1);
    private final ResourceLocation channel = SophUpgradeAddons.id("main_channel");
    private final SimpleChannel handler = NetworkRegistry.ChannelBuilder
            .named(channel)
            .clientAcceptedVersions(protocolVersion::equals)
            .serverAcceptedVersions(protocolVersion::equals)
            .networkProtocolVersion(() -> protocolVersion)
            .simpleChannel();

    public static void init() {
        NETWORK_HANDLER.register();
    }

    public void register() {
        int id = 0;
        handler.registerMessage(id++, S2CEnderChestSyncMessage.class, S2CEnderChestSyncMessage::encode, S2CEnderChestSyncMessage::decode, S2CEnderChestSyncMessage::handle);
    }

    public <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
