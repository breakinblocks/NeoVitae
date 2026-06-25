package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.concurrent.CompletableFuture;

public class NVEntityTagProvider extends EntityTypeTagsProvider {

    public NVEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, NeoVitae.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(NVTags.Entities.TELEPOSE_BLACKLIST);

        tag(NVTags.Entities.RITUAL_BOSS_BLACKLIST)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.WARDEN);

        getOrCreateRawBuilder(NVTags.Entities.NO_SENTIENT_TRAINING)
                .addOptionalElement(Identifier.fromNamespaceAndPath("dummmmmmy", "target_dummy"));

        tag(NVTags.Entities.DENY_IMPRISONMENT)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.WARDEN);
    }
}
