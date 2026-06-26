package top.morenrx.sua.upgrades.rs_magnet;

import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModFluids;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.*;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import net.p3pp3rf1y.sophisticatedcore.util.XpHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.morenrx.sua.SophUpgradeAddons;
import top.morenrx.sua.data.RSLocation;
import top.morenrx.sua.util.SUAUtils;

import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SophUpgradeAddons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RSMagnetUpgradeWrapper extends UpgradeWrapperBase<RSMagnetUpgradeWrapper, RSMagnetUpgrade>
        implements IContentsFilteredUpgrade, ITickableUpgrade, IPickupResponseUpgrade {
    private static final int COOLDOWN_TICKS = 10;
    private static long nextTickTime = Long.MIN_VALUE;

    @SubscribeEvent
    public static void globalPostTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide()) {
            return;
        }

        long gameTime = event.level.getGameTime();
        if (gameTime > nextTickTime) {
            nextTickTime = gameTime + COOLDOWN_TICKS;
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload evt) {
        nextTickTime = Long.MIN_VALUE;
    }

    private static final int FULL_COOLDOWN_TICKS = 40;
    private final ContentsFilterLogic filterLogic;
    private RSLocation rsLocationCache = null;
    private Player playerCache = null;


    public RSMagnetUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
        super(storageWrapper, upgrade, upgradeSaveHandler);
        filterLogic = new ContentsFilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getFilterSlotCount(), storageWrapper::getInventoryHandler, storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class));
    }

    private boolean isInCooldown(Level level, @Nullable Entity entity) {
        if (!(entity instanceof Player)) {
            return super.isInCooldown(level);
        }

        return nextTickTime > level.getGameTime();
    }

    @Override
    public @NotNull ContentsFilterLogic getFilterLogic() {
        return filterLogic;
    }

    @Override
    public @NotNull ItemStack pickup(@NotNull Level world, @NotNull ItemStack stack, boolean simulate) {
        if (!shouldPickupItems() || !filterLogic.matchesFilter(stack)) return stack;
        if (!(world instanceof ServerLevel level)) return stack;

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) return ItemStack.EMPTY;

        if (this.playerCache == null) playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));

        if (rsLocationCache == null) {
            CompoundTag tag = upgrade.getOrCreateTag();
            if ((rsLocationCache = RSLocation.create(level, tag.getLong(SUAUtils.Data.KEY_NBT_POS), tag.getString(SUAUtils.Data.KEY_NBT_DIM))) == null) {
                return stack;
            }
        }

        INetwork network = SUAUtils.RS.getRSNetwork(rsLocationCache);
        if (network == null || !network.canRun()) return stack;

        return SUAUtils.RS.insertItemToRS(network, stack, playerCache, simulate);
    }

    @Override
    public void tick(@Nullable Entity entity, @NotNull Level world, @NotNull BlockPos pos) {
        if (isInCooldown(world, entity)) return;

        if (world instanceof ServerLevel level) {
            if (this.playerCache != entity && entity instanceof Player player) {
                this.playerCache = player;
            } else if (this.playerCache == null) {
                this.playerCache = SUAUtils.Backpack.getBackpackOwner(level, storageWrapper.getContentsUuid().orElse(null));
            }
        }

        int cooldown = shouldPickupItems() ? pickupItems(entity, world, pos) : FULL_COOLDOWN_TICKS;

        if (shouldPickupXp() && canFillStorageWithXp()) {
            cooldown = Math.min(cooldown, pickupXpOrbs(entity, world, pos));
        }

        setCooldown(world, cooldown);
    }

    private boolean canFillStorageWithXp() {
        return storageWrapper.getFluidHandler().map(fluidHandler -> fluidHandler.fill(ModFluids.EXPERIENCE_TAG, 1, ModFluids.XP_STILL.get(), IFluidHandler.FluidAction.SIMULATE) > 0).orElse(false);
    }

    private int pickupXpOrbs(@Nullable Entity entity, Level world, BlockPos pos) {
        List<ExperienceOrb> xpEntities = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos).inflate(upgradeItem.getRadius()), e -> true);
        if (xpEntities.isEmpty()) {
            return COOLDOWN_TICKS;
        }

        int cooldown = COOLDOWN_TICKS;
        for (ExperienceOrb xpOrb : xpEntities) {
            if (xpOrb.isAlive() && !canNotPickup(xpOrb, entity) && !tryToFillTank(xpOrb, entity, world)) {
                cooldown = FULL_COOLDOWN_TICKS;
                break;
            }
        }
        return cooldown;
    }

    private boolean tryToFillTank(ExperienceOrb xpOrb, @Nullable Entity entity, Level world) {
        int amountToTransfer = XpHelper.experienceToLiquid(xpOrb.getValue());

        return storageWrapper.getFluidHandler().map(fluidHandler -> {
            int amountAdded = fluidHandler.fill(ModFluids.EXPERIENCE_TAG, amountToTransfer, ModFluids.XP_STILL.get(), IFluidHandler.FluidAction.EXECUTE);

            if (amountAdded > 0) {
                Vec3 pos = xpOrb.position();
                xpOrb.value = 0;
                xpOrb.discard();

                if (entity instanceof Player player) {
                    playXpPickupSound(world, player);
                }

                if (amountToTransfer > amountAdded) {
                    world.addFreshEntity(new ExperienceOrb(world, pos.x(), pos.y(), pos.z(), (int) XpHelper.liquidToExperience(amountToTransfer - amountAdded)));
                }
                return true;
            }
            return false;
        }).orElse(false);
    }

    private int pickupItems(@Nullable Entity entity, Level world, BlockPos pos) {
        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(upgradeItem.getRadius()), e -> true);
        if (itemEntities.isEmpty()) {
            return COOLDOWN_TICKS;
        }

        Player player = entity instanceof Player ? (Player) entity : null;

        int cooldown = FULL_COOLDOWN_TICKS;
        for (ItemEntity itemEntity : itemEntities) {
            if (!itemEntity.isAlive() || itemEntity.pickupDelay == ItemEntity.INFINITE_PICKUP_DELAY || !filterLogic.matchesFilter(itemEntity.getItem()) || canNotPickup(itemEntity, entity)) {
                continue;
            }
            if (tryToInsertItem(itemEntity)) {
                if (player != null) {
                    playItemPickupSound(world, player);
                }
                cooldown = COOLDOWN_TICKS;
            }
        }
        return cooldown;
    }

    private static void playItemPickupSound(Level world, @NotNull Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (world.random.nextFloat() - world.random.nextFloat()) * 1.4F + 2.0F);
    }

    private static void playXpPickupSound(Level world, @NotNull Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (world.random.nextFloat() - world.random.nextFloat()) * 0.35F + 0.9F);
    }

    private boolean canNotPickup(Entity pickedUpEntity, @Nullable Entity entity) {
        CompoundTag data = pickedUpEntity.getPersistentData();
        return entity instanceof Player ? data.contains(RSMagnetUpgrade.Data.KEY_PREVENT_REMOTE_MOVEMENT) : data.contains(RSMagnetUpgrade.Data.KEY_PREVENT_REMOTE_MOVEMENT) && !data.contains(RSMagnetUpgrade.Data.KEY_ALLOW_MACHINE_MOVEMENT);
    }

    private boolean tryToInsertItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();

        if (shouldEnableVoid() && SUAUtils.Backpack.shouldDestroy(storageWrapper, stack)) {
            itemEntity.setItem(ItemStack.EMPTY);
            return true;
        }

        if (rsLocationCache == null) {
            CompoundTag tag = upgrade.getOrCreateTag();
            if ((rsLocationCache = RSLocation.create(itemEntity.level(), tag.getLong(SUAUtils.Data.KEY_NBT_POS), tag.getString(SUAUtils.Data.KEY_NBT_DIM))) == null) {
                return false;
            }
        }

        INetwork network = SUAUtils.RS.getRSNetwork(rsLocationCache);
        if (network == null || !network.canRun()) return false;

        ItemStack remainingStack = SUAUtils.RS.insertItemToRS(network, stack, playerCache, true);
        if (remainingStack.getCount() >= stack.getCount()) return false;
        remainingStack = SUAUtils.RS.insertItemToRS(network, stack, playerCache, false);

        itemEntity.setItem(remainingStack);
        return true;
    }

    public void setPickupItems(boolean pickupItems) {
        NBTHelper.setBoolean(upgrade, RSMagnetUpgrade.Data.KEY_PICKUP_ITEMS, pickupItems);
        save();
    }

    public boolean shouldPickupItems() {
        return NBTHelper.getBoolean(upgrade, RSMagnetUpgrade.Data.KEY_PICKUP_ITEMS).orElse(true);
    }

    public void setPickupXp(boolean pickupXp) {
        NBTHelper.setBoolean(upgrade, RSMagnetUpgrade.Data.KEY_PICKUP_XP, pickupXp);
        save();
    }

    public boolean shouldPickupXp() {
        return NBTHelper.getBoolean(upgrade, RSMagnetUpgrade.Data.KEY_PICKUP_XP).orElse(true);
    }

    public void setEnableVoid(boolean enableVoid) {
        NBTHelper.setBoolean(upgrade, RSMagnetUpgrade.Data.KEY_ENABLE_VOID, enableVoid);
        save();
    }

    public boolean shouldEnableVoid() {
        return NBTHelper.getBoolean(upgrade, RSMagnetUpgrade.Data.KEY_ENABLE_VOID).orElse(true);
    }
}