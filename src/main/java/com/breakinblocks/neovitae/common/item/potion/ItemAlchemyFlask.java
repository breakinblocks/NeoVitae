// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.potion;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * Alchemy Flask - A reusable potion container that can hold custom potion effects.
 * Uses durability to track remaining uses.
 *
 * The flask uses FlaskEffects data component to store effect data with duration modifiers,
 * and syncs to PotionContents for vanilla compatibility (tooltips, colors).
 */
public class ItemAlchemyFlask extends Item {

    public static final int MAX_USES = 8;
    public static final int RINSE_AMOUNT = 250;

    public ItemAlchemyFlask(Item.Properties props) {
        super(props.stacksTo(1).durability(MAX_USES));
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.arctool.uses", getRemainingUses(stack))
                .withStyle(ChatFormatting.GOLD));

        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            PotionContents.addPotionTooltip(contents.getAllEffects(), tooltip, 1.0F, context.tickRate());
        }

        FlaskEffects effects = getFlaskEffects(stack);
        if (effects.effects().size() > 1) {
            tooltip.accept(Component.translatable("tooltip.neovitae.flask.combination")
                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        }
    }

    public int getRemainingUses(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        InteractionResult rinsed = rinseInWater(level, player, hand);
        if (rinsed.consumesAction()) {
            return rinsed;
        }

        ItemStack heldStack = player.getItemInHand(hand);

        if (getRemainingUses(heldStack) <= 0) {
            return InteractionResult.PASS;
        }

        if (!hasFlaskEffects(heldStack) && !hasEffects(heldStack)) {
            return InteractionResult.PASS;
        }

        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player) entityLiving : null;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
        }

        if (level instanceof ServerLevel serverLevel) {
            FlaskEffects flaskEffects = getFlaskEffects(stack);
            if (!flaskEffects.isEmpty()) {
                for (MobEffectInstance effectInstance : flaskEffects.toEffectInstances(false, true)) {
                    if (effectInstance.getEffect().value().isInstantenous()) {
                        effectInstance.getEffect().value().applyInstantenousEffect(
                                serverLevel, player, player, entityLiving, effectInstance.getAmplifier(), 1.0D);
                    } else {
                        entityLiving.addEffect(new MobEffectInstance(effectInstance));
                    }
                }
            } else {
                // Fallback to PotionContents for backwards compatibility
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null) {
                    for (MobEffectInstance effectInstance : contents.getAllEffects()) {
                        if (effectInstance.getEffect().value().isInstantenous()) {
                            effectInstance.getEffect().value().applyInstantenousEffect(
                                    serverLevel, player, player, entityLiving, effectInstance.getAmplifier(), 1.0D);
                        } else {
                            entityLiving.addEffect(new MobEffectInstance(effectInstance));
                        }
                    }
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.setDamageValue(stack.getDamageValue() + 1);
            }

            if (level instanceof ServerLevel serverLevel) {
                int color = 0xAA0000;
                PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null && contents.hasEffects()) {
                    color = contents.getColor();
                }
                serverLevel.sendParticles(
                        new ColoredParticleOptions(
                                NVParticles.BLOOD_FLAME.get(), color),
                        player.getX(), player.getY() + 1.0, player.getZ(), 6, 0.2, 0.3, 0.2, 0.02);
                serverLevel.sendParticles(
                        new ColoredParticleOptions(
                                NVParticles.BLOOD_GLOW.get(), color),
                        player.getX(), player.getY() + 1.2, player.getZ(), 3, 0.15, 0.2, 0.15, 0.01);
            }
        }

        return stack;
    }


    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !wantsRinse(player, stack)) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        if (!level.mayInteract(player, pos) || !drawRinseWater(level, pos, face, false)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            drawRinseWater(level, pos, face, true);
            rinse(stack);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.SUCCESS;
    }

    protected InteractionResult rinseInWater(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!wantsRinse(player, stack)) {
            return InteractionResult.PASS;
        }

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hit.getBlockPos();
        if (!level.mayInteract(player, pos) || !level.getFluidState(pos).is(FluidTags.WATER)) {
            return InteractionResult.PASS;
        }

        rinse(stack);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        return InteractionResult.SUCCESS;
    }

    protected boolean wantsRinse(Player player, ItemStack stack) {
        boolean hasContents = hasFlaskEffects(stack) || hasEffects(stack);
        if (!hasContents && !stack.isDamaged()) {
            return false;
        }
        return player.isSecondaryUseActive() || !hasContents || getRemainingUses(stack) <= 0;
    }

    private static boolean drawRinseWater(Level level, BlockPos pos, Direction face, boolean execute) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.WATER_CAULDRON)) {
            if (execute) {
                LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            }
            return true;
        }

        ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, face);
        if (handler == null) {
            return false;
        }

        try (Transaction tx = Transaction.openRoot()) {
            if (handler.extract(FluidResource.of(Fluids.WATER), RINSE_AMOUNT, tx) < RINSE_AMOUNT) {
                return false;
            }
            if (execute) {
                tx.commit();
            }
            return true;
        }
    }

    public static void rinse(ItemStack stack) {
        stack.remove(NVDataComponents.FLASK_EFFECTS.get());
        stack.remove(DataComponents.POTION_CONTENTS);
        stack.setDamageValue(0);
    }

    public static FlaskEffects getFlaskEffects(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.FLASK_EFFECTS.get(), FlaskEffects.EMPTY);
    }

    public static void setFlaskEffects(ItemStack stack, FlaskEffects effects) {
        stack.set(NVDataComponents.FLASK_EFFECTS.get(), effects);
        resyncPotionContents(stack);
    }

    public static boolean hasFlaskEffects(ItemStack stack) {
        FlaskEffects effects = stack.get(NVDataComponents.FLASK_EFFECTS.get());
        return effects != null && !effects.isEmpty();
    }

    public static void resyncPotionContents(ItemStack stack) {
        FlaskEffects flaskEffects = getFlaskEffects(stack);
        if (flaskEffects.isEmpty()) {
            stack.remove(DataComponents.POTION_CONTENTS);
            return;
        }

        List<MobEffectInstance> effectList = flaskEffects.toEffectInstances(false, true);
        List<MobEffectInstance> stable = new ArrayList<>();
        for (MobEffectInstance inst : effectList) {
            MobEffectInstance copy = new MobEffectInstance(inst.getEffect(), inst.getDuration(), inst.getAmplifier(), inst.isAmbient(), inst.isVisible());
            stable.add(copy);
        }
        PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), stable, Optional.empty());
        stack.set(DataComponents.POTION_CONTENTS, contents);
    }

    public static List<EffectHolder> getEffectHolders(ItemStack stack) {
        return getFlaskEffects(stack).toMutableList();
    }

    public static void setEffectHolders(ItemStack stack, List<EffectHolder> holders) {
        setFlaskEffects(stack, new FlaskEffects(holders));
    }


    public static ItemStack setEffects(ItemStack stack, Iterable<MobEffectInstance> effects) {
        List<MobEffectInstance> effectList = new ArrayList<>();
        effects.forEach(effectList::add);
        PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), effectList, Optional.empty());
        stack.set(DataComponents.POTION_CONTENTS, contents);
        return stack;
    }

    public static PotionContents getContents(ItemStack stack) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
    }

    public static boolean hasEffects(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return contents != null && contents.hasEffects();
    }
}
