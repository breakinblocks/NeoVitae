package com.breakinblocks.neovitae.common.item;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * Tints a {@link MaterialItem} layer using the material's configured base color.
 */
@OnlyIn(Dist.CLIENT)
public record MaterialItemColor() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "material");
    public static final MapCodec<MaterialItemColor> MAP_CODEC = MapCodec.unit(new MaterialItemColor());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof MaterialItem materialItem) {
            return 0xFF000000 | materialItem.getMaterialColor();
        }
        return 0xFFFFFFFF;
    }

    @Override
    public MapCodec<MaterialItemColor> type() {
        return MAP_CODEC;
    }
}
