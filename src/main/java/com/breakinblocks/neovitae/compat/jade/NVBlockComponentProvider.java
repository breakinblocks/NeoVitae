package com.breakinblocks.neovitae.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;
import com.breakinblocks.neovitae.util.helper.NumeralHelper;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.text.DecimalFormat;

public enum NVBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

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
            String arrow = down ? "▼" : "▲";
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
            tooltip.add(Component.translatable("hud.neovitae.altar.tier", NumeralHelper.toRoman(data.getIntOr("altar_tier", 0) + 1)).withStyle(ChatFormatting.GOLD));
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

        if (data.contains("link_linked")) {
            if (data.getBooleanOr("link_linked", false)) {
                tooltip.add(Component.translatable("jade.neovitae.vitae_link.tier",
                        data.getIntOr("link_tier", 0), data.getIntOr("link_max", 0)).withStyle(ChatFormatting.GOLD));
                if (data.getBooleanOr("link_crafting", false)) {
                    tooltip.add(Component.translatable("jade.neovitae.vitae_link.crafting").withStyle(ChatFormatting.GREEN));
                }
            } else {
                tooltip.add(Component.translatable("jade.neovitae.vitae_link.unlinked").withStyle(ChatFormatting.RED));
            }
        }

        if (data.contains("orb_link_linked")) {
            if (data.getBooleanOr("orb_link_linked", false)) {
                tooltip.add(Component.translatable("jade.neovitae.orb_link.linked").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("jade.neovitae.orb_link.network",
                        data.getIntOr("orb_link_network", 0)).withStyle(ChatFormatting.DARK_RED));
            } else {
                tooltip.add(Component.translatable("jade.neovitae.orb_link.unlinked").withStyle(ChatFormatting.RED));
            }
        }

        if (data.contains("accumulator_stored")) {
            SpiritusType type = typeByName(data.getStringOr("accumulator_type", ""));
            if (type == null) {
                tooltip.add(Component.translatable("jade.neovitae.spirit_accumulator.unattuned").withStyle(ChatFormatting.GRAY));
            } else {
                addSpiritusLine(tooltip, type, data.getDoubleOr("accumulator_stored", 0d), SpiritAccumulatorBlockEntity.CAPACITY);
            }
        }

        if (data.contains("vas_type")) {
            SpiritusType type = typeByName(data.getStringOr("vas_type", ""));
            if (type != null) {
                addSpiritusLine(tooltip, type, data.getDoubleOr("vas_stored", 0d), data.getDoubleOr("vas_max", 0d));
            }
            switch (data.getStringOr("vas_mode", "")) {
                case "filling" -> tooltip.add(Component.translatable("jade.neovitae.vas.filling").withStyle(ChatFormatting.GREEN));
                case "releasing" -> tooltip.add(Component.translatable("jade.neovitae.vas.releasing").withStyle(ChatFormatting.GOLD));
                case "seeding" -> tooltip.add(Component.translatable("jade.neovitae.vas.seeding").withStyle(ChatFormatting.LIGHT_PURPLE));
                case "idle" -> tooltip.add(Component.translatable("jade.neovitae.vas.idle").withStyle(ChatFormatting.DARK_GRAY));
                default -> { }
            }
        }
    }

    private static SpiritusType typeByName(String name) {
        for (SpiritusType candidate : SpiritusType.values()) {
            if (candidate.getSerializedName().equals(name)) {
                return candidate;
            }
        }
        return null;
    }

    private static void addSpiritusLine(ITooltip tooltip, SpiritusType type, double stored, double max) {
        Component typeName = Component.translatable("tooltip.neovitae.spiritus." + type.getSerializedName())
                .withColor(SpiritusTooltipHelper.spiritusColor(type));
        if (max > 0) {
            tooltip.add(Component.translatable("jade.neovitae.spiritus_stored", typeName, FORMAT.format(stored), FORMAT.format(max)));
        } else {
            tooltip.add(Component.translatable("jade.neovitae.spiritus_amount", typeName, FORMAT.format(stored)));
        }
    }

    @Override
    public Identifier getUid() {
        return NVBlockDataProvider.UID;
    }
}
