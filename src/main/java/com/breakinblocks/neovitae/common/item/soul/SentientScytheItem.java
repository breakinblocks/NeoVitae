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
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.helper.ClientSpiritusTooltipHelper;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;

import java.util.List;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

/**
 * Sentient Scythe - a will-powered weapon with area attack capabilities.
 * Unlike other sentient tools, the scythe can damage multiple entities in a sweep.
 */
public class SentientScytheItem extends SwordItem implements ISentientTool {

    private static final double BASE_DAMAGE = 5;
    private static final double ATTACK_SPEED = -2.6;

    private static final double[] DEFAULT_DAMAGE = {1, 1.5, 2, 2.5, 3, 3.5, 4};
    private static final double[] DESTRUCTIVE_DAMAGE = {1.5, 2.25, 3, 3.75, 4.5, 5.25, 6};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};

    /** Area attack range based on will level (scythe-specific). */
    private static final double[] AREA_RANGE = {2.5, 3, 3.5, 4, 4.5, 5, 5.5};

    public SentientScytheItem() {
        super(NVMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(SwordItem.createAttributes(NVMaterialsAndTiers.SENTIENT, (int) BASE_DAMAGE, (float) ATTACK_SPEED))
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW));
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
        return "sentientScythe";
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

                    // Area attack - hit nearby enemies (scythe-specific)
                    performAreaAttack(player, target, type, spiritusBracket);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Performs the scythe's unique area attack, damaging nearby enemies for the
     * weapon's full attack damage (which already includes the spiritus bonus).
     */
    private void performAreaAttack(Player player, LivingEntity target, SpiritusType type, int spiritusBracket) {
        double range = AREA_RANGE[spiritusBracket];
        AABB area = new AABB(
                target.getX() - range, target.getY() - range, target.getZ() - range,
                target.getX() + range, target.getY() + range, target.getZ() + range);

        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity != target && entity.isAlive() && entity instanceof Enemy);

        float sweepDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity entity : nearbyEntities) {
            entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
            applyEffectToEntity(type, spiritusBracket, entity, player);
        }
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
        double extraDamage = getExtraDamage(type, level);

        setActivatedState(stack, soulsRemaining > ACTIVATION_THRESHOLD);
        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, BASE_DAMAGE + extraDamage);
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);

        updateAttributeModifiers(stack, BASE_DAMAGE + extraDamage);
    }

    private void updateAttributeModifiers(ItemStack stack, double damage) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        NeoVitae.rl("sentient_scythe_damage"),
                        damage,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        NeoVitae.rl("sentient_scythe_speed"),
                        ATTACK_SPEED,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        SpiritusType type = getCurrentType(stack);
        SpiritusTooltipHelper.appendSpiritusInfo(stack, getTooltipKey(), tooltip, flag);
        if (flag.hasShiftDown()) {
            ClientSpiritusTooltipHelper.appendAreaRadius(type, AREA_RANGE, tooltip);
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
