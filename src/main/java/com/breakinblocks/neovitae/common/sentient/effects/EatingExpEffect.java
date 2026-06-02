package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.Map;

public record EatingExpEffect(Holder<SentientUpgrade> upgrade) implements SentientEntityEffect {
    public static final MapCodec<EatingExpEffect> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(EatingExpEffect::upgrade)
    ).apply(builder, EatingExpEffect::new));

    private static final ResourceLocation FOOD = ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "food");

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        Player wearer = (Player) entity;
        int current = wearer.getFoodData().getFoodLevel();

        Map<ResourceLocation, Double> data = wearer.getData(NVDataAttachments.SENTIENT_ADDITIONAL);
        double previous = data.getOrDefault(FOOD, (double) current);
        data.put(FOOD, (double) current);
        wearer.setData(NVDataAttachments.SENTIENT_ADDITIONAL, data);

        double gained = current - previous;
        if (gained > 0) {
            SentientHelper.applyExp(wearer, upgrade, (float) gained);
        }
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }
}
