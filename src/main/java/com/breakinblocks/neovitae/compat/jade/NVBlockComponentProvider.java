package com.breakinblocks.neovitae.compat.jade;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffect;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectBounce;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectDay;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectElevator;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectMovement;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectNight;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectSpike;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectUndertow;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectUpdraft;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import net.minecraft.world.item.Items;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.IncenseAltarBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.text.DecimalFormat;

public enum NVBlockComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    private static final Identifier UID = NeoVitae.rl("block_info");
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data == null || data.isEmpty()) return;

        if (data.contains("array_effect")) {
            tooltip.add(Component.translatable(data.getStringOr("array_effect", "jade.neovitae.array_effect.generic")).withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        if (data.contains("array_mode")) {
            boolean down = data.getBooleanOr("array_mode_down", false);
            String arrow = down ? "\u25BC" : "\u25B2";
            tooltip.add(Component.translatable("jade.neovitae.array_direction", arrow, data.getStringOr("array_mode", ""))
                    .withStyle(down ? ChatFormatting.BLUE : ChatFormatting.AQUA));
        }

        if (data.contains("array_accel")) {
            tooltip.add(Component.translatable("jade.neovitae.array_accel", String.format("%.3f", data.getDoubleOr("array_accel", 0d)))
                    .withStyle(ChatFormatting.GOLD));
        }

        if (data.contains("array_max_vel")) {
            tooltip.add(Component.translatable("jade.neovitae.array_max_vel", String.format("%.2f", data.getDoubleOr("array_max_vel", 0d)))
                    .withStyle(ChatFormatting.GOLD));
        }

        if (data.contains("altar_tier")) {
            tooltip.add(Component.translatable("jade.neovitae.altar_tier", data.getIntOr("altar_tier", 0)).withStyle(ChatFormatting.GOLD));
            if (data.getBooleanOr("altar_active", false)) {
                tooltip.add(Component.translatable("jade.neovitae.crafting").withStyle(ChatFormatting.GREEN));
            }
        }

        if (data.contains("ritual_name")) {
            String name = data.getStringOr("ritual_name", "");
            boolean active = data.getBooleanOr("ritual_active", false);
            tooltip.add(Component.translatable(name).withStyle(active ? ChatFormatting.GREEN : ChatFormatting.GRAY));
            if (!active) {
                tooltip.add(Component.translatable("jade.neovitae.inactive").withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        if (data.contains("tank_amount")) {
            tooltip.add(Component.translatable("jade.neovitae.tank_ev", FORMAT.format(data.getIntOr("tank_amount", 0)), FORMAT.format(data.getIntOr("tank_capacity", 0))).withStyle(ChatFormatting.DARK_RED));
        }

        if (data.contains("light_color")) {
            tooltip.add(Component.translatable("jade.neovitae.light_color", data.getStringOr("light_color", "")).withStyle(ChatFormatting.YELLOW));
        }

        if (data.contains("forge_progress")) {
            int progress = data.getIntOr("forge_progress", 0);
            if (progress > 0) {
                tooltip.add(Component.translatable("jade.neovitae.progress", progress).withStyle(ChatFormatting.GOLD));
            }
        }

        if (data.contains("tranquility")) {
            tooltip.add(Component.translatable("jade.neovitae.tranquility", String.format("%.1f", data.getDoubleOr("tranquility", 0d))).withStyle(ChatFormatting.AQUA));
            double bonus = data.getDoubleOr("incense_bonus", 0d);
            if (bonus > 0) {
                tooltip.add(Component.translatable("jade.neovitae.incense_bonus", String.format("%.1f", bonus * 100)).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();

        if (be instanceof AlchemyArrayBlockEntity array) {
            AlchemyArrayEffect effect = array.arrayEffect;
            if (effect != null) {
                data.putString("array_effect", getEffectName(effect));

                if (effect instanceof AlchemyArrayEffectUpdraft) {
                    int feathers = array.getItem(0).is(Items.FEATHER) ? array.getItem(0).getCount() : 0;
                    int glowstone = array.getItem(1).is(Items.GLOWSTONE_DUST) ? array.getItem(1).getCount() : 0;
                    double base = 1.0;
                    data.putDouble("array_max_vel", base + 0.1 * glowstone + 0.05 * feathers);
                } else if (effect instanceof AlchemyArrayEffectUndertow undertow) {
                    int kelp = array.getItem(0).is(Items.KELP) ? array.getItem(0).getCount() : 0;
                    int redstone = array.getItem(1).is(Items.REDSTONE) ? array.getItem(1).getCount() : 0;
                    boolean dragDown = undertow.isDragDown();
                    data.putString("array_mode", dragDown ? "Downward" : "Upward");
                    data.putBoolean("array_mode_down", dragDown);
                    data.putDouble("array_accel", (dragDown ? 0.05 : 0.12) + 0.003 * redstone);
                    data.putDouble("array_max_vel", (dragDown ? 0.9 : 1.8) + 0.015 * kelp);
                }
            }
        }

        if (be instanceof AraVitaeTile altar) {
            data.putInt("altar_tier", altar.getTier());
            data.putInt("altar_ev", altar.getMainTank());
            data.putInt("altar_capacity", altar.getMainCapacity());
            data.putBoolean("altar_active", altar.isActive());
        }

        if (be instanceof MasterRitualStoneBlockEntity mrs) {
            if (mrs.getCurrentRitual() != null) {
                data.putString("ritual_name", mrs.getCurrentRitual().getTranslationKey());
                data.putBoolean("ritual_active", mrs.isActive());
            }
        }

        if (be instanceof BloodTankBlockEntity tank) {
            data.putInt("tank_amount", tank.getFluidContained().getAmount());
            data.putInt("tank_capacity", tank.getCapacity());
        }

        if (be instanceof BloodLightBlockEntity light) {
            data.putString("light_color", light.getColor().getName());
        }

        if (be instanceof HellfireForgeBlockEntity forge) {
            int progress = forge.getProgress();
            if (progress > 0) {
                data.putInt("forge_progress", (int) (forge.getProgressForGui() * 100));
            }
        }

        if (be instanceof IncenseAltarBlockEntity incense) {
            data.putDouble("tranquility", incense.getTranquility());
            data.putDouble("incense_bonus", incense.getIncenseAddition());
        }
    }

    private static String getEffectName(AlchemyArrayEffect effect) {
        if (effect instanceof AlchemyArrayEffectBounce) return "jade.neovitae.array_effect.bounce";
        if (effect instanceof AlchemyArrayEffectSpike) return "jade.neovitae.array_effect.spike";
        if (effect instanceof AlchemyArrayEffectUpdraft) return "jade.neovitae.array_effect.updraft";
        if (effect instanceof AlchemyArrayEffectUndertow) return "jade.neovitae.array_effect.undertow";
        if (effect instanceof AlchemyArrayEffectMovement) return "jade.neovitae.array_effect.movement";
        if (effect instanceof AlchemyArrayEffectDay) return "jade.neovitae.array_effect.day";
        if (effect instanceof AlchemyArrayEffectNight) return "jade.neovitae.array_effect.night";
        if (effect instanceof AlchemyArrayEffectElevator) return "jade.neovitae.array_effect.elevator";
        return "jade.neovitae.array_effect.generic";
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
