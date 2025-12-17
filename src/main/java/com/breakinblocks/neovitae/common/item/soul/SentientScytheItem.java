package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

import java.util.List;
import java.util.Locale;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

/**
 * Sentient Scythe - a will-powered weapon with area attack capabilities.
 * Unlike other sentient tools, the scythe can damage multiple entities in a sweep.
 */
public class SentientScytheItem extends SwordItem implements ISentientTool {

    private static final double[] DEFAULT_DAMAGE = {1, 1.5, 2, 2.5, 3, 3.5, 4};
    private static final double[] DESTRUCTIVE_DAMAGE = {1.5, 2.25, 3, 3.75, 4.5, 5.25, 6};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};

    /** Area attack range based on will level (scythe-specific). */
    private static final double[] AREA_RANGE = {2.5, 3, 3.5, 4, 4.5, 5, 5.5};

    public SentientScytheItem() {
        super(BMMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(SwordItem.createAttributes(BMMaterialsAndTiers.SENTIENT, 5, -2.6f))
                .component(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT));
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
        return "sentientScythe";
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

                    // Area attack - hit nearby enemies (scythe-specific)
                    performAreaAttack(player, target, type, willBracket);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Performs the scythe's unique area attack, damaging nearby enemies.
     */
    private void performAreaAttack(Player player, LivingEntity target, EnumWillType type, int willBracket) {
        double range = AREA_RANGE[willBracket];
        AABB area = new AABB(
                target.getX() - range, target.getY() - range, target.getZ() - range,
                target.getX() + range, target.getY() + range, target.getZ() + range);

        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity != target && entity.isAlive() && entity instanceof Enemy);

        float sweepDamage = 1.0f + (float) getExtraDamage(type, willBracket) * 0.5f;
        for (LivingEntity entity : nearbyEntities) {
            entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
            applyEffectToEntity(type, willBracket, entity, player);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        recalculatePowers(stack, player.level(), player);
        if (handleWillDrain(stack, player)) {
            return false;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        EnumWillType type = PlayerDemonWillHandler.getLargestWillType(player);
        double soulsRemaining = PlayerDemonWillHandler.getTotalDemonWill(type, player);

        setCurrentType(stack, soulsRemaining > 0 ? type : EnumWillType.DEFAULT);
        int level = getLevel(soulsRemaining);

        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, getExtraDamage(type, level));
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);
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
}
