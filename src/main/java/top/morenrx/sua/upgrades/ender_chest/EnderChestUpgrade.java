package top.morenrx.sua.upgrades.ender_chest;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sua.network.S2CEnderChestSyncMessage;
import top.morenrx.sua.upgrades.base.ISUAItemConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnderChestUpgrade extends UpgradeItemBase<EnderChestUpgrade.Wrapper> implements ISUAItemConfig {
    public static final UpgradeType<EnderChestUpgrade.Wrapper> TYPE = new UpgradeType<>(EnderChestUpgrade.Wrapper::new);
    public static final List<UpgradeConflictDefinition> UPGRADE_CONFLICT_DEFINITIONS = List.of(new UpgradeConflictDefinition(EnderChestUpgrade.class::isInstance, 0, SBPTranslationHelper.INSTANCE.translError("add.ender_chest_exists")));
    private final BooleanSupplier enable;

    public EnderChestUpgrade(BooleanSupplier enable) {
        super(Config.SERVER.maxUpgradesPerStorage);
        this.enable = enable;
    }

    @Override
    public @NotNull UpgradeType<Wrapper> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<UpgradeConflictDefinition> getUpgradeConflicts() {
        return UPGRADE_CONFLICT_DEFINITIONS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public boolean isEnable() {
        return enable.getAsBoolean();
    }

    public static class Wrapper extends UpgradeWrapperBase<Wrapper, EnderChestUpgrade> {
        public Wrapper(IStorageWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
            super(backpackWrapper, upgrade, upgradeSaveHandler);
        }
        @Override
        public boolean hideSettingsTab() {
            return true;
        }
        @Override
        public boolean canBeDisabled() {
            return false;
        }
    }

    public static void init() {
        IEventBus eventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(EnderChestUpgrade::onEnderChestTick);
        eventBus.addListener(EnderChestUpgrade::onPlayerJoin);
        eventBus.addListener(EnderChestUpgrade::onPlayerChangedDimension);
        eventBus.addListener(EnderChestUpgrade::onPlayerRespawn);
        eventBus.addListener(EnderChestUpgrade::onPlayerClone);
        eventBus.addListener(EnderChestUpgrade::onContainerClose);
        initEnderChestCompat();
    }

    private static void initEnderChestCompat() {
        try {
            Method method = PlayerInventoryProvider.class.getMethod("addPlayerInventoryHandler",
                    String.class, Function.class,
                    PlayerInventoryHandler.SlotCountGetter.class,
                    PlayerInventoryHandler.SlotStackGetter.class,
                    boolean.class, boolean.class, boolean.class, boolean.class
            );

            Function<Object, Set<String>> identifiersGetter = ignored -> PlayerInventoryHandler.SINGLE_IDENTIFIER;

            method.invoke(PlayerInventoryProvider.get(), "ender_chest",
                    identifiersGetter,
                    (PlayerInventoryHandler.SlotCountGetter) (player, identifier) -> player.getEnderChestInventory().getContainerSize(),
                    (PlayerInventoryHandler.SlotStackGetter) EnderChestUpgrade::enderChestSlotStackGetter,
                    false, false, false, false
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static ItemStack enderChestSlotStackGetter(Player player, String identifier, int slot) {
        ItemStack stack = player.getEnderChestInventory().getItem(slot);
        if (!(stack.getItem() instanceof BackpackItem)) return ItemStack.EMPTY;
        if (player.level().isClientSide()) return stack;

        IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
        if (wrapper != IBackpackWrapper.Noop.INSTANCE) {
            UpgradeHandler upgradeHandler = wrapper.getUpgradeHandler();
            if (upgradeHandler.hasUpgrade(EnderChestUpgrade.TYPE)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onEnderChestTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isSpectator() || player.isDeadOrDying()) return;
        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        for (int i = 0; i < enderChestInventory.getContainerSize(); i++) {
            ItemStack stack = enderChestInventory.getItem(i);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof BackpackItem)) continue;
            IBackpackWrapper wrapper = BackpackWrapper.fromStack(stack);
            if (wrapper != IBackpackWrapper.Noop.INSTANCE) {
                UpgradeHandler upgradeHandler = wrapper.getUpgradeHandler();
                if (upgradeHandler.hasUpgrade(EnderChestUpgrade.TYPE)) {
                    upgradeHandler.getWrappersThatImplement(ITickableUpgrade.class).forEach(upgrade ->
                            upgrade.tick(player, player.level(), player.blockPosition()));
                }
            }
        }
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CEnderChestSyncMessage.sync(player);
        }
    }

    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getContainer() instanceof ChestMenu menu)) return;
        if (!(menu.getContainer() instanceof PlayerEnderChestContainer)) return;
        S2CEnderChestSyncMessage.sync(player);
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CEnderChestSyncMessage.sync(player);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            S2CEnderChestSyncMessage.sync(player);
        }
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer) {
            S2CEnderChestSyncMessage.sync(newPlayer);
        }
    }
}
