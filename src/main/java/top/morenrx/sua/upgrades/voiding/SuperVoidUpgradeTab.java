package top.morenrx.sua.upgrades.voiding;

import net.minecraftforge.fml.loading.FMLLoader;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPTranslationHelper;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.controls.WidgetBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import java.lang.reflect.Field;

public class SuperVoidUpgradeTab extends VoidUpgradeTab {

    public SuperVoidUpgradeTab(VoidUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsPerRow) {
        super(upgradeContainer, position, screen, SBPTranslationHelper.INSTANCE.translUpgrade("super_void"), SBPTranslationHelper.INSTANCE.translUpgradeTooltip("super_void"));
        ArtifactVersion currentVersion = new DefaultArtifactVersion(FMLLoader.getLoadingModList().getModFileById(SophisticatedCore.MOD_ID).versionString());
        try {
            WidgetBase control;

            if (currentVersion.compareTo(new DefaultArtifactVersion("1.3.79.2250")) >= 0) {
                Class<?> clazz = Class.forName("net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidFilterLogicControl$Advanced");
                Object fluidFilterContainer = VoidUpgradeContainer.class.getMethod("getFluidFilterContainer").invoke(this.getContainer());

                control = (WidgetBase) clazz.getConstructors()[0].newInstance(screen, new Position(this.x + 3, this.y + 44),
                        this.getContainer().getFilterLogicContainer(), fluidFilterContainer, slotsPerRow);
            } else {
                Class<?> clazz = Class.forName("net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogicControl$Advanced");

                control = (WidgetBase) clazz.getConstructors()[0].newInstance(screen, new Position(this.x + 3, this.y + 44),
                        this.getContainer().getFilterLogicContainer(), slotsPerRow);
            }

            Field field = VoidUpgradeTab.class.getDeclaredField("filterLogicControl");
            field.setAccessible(true);
            field.set(this, this.addHideableChild(control));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("[SUA] SuperVoidUpgradeTab 反射失败", e);
        }
    }
}
