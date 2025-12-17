package com.breakinblocks.neovitae.api.sigil.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.registry.SigilEffectRegistry;
import com.breakinblocks.neovitae.util.helper.NumeralHelper;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.function.Supplier;

/**
 * Sigil effect that displays information about the player's soul network
 * or a blood altar they're looking at. The seer variant shows additional details.
 */
public record DivinationSigilEffect(boolean isSeer) implements SigilEffect {

    private static final String TOOLTIP_BASE = "tooltip.neovitae.sigil.divination.";

    public static final MapCodec<DivinationSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("is_seer", false).forGetter(DivinationSigilEffect::isSeer)
            ).apply(instance, DivinationSigilEffect::new)
    );

    public static final Supplier<MapCodec<DivinationSigilEffect>> REGISTRATION =
            SigilEffectRegistry.SIGIL_EFFECT_TYPES.register("divination", () -> CODEC);

    @Override
    public MapCodec<? extends SigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return false;
        }

        HitResult rayTrace = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (rayTrace == null || rayTrace.getType() == HitResult.Type.MISS) {
            // No block hit - show soul network info
            showNetworkInfo(player, stack);
        } else if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) rayTrace;
            BlockPos blockPos = blockHit.getBlockPos();
            BlockEntity tile = level.getBlockEntity(blockPos);

            if (tile instanceof BloodAltarTile altar) {
                showAltarInfo(player, altar);
            } else {
                // Show soul network info
                showNetworkInfo(player, stack);
            }
        }

        return true;
    }

    private void showNetworkInfo(Player player, ItemStack stack) {
        Binding binding = stack.get(BMDataComponents.BINDING.get());
        if (binding != null) {
            SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
            if (network != null) {
                int currentLP = network.getCurrentEssence();
                if (!binding.uuid().equals(player.getGameProfile().getId())) {
                    player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "otherNetwork", binding.name()));
                }
                player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "currentNetworkLP", currentLP));
            }
        }
    }

    private void showAltarInfo(Player player, BloodAltarTile altar) {
        int tier = altar.getTier();
        int currentEssence = altar.getMainTank();
        int capacity = altar.getTankCapacity(0);

        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "currentAltarTier", NumeralHelper.toRoman(tier + 1)));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "currentEssence", currentEssence));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "currentAltarCapacity", capacity));

        // Show detailed stats in creative mode or with seer variant
        if (isSeer || player.isCreative()) {
            showDetailedAltarStats(player, altar);
        }
    }

    private void showDetailedAltarStats(Player player, IBloodAltar altar) {
        player.sendSystemMessage(Component.literal("--- Altar Stats ---").withStyle(ChatFormatting.GOLD));

        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.capacityMod",
                String.format("%.2f", altar.getBonusCapacity())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.speedMod",
                String.format("%.2f", altar.getSpeedBonus())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.tickRate",
                altar.getTickRate()).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.sacrificeMod",
                String.format("%.2f", altar.getSacrificeBonus())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.selfSacMod",
                String.format("%.2f", altar.getSelfSacrificeBonus())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.dislocationMod",
                String.format("%.2f", altar.getDislocationBonus())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.orbCapMod",
                String.format("%.2f", altar.getOrbCapacityBonus())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.efficiencyMod",
                String.format("%.2f", altar.getEfficiency())).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(TOOLTIP_BASE + "creative.chargingRate",
                altar.getChargingRate()).withStyle(ChatFormatting.AQUA));

        player.sendSystemMessage(Component.literal("--- End Stats ---").withStyle(ChatFormatting.GOLD));
    }
}
