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
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;

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
        super(NVMaterialsAndTiers.SENTIENT, new Properties()
                .attributes(PickaxeItem.createAttributes(NVMaterialsAndTiers.SENTIENT, 1, -2.8f))
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

        setActivatedState(stack, soulsRemaining > ACTIVATION_THRESHOLD);
        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, getExtraDamage(type, level));
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);
        setDigSpeedBonus(stack, level >= 0 ? DEFAULT_DIG_SPEED_ADDED[level] : 0);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (isSelected && !level.isClientSide && entity instanceof Player player) {
            recalculatePowers(stack, level, player);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        SpiritusTooltipHelper.appendSpiritusInfo(stack, getTooltipKey(), tooltip, flag);
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
