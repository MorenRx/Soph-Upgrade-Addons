package top.morenrx.sua.mixin.common.sophisticatedbackpacks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.morenrx.sua.upgrades.network_deposit.NetworkDepositUpgradeHandler;
import top.morenrx.sua.upgrades.network_restock.NetworkRestockUpgradeHandler;

@Mixin(value = InventoryInteractionHelper.class, remap = false)
public class MixinInventoryInteractionHelper {

    @Inject(
            method = "tryInventoryInteraction(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onTryInventoryInteraction(BlockPos pos, Level level, ItemStack backpack, Direction face, Player player, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te != null && (NetworkDepositUpgradeHandler.tryDeposit(face, player, backpack, te)
                || NetworkRestockUpgradeHandler.tryRestock(face, player, backpack, te))) {
            cir.setReturnValue(true);
        }
    }
}
