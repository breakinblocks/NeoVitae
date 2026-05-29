package com.breakinblocks.neovitae.datagen.content;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarEffect;
import com.breakinblocks.neovitae.common.registry.AltarEffectType;
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
 *
 * Each tier also carries an optional list of {@link AltarEffect}s naming the
 * visual style ({@link AltarEffectType}), the offsets it originates from, and
 * a tint colour. Pack authors can override the effects field in the same JSON
 * to relocate or restyle the per-tier capstone visuals without touching code.
 */
public class AltarTiers {

    public static void bootstrap(BootstrapContext<AltarTier> builder) {
        builder.register(Keys.WEAK, new AltarTier(0, WEAK, List.of()));
        builder.register(Keys.APPRENTICE, new AltarTier(1, APPRENTICE, APPRENTICE_FX));
        builder.register(Keys.MAGE, new AltarTier(2, MAGE, MAGE_FX));
        builder.register(Keys.MASTER, new AltarTier(3, MASTER, MASTER_FX));
        builder.register(Keys.ARCHMAGE, new AltarTier(4, ARCHMAGE, ARCHMAGE_FX));
        builder.register(Keys.TRANSCENDENT, new AltarTier(5, TRANSCENDENT, TRANSCENDENT_FX));
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

    // Cardinal cap positions per tier; these match the addCardinalCap calls below
    // and are reused as the origins for each tier's visual effect entries.
    private static final List<BlockPos> MAGE_CAPS = cardinals(4, 1);
    private static final List<BlockPos> MASTER_CAPS = corners(6, 2);
    private static final List<BlockPos> ARCHMAGE_CAPS = cardinals(9, -4);
    private static final List<BlockPos> TRANSCENDENT_CAPS = cardinals(12, 3);

    public static final List<AltarEffect> APPRENTICE_FX = List.of();
    public static final List<AltarEffect> MAGE_FX = List.of(
            new AltarEffect(AltarEffectType.CAP_ORBIT_LIFE_PULSE, MAGE_CAPS, 0xBB3300)
    );
    public static final List<AltarEffect> MASTER_FX = appendFx(MAGE_FX,
            new AltarEffect(AltarEffectType.CAP_ORBIT_SPIRAL_STAGGERED, MASTER_CAPS, 0x880011)
    );
    public static final List<AltarEffect> ARCHMAGE_FX = appendFx(MASTER_FX,
            new AltarEffect(AltarEffectType.CAP_BURST, ARCHMAGE_CAPS, 0x8800CC),
            new AltarEffect(AltarEffectType.CAP_RENDER_HOVER_ARRAY, ARCHMAGE_CAPS, 0xFFFFFF)
    );
    public static final List<AltarEffect> TRANSCENDENT_FX = appendFx(ARCHMAGE_FX,
            new AltarEffect(AltarEffectType.CAP_CRYSTAL_CASCADE, TRANSCENDENT_CAPS, 0x8800CC)
    );

    private static List<BlockPos> cardinals(int distance, int y) {
        return List.of(
                new BlockPos(distance, y, 0),
                new BlockPos(-distance, y, 0),
                new BlockPos(0, y, distance),
                new BlockPos(0, y, -distance)
        );
    }

    private static List<BlockPos> corners(int distance, int y) {
        return List.of(
                new BlockPos(distance, y, distance),
                new BlockPos(-distance, y, distance),
                new BlockPos(distance, y, -distance),
                new BlockPos(-distance, y, -distance)
        );
    }

    private static List<AltarEffect> appendFx(List<AltarEffect> base, AltarEffect... extras) {
        List<AltarEffect> out = new ArrayList<>(base);
        for (AltarEffect e : extras) out.add(e);
        return List.copyOf(out);
    }

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
        addCornerCap(out, 6, 2, T4_CAP);
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
                out.add(new AltarComponent(new BlockPos(dir[0], y, dir[1]), material, false, true));
            }
        }
    }

    private static void addCardinalCap(List<AltarComponent> out, int distance, int y, TagOrElementLocation material) {
        out.add(new AltarComponent(new BlockPos(distance, y, 0), material, false));
        out.add(new AltarComponent(new BlockPos(-distance, y, 0), material, false));
        out.add(new AltarComponent(new BlockPos(0, y, distance), material, false));
        out.add(new AltarComponent(new BlockPos(0, y, -distance), material, false));
    }

    private static void addCornerCap(List<AltarComponent> out, int distance, int y, TagOrElementLocation material) {
        out.add(new AltarComponent(new BlockPos(distance, y, distance), material, false));
        out.add(new AltarComponent(new BlockPos(-distance, y, distance), material, false));
        out.add(new AltarComponent(new BlockPos(distance, y, -distance), material, false));
        out.add(new AltarComponent(new BlockPos(-distance, y, -distance), material, false));
    }
}
