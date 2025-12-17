package com.breakinblocks.neovitae.compat.patchouli;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Registers multiblock structures with Patchouli for the guidebook.
 * Call during FMLCommonSetupEvent if Patchouli is loaded.
 */
public class RegisterPatchouliMultiblocks {
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;
    private int maxZ;
    private int minZ;

    public RegisterPatchouliMultiblocks() {
        IPatchouliAPI patAPI = PatchouliAPI.get();

        // Register Ritual multiblocks
        for (Ritual ritual : RitualRegistry.getAllRituals()) {
            ResourceLocation ritualId = RitualRegistry.getId(ritual);
            if (ritualId == null) continue;

            String ritualName = ritualId.getPath();
            Map<BlockPos, EnumRuneType> ritualMap = Maps.newHashMap();
            List<RitualComponent> components = Lists.newArrayList();
            ritual.gatherComponents(components::add);

            resetMinMaxValues();
            for (RitualComponent component : components) {
                ritualMap.put(component.offset(), component.runeType());
                checkAndSetMinMaxValues(component.getX(), component.getY(), component.getZ());
            }

            String[][] pattern = makePattern(ritualMap);

            // Manual Overrides (to add things like Chests)
            if (ritualName.equals("downgrade")) {
                if (pattern.length > 2 && pattern[2].length > 3) {
                    pattern[2][3] = "_FDC______";
                }
            }

            IMultiblock multiblock = patAPI.makeMultiblock(
                    pattern,
                    '0', BMBlocks.MASTER_RITUAL_STONE.block().get(),
                    'B', BMBlocks.BLANK_RITUAL_STONE.block().get(),
                    'W', BMBlocks.WATER_RITUAL_STONE.block().get(),
                    'F', BMBlocks.FIRE_RITUAL_STONE.block().get(),
                    'E', BMBlocks.EARTH_RITUAL_STONE.block().get(),
                    'A', BMBlocks.AIR_RITUAL_STONE.block().get(),
                    'D', BMBlocks.DUSK_RITUAL_STONE.block().get(),
                    'd', BMBlocks.DAWN_RITUAL_STONE.block().get(),
                    'C', Blocks.CHEST
            );

            patAPI.registerMultiblock(NeoVitae.rl(ritualName), multiblock);
        }

        // Register Blood Altar multiblocks
        registerAltarMultiblocks(patAPI);
    }

