package top.morenrx.sua.mixin.common.sophisticatedbackpacks;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.deposit.DepositUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.morenrx.sua.upgrades.rs_deposit.RSDepositUpgrade;
import top.morenrx.sua.util.SUAUtils;

import java.util.List;

@Mixin(value = InventoryInteractionHelper.class, remap = false)
public class MixinInventoryInteractionHelper {

    @Inject(method = "lambda$tryInventoryInteraction$2", at = @At("HEAD"), cancellable = true)
    private static void onTeLambdaHead(Direction face, Player player, ItemStack backpack, BlockEntity te, CallbackInfoReturnable<Boolean> cir) {
        if (!(te instanceof INetworkNodeProxy<?> networkNode)) return;
        cir.setReturnValue(backpack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
                .map(wrapper -> {
                    List<DepositUpgradeWrapper> upgradeWrappers = wrapper.getUpgradeHandler().getTypeWrappers(RSDepositUpgrade.TYPE);
                    if (upgradeWrappers.isEmpty()) return false;
                    if (player.level().isClientSide()) return true;

                    INetwork network = networkNode.getNode().getNetwork();
                    if (network == null || !network.canRun()) return false;

                    for (DepositUpgradeWrapper upgradeWrapper : upgradeWrappers) {
                        InventoryHandler inventoryHandler = wrapper.getInventoryHandler();
                        int index = 0;
                        for (int i = 0; i < inventoryHandler.getSlots(); i++) {
                            ItemStack slotStack = inventoryHandler.getSlotStack(i);
                            if (slotStack.isEmpty() || !upgradeWrapper.getFilterLogic().matchesFilter(slotStack)) continue;

                            ItemStack remainingStack = SUAUtils.RS.insertItemToRS(network, slotStack, player, true);
                            if (remainingStack.getCount() >= slotStack.getCount()) continue;

                            remainingStack = SUAUtils.RS.insertItemToRS(network, slotStack, player, false);
                            inventoryHandler.extractItem(i, slotStack.getCount() - remainingStack.getCount(), false);
                            index++;
                        }
                        String translKey = index > 0 ? "gui.sophisticatedbackpacks.status.stacks_deposited" : "gui.sophisticatedbackpacks.status.nothing_to_deposit";
                        player.displayClientMessage(Component.translatable(translKey, index), true);
                    }
                    return true;
                }).orElse(false));
    }
}
