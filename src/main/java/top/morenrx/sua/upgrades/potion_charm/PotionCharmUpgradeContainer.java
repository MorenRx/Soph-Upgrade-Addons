package top.morenrx.sua.upgrades.potion_charm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;
import net.p3pp3rf1y.sophisticatedcore.common.gui.ISyncedContainer;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerBase;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import org.jetbrains.annotations.NotNull;

public class PotionCharmUpgradeContainer extends UpgradeContainerBase<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> implements ISyncedContainer {
    public PotionCharmUpgradeContainer(Player player, int containerId, PotionCharmUpgradeWrapper wrapper, UpgradeContainerType<PotionCharmUpgradeWrapper, PotionCharmUpgradeContainer> type) {
        super(player, containerId, wrapper, type);
        for (int slot = 0; slot < upgradeWrapper.getPotionCharmInventory().getSlots(); slot++) {
            slots.add(new SlotItemHandler(upgradeWrapper.getPotionCharmInventory(), slot, -100, -100));
        }
    }

    @Override
    public void handleMessage(@NotNull CompoundTag compoundTag) {

    }
}
