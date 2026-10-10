package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;

import java.util.function.Consumer;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

public class SentientSpearItem extends Item implements ISentientTool {

    public static final double NETHERITE_SPEAR_DAMAGE = 4.0;
    public static final double BASE_SPEAR_DAMAGE = NETHERITE_SPEAR_DAMAGE - 0.5;
    public static final float SPEAR_ATTACK_DURATION = 1.15F;
    public static final double BASE_SPEAR_ATTACK_SPEED = 1.0 / SPEAR_ATTACK_DURATION - 4.0;

    public SentientSpearItem(Item.Properties props) {
        super(props.spear(NVMaterialsAndTiers.SENTIENT, SPEAR_ATTACK_DURATION, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F)
                .attributes(buildWeaponModifiers("sentient_spear", BASE_SPEAR_DAMAGE, BASE_SPEAR_ATTACK_SPEED, 0))
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW)
                .component(NVDataComponents.SIGIL_ACTIVATED, false));
    }

    public static double getChargeDamageOffset(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.SENTIENT_SWORD_DAMAGE, BASE_SPEAR_DAMAGE) - NETHERITE_SPEAR_DAMAGE;
    }

    @Override
    public double[] getDamageForSpiritusType(SpiritusType type) {
        return getWeaponDamage(type);
    }

    @Override
    public String getTooltipKey() {
        return "sentientSpear";
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {
            if (level.getGameTime() % 20 == 0) {
                recalculatePowers(stack, level, player);
            }
        }
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        recalculatePowers(player.getItemInHand(hand), world, player);
        return super.use(world, player, hand);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            recalculatePowers(stack, player.level(), player);
            SpiritusType type = getCurrentType(stack);
            double will = PlayerSpiritusHandler.getTotalSpiritus(type, player);
            int spiritusBracket = getLevel(will);

            if (spiritusBracket >= 0) {
                applyEffectToEntity(type, spiritusBracket, target, player);
            }
            handleSpiritusDrain(stack, player);
        }
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        SpiritusType type = PlayerSpiritusHandler.getLargestSpiritusType(player);
        double soulsRemaining = PlayerSpiritusHandler.getTotalSpiritus(type, player);

        setCurrentType(stack, soulsRemaining > 0 ? type : SpiritusType.RAW);
        int level = getLevel(soulsRemaining);

        double damage = BASE_SPEAR_DAMAGE + getExtraDamage(type, level);
        double attackSpeed = BASE_SPEAR_ATTACK_SPEED + getWeaponAttackSpeed(type, level) - DEFAULT_ATTACK_SPEED;

        setActivatedState(stack, soulsRemaining > ACTIVATION_THRESHOLD);
        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, damage);
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, buildWeaponModifiers("sentient_spear", damage,
                attackSpeed, getWeaponMovementSpeed(type, level)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        SpiritusTooltipHelper.appendSpiritusInfo(stack, getTooltipKey(), tooltip, flag);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
