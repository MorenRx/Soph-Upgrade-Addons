package top.morenrx.sbua.upgrades.ender_chest;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sbua.SBUAConfig;
import top.morenrx.sbua.SophUpgradeAddons;
import top.morenrx.sbua.network.S2CEnderChestSyncMessage;
import top.morenrx.sbua.upgrades.base.ISBUAItemConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = SophUpgradeAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnderChestUpgrade extends UpgradeItemBase<EnderChestUpgrade.Wrapper> implements ISBUAItemConfig {
    public static final UpgradeType<EnderChestUpgrade.Wrapper> TYPE = new UpgradeType<>(EnderChestUpgrade.Wrapper::new);
    public static final List<UpgradeConflictDefinition> UPGRADE_CONFLICT_DEFINITIONS = List.of(new UpgradeConflictDefinition(EnderChestUpgrade.class::isInstance, 0, SBPTranslationHelper.INSTANCE.translError("add.ender_chest_exists")));

    public EnderChestUpgrade() {
        super(Config.SERVER.maxUpgradesPerStorage);
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (!isEnable()) {
            tooltip.add(Component.translatable("item.soph_upgrade_addons.tooltip.disable").withStyle(ChatFormatting.RED));
            return;
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isEnable() {
        return SBUAConfig.INSTANCE.endChestUpgrade.enable.get();
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
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(EnderChestUpgrade::onEnderChestTick);
        eventBus.addListener(EnderChestUpgrade::onPlayerJoin);
        eventBus.addListener(EnderChestUpgrade::onContainerClose);
        eventBus.addListener(EnderChestUpgrade::onPlayerClone);
        initEnderChestCompat();

    }

    private static void initEnderChestCompat() {
//        ArtifactVersion currentVersion = new DefaultArtifactVersion(FMLLoader.getLoadingModList().getModFileById(SophisticatedBackpacks.MOD_ID).versionString());
//        ArtifactVersion targetVersion = new DefaultArtifactVersion("3.24.14");
//        if (currentVersion.compareTo(targetVersion) >= 0) {
//            PlayerInventoryProvider.get().addPlayerInventoryHandler("ender_chest", (player) -> PlayerInventoryHandler.SINGLE_IDENTIFIER, (player, identifier) -> player.getEnderChestInventory().getContainerSize(),
//                    EnderChestUpgrade::enderChestSlotStackGetter, false, false, false, false);
//            return;
//        }

        try {
            Method method = PlayerInventoryProvider.class.getMethod("addPlayerInventoryHandler", String.class, Function.class,
                    PlayerInventoryHandler.SlotCountGetter.class,
                    PlayerInventoryHandler.SlotStackGetter.class,
                    boolean.class, boolean.class, boolean.class, boolean.class
            );

            method.invoke(PlayerInventoryProvider.get(), "ender_chest",
                    (Function<Object, Set<String>>) ignored -> PlayerInventoryHandler.SINGLE_IDENTIFIER,
                    (PlayerInventoryHandler.SlotCountGetter) (player, identifier) -> player.getEnderChestInventory().getContainerSize(),
                    (PlayerInventoryHandler.SlotStackGetter) EnderChestUpgrade::enderChestSlotStackGetter,
                    false, false, false, false
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static ItemStack enderChestSlotStackGetter(Player player, String identifier, int slot) {
        ItemStack stack = player.getEnderChestInventory().getItem(slot);
        if (!(stack.getItem() instanceof BackpackItem)) return ItemStack.EMPTY;
        if (player.level().isClientSide()) return player.getEnderChestInventory().getItem(slot);

        LazyOptional<IBackpackWrapper> backpackWrapper = stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance());
        return backpackWrapper.map(wrapper -> {
            UpgradeHandler upgradeHandler = wrapper.getUpgradeHandler();
            if (upgradeHandler.hasUpgrade(EnderChestUpgrade.TYPE)) {
                return stack;
            } else {
                return ItemStack.EMPTY;
            }
        }).orElse(ItemStack.EMPTY);
    }


    public static void onEnderChestTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        if (player.isSpectator() || player.isDeadOrDying()) return;
        PlayerEnderChestContainer enderChestInventory = player.getEnderChestInventory();
        for (int i = 0; i < enderChestInventory.getContainerSize(); i++) {
            ItemStack stack = enderChestInventory.getItem(i);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof BackpackItem)) continue;
            stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent(wrapper -> {
                UpgradeHandler upgradeHandler = wrapper.getUpgradeHandler();
                if (upgradeHandler.hasUpgrade(EnderChestUpgrade.TYPE)) {
                    upgradeHandler.getWrappersThatImplement(ITickableUpgrade.class).forEach((upgrade) ->
                            upgrade.tick(player, player.level(), player.blockPosition()));
                }
            });
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

    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer newPlayer) {
            S2CEnderChestSyncMessage.sync(newPlayer);
        }
    }
}
