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
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;

public class BloodOrbItem extends Item implements IBindable {

    public BloodOrbItem() {
        super(new Item.Properties().stacksTo(1).component(NVDataComponents.BINDING, Binding.EMPTY));
    }

    private static int getShieldMinEV() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_MIN_EV.get(); }
    private static int getShieldDrain() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_DRAIN_PER_SECOND.get(); }

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
            IAnima network = NeoVitaeAPI.getInstance().getAnima(player.getUUID());
            if (network != null && network.getCurrentEV() >= getShieldMinEV()) {
                player.startUsingItem(hand);
                if (!level.isClientSide) {
                    BloodShieldEntity shield = new BloodShieldEntity(level, player);
                    level.addFreshEntity(shield);
                }
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,
                    0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
            );

            int animaCapacity = getAnimaCapacity(stack);
            if (animaCapacity == 0)
                return InteractionResultHolder.fail(stack);

            Anima ownerNetwork = AnimaHelper.getAnima(binding);
            ownerNetwork.add(AnimaTicket.create(200), animaCapacity);
            ownerNetwork.hurtPlayer(player, 200);
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

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        if (player.tickCount % 20 == 0) {
            IAnima network = NeoVitaeAPI.getInstance().getAnima(player.getUUID());
            if (network == null || network.getCurrentEV() < getShieldDrain()) {
                player.stopUsingItem();
                return;
            }
            network.syphon(AnimaTicket.create(getShieldDrain()));
        }
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