    /**
     * Registers static blood altar tier multiblocks.
     * Since altar tiers are data-driven in 1.21.1, we define standard patterns here.
     */
    private void registerAltarMultiblocks(IPatchouliAPI patAPI) {
        IStateMatcher anyRune = new RuneStateMatcher();
        IStateMatcher blankRune = patAPI.strictBlockMatcher(BMBlocks.RUNE_BLANK.block().get());
        IStateMatcher notAir = patAPI.predicateMatcher(Blocks.STONE_BRICKS, state -> !state.isAir() && state.getFluidState().isEmpty());
        IStateMatcher glowstone = patAPI.strictBlockMatcher(Blocks.GLOWSTONE);
        IStateMatcher bloodstone = new BloodstoneStateMatcher();
        IStateMatcher crystal = patAPI.strictBlockMatcher(BMBlocks.CRYSTAL_CLUSTER.block().get());

        // Tier 1: Just the altar
        IMultiblock tier1 = patAPI.makeMultiblock(
                new String[][]{{"0"}, {"_"}},
                '0', BMBlocks.BLOOD_ALTAR.block().get()
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_one"), tier1);

        // Tier 2: 8 runes in a 3x3 square around the altar at y=-1
        IMultiblock tier2 = patAPI.makeMultiblock(
                new String[][]{
                        {"___", "_0_", "___"},  // y=0: altar
                        {"RRR", "R_R", "RRR"}   // y=-1: 8 runes in square
                },
                '0', BMBlocks.BLOOD_ALTAR.block().get(),
                'R', anyRune
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_two"), tier2);

        // Tier 3: Builds on tier 2 + adds outer 7x7 ring at y=-2 + pillars with glowstone
        // '0' must be at y=0 (altar level) so tier 2 runes at y=-1 render correctly
        IMultiblock tier3 = patAPI.makeMultiblock(
                new String[][]{
                        {"G_____G", "_______", "_______", "_______", "_______", "_______", "G_____G"},  // y=1: glowstone caps
                        {"P_____P", "_______", "_______", "___0___", "_______", "_______", "P_____P"},  // y=0: altar + pillars
                        {"P_____P", "_______", "__RRR__", "__R_R__", "__RRR__", "_______", "P_____P"},  // y=-1: tier 2 inner runes + pillars
                        {"_RRRRR_", "R_____R", "R_____R", "R_____R", "R_____R", "R_____R", "_RRRRR_"}   // y=-2: tier 3 outer square
                },
                '0', BMBlocks.BLOOD_ALTAR.block().get(),
                'R', anyRune,
                'P', notAir,
                'G', glowstone
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_three"), tier3);

        // Tier 4: Builds on tier 3 + adds outer 11x11 ring at y=-3 + taller pillars with bloodstone
        IMultiblock tier4 = patAPI.makeMultiblock(
                new String[][]{
                        // y=2: bloodstone caps at outer corners (±5)
                        {"S_________S", "___________", "___________", "___________", "___________", "___________", "___________", "___________", "___________", "___________", "S_________S"},
                        // y=1: glowstone at inner corners (±3), pillars at outer (±5)
                        {"P_________P", "___G___G___", "___________", "___________", "___________", "___________", "___________", "___________", "___________", "___G___G___", "P_________P"},
                        // y=0: altar + pillars
                        {"P_________P", "___P___P___", "___________", "___________", "___________", "_____0_____", "___________", "___________", "___________", "___P___P___", "P_________P"},
                        // y=-1: tier 2 inner runes + pillars
                        {"P_________P", "___P___P___", "___________", "___________", "____RRR____", "____R_R____", "____RRR____", "___________", "___________", "___P___P___", "P_________P"},
                        // y=-2: tier 3 outer square (7x7 with corner gaps)
                        {"P_________P", "___________", "___RRRRR___", "__R_____R__", "__R_____R__", "__R_____R__", "__R_____R__", "__R_____R__", "___RRRRR___", "___________", "P_________P"},
                        // y=-3: tier 4 outer square (11x11 with corner gaps)
                        {"_RRRRRRRRR_", "R_________R", "R_________R", "R_________R", "R_________R", "R_________R", "R_________R", "R_________R", "R_________R", "R_________R", "_RRRRRRRRR_"}
                },
                '0', BMBlocks.BLOOD_ALTAR.block().get(),
                'R', anyRune,
                'P', notAir,
                'G', glowstone,
                'S', bloodstone
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_four"), tier4);

        // Tier 5: Full structure including tiers 1-4 + outer ring at y=-4 with hellforged blocks
        // 17x17 grid (positions -8 to +8): center at index 8
        // Tier 5 ring: hellforged at corners (±8,±8), runes from ±6 (13 per edge), gaps at ±7
        IStateMatcher hellforged = patAPI.strictBlockMatcher(BMBlocks.HELLFORGED_BLOCK.block().get());
        IMultiblock tier5 = patAPI.makeMultiblock(
                new String[][]{
                        // y=2: bloodstone caps at ±5 (indices 3 and 13)
                        {"_________________", "_________________", "_________________", "___S_________S___", "_________________", "_________________", "_________________", "_________________", "_________________", "_________________", "_________________", "_________________", "_________________", "___S_________S___", "_________________", "_________________", "_________________"},
                        // y=1: glowstone at ±3 (indices 5 and 11), pillars at ±5 (indices 3 and 13)
                        {"_________________", "_________________", "_________________", "___P_________P___", "_________________", "_____G_____G_____", "_________________", "_________________", "_________________", "_________________", "_________________", "_____G_____G_____", "_________________", "___P_________P___", "_________________", "_________________", "_________________"},
                        // y=0: altar + pillars at ±3 and ±5
                        {"_________________", "_________________", "_________________", "___P_________P___", "_________________", "_____P_____P_____", "_________________", "_________________", "________0________", "_________________", "_________________", "_____P_____P_____", "_________________", "___P_________P___", "_________________", "_________________", "_________________"},
                        // y=-1: tier 2 runes (3x3 at center) + pillars
                        {"_________________", "_________________", "_________________", "___P_________P___", "_________________", "_____P_____P_____", "_________________", "_______RRR_______", "_______R_R_______", "_______RRR_______", "_________________", "_____P_____P_____", "_________________", "___P_________P___", "_________________", "_________________", "_________________"},
                        // y=-2: tier 3 ring (7x7, ±3) + pillars at ±5
                        {"_________________", "_________________", "_________________", "___P_________P___", "_________________", "______RRRRR______", "_____R_____R_____", "_____R_____R_____", "_____R_____R_____", "_____R_____R_____", "_____R_____R_____", "______RRRRR______", "_________________", "___P_________P___", "_________________", "_________________", "_________________"},
                        // y=-3: tier 4 ring (11x11, ±5)
                        {"_________________", "_________________", "_________________", "____RRRRRRRRR____", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "___R_________R___", "____RRRRRRRRR____", "_________________", "_________________", "_________________"},
                        // y=-4: tier 5 ring (17x17, ±8) with hellforged at corners (±8,±8), gaps at ±7, runes from ±6
                        {"H_RRRRRRRRRRRRR_H", "_________________", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "R_______________R", "_________________", "H_RRRRRRRRRRRRR_H"}
                },
                '0', BMBlocks.BLOOD_ALTAR.block().get(),
                'R', anyRune,
                'P', notAir,
                'G', glowstone,
                'S', bloodstone,
                'H', hellforged
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_five"), tier5);

        // Tier 6: Full structure including tiers 1-5 + outer ring at y=-5 + pillars and capstones
        // 23x23 grid (positions -11 to +11): center at index 11
        // Inner structure positions: ±3 at 8/14, ±5 at 6/16, ±8 at 3/19, ±11 at 0/22
        // Tier 6 ring at y=-5: runes from ±9 (19 per edge), gaps at ±10, NO corners
        // Tier 6 pillars at ±11 from y=-4 to y=+2, capstones at y=+3
        IMultiblock tier6 = patAPI.makeMultiblock(
                new String[][]{
                        // y=+3: tier 6 capstones at ±11 (indices 0 and 22)
                        {"C_____________________C", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "C_____________________C"},
                        // y=+2: bloodstone caps at ±5 (indices 6 and 16) + tier 6 pillars at ±11 (indices 0 and 22)
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______S_________S______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______S_________S______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=+1: glowstone at ±3 (indices 8 and 14), pillars at ±5 (indices 6 and 16), tier 6 pillars at ±11
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______P_________P______", "_______________________", "________G_____G________", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "________G_____G________", "_______________________", "______P_________P______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=0: altar + pillars at ±3 and ±5, tier 6 pillars at ±11
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______P_________P______", "_______________________", "________P_____P________", "_______________________", "_______________________", "___________0___________", "_______________________", "_______________________", "________P_____P________", "_______________________", "______P_________P______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=-1: tier 2 runes (3x3 at center) + pillars, tier 6 pillars at ±11
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______P_________P______", "_______________________", "________P_____P________", "_______________________", "__________RRR__________", "__________R_R__________", "__________RRR__________", "_______________________", "________P_____P________", "_______________________", "______P_________P______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=-2: tier 3 ring (7x7, ±3) + pillars at ±5, tier 6 pillars at ±11
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "______P_________P______", "_______________________", "_________RRRRR_________", "________R_____R________", "________R_____R________", "________R_____R________", "________R_____R________", "________R_____R________", "_________RRRRR_________", "_______________________", "______P_________P______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=-3: tier 4 ring (11x11, ±5) + tier 6 pillars at ±11
                        {"P_____________________P", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "_______RRRRRRRRR_______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "______R_________R______", "_______RRRRRRRRR_______", "_______________________", "_______________________", "_______________________", "_______________________", "_______________________", "P_____________________P"},
                        // y=-4: tier 5 ring (17x17, ±8) with hellforged at corners (±8 = indices 3/19), tier 6 pillars at ±11 (corners only)
                        {"P_____________________P", "_______________________", "_______________________", "___H_RRRRRRRRRRRRR_H___", "_______________________", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "___R_______________R___", "_______________________", "___H_RRRRRRRRRRRRR_H___", "_______________________", "_______________________", "P_____________________P"},
                        // y=-5: tier 6 ring (23x23, ±11) with runes from ±9 (indices 2-20), gaps at ±10, NO corners
                        {"__RRRRRRRRRRRRRRRRRRR__", "_______________________", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "R_____________________R", "_______________________", "__RRRRRRRRRRRRRRRRRRR__"}
                },
                '0', BMBlocks.BLOOD_ALTAR.block().get(),
                'R', anyRune,
                'P', notAir,
                'G', glowstone,
                'S', bloodstone,
                'H', hellforged,
                'C', crystal
        );
        patAPI.registerMultiblock(NeoVitae.rl("altar_six"), tier6);
    }

    private void resetMinMaxValues() {
        maxX = minX = maxY = minY = maxZ = minZ = 0;
    }

    private void checkAndSetMinMaxValues(int x, int y, int z) {
        if (x > maxX) maxX = x;
        if (x < minX) minX = x;
        if (y > maxY) maxY = y;
        if (y < minY) minY = y;
        if (z > maxZ) maxZ = z;
        if (z < minZ) minZ = z;
    }

    private String[][] makePattern(Map<BlockPos, EnumRuneType> ritualMap) {
        String[][] pattern = new String[1 + maxY - minY][1 + maxX - minX];
        for (int y = maxY; y >= minY; y--) {
            for (int x = minX; x <= maxX; x++) {
                StringBuilder row = new StringBuilder();
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    EnumRuneType rune = ritualMap.get(pos);
                    if (rune != null) {
                        row.append(getRuneChar(rune));
                    } else {
                        row.append(checkEmptySpace(x, y, z));
                    }
                }
                pattern[maxY - y][x - minX] = row.toString();
            }
        }
        return pattern;
    }

    private char getRuneChar(EnumRuneType rune) {
        return switch (rune) {
            case BLANK -> 'B';
            case WATER -> 'W';
            case FIRE -> 'F';
            case EARTH -> 'E';
            case AIR -> 'A';
            case DUSK -> 'D';
            case DAWN -> 'd';
        };
    }

    private char checkEmptySpace(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            return '0'; // Center of Multiblock
        }
        return '_'; // Patchouli's "Any Block" Symbol
    }

    /**
     * State matcher that accepts any blood rune block.
     */
    private static class RuneStateMatcher implements IStateMatcher {
        private static final List<Block> RUNE_BLOCKS = List.of(
                BMBlocks.RUNE_BLANK.block().get(),
                BMBlocks.RUNE_SACRIFICE.block().get(),
                BMBlocks.RUNE_SELF_SACRIFICE.block().get(),
                BMBlocks.RUNE_CAPACITY.block().get(),
                BMBlocks.RUNE_CAPACITY_AUGMENTED.block().get(),
                BMBlocks.RUNE_CHARGING.block().get(),
                BMBlocks.RUNE_SPEED.block().get(),
                BMBlocks.RUNE_ACCELERATION.block().get(),
                BMBlocks.RUNE_DISLOCATION.block().get(),
                BMBlocks.RUNE_ORB.block().get(),
                BMBlocks.RUNE_EFFICIENCY.block().get(),
                BMBlocks.RUNE_2_SACRIFICE.block().get(),
                BMBlocks.RUNE_2_SELF_SACRIFICE.block().get(),
                BMBlocks.RUNE_2_CAPACITY.block().get(),
                BMBlocks.RUNE_2_CAPACITY_AUGMENTED.block().get(),
                BMBlocks.RUNE_2_CHARGING.block().get(),
                BMBlocks.RUNE_2_SPEED.block().get(),
                BMBlocks.RUNE_2_ACCELERATION.block().get(),
                BMBlocks.RUNE_2_DISLOCATION.block().get(),
                BMBlocks.RUNE_2_ORB.block().get(),
                BMBlocks.RUNE_2_EFFICIENCY.block().get()
        );

        @Override
        public BlockState getDisplayedState(long ticks) {
            int idx = (int) ((ticks / 20) % RUNE_BLOCKS.size());
            return RUNE_BLOCKS.get(idx).defaultBlockState();
        }

        @Override
        public vazkii.patchouli.api.TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
            return (world, pos, state) -> RUNE_BLOCKS.stream().anyMatch(state::is);
        }
    }

    /**
     * State matcher that accepts bloodstone blocks.
     */
    private static class BloodstoneStateMatcher implements IStateMatcher {
        private static final List<Block> BLOODSTONE_BLOCKS = List.of(
                BMBlocks.BLOODSTONE.block().get(),
                BMBlocks.BLOODSTONE_BRICK.block().get()
        );

        @Override
        public BlockState getDisplayedState(long ticks) {
            int idx = (int) ((ticks / 20) % BLOODSTONE_BLOCKS.size());
            return BLOODSTONE_BLOCKS.get(idx).defaultBlockState();
        }

        @Override
        public vazkii.patchouli.api.TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
            return (world, pos, state) -> BLOODSTONE_BLOCKS.stream().anyMatch(state::is);
        }
    }
}
