// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.item.IActivatable;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Unified sigil item that uses datapack-driven sigil types.
 * This single item class can represent any sigil by storing
 * a SigilType reference in its data components.
 */
public class SigilItem extends Item implements IBindable, IActivatable, ISigil {

    private final ResourceKey<SigilType> defaultSigilType;
    private final String tooltipBase;

    public SigilItem(ResourceKey<SigilType> sigilTypeKey) {
        super(new Item.Properties().stacksTo(1));
        this.defaultSigilType = sigilTypeKey;
        this.tooltipBase = "tooltip.neovitae.sigil." + sigilTypeKey.location().getPath() + ".";
    }

    public SigilItem(ResourceKey<SigilType> sigilTypeKey, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.defaultSigilType = sigilTypeKey;
        this.tooltipBase = "tooltip.neovitae.sigil." + sigilTypeKey.location().getPath() + ".";
    }

    @Nullable
    public SigilType getSigilType(ItemStack stack, Level level) {
        Holder<SigilType> holder = stack.get(NVDataComponents.SIGIL_TYPE.get());
        if (holder != null) {
            return holder.value();
        }

        // Fall back to default type from registry
        if (level != null) {
            var registry = level.registryAccess().registry(SigilTypeRegistry.SIGIL_TYPE_KEY);
            if (registry.isPresent()) {
                return registry.get().get(defaultSigilType);
            }
        }
        return null;
    }

    @Nullable
    public ISigilEffect getSigilEffect(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.effect().orElse(null) : null;
    }

