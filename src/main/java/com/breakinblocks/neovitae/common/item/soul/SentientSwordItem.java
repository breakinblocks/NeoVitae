package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

import java.util.List;
import java.util.Locale;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

/**
 * Sentient Sword - a will-powered weapon that scales with demon will in the player's inventory.
 * Kills enemies to collect will based on the current will type.
 */
public class SentientSwordItem extends SwordItem implements ISentientTool {

    // Damage added per level for each will type
    private static final double[] DEFAULT_DAMAGE = {1, 1.5, 2, 2.5, 3, 3.5, 4};
    private static final double[] DESTRUCTIVE_DAMAGE = {1.5, 2.25, 3, 3.75, 4.5, 5.25, 6};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};

    // Attack speed modifiers (sword-specific)
    private static final double[] VENGEFUL_ATTACK_SPEED = {-2.1, -2.0, -1.8, -1.7, -1.6, -1.6, -1.5};
    private static final double[] DESTRUCTIVE_ATTACK_SPEED = {-2.6, -2.7, -2.8, -2.9, -3, -3, -3};

    // Movement speed bonus (sword-specific, Vengeful only)
    private static final double[] MOVEMENT_SPEED = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4};

    public SentientSwordItem() {
        super(BMMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(SwordItem.createAttributes(BMMaterialsAndTiers.SENTIENT, 6, -2.4f))
                .component(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT)
                .component(BMDataComponents.SIGIL_ACTIVATED, false));
    }

    @Override
    public double[] getDamageForWillType(EnumWillType type) {
        return switch (type) {
            case DESTRUCTIVE -> DESTRUCTIVE_DAMAGE;
            case VENGEFUL -> VENGEFUL_DAMAGE;
            case STEADFAST -> STEADFAST_DAMAGE;
            default -> DEFAULT_DAMAGE;
        };
    }

    @Override
    public String getTooltipKey() {
        return "sentientSword";
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            // Recalculate powers every second (20 ticks) when selected
            if (level.getGameTime() % 20 == 0) {
                recalculatePowers(stack, level, player);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        recalculatePowers(player.getItemInHand(hand), world, player);
        return super.use(world, player, hand);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (super.hurtEnemy(stack, target, attacker)) {
            if (attacker instanceof Player player) {
                recalculatePowers(stack, player.level(), player);
                EnumWillType type = getCurrentType(stack);
                double will = PlayerDemonWillHandler.getTotalDemonWill(type, player);
                int willBracket = getLevel(will);

                if (willBracket >= 0) {
                    applyEffectToEntity(type, willBracket, target, player);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        recalculatePowers(stack, player.level(), player);
        if (handleWillDrain(stack, player)) {
            return false; // Cancel attack - not enough will
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        EnumWillType type = PlayerDemonWillHandler.getLargestWillType(player);
        double soulsRemaining = PlayerDemonWillHandler.getTotalDemonWill(type, player);

        setCurrentType(stack, soulsRemaining > 0 ? type : EnumWillType.DEFAULT);
        int level = getLevel(soulsRemaining);

        double drain = level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0;
        double extraDamage = getExtraDamage(type, level);
        double attackSpeed = getAttackSpeed(type, level);
        double movementSpeed = getMovementSpeed(type, level);

        setActivatedState(stack, soulsRemaining > 16);
        setDrainAmount(stack, drain);
        setDamageBonus(stack, 5 + extraDamage);
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);

        // Update actual attribute modifiers on the item
        updateAttributeModifiers(stack, 5 + extraDamage, attackSpeed, movementSpeed);
    }

    private double getAttackSpeed(EnumWillType type, int willBracket) {
        if (willBracket < 0) {
            return -2.4; // Base attack speed
        }
        return switch (type) {
            case VENGEFUL -> VENGEFUL_ATTACK_SPEED[willBracket];
            case DESTRUCTIVE -> DESTRUCTIVE_ATTACK_SPEED[willBracket];
            default -> -2.4;
        };
    }

    private double getMovementSpeed(EnumWillType type, int willBracket) {
        if (willBracket < 0 || type != EnumWillType.VENGEFUL) {
            return 0;
        }
        return MOVEMENT_SPEED[willBracket];
    }

    private void updateAttributeModifiers(ItemStack stack, double damage, double attackSpeed, double movementSpeed) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        NeoVitae.rl("sentient_sword_damage"),
                        damage,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        NeoVitae.rl("sentient_sword_speed"),
                        attackSpeed,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        // Movement speed bonus for VENGEFUL will
        if (movementSpeed > 0) {
            builder.add(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(
                            NeoVitae.rl("sentient_sword_movement"),
                            movementSpeed,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    EquipmentSlotGroup.MAINHAND);
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.currentType." + getCurrentType(stack).name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    // Sword-specific: activated state for sigil-like behavior
    private boolean getActivated(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SIGIL_ACTIVATED, false);
    }

    private void setActivatedState(ItemStack stack, boolean activated) {
        stack.set(BMDataComponents.SIGIL_ACTIVATED, activated);
    }
}
