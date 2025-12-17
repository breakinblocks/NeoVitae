package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import com.breakinblocks.neovitae.anointment.Anointment;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Item that can be used to apply anointments to weapons and tools.
 * Right-click with the anointment in one hand and the tool in the other.
 */
public class ItemAnointmentProvider extends Item {
    private final ResourceLocation anointmentKey;
    private final int color;
    private final int level;
    private final int maxDamage;

    public ItemAnointmentProvider(ResourceLocation anointmentKey, int color, int level, int maxDamage) {
        super(new Item.Properties().stacksTo(16));
        this.anointmentKey = anointmentKey;
        this.color = color;
        this.level = level;
        this.maxDamage = maxDamage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack targetStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (!level.isClientSide) {
            if (!targetStack.isEmpty() && isItemValidForApplication(targetStack)) {
                AnointmentHolder holder = targetStack.get(BMDataComponents.ANOINTMENT_HOLDER.get());
                if (holder == null) {
                    holder = AnointmentHolder.empty();
                }

                Anointment anointment = AnointmentRegistrar.get(anointmentKey);
                if (canApplyAnointment(holder, anointment, this.level, this.maxDamage)) {
                    // Apply the anointment
                    AnointmentHolder newHolder = applyAnointment(holder, anointmentKey, this.level, this.maxDamage);
                    targetStack.set(BMDataComponents.ANOINTMENT_HOLDER.get(), newHolder);

                    level.playSound(null, player.blockPosition(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    stack.shrink(1);
                    return InteractionResultHolder.consume(stack);
                }
            }
        } else {
            // Client side - particles
            if (!targetStack.isEmpty() && isItemValidForApplication(targetStack)) {
                AnointmentHolder holder = targetStack.get(BMDataComponents.ANOINTMENT_HOLDER.get());
                if (holder == null) {
                    holder = AnointmentHolder.empty();
                }

                Anointment anointment = AnointmentRegistrar.get(anointmentKey);
                if (canApplyAnointment(holder, anointment, this.level, this.maxDamage)) {
                    // Add colorful particles
                    for (int i = 0; i < 16; i++) {
                        level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color),
                                player.getRandomX(0.3D), player.getRandomY(), player.getRandomZ(0.3D),
                                0, 0, 0);
                    }
                    return InteractionResultHolder.consume(stack);
                }
            }
        }

        return super.use(level, player, hand);
    }

    private boolean canApplyAnointment(AnointmentHolder holder, Anointment anointment, int level, int maxDamage) {
        ResourceLocation key = anointment.getKey();

        // Check incompatibilities
        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            Anointment existing = AnointmentRegistrar.get(entry.key());
            if (!anointment.isCompatible(entry.key()) || !existing.isCompatible(key)) {
                return false;
            }
        }

        // Check if upgrading existing anointment
        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            if (entry.key().equals(key)) {
                // Can upgrade if new level is higher or if same level but more uses
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

    private AnointmentHolder applyAnointment(AnointmentHolder holder, ResourceLocation key, int level, int maxDamage) {
        List<AnointmentHolder.AnointmentEntry> newList = new ArrayList<>();

        // Keep all anointments except the one being replaced
        for (AnointmentHolder.AnointmentEntry entry : holder.anointments()) {
            if (!entry.key().equals(key)) {
                newList.add(entry);
            }
        }

        // Add the new anointment
        newList.add(new AnointmentHolder.AnointmentEntry(key, level, 0, maxDamage));

        return new AnointmentHolder(newList);
    }

    public boolean isItemValidForApplication(ItemStack stack) {
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
        return stack.getItem() instanceof SwordItem;
    }

    public static List<ItemAbility> validToolActions() {
        List<ItemAbility> actionList = new ArrayList<>();
        actionList.add(ItemAbilities.AXE_DIG);
        actionList.add(ItemAbilities.SHOVEL_DIG);
        actionList.add(ItemAbilities.SWORD_DIG);
        actionList.add(ItemAbilities.PICKAXE_DIG);
        actionList.add(ItemAbilities.HOE_DIG);
        return actionList;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Anointment anointment = AnointmentRegistrar.get(anointmentKey);
        tooltip.add(Component.translatable("tooltip.neovitae.anointment.level", level));
        tooltip.add(Component.translatable("tooltip.neovitae.anointment.uses", maxDamage));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
