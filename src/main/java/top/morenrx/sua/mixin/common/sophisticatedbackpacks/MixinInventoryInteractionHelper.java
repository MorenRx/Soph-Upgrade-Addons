package top.morenrx.sua.mixin.common.sophisticatedbackpacks;

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
import top.morenrx.sua.upgrades.compat.network.NetworkStorageHandler;
import top.morenrx.sua.upgrades.compat.network.NetworkStorageProvider;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgrade;

import java.util.List;

@Mixin(value = InventoryInteractionHelper.class, remap = false)
public class MixinInventoryInteractionHelper {

    @Inject(method = "lambda$tryInventoryInteraction$2", at = @At("HEAD"), cancellable = true)
    private static void onTeLambdaHead(Direction face, Player player, ItemStack backpack, BlockEntity te, CallbackInfoReturnable<Boolean> cir) {
        NetworkStorageProvider.get().getNetworkStorageHandlers().forEach((name, handler) -> {
            if (!handler.blockValidGetter().apply(te)) return;
            cir.setReturnValue(backpack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance())
                    .map(wrapper -> {
                        List<DepositUpgradeWrapper> upgradeWrappers = wrapper.getUpgradeHandler().getTypeWrappers(NetworkDepositUpgrade.TYPE);
                        if (upgradeWrappers.isEmpty()) return false;
                        if (player.level().isClientSide()) return true;
                        NetworkStorageHandler.InsertHandler insertHandler = handler.insertHandlerGetter().apply(te);
                        if (insertHandler == null) return false;

                        for (DepositUpgradeWrapper upgradeWrapper : upgradeWrappers) {
                            InventoryHandler inventoryHandler = wrapper.getInventoryHandler();
                            int index = 0;
                            for (int i = 0; i < inventoryHandler.getSlots(); i++) {
                                ItemStack slotStack = inventoryHandler.getSlotStack(i);
                                if (slotStack.isEmpty() || !upgradeWrapper.getFilterLogic().matchesFilter(slotStack))
                                    continue;

                                ItemStack remainingStack = insertHandler.insert(slotStack, player, true);
                                if (remainingStack.getCount() >= slotStack.getCount()) continue;

                                remainingStack = insertHandler.insert(slotStack, player, false);
                                inventoryHandler.extractItem(i, slotStack.getCount() - remainingStack.getCount(), false);
                                index++;
                            }
                            String translKey = index > 0 ? "gui.sophisticatedbackpacks.status.stacks_deposited" : "gui.sophisticatedbackpacks.status.nothing_to_deposit";
                            player.displayClientMessage(Component.translatable(translKey, index), true);
                        }
                        return true;
                    }).orElse(false));
        });
    }
}
