package top.morenrx.sua.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.morenrx.sua.network.S2CEnderChestSyncMessage;

public class SUANetwork {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(SUANetwork::registerPayloads);
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                S2CEnderChestSyncMessage.TYPE,
                S2CEnderChestSyncMessage.STREAM_CODEC,
                S2CEnderChestSyncMessage::handle
        );
    }
}
