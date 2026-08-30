package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.concurrent.CompletableFuture;

public class NVEntityTagProvider extends EntityTypeTagsProvider {

    public NVEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, NeoVitae.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(NVTags.Entities.TELEPOSE_BLACKLIST);

        tag(NVTags.Entities.LOYAL_FRIENDS_BLACKLIST);

        tag(NVTags.Entities.RITUAL_BOSS_BLACKLIST)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.WARDEN);

        tag(NVTags.Entities.NO_SENTIENT_TRAINING)
                .addOptional(ResourceLocation.fromNamespaceAndPath("dummmmmmy", "target_dummy"));

        tag(NVTags.Entities.DENY_IMPRISONMENT)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.WARDEN);
    }
}
