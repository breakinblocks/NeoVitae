package com.breakinblocks.neovitae.datagen.content;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs.TagOrElementLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Default tier layouts shipped with NeoVitae. The validator and Modonomicon
 * preview both load these from the {@code neovitae:altar_tier} datapack
 * registry, so pack authors can re-tune the geometry by dropping replacement
 * JSON. The classic square layout is preserved under
 * {@code examples/datapacks/neovitae_classic_altar/} as a reference template.
 */
public class AltarTiers {

    public static void bootstrap(BootstrapContext<AltarTier> builder) {
        builder.register(Keys.WEAK, new AltarTier(0, WEAK));
        builder.register(Keys.APPRENTICE, new AltarTier(1, APPRENTICE));
        builder.register(Keys.MAGE, new AltarTier(2, MAGE));
        builder.register(Keys.MASTER, new AltarTier(3, MASTER));
        builder.register(Keys.ARCHMAGE, new AltarTier(4, ARCHMAGE));
        builder.register(Keys.TRANSCENDENT, new AltarTier(5, TRANSCENDENT));
    }

    public static void tags(Function<TagKey<AltarTier>, TagAppender<ResourceKey<AltarTier>, AltarTier>> setter) {
        setter.apply(NVTags.Tiers.VALID_TIERS)
                .add(Keys.WEAK)
                .add(Keys.APPRENTICE)
                .add(Keys.MAGE)
                .add(Keys.MASTER)
                .add(Keys.ARCHMAGE)
                .add(Keys.TRANSCENDENT);
    }

    protected static Identifier bm(String path) {
        return Identifier.fromNamespaceAndPath(NeoVitae.MODID, path);
    }

    public static class Keys {
        public static final ResourceKey<AltarTier> WEAK = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.WEAK);
        public static final ResourceKey<AltarTier> APPRENTICE = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.APPRENTICE);
        public static final ResourceKey<AltarTier> MAGE = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.MAGE);
        public static final ResourceKey<AltarTier> MASTER = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.MASTER);
        public static final ResourceKey<AltarTier> ARCHMAGE = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.ARCHMAGE);
        public static final ResourceKey<AltarTier> TRANSCENDENT = ResourceKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, Locs.TRANSCENDENT);
    }

    public static class Locs {
        public static final Identifier WEAK = bm("weak");
        public static final Identifier APPRENTICE = bm("apprentice");
        public static final Identifier MAGE = bm("mage");
        public static final Identifier MASTER = bm("master");
        public static final Identifier ARCHMAGE = bm("archmage");
        public static final Identifier TRANSCENDENT = bm("transcendent");
    }

    private static final TagOrElementLocation ALTAR = new TagOrElementLocation(NVBlocks.ARA_VITAE.block().getId(), false);
    private static final TagOrElementLocation PILLAR = new TagOrElementLocation(NVTags.Blocks.PILLARS.location(), true);
    private static final TagOrElementLocation RUNE = new TagOrElementLocation(NVTags.Blocks.RUNES.location(), true);
    private static final TagOrElementLocation T3_CAP = new TagOrElementLocation(NVTags.Blocks.T3_CAPSTONES.location(), true);
    private static final TagOrElementLocation T4_CAP = new TagOrElementLocation(NVTags.Blocks.T4_CAPSTONES.location(), true);
    private static final TagOrElementLocation T5_CAP = new TagOrElementLocation(NVTags.Blocks.T5_CAPSTONES.location(), true);
    private static final TagOrElementLocation T6_CAP = new TagOrElementLocation(NVTags.Blocks.T6_CAPSTONES.location(), true);

    public static final List<AltarComponent> WEAK = List.of(new AltarComponent(new BlockPos(0, 0, 0), ALTAR, false));

    public static final List<AltarComponent> APPRENTICE = buildApprentice();
    public static final List<AltarComponent> MAGE = buildMage();
    public static final List<AltarComponent> MASTER = buildMaster();
    public static final List<AltarComponent> ARCHMAGE = buildArchmage();
    public static final List<AltarComponent> TRANSCENDENT = buildTranscendent();

    private static List<AltarComponent> buildApprentice() {
        List<AltarComponent> out = new ArrayList<>(WEAK);
        out.add(new AltarComponent(new BlockPos(1, -1, 0), RUNE, true));
        out.add(new AltarComponent(new BlockPos(0, -1, 1), RUNE, true));
        out.add(new AltarComponent(new BlockPos(-1, -1, 0), RUNE, true));
        out.add(new AltarComponent(new BlockPos(0, -1, -1), RUNE, true));
        out.add(new AltarComponent(new BlockPos(1, -1, 1), RUNE, false));
        out.add(new AltarComponent(new BlockPos(1, -1, -1), RUNE, false));
        out.add(new AltarComponent(new BlockPos(-1, -1, 1), RUNE, false));
        out.add(new AltarComponent(new BlockPos(-1, -1, -1), RUNE, false));
        return out;
    }

    private static List<AltarComponent> buildMage() {
        List<AltarComponent> out = new ArrayList<>(WEAK);
        addRing(out, 1, -1, RUNE, true);
        addRing(out, 3, -2, RUNE, true);
        addCardinalPillars(out, 4, -1, 0, PILLAR);
        addCardinalCap(out, 4, 1, T3_CAP);
        return out;
    }

    private static List<AltarComponent> buildMaster() {
        List<AltarComponent> out = new ArrayList<>(MAGE);
        addRing(out, 5, -3, RUNE, true);
        addCardinalPillars(out, 6, -2, 1, PILLAR);
        addCardinalCap(out, 6, 2, T4_CAP);
        return out;
    }

    private static List<AltarComponent> buildArchmage() {
        List<AltarComponent> out = new ArrayList<>(MASTER);
        addRing(out, 8, -4, RUNE, true);
        addCardinalCap(out, 9, -4, T5_CAP);
        return out;
    }

    private static List<AltarComponent> buildTranscendent() {
        List<AltarComponent> out = new ArrayList<>(ARCHMAGE);
        addRing(out, 11, -5, RUNE, true);
        addCardinalPillars(out, 12, -4, 2, PILLAR);
        addCardinalCap(out, 12, 3, T6_CAP);
        return out;
    }

    private static void addRing(List<AltarComponent> out, int radius, int y, TagOrElementLocation material, boolean isUpgrade) {
        int rSq = radius * radius;
        int lo = rSq - radius + 1;
        int hi = rSq + radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                int d = x * x + z * z;
                if (d >= lo && d <= hi) {
                    out.add(new AltarComponent(new BlockPos(x, y, z), material, isUpgrade));
                }
            }
        }
    }

    private static void addCardinalPillars(List<AltarComponent> out, int distance, int yLo, int yHi, TagOrElementLocation material) {
        int[][] dirs = {{distance, 0}, {-distance, 0}, {0, distance}, {0, -distance}};
        for (int[] dir : dirs) {
            for (int y = yLo; y <= yHi; y++) {
                out.add(new AltarComponent(new BlockPos(dir[0], y, dir[1]), material, false));
            }
        }
    }

    private static void addCardinalCap(List<AltarComponent> out, int distance, int y, TagOrElementLocation material) {
        out.add(new AltarComponent(new BlockPos(distance, y, 0), material, false));
        out.add(new AltarComponent(new BlockPos(-distance, y, 0), material, false));
        out.add(new AltarComponent(new BlockPos(0, y, distance), material, false));
        out.add(new AltarComponent(new BlockPos(0, y, -distance), material, false));
    }
}
