package top.morenrx.sbua.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.morenrx.sbua.SophUpgradeAddons;
import top.morenrx.sbua.network.S2CEnderChestSyncMessage;

public class SBUANetwork {
    public static final SBUANetwork NETWORK_HANDLER = new SBUANetwork();
    private final String protocolVersion = Integer.toString(1);
    private final ResourceLocation channel = ResourceLocation.fromNamespaceAndPath(SophUpgradeAddons.MODID, "main_channel");
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

    public <MSG> void sendToTrackingAndSelf(MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), message);
    }

    public void sendToServer(Object message) {
        handler.send(PacketDistributor.SERVER.noArg(), message);
    }

}
