package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.client.ClientAnimaCache;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.effect.SoulFrayEffect;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.incense.IncenseHelper;
import com.breakinblocks.neovitae.util.AltarUtil;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BloodOrbItem extends Item implements IBindable {

    private static final int ORB_ALTAR_RANGE = 5;

    public BloodOrbItem() {
        super(new Item.Properties().stacksTo(1).component(NVDataComponents.BINDING, Binding.EMPTY));
    }

    public static int getShieldMinEV() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_MIN_EV.get(); }
    public static int getShieldDrain() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_DRAIN_PER_SECOND.get(); }

    private static final Set<UUID> SHIELD_ACTIVE = ConcurrentHashMap.newKeySet();

    public static boolean isShieldActive(Player player) {
        return SHIELD_ACTIVE.contains(player.getUUID());
    }

    public static void setShieldActive(Player player, boolean on) {
        if (on) {
            SHIELD_ACTIVE.add(player.getUUID());
        } else {
            SHIELD_ACTIVE.remove(player.getUUID());
        }
    }

    public static void clearShieldActive(UUID id) {
        SHIELD_ACTIVE.remove(id);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof FakePlayer)
            return InteractionResultHolder.consume(stack);

        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty())
            return InteractionResultHolder.consume(stack);

        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        AraVitaeTile altar = AltarUtil.getAltarTile(player, ORB_ALTAR_RANGE);

        int orbCapacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        SimpleFluidContent currentFluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int orbRoom = orbCapacity - (currentFluid.isEmpty() ? 0 : currentFluid.getAmount());
        int altarRoom = altar != null ? altar.getMainCapacity() - altar.getMainTank() : 0;
        boolean toAltar = altarRoom > 0;

        if (!toAltar && orbRoom <= 0) {
            return InteractionResultHolder.fail(stack);
        }

        boolean ceremonial = player.hasEffect(NVMobEffects.BLESSED_SACRIFICE)
                && SoulFrayEffect.canPerformCeremonialSacrifice(player);
        int healthSacrificed;
        int evAdded;
        if (player.getAbilities().instabuild) {
            healthSacrificed = 0;
            evAdded = toAltar ? altarRoom : orbRoom;
        } else if (ceremonial) {
            healthSacrificed = Math.max(1, (int) (player.getHealth() - player.getMaxHealth() / 10F));
            double incenseBonus = IncenseHelper.getCurrentIncense(player);
            evAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed, incenseBonus);
        } else {
            healthSacrificed = 2;
            evAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed);
        }

        if (!level.isClientSide) {
            if (healthSacrificed > 0 && AltarUtil.takeSacrifice(player, healthSacrificed) <= 0) {
                // Nothing was actually given up, so nothing is owed in return.
                player.displayClientMessage(Component.translatable("message.neovitae.altar_blood_refused"), true);
                return InteractionResultHolder.fail(stack);
            }
            if (toAltar) {
                altar.addSacrificeEV(Math.max(0, evAdded), false);
            } else {
                OrbFluidHandler.fillInternal(stack,
                        new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), Math.max(0, evAdded)), FluidAction.EXECUTE);
            }

            if (ceremonial) {
                IncenseHelper.clearIncense(player);
                player.removeEffect(NVMobEffects.BLESSED_SACRIFICE);
                player.addEffect(new MobEffectInstance(NVMobEffects.SOUL_FRAY, SoulFrayEffect.DEFAULT_DURATION, 0));
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 0.6F, 0.8F + level.random.nextFloat() * 0.4F);
            if (level instanceof ServerLevel serverLevel) {
                double handY = player.getY() + player.getBbHeight() * 0.7;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0x990011),
                        player.getX(), handY, player.getZ(), 3, 0.1, 0.05, 0.1, 0);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }


    public int getFluidCapacity(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).fluidCapacity();
    }

    public int getAnimaCapacity(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).animaCapacity();
    }

    public int getFillRate(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).fillRate();
    }

    public int getOrbTier(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).tier();
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack.copy();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity <= 0) return false;
        SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        return !fluid.isEmpty() && fluid.getAmount() >= capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var stats = stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (stats != null) {
            tooltip.add(Component.translatable("tooltip.neovitae.orb.tier", stats.tier())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.neovitae.orb.anima_max", stats.animaCapacity())
                    .withStyle(ChatFormatting.GRAY));
        }

        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (!binding.isEmpty() && ClientAnimaCache.has(binding.uuid())) {
            tooltip.add(Component.translatable("tooltip.neovitae.orb.network_ev", ClientAnimaCache.getCurrentEV())
                    .withStyle(ChatFormatting.GRAY));
        }

        SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity > 0) {
            int amount = fluid.isEmpty() ? 0 : fluid.getAmount();
            tooltip.add(Component.translatable("tooltip.neovitae.orb.fluid", amount, capacity)
                    .withStyle(ChatFormatting.DARK_RED));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
