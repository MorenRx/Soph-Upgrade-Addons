package top.morenrx.sua.upgrades.potion_charm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeSettingsTab;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Dimension;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import org.jetbrains.annotations.NotNull;

public class PotionCharmUpgradeTab extends UpgradeSettingsTab<PotionCharmUpgradeContainer> {
    private final int slotsInRow;

    protected PotionCharmUpgradeTab(PotionCharmUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, Component tabLabel, Component closedTooltip, int slotsInRow) {
        super(upgradeContainer, position, screen, tabLabel, closedTooltip);
        this.slotsInRow = slotsInRow;
        this.openTabDimension = new Dimension(
                9 + 18 * slotsInRow,
                30 + 18 * (int) Math.ceil((float) getContainer().getSlots().size() / slotsInRow)
        );
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, @NotNull Minecraft minecraft, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, minecraft, mouseX, mouseY);
        if (getContainer().isOpen()) {
            GuiHelper.renderSlotsBackground(guiGraphics, x + 3, y + 24, slotsInRow,  getContainer().getSlots().size() / slotsInRow, getContainer().getSlots().size() % slotsInRow);
        }
    }

    @Override
    protected void moveSlotsToTab() {
        int slotIndex = 0;
        for (Slot slot : getContainer().getSlots()) {
            slot.x = x - screen.getGuiLeft() + 4 + (slotIndex % slotsInRow) * 18;
            slot.y = y - screen.getGuiTop() + 25 + (slotIndex / slotsInRow) * 18;
            slotIndex++;
        }
    }

    public static class Basic extends PotionCharmUpgradeTab {
        public Basic(PotionCharmUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsInRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("potion_charm"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("potion_charm"), slotsInRow);
        }
    }

    public static class Advanced extends PotionCharmUpgradeTab {
        public Advanced(PotionCharmUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsInRow) {
            super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("advanced_potion_charm"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("advanced_potion_charm"), slotsInRow);
        }
    }
}
