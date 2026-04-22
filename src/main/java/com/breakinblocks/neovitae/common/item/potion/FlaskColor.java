package com.breakinblocks.neovitae.common.item.potion;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tints the flask texture based on its effects. Registered via
 * {@code RegisterColorHandlersEvent.ItemTintSources}. Each JSON tint entry declares its
 * target layer (0 = bottom, 1 = top) so a two-layer flask model needs two tint entries.
 */
public record FlaskColor(int layer) implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "flask");
    public static final int DEFAULT_COLOR = 0xFF385DC6;

    public static final MapCodec<FlaskColor> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("layer").forGetter(FlaskColor::layer)
    ).apply(inst, FlaskColor::new));

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        FlaskEffects effects = stack.get(NVDataComponents.FLASK_EFFECTS.get());
        if (effects == null || effects.effects().isEmpty()) {
            return DEFAULT_COLOR;
        }
        List<EffectHolder> holders = effects.effects();
        int holderIndex = holders.size() >= 2 && layer == 1 ? 1 : 0;
        return 0xFF000000 | holders.get(holderIndex).effect().value().getColor();
    }

    @Override
    public MapCodec<FlaskColor> type() {
        return MAP_CODEC;
    }
}
