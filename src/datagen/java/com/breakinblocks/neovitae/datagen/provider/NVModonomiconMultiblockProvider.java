package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import com.klikli_dev.modonomicon.api.datagen.MultiblockProvider;
import net.minecraft.data.PackOutput;

/**
 * TODO(phase15 stage2): Multiblock datagen was built on SparseMultiblockBuilder which wrapped
 * pre-26.1 recipe-builder APIs that have been deleted. Stubbed for Stage 1 - committed JSON
 * under src/generated/resources/data/neovitae/modonomicon/ covers runtime.
 */
public class NVModonomiconMultiblockProvider extends MultiblockProvider {

    public NVModonomiconMultiblockProvider(PackOutput packOutput) {
        super(packOutput, NeoVitae.MODID);
    }

    @Override
    public void buildMultiblocks() {
        // Intentionally empty - see class-level TODO.
    }
}
