package com.breakinblocks.neovitae.common.damagesource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.NeoVitae;

public class BMDamageSources {
    // Mob sacrifice damage - used by soul network siphoning and Dagger of Sacrifice (when implemented)
    public static final ResourceKey<DamageType> SACRIFICE = key("sacrifice");
    // Self-sacrifice damage - used by Sacrificial Dagger for player self-sacrifice
    public static final ResourceKey<DamageType> SELF_SACRIFICE = key("self_sacrifice");
    // Ritual damage - used by rituals that deal damage to entities
    public static final ResourceKey<DamageType> RITUAL = key("ritual");
    // Spike damage - used by dungeon spike blocks
    public static final ResourceKey<DamageType> SPIKES = key("spikes");

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, NeoVitae.rl(path));
    }

    public static DamageSource spikes(Level level) {
        return level.damageSources().source(SPIKES);
    }
}
