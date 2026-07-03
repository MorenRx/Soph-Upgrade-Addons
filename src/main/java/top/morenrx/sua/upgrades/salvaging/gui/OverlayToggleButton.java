package top.morenrx.sua.upgrades.salvaging.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ButtonDefinition;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.ToggleButton;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.GuiHelper;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.TextureBlitData;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class OverlayToggleButton<T extends Comparable<T>> extends ToggleButton<T> {
    private final TextureBlitData overlayTexture;
    private final Supplier<Integer> colorGetter;
    public OverlayToggleButton(Position position, StateData<T> overlayStateData, IntConsumer onClick, Supplier<T> getState) {
        super(position, overlayStateData.buttonDefinition(), onClick, getState);
        overlayTexture = overlayStateData.overlayTexture;
        colorGetter = overlayStateData.colorGetter;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int color = colorGetter.get();
        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;
        float a = ((color >> 24) & 0xFF) / 255F;

        guiGraphics.setColor(r, g, b, a);
        GuiHelper.blit(guiGraphics, x, y, overlayTexture);
        guiGraphics.setColor(1F, 1F, 1F, 1F);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, @NotNull Minecraft minecraft, int mouseX, int mouseY) {
    }

    public record StateData <T extends Comparable<T>> (
            ButtonDefinition.Toggle<T> buttonDefinition,
            TextureBlitData overlayTexture,
            Supplier<Integer> colorGetter
    ){ }
}