    public boolean isUnusable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(NVDataComponents.SIGIL_UNUSABLE.get(), false);
    }

    public ItemStack setUnusable(ItemStack stack, boolean unusable) {
        if (!stack.isEmpty()) {
            stack.set(NVDataComponents.SIGIL_UNUSABLE.get(), unusable);
        }
        return stack;
    }

    public int getLpCost(ItemStack stack, Level level, SigilType.UseContext context) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.getCostForContext(context) : 0;
    }

    public boolean isToggleable(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null && type.isToggleable();
    }

    public int getDrainInterval(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.drainInterval() : SigilType.DEFAULT_DRAIN_INTERVAL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipBase + "desc")
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));

        Binding binding = getBinding(stack);
        if (binding != null) {
            tooltip.add(Component.translatable("tooltip.neovitae.currentOwner", binding.name())
                    .withStyle(ChatFormatting.GRAY));
        }

        Level level = context.level();
        if (level != null && isToggleable(stack, level)) {
            String stateKey = getActivated(stack) ? "tooltip.neovitae.activated" : "tooltip.neovitae.deactivated";
            tooltip.add(Component.translatable(stateKey).withStyle(ChatFormatting.GRAY));

            if (getActivated(stack)) {
                int upkeep = getLpCost(stack, level, SigilType.UseContext.ACTIVE);
                if (upkeep > 0) {
                    double seconds = getDrainInterval(stack, level) / 20.0;
                    String secondsLabel = seconds == Math.floor(seconds)
                            ? String.format("%d", (int) seconds)
                            : String.format("%.1f", seconds);
                    tooltip.add(Component.translatable("tooltip.neovitae.sigil.upkeep", upkeep, secondsLabel)
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        }

        Integer brightness = stack.get(NVDataComponents.BLOOD_LIGHT_BRIGHTNESS.get());
        if (brightness != null) {
            tooltip.add(Component.translatable("tooltip.neovitae.sigil.blood_light.brightness", brightness)
                    .withStyle(ChatFormatting.GOLD));
        }

        DyeColor color = stack.get(NVDataComponents.BLOOD_LIGHT_COLOR.get());
        if (color != null) {
            boolean rainbow = stack.getOrDefault(NVDataComponents.BLOOD_LIGHT_RAINBOW.get(), false);
            if (rainbow) {
                tooltip.add(Component.translatable("tooltip.neovitae.sigil.blood_light.color",
                        Component.translatable("tooltip.neovitae.sigil.blood_light.rainbow"))
                        .withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.neovitae.sigil.blood_light.color",
                        Component.translatable("color.minecraft." + color.getSerializedName()))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = resolveStackForUse(player, hand);
        if (stack == null) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        Binding binding = getBinding(stack);
        if (binding == null) {
            if (!level.isClientSide) {
                bind(player, stack);
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        // Toggleable sigils are toggled by shift + right-click in open air only.
        // useOn / interactLivingEntity handle block / entity right-clicks; use() is the air path.
        if (isToggleable(stack, level) && player.isShiftKeyDown()) {
            if (!level.isClientSide && !isUnusable(stack)) {
                boolean newState = !getActivated(stack);
                setActivatedState(stack, newState);
                level.playSound(null, player.blockPosition(), newState ? NVSounds.SIGIL_TOGGLE_ON.get() : NVSounds.SIGIL_TOGGLE_OFF.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
                ServerLevel serverLevel = (ServerLevel) level;
                for (int i = 0; i < 8; i++) {
                    double angle = (2 * Math.PI / 8) * i;
                    double offX = Math.cos(angle) * 0.6;
                    double offZ = Math.sin(angle) * 0.6;
                    serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), player.getX() + offX, player.getY() + 1, player.getZ() + offZ, 1, 0, 0, 0, 0);
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        if (!isUnusable(stack)) {
            ISigilEffect effect = getSigilEffect(stack, level);
            if (effect != null && effect.useOnAir(level, player, stack)) {
                if (!level.isClientSide) {
                    level.playSound(null, player.blockPosition(), NVSounds.SIGIL_ACTIVATE.get(), SoundSource.PLAYERS, 0.4f, 1.0f);
                    ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), player.getX(), player.getY() + 1, player.getZ(), 4, 0.2, 0.1, 0.2, 0);
                    if (!player.isCreative()) {
                        int cost = getReducedCost(getLpCost(stack, level, SigilType.UseContext.AIR), player);
                        if (cost > 0) {
                            Anima network = AnimaHelper.getAnima(binding);
                            if (network != null) {
                                setUnusable(stack, !network.syphonAndDamage(player, AnimaTicket.create(cost)).success());
                            }
                        }
                    }
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = ISigil.resolveHeldStack(context.getItemInHand(), player);

        Binding binding = getBinding(stack);
        if (binding == null) {
            if (!level.isClientSide && player != null) {
                bind(player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        ISigilEffect effect = getSigilEffect(stack, level);
        if (effect != null) {
            Direction side = context.getClickedFace();
            Vec3 hitVec = context.getClickLocation();

            if (effect.useOnBlock(level, player, stack, blockPos, side, hitVec)) {
                if (!level.isClientSide && player != null && !player.isCreative()) {
                    int cost = getReducedCost(getLpCost(stack, level, SigilType.UseContext.BLOCK), player);
                    if (cost > 0) {
                        Anima network = AnimaHelper.getAnima(binding);
                        if (network != null) {
                            setUnusable(stack, !network.syphonAndDamage(player, AnimaTicket.create(cost)).success());
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        ItemStack useStack = ISigil.resolveHeldStack(stack, player);

        Binding binding = getBinding(useStack);
        if (binding == null) {
            return InteractionResult.CONSUME;
        }

        Level level = player.level();
        ISigilEffect effect = getSigilEffect(useStack, level);
        if (effect != null && effect.useOnEntity(level, player, useStack, target)) {
            if (!level.isClientSide && !player.isCreative()) {
                int cost = getReducedCost(getLpCost(useStack, level, SigilType.UseContext.ENTITY), player);
                if (cost > 0) {
                    Anima network = AnimaHelper.getAnima(binding);
                    if (network != null) {
                        setUnusable(useStack, !network.syphonAndDamage(player, AnimaTicket.create(cost)).success());
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }

        if (!isToggleable(stack, level) || !getActivated(stack)) {
            return;
        }

        Binding binding = getBinding(stack);
        if (binding == null) {
            return;
        }

        int drainInterval = getDrainInterval(stack, level);
        if (entity.tickCount % drainInterval == 0) {
            int cost = getReducedCost(getLpCost(stack, level, SigilType.UseContext.ACTIVE), player);
            if (cost > 0) {
                Anima network = AnimaHelper.getAnima(binding);
                if (network != null) {
                    if (!network.syphonAndDamage(player, AnimaTicket.create(cost)).success()) {
                        setActivatedState(stack, false);
                        return;
                    }
                }
            }
        }

        ISigilEffect effect = getSigilEffect(stack, level);
        if (effect != null) {
            effect.activeTick(level, player, stack, itemSlot, isSelected);
        }
    }

    @Nullable
    protected ItemStack resolveStackForUse(Player player, InteractionHand hand) {
        return ISigil.resolveForUse(player, hand);
    }

    public ResourceKey<SigilType> getDefaultSigilType() {
        return defaultSigilType;
    }

    public String getTooltipBase() {
        return tooltipBase;
    }

    private static int getReducedCost(int baseCost, Player player) {
        if (baseCost <= 0) return baseCost;
        double reduction = player.getAttributeValue(NVAttributes.SIGIL_COST_REDUCTION);
        if (reduction > 0) {
            return Math.max(1, (int) (baseCost * (1 - reduction / 100)));
        }
        return baseCost;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || getActivated(stack);
    }
}
