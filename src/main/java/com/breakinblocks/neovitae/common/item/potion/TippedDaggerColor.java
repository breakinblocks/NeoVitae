package com.breakinblocks.neovitae.common.item.potion;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

/**
 * Tints the potion-overlay layer of a tipped throwing dagger with the stored potion color.
 * Registered via {@code RegisterColorHandlersEvent.ItemTintSources}; the dagger's
 * {@code assets/neovitae/items/throwing_dagger_tipped.json} must declare this tint against
 * its overlay layer.
 */
public record TippedDaggerColor() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "tipped_dagger");
    public static final int DEFAULT_POTION_COLOR = 0xFF385DC6;
    public static final MapCodec<TippedDaggerColor> MAP_CODEC = MapCodec.unit(new TippedDaggerColor());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null && contents.hasEffects()) {
            return 0xFF000000 | contents.getColor();
        }
        return DEFAULT_POTION_COLOR;
    }

    @Override
    public MapCodec<TippedDaggerColor> type() {
        return MAP_CODEC;
    }
}
