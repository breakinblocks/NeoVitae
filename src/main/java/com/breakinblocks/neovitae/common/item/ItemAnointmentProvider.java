// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import com.breakinblocks.neovitae.anointment.Anointment;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemAnointmentProvider extends Item {
    private final Identifier anointmentKey;
    private final int color;
    private final int level;
    private final int maxDamage;

    public ItemAnointmentProvider(Item.Properties props, Identifier anointmentKey, int color, int level, int maxDamage) {
        super(props.stacksTo(16));
        this.anointmentKey = anointmentKey;
        this.color = color;
        this.level = level;
        this.maxDamage = maxDamage;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack targetStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (!level.isClientSide()) {
            if (!targetStack.isEmpty() && isItemValidForApplication(targetStack)) {
                AnointmentHolder holder = targetStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());
                if (holder == null) {
                    holder = AnointmentHolder.empty();
                }

                Anointment anointment = AnointmentRegistrar.get(anointmentKey);
                if (canApplyAnointment(holder, anointment, this.level, this.maxDamage)) {
                    AnointmentHolder newHolder = applyAnointment(holder, anointmentKey, this.level, this.maxDamage);
                    targetStack.set(NVDataComponents.ANOINTMENT_HOLDER.get(), newHolder);

                    level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    stack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }
        } else {
            if (!targetStack.isEmpty() && isItemValidForApplication(targetStack)) {
                AnointmentHolder holder = targetStack.get(NVDataComponents.ANOINTMENT_HOLDER.get());
                if (holder == null) {
                    holder = AnointmentHolder.empty();
                }

                Anointment anointment = AnointmentRegistrar.get(anointmentKey);
                if (canApplyAnointment(holder, anointment, this.level, this.maxDamage)) {
                    for (int i = 0; i < 16; i++) {
                        level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                                player.getRandomX(0.3D), player.getRandomY(), player.getRandomZ(0.3D),
                                0, 0, 0);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }

        return super.use(level, player, hand);
    }

    private boolean canApplyAnointment(AnointmentHolder holder, Anointment anointment, int level, int maxDamage) {
        Identifier key = anointment.getKey();

        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            Anointment existing = AnointmentRegistrar.get(entry.key());
            if (!anointment.isCompatible(entry.key()) || !existing.isCompatible(key)) {
                return false;
            }
        }

        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            if (entry.key().equals(key)) {
                if (level < entry.level()) {
                    return false;
                }
                if (level == entry.level() && maxDamage <= entry.remainingUses()) {
                    return false;
                }
            }
        }

        return true;
    }

    private AnointmentHolder applyAnointment(AnointmentHolder holder, Identifier key, int level, int maxDamage) {
        List<AnointmentHolder.AnointmentEntry> newList = new ArrayList<>();

        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            if (!entry.key().equals(key)) {
                newList.add(entry);
            }
        }

        newList.add(new AnointmentHolder.AnointmentEntry(key, level, 0, maxDamage));

        return new AnointmentHolder(newList);
    }

    public boolean isItemValidForApplication(ItemStack stack) {
        Anointment anointment = AnointmentRegistrar.get(anointmentKey);
        TagKey<Item> applicable = anointment.getApplicableItems();
        if (applicable != null) {
            return stack.is(applicable);
        }
        return isItemTool(stack) || isItemSword(stack);
    }

    public static boolean isItemTool(ItemStack stack) {
        for (ItemAbility action : validToolActions()) {
            if (stack.canPerformAction(action)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isItemSword(ItemStack stack) {
        // 26.1: SwordItem class removed; check by vanilla "swords" tag.
        return stack.is(ItemTags.SWORDS);
    }

    public static List<ItemAbility> validToolActions() {
        // 26.1: AXE_DIG / PICKAXE_DIG / SHOVEL_DIG / SWORD_DIG / HOE_DIG were removed when
        // digging moved to ToolMaterial-driven resolution. Only SHEARS_DIG remains, and tool
        // category is now determined by item JSON / ToolMaterial mining rules, not by
        // `canPerformAction` against a dig ability. Anointment compatibility therefore falls
        // back to the tool-category tags (ItemTags.AXES etc.) in callers.
        // See migration_alterations.md.
        return List.of();
    }

    public int getColor() {
        return color;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.anointment." + anointmentKey.getPath() + ".desc")
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.anointment.level", level));
        tooltip.accept(Component.translatable("tooltip.neovitae.anointment.uses", maxDamage));}
}
