package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

import net.minecraft.server.level.ServerLevel;
public class SentientScytheItem extends Item implements ISentientTool {

    private static final double[] DEFAULT_DAMAGE = {1, 1.5, 2, 2.5, 3, 3.5, 4};
    private static final double[] DESTRUCTIVE_DAMAGE = {1.5, 2.25, 3, 3.75, 4.5, 5.25, 6};
    private static final double[] VENGEFUL_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};
    private static final double[] STEADFAST_DAMAGE = {0, 0.5, 1, 1.5, 2, 2.25, 2.5};

    private static final double[] AREA_RANGE = {2.5, 3, 3.5, 4, 4.5, 5, 5.5};

    public SentientScytheItem(Item.Properties props) {
        super(props.sword(NVMaterialsAndTiers.SENTIENT, 5, -2.6f)
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
        return "sentientScythe";
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
            int spiritusBracket = getLevel(will);

            if (spiritusBracket >= 0) {
                applyEffectToEntity(type, spiritusBracket, target, player);
                performAreaAttack(player, target, type, spiritusBracket);
            }
        }
    }

    private void performAreaAttack(Player player, LivingEntity target, SpiritusType type, int spiritusBracket) {
        double range = AREA_RANGE[spiritusBracket];
        AABB area = new AABB(
                target.getX() - range, target.getY() - range, target.getZ() - range,
                target.getX() + range, target.getY() + range, target.getZ() + range);

        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity != target && entity.isAlive() && entity instanceof Enemy);

        float sweepDamage = 1.0f + (float) getExtraDamage(type, spiritusBracket) * 0.5f;
        for (LivingEntity entity : nearbyEntities) {
            entity.hurtServer((ServerLevel) entity.level(), player.damageSources().playerAttack(player), sweepDamage);
            applyEffectToEntity(type, spiritusBracket, entity, player);
        }
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        recalculatePowers(stack, player.level(), player);
        return handleSpiritusDrain(stack, player);
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
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae." + getTooltipKey() + ".desc").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        SpiritusType type = getCurrentType(stack);
        SpiritusTooltipHelper.appendSpiritusInfo(stack, getTooltipKey(), tooltip, flag);
        if (flag.hasShiftDown()) {
            var localPlayer = net.minecraft.client.Minecraft.getInstance().player;
            if (localPlayer != null) {
                int level = getLevel(PlayerSpiritusHandler.getTotalSpiritus(type, localPlayer));
                if (level >= 0) {
                    double radius = AREA_RANGE[level];
                    tooltip.accept(Component.translatable("tooltip.neovitae.spiritus.aoe_radius",
                            String.format(Locale.ROOT, "%.1f", radius)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
