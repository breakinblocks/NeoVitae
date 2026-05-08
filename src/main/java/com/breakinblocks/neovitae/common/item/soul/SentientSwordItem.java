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
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

import java.util.List;
import java.util.Locale;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

/**
 * Sentient Sword - a will-powered weapon that scales with spiritus in the player's inventory.
 * Kills enemies to collect will based on the current will type.
 */
public class SentientSwordItem extends SwordItem implements ISentientTool {

    private static final double[] DEFAULT_DAMAGE = {1, 1.5, 2, 2.5, 3, 3.5, 4};
    private static final double[] DESTRUCTIVE_DAMAGE = {1.5, 2.25, 3, 3.75, 4.5, 5.25, 6};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};

    private static final double[] VENGEFUL_ATTACK_SPEED = {-2.1, -2.0, -1.8, -1.7, -1.6, -1.6, -1.5};
    private static final double[] DESTRUCTIVE_ATTACK_SPEED = {-2.6, -2.7, -2.8, -2.9, -3, -3, -3};

    private static final double[] MOVEMENT_SPEED = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4};

    public SentientSwordItem() {
        super(NVMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(SwordItem.createAttributes(NVMaterialsAndTiers.SENTIENT, 6, -2.4f))
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW)
                .component(NVDataComponents.SIGIL_ACTIVATED, false));
    }

    @Override
    public double[] getDamageForSpiritusType(SpiritusType type) {
        return switch (type) {
            case NIHILUM -> DESTRUCTIVE_DAMAGE;
            case VINDICTA -> VENGEFUL_DAMAGE;
            case INVICTUS -> STEADFAST_DAMAGE;
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
                SpiritusType type = getCurrentType(stack);
                double will = PlayerSpiritusHandler.getTotalSpiritus(type, player);
                int spiritusBracket = getLevel(will);

                if (spiritusBracket >= 0) {
                    applyEffectToEntity(type, spiritusBracket, target, player);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        recalculatePowers(stack, player.level(), player);
        if (handleSpiritusDrain(stack, player)) {
            return false;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        SpiritusType type = PlayerSpiritusHandler.getLargestSpiritusType(player);
        double soulsRemaining = PlayerSpiritusHandler.getTotalSpiritus(type, player);

        setCurrentType(stack, soulsRemaining > 0 ? type : SpiritusType.RAW);
        int level = getLevel(soulsRemaining);

        double drain = level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0;
        double extraDamage = getExtraDamage(type, level);
        double attackSpeed = getAttackSpeed(type, level);
        double movementSpeed = getMovementSpeed(type, level);

        setActivatedState(stack, soulsRemaining > ACTIVATION_THRESHOLD);
        setDrainAmount(stack, drain);
        setDamageBonus(stack, 5 + extraDamage);
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);

        updateAttributeModifiers(stack, 5 + extraDamage, attackSpeed, movementSpeed);
    }

    private double getAttackSpeed(SpiritusType type, int spiritusBracket) {
        if (spiritusBracket < 0) {
            return -2.4;
        }
        return switch (type) {
            case VINDICTA -> VENGEFUL_ATTACK_SPEED[spiritusBracket];
            case NIHILUM -> DESTRUCTIVE_ATTACK_SPEED[spiritusBracket];
            default -> -2.4;
        };
    }

    private double getMovementSpeed(SpiritusType type, int spiritusBracket) {
        if (spiritusBracket < 0 || type != SpiritusType.VINDICTA) {
            return 0;
        }
        return MOVEMENT_SPEED[spiritusBracket];
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
        tooltip.add(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        SpiritusTooltipHelper.appendSpiritusInfo(stack, getTooltipKey(), tooltip);
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
