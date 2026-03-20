package com.breakinblocks.neovitae.datagen.content;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.function.Function;

public class NVDamageSourcesContent {
    public static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(NVDamageSources.SACRIFICE, new DamageType("sacrifice", DamageScaling.NEVER, 0F));
        context.register(NVDamageSources.SELF_SACRIFICE, new DamageType("self_sacrifice", DamageScaling.NEVER, 0F));
        context.register(NVDamageSources.RITUAL, new DamageType("ritual", DamageScaling.NEVER, 0F));
        context.register(NVDamageSources.SPIKES, new DamageType("spikes", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0F));
    }

    public static void tags(Function<TagKey<DamageType>, TagsProvider.TagAppender<DamageType>> setter) {
        setter.apply(DamageTypeTags.BYPASSES_ARMOR)
                .add(NVDamageSources.SELF_SACRIFICE)
                .add(NVDamageSources.SACRIFICE);

        setter.apply(DamageTypeTags.BYPASSES_EFFECTS)
                .add(NVDamageSources.SELF_SACRIFICE)
                .add(NVDamageSources.SACRIFICE);

        setter.apply(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(NVDamageSources.SELF_SACRIFICE)
                .add(NVDamageSources.SACRIFICE);

        setter.apply(DamageTypeTags.NO_IMPACT)
                .add(NVDamageSources.SELF_SACRIFICE)
                .add(NVDamageSources.SACRIFICE);

        setter.apply(DamageTypeTags.NO_KNOCKBACK)
                .add(NVDamageSources.SELF_SACRIFICE)
                .add(NVDamageSources.SACRIFICE);

        setter.apply(NVTags.DamageTypes.SELF_SACRIFICE)
                .add(NVDamageSources.SELF_SACRIFICE); // needed later for damage predicates

        setter.apply(NVTags.DamageTypes.TOUGH_IGNORED)
                .addTag(DamageTypeTags.IS_FIRE)
                .addTag(DamageTypeTags.IS_EXPLOSION)
                .addTag(DamageTypeTags.IS_FALL)
                .addTag(DamageTypeTags.IS_PROJECTILE);
    }
}
