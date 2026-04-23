package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

import java.util.Locale;
import java.util.function.Consumer;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

public class SentientShovelItem extends ShovelItem implements ISentientTool {

    private static final double[] DEFAULT_DAMAGE = {0.5, 1, 1.5, 2, 2.5, 3, 3.5};
    private static final double[] DESTRUCTIVE_DAMAGE = {1, 2, 3, 4, 5, 6, 7};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.25, 0.5, 0.75, 1, 1.25, 1.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.25, 0.5, 0.75, 1, 1.25, 1.5};

    public SentientShovelItem(Item.Properties props) {
        super(NVMaterialsAndTiers.SENTIENT, 1.5f, -3.0f,
                props.component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT)
                     .component(NVDataComponents.SIGIL_ACTIVATED, false));
    }

    @Override
    public double[] getDamageForWillType(SpiritusType type) {
        return switch (type) {
            case DESTRUCTIVE -> DESTRUCTIVE_DAMAGE;
            case VENGEFUL -> VENGEFUL_DAMAGE;
            case STEADFAST -> STEADFAST_DAMAGE;
            default -> DEFAULT_DAMAGE;
        };
    }

    @Override
    public String getTooltipKey() {
        return "sentientShovel";
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
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        recalculatePowers(player.getItemInHand(hand), world, player);
        return InteractionResult.PASS;
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            recalculatePowers(stack, player.level(), player);
            SpiritusType type = getCurrentType(stack);
            double will = PlayerSpiritusHandler.getTotalSpiritus(type, player);
            int willBracket = getLevel(will);

            if (willBracket >= 0) {
                applyEffectToEntity(type, willBracket, target, player);
            }
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        recalculatePowers(stack, player.level(), player);
        return handleWillDrain(stack, player);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        SpiritusType type = PlayerSpiritusHandler.getLargestSpiritusType(player);
        double soulsRemaining = PlayerSpiritusHandler.getTotalSpiritus(type, player);

        setCurrentType(stack, soulsRemaining > 0 ? type : SpiritusType.DEFAULT);
        int level = getLevel(soulsRemaining);

        setActivatedState(stack, soulsRemaining > ACTIVATION_THRESHOLD);
        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, getExtraDamage(type, level));
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);
        setDigSpeedBonus(stack, level >= 0 ? DEFAULT_DIG_SPEED_ADDED[level] : 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.currentType." + getCurrentType(stack).name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
