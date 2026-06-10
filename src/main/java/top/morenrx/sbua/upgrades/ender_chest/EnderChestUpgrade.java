package top.morenrx.sbua.upgrades.end_chest;


import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import org.jetbrains.annotations.NotNull;
import top.morenrx.sbua.SBUAInit;

import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SBUAInit.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EndChestUpgrade extends UpgradeItemBase<EndChestUpgrade.Wrapper> {
    public static final UpgradeType<EndChestUpgrade.Wrapper> TYPE = new UpgradeType<>(EndChestUpgrade.Wrapper::new);
    public static final List<UpgradeConflictDefinition> UPGRADE_CONFLICT_DEFINITIONS = List.of(new UpgradeConflictDefinition(EndChestUpgrade.class::isInstance, 0, SBPTranslationHelper.INSTANCE.translError("add.ender_chest_exists")));


    public EndChestUpgrade() {
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

    public static class Wrapper extends UpgradeWrapperBase<Wrapper, EndChestUpgrade> {
        public Wrapper(IStorageWrapper backpackWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
            super(backpackWrapper, upgrade, upgradeSaveHandler);
        }

        public boolean hideSettingsTab() {
            return true;
        }

        public boolean canBeDisabled() {
            return false;
        }
    }
}
