package com.breakinblocks.neovitae.common.sentient.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.sentient.SentientEntityEffect;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

import java.util.HashMap;
import java.util.Map;

public record DistanceExpGain(Holder<SentientUpgrade> upgrade, Movement movement) implements SentientEntityEffect {
    public static final MapCodec<DistanceExpGain> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            SentientUpgrade.HOLDER_CODEC.fieldOf("upgrade").forGetter(DistanceExpGain::upgrade),
            Movement.CODEC.fieldOf("movement").forGetter(DistanceExpGain::movement)
    ).apply(builder, DistanceExpGain::new));


    private static final Identifier X = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "x");
    private static final Identifier Y = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "y");
    private static final Identifier Z = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "z");

    @Override
    public void apply(int upgradeLevel, Entity entity) {
        Player wearer = (Player) entity;

        double x = wearer.getX();
        double y = wearer.getY();
        double z = wearer.getZ();

        Map<Identifier, Double> data = wearer.getData(NVDataAttachments.SENTIENT_ADDITIONAL);
        double oldX = data.getOrDefault(X, x);
        double oldY = data.getOrDefault(Y, y);
        double oldZ = data.getOrDefault(Z, z);

        data.put(X, x);
        data.put(Y, y);
        data.put(Z, z);

        wearer.setData(NVDataAttachments.SENTIENT_ADDITIONAL, data);

        double amount = switch (movement) {
            case HORIZONTAL -> Math.sqrt(Math.abs(x - oldX) * Math.abs(x - oldX) + Math.abs(z - oldZ) * Math.abs(z - oldZ));
            case VERTICAL -> y - oldY;
        };

        if (amount > 0 && amount < 50) {
            SentientHelper.applyExp(wearer, upgrade, (float) amount);
        }
    }

    @Override
    public MapCodec<? extends SentientEntityEffect> codec() {
        return CODEC;
    }

    public enum Movement {
        HORIZONTAL,
        VERTICAL;
        /*
        X,
        Y,
        Z,
        ALL
         */

        public static final Codec<Movement> CODEC = Codec.stringResolver(Movement::toString, Movement::valueOf);
    }
}
