package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
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
        // Altar multiblocks are derived at server start from the AltarTier
        // datapack registry by NVAltarBookSync, so datapacks that retune the
        // tier layout get a matching in-book preview without authoring a
        // parallel Modonomicon multiblock JSON.
        buildRitualMultiblocks();
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
