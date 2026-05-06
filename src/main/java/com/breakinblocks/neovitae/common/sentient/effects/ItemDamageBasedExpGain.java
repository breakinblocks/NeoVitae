package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

public record ItemDamageBasedExpGain(Holder<SentientUpgrade> upgrade) implements SentientEntityEffect {
    public static final MapCodec<ItemDamageBasedExpGain> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(ItemDamageBasedExpGain::upgrade)
    ).apply(builder, ItemDamageBasedExpGain::new));

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        Player wearer = (Player) entity;
        ItemStack chestStack = SentientHelper.getChest(wearer);
        if (chestStack.has(NVDataComponents.PREVIOUS_DAMAGE)) {
            Integer prev = chestStack.get(NVDataComponents.PREVIOUS_DAMAGE);
            int delta = prev - chestStack.getDamageValue();
            if (delta > 0) {
                SentientHelper.applyExp(wearer, upgrade, delta);
            }
            if (delta != 0) {
                chestStack.set(NVDataComponents.PREVIOUS_DAMAGE, chestStack.getDamageValue());
            }
        } else {
            chestStack.set(NVDataComponents.PREVIOUS_DAMAGE, chestStack.getDamageValue());
        }
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }
}
