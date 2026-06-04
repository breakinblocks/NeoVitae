package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
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
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class BloodOrbItem extends Item implements IBindable {

    public BloodOrbItem(Item.Properties props) {
        super(props.stacksTo(1).component(NVDataComponents.BINDING, Binding.EMPTY));
    }

    private static int getShieldMinEV() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_MIN_EV.get(); }
    private static int getShieldDrain() { return NeoVitae.SERVER_CONFIG.SANGUINE_WARD_DRAIN_PER_SECOND.get(); }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (player instanceof FakePlayer)
            return InteractionResult.CONSUME;

        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty())
            return InteractionResult.CONSUME;

        if (hand == InteractionHand.OFF_HAND) {
            IAnima network = NeoVitaeAPI.getInstance().getAnima(player.getUUID());
            if (network != null && network.getCurrentEV() >= getShieldMinEV()) {
                player.startUsingItem(hand);
                if (!level.isClientSide()) {
                    BloodShieldEntity shield = new BloodShieldEntity(level, player);
                    level.addFreshEntity(shield);
                }
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,
                    0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F
            );

            int animaCapacity = getAnimaCapacity(stack);
            if (animaCapacity == 0)
                return InteractionResult.FAIL;

            Anima ownerNetwork = AnimaHelper.getAnima(binding);
            ownerNetwork.add(AnimaTicket.create(200), animaCapacity);
            ownerNetwork.hurtPlayer(player, 200);
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide()) return;

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
        return stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS).fluidCapacity();
    }

    public int getAnimaCapacity(ItemStack stack) {
        return stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS).animaCapacity();
    }

    public int getFillRate(ItemStack stack) {
        return stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS).fillRate();
    }

    public int getOrbTier(ItemStack stack) {
        return stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS).tier();
    }

    @Override
    public ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
        DataComponentPatch patch = instance instanceof ItemStack stack ? stack.getComponentsPatch() : DataComponentPatch.EMPTY;
        return new ItemStackTemplate(this, 1, patch);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity <= 0) return false;
        SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        return !fluid.isEmpty() && fluid.getAmount() >= capacity;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        var stats = stack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (stats != null) {
            tooltip.accept(Component.translatable("tooltip.neovitae.orb.tier", stats.tier())
                    .withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.neovitae.orb.anima_max", stats.animaCapacity())
                    .withStyle(ChatFormatting.GRAY));
        }

        SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity > 0) {
            int amount = fluid.isEmpty() ? 0 : fluid.getAmount();
            tooltip.accept(Component.translatable("tooltip.neovitae.orb.fluid", amount, capacity)
                    .withStyle(ChatFormatting.DARK_RED));
        }}
}
