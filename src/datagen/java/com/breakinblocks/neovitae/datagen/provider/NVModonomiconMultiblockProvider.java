package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.datagen.builder.SparseMultiblockBuilder;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class NVModonomiconMultiblockProvider extends MultiblockProvider {

    public NVModonomiconMultiblockProvider(PackOutput packOutput) {
        super(packOutput, NeoVitae.MODID);
    }

    @Override
    public void buildMultiblocks() {
        buildAltarMultiblocks();
        buildRitualMultiblocks();
    }

    private void buildAltarMultiblocks() {
        // Tier 1: Just the altar (padded to 3x1x3 so Modonomicon doesn't over-scale)
        this.add(this.modLoc("altar_one"), new DenseMultiblockBuilder()
                .layer(
                        "___",
                        "_0_",
                        "___"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .build(false)
        );

        // Tier 2: Altar + 8 runes below
        this.add(this.modLoc("altar_two"), new DenseMultiblockBuilder()
                .layer(
                        "___",
                        "_0_",
                        "___"
                )
                .layer(
                        "RRR",
                        "R_R",
                        "RRR"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .tag('R', NVTags.Blocks.RUNES, () -> NVBlocks.RUNE_BLANK.block().get())
                .build(false)
        );

        // Tier 3: Tier 2 + outer 7x7 ring at y=-2 + pillars with glowstone caps
        this.add(this.modLoc("altar_three"), new DenseMultiblockBuilder()
                .layer( // y=+1: glowstone caps
                        "G_____G",
                        "_______",
                        "_______",
                        "_______",
                        "_______",
                        "_______",
                        "G_____G"
                )
                .layer( // y=0: altar + pillars
                        "P_____P",
                        "_______",
                        "_______",
                        "___0___",
                        "_______",
                        "_______",
                        "P_____P"
                )
                .layer( // y=-1: tier 2 inner runes + pillars
                        "P_____P",
                        "_______",
                        "__RRR__",
                        "__R_R__",
                        "__RRR__",
                        "_______",
                        "P_____P"
                )
                .layer( // y=-2: tier 3 outer square
                        "_RRRRR_",
                        "R_____R",
                        "R_____R",
                        "R_____R",
                        "R_____R",
                        "R_____R",
                        "_RRRRR_"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .tag('R', NVTags.Blocks.RUNES, () -> NVBlocks.RUNE_BLANK.block().get())
                .block('G', () -> Blocks.GLOWSTONE)
                .predicate('P', NeoVitae.rl("non_air_solid"), true, () -> Blocks.STONE_BRICKS)
                .build(false)
        );

        // Tier 4: Tier 3 + outer 11x11 ring at y=-3 + taller pillars with bloodstone caps
        this.add(this.modLoc("altar_four"), new DenseMultiblockBuilder()
                .layer( // y=+2: bloodstone caps at outer corners
                        "S_________S",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "S_________S"
                )
                .layer( // y=+1: glowstone at inner corners, pillars at outer
                        "P_________P",
                        "___G___G___",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___________",
                        "___G___G___",
                        "P_________P"
                )
                .layer( // y=0: altar + pillars
                        "P_________P",
                        "___P___P___",
                        "___________",
                        "___________",
                        "___________",
                        "_____0_____",
                        "___________",
                        "___________",
                        "___________",
                        "___P___P___",
                        "P_________P"
                )
                .layer( // y=-1: tier 2 inner runes + pillars
                        "P_________P",
                        "___P___P___",
                        "___________",
                        "___________",
                        "____RRR____",
                        "____R_R____",
                        "____RRR____",
                        "___________",
                        "___________",
                        "___P___P___",
                        "P_________P"
                )
                .layer( // y=-2: tier 3 outer square
                        "P_________P",
                        "___________",
                        "___RRRRR___",
                        "__R_____R__",
                        "__R_____R__",
                        "__R_____R__",
                        "__R_____R__",
                        "__R_____R__",
                        "___RRRRR___",
                        "___________",
                        "P_________P"
                )
                .layer( // y=-3: tier 4 outer square
                        "_RRRRRRRRR_",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "R_________R",
                        "_RRRRRRRRR_"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .tag('R', NVTags.Blocks.RUNES, () -> NVBlocks.RUNE_BLANK.block().get())
                .block('G', () -> Blocks.GLOWSTONE)
                .tag('S', NVTags.Blocks.BLOODSTONES, () -> NVBlocks.BLOODSTONE.block().get())
                .predicate('P', NeoVitae.rl("non_air_solid"), true, () -> Blocks.STONE_BRICKS)
                .build(false)
        );

        // Tier 5: Tier 4 + outer 17x17 ring at y=-4 with hellforged at corners
        this.add(this.modLoc("altar_five"), new DenseMultiblockBuilder()
                .layer( // y=+2: bloodstone caps at ±5
                        "_________________",
                        "_________________",
                        "_________________",
                        "___S_________S___",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "___S_________S___",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=+1: glowstone at ±3, pillars at ±5
                        "_________________",
                        "_________________",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_____G_____G_____",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_________________",
                        "_____G_____G_____",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=0: altar + pillars at ±3 and ±5
                        "_________________",
                        "_________________",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_____P_____P_____",
                        "_________________",
                        "_________________",
                        "________0________",
                        "_________________",
                        "_________________",
                        "_____P_____P_____",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=-1: tier 2 runes + pillars
                        "_________________",
                        "_________________",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_____P_____P_____",
                        "_________________",
                        "_______RRR_______",
                        "_______R_R_______",
                        "_______RRR_______",
                        "_________________",
                        "_____P_____P_____",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=-2: tier 3 ring + pillars at ±5
                        "_________________",
                        "_________________",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "______RRRRR______",
                        "_____R_____R_____",
                        "_____R_____R_____",
                        "_____R_____R_____",
                        "_____R_____R_____",
                        "_____R_____R_____",
                        "______RRRRR______",
                        "_________________",
                        "___P_________P___",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=-3: tier 4 ring
                        "_________________",
                        "_________________",
                        "_________________",
                        "____RRRRRRRRR____",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "___R_________R___",
                        "____RRRRRRRRR____",
                        "_________________",
                        "_________________",
                        "_________________"
                )
                .layer( // y=-4: tier 5 ring with hellforged at corners
                        "H_RRRRRRRRRRRRR_H",
                        "_________________",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "R_______________R",
                        "_________________",
                        "H_RRRRRRRRRRRRR_H"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .tag('R', NVTags.Blocks.RUNES, () -> NVBlocks.RUNE_BLANK.block().get())
                .block('G', () -> Blocks.GLOWSTONE)
                .tag('S', NVTags.Blocks.BLOODSTONES, () -> NVBlocks.BLOODSTONE.block().get())
                .block('H', () -> NVBlocks.HELLFORGED_BLOCK.block().get())
                .predicate('P', NeoVitae.rl("non_air_solid"), true, () -> Blocks.STONE_BRICKS)
                .build(false)
        );

        // Tier 6: Tier 5 + outer 23x23 ring at y=-5 + tier 6 pillars + crystal capstones
        this.add(this.modLoc("altar_six"), new DenseMultiblockBuilder()
                .layer( // y=+3: crystal capstones at ±11
                        "C_____________________C",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "C_____________________C"
                )
                .layer( // y=+2: bloodstone caps at ±5 + tier 6 pillars at ±11
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______S_________S______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______S_________S______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=+1: glowstone at ±3, pillars at ±5, tier 6 pillars at ±11
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "________G_____G________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "________G_____G________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=0: altar + pillars
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "________P_____P________",
                        "_______________________",
                        "_______________________",
                        "___________0___________",
                        "_______________________",
                        "_______________________",
                        "________P_____P________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=-1: tier 2 runes + pillars
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "________P_____P________",
                        "_______________________",
                        "__________RRR__________",
                        "__________R_R__________",
                        "__________RRR__________",
                        "_______________________",
                        "________P_____P________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=-2: tier 3 ring + pillars
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "_________RRRRR_________",
                        "________R_____R________",
                        "________R_____R________",
                        "________R_____R________",
                        "________R_____R________",
                        "________R_____R________",
                        "_________RRRRR_________",
                        "_______________________",
                        "______P_________P______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=-3: tier 4 ring + tier 6 pillars
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______RRRRRRRRR_______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "______R_________R______",
                        "_______RRRRRRRRR_______",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=-4: tier 5 ring with hellforged + tier 6 pillars
                        "P_____________________P",
                        "_______________________",
                        "_______________________",
                        "___H_RRRRRRRRRRRRR_H___",
                        "_______________________",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "___R_______________R___",
                        "_______________________",
                        "___H_RRRRRRRRRRRRR_H___",
                        "_______________________",
                        "_______________________",
                        "P_____________________P"
                )
                .layer( // y=-5: tier 6 ring
                        "__RRRRRRRRRRRRRRRRRRR__",
                        "_______________________",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "R_____________________R",
                        "_______________________",
                        "__RRRRRRRRRRRRRRRRRRR__"
                )
                .block('0', () -> NVBlocks.BLOOD_ALTAR.block().get())
                .tag('R', NVTags.Blocks.RUNES, () -> NVBlocks.RUNE_BLANK.block().get())
                .block('G', () -> Blocks.GLOWSTONE)
                .tag('S', NVTags.Blocks.BLOODSTONES, () -> NVBlocks.BLOODSTONE.block().get())
                .block('H', () -> NVBlocks.HELLFORGED_BLOCK.block().get())
                .block('C', () -> NVBlocks.CRYSTAL_CLUSTER.block().get())
                .predicate('P', NeoVitae.rl("non_air_solid"), true, () -> Blocks.STONE_BRICKS)
                .build(false)
        );
    }

    private void buildRitualMultiblocks() {
        for (Ritual ritual : RitualRegistry.getAllRituals()) {
            ResourceLocation ritualId = RitualRegistry.getId(ritual);
            if (ritualId == null) continue;

            List<RitualComponent> components = new ArrayList<>();
            ritual.gatherComponents(components::add);

            if (components.isEmpty()) continue;

            SparseMultiblockBuilder builder = new SparseMultiblockBuilder();

            // Add master ritual stone at origin
            builder.at('0', 0, 0, 0);
            builder.block('0', () -> NVBlocks.MASTER_RITUAL_STONE.block().get());

            // Add invisible padding above and below to give height for rotation scaling
            builder.at('_', 0, 3, 0);
            builder.at('_', 0, -1, 0);
            builder.air('_');

            for (RitualComponent comp : components) {
                char runeChar = getRuneChar(comp.runeType());
                builder.at(runeChar, comp.offset().getX(), comp.offset().getY(), comp.offset().getZ());
            }

            // Map rune chars to their specific ritual stone blocks
            mapRitualStoneMatchers(builder);

            // Special case: downgrade ritual has a chest
            if (ritualId.getPath().equals("downgrade")) {
                addDowngradeChest(builder, components);
            }

            this.add(this.modLoc("ritual/" + ritualId.getPath()), builder.build());
        }
    }

    private void mapRitualStoneMatchers(SparseMultiblockBuilder builder) {
        builder.block('B', () -> NVBlocks.BLANK_RITUAL_STONE.block().get());
        builder.block('W', () -> NVBlocks.WATER_RITUAL_STONE.block().get());
        builder.block('F', () -> NVBlocks.FIRE_RITUAL_STONE.block().get());
        builder.block('E', () -> NVBlocks.EARTH_RITUAL_STONE.block().get());
        builder.block('A', () -> NVBlocks.AIR_RITUAL_STONE.block().get());
        builder.block('D', () -> NVBlocks.DUSK_RITUAL_STONE.block().get());
        builder.block('d', () -> NVBlocks.DAWN_RITUAL_STONE.block().get());
    }

    private void addDowngradeChest(SparseMultiblockBuilder builder, List<RitualComponent> components) {
        // The downgrade ritual expects a chest at a specific position
        // In the Patchouli version this was manually injected into the pattern
        // Find the appropriate position (typically offset from master stone)
        builder.block('X', () -> Blocks.CHEST);
    }

    private static char getRuneChar(EnumRuneType rune) {
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
}
