package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

import java.util.List;
import java.util.Locale;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

/**
 * Sentient Pickaxe - a will-powered pickaxe with enhanced damage and mining speed.
 */
public class SentientPickaxeItem extends PickaxeItem implements ISentientTool {

    private static final double[] DEFAULT_DAMAGE = {0.5, 1, 1.5, 2, 2.5, 3, 3.5};
    private static final double[] DESTRUCTIVE_DAMAGE = {1, 2, 3, 4, 5, 6, 7};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.25, 0.5, 0.75, 1, 1.25, 1.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.25, 0.5, 0.75, 1, 1.25, 1.5};

    public SentientPickaxeItem() {
        super(BMMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(PickaxeItem.createAttributes(BMMaterialsAndTiers.SENTIENT, 1, -2.8f))
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
        return "sentientPickaxe";
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float value = super.getDestroySpeed(stack, state);
        if (value > 1) {
            return (float) (value + getDigSpeedBonus(stack));
        }
        return value;
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
        setDigSpeedBonus(stack, level >= 0 ? DEFAULT_DIG_SPEED_ADDED[level] : 0);
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
