package com.breakinblocks.neovitae.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.BMRegistries;
import com.breakinblocks.neovitae.common.tag.BMTags;

import java.util.*;
import java.util.function.Predicate;

/**
 * Blood Magic multiblock management.
 * Uses custom validation instead of Patchouli dependency.
 */
public class BMMultiblock {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BMMultiblock::onServerStarted);
        eventBus.addListener(BMMultiblock::onServerStopped);
    }

    public static ResourceLocation[] TIER_KEYS = new ResourceLocation[]{};
    public static AltarTier[] TIER_LIST = new AltarTier[]{};
    public static MultiblockValidator[] TIER_VALIDATORS = new MultiblockValidator[]{};

    public static void onServerStarted(ServerStartedEvent event) {
        NeoVitae.LOGGER.info("BMMultiblock.onServerStarted: Loading altar tier definitions...");

        List<Holder<AltarTier>> tierList = event.getServer().registryAccess()
                .registryOrThrow(BMRegistries.Keys.ALTAR_TIER_KEY)
                .getOrCreateTag(BMTags.Tiers.VALID_TIERS)
                .stream().toList();

        NeoVitae.LOGGER.info("BMMultiblock: Found {} altar tiers in VALID_TIERS tag", tierList.size());
        
        ResourceLocation[] keys = new ResourceLocation[tierList.size()];
        AltarTier[] tiers = new AltarTier[tierList.size()];
        MultiblockValidator[] validators = new MultiblockValidator[tierList.size()];

        for (Holder<AltarTier> holder : tierList) {
            int tier = holder.value().tier();
            keys[tier] = holder.getKey().location();
            tiers[tier] = holder.value();
            
            // Build the multiblock validator for this tier
            MultiblockValidator.Builder builder = MultiblockValidator.builder();
            Registry<Block> blockRegistry = event.getServer().registryAccess().registryOrThrow(Registries.BLOCK);
            
            for (AltarComponent component : tiers[tier].components()) {
                Predicate<BlockState> matcher = createMatcher(component, blockRegistry);
                builder.add(component.pos(), matcher);
            }
            
            validators[tier] = builder
                    .symmetrical(true)
                    .build();
        }
        
        TIER_KEYS = keys;
        TIER_LIST = tiers;
        TIER_VALIDATORS = validators;
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        TIER_KEYS = new ResourceLocation[]{};
        TIER_LIST = new AltarTier[]{};
        TIER_VALIDATORS = new MultiblockValidator[]{};
    }

    /**
     * Creates a block state matcher predicate for an altar component.
     */
    private static Predicate<BlockState> createMatcher(AltarComponent component, Registry<Block> blockRegistry) {
        if (component.material().tag()) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, component.material().id());
            
            // Special handling for pillars tag - allow any solid block if tag is empty
            if (component.material().id().equals(BMTags.Blocks.PILLARS.location())) {
                List<Block> tagBlocks = blockRegistry.getOrCreateTag(tag).stream()
                        .map(Holder::value).toList();
                if (tagBlocks.isEmpty()) {
                    return BlockState::canOcclude;
                }
            }
            
            return state -> state.is(tag);
        } else {
            Block block = blockRegistry.getOrThrow(ResourceKey.create(Registries.BLOCK, component.material().id()));
            return state -> state.is(block);
        }
    }

    /**
     * Gets display states for an altar component (for book visualization).
     */
    public static List<BlockState> getDisplayStates(AltarComponent component, RegistryAccess registries) {
        Registry<Block> blockRegistry = registries.registryOrThrow(Registries.BLOCK);
        List<BlockState> stateList = new ArrayList<>();
        
        if (component.material().tag()) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, component.material().id());
            blockRegistry.getOrCreateTag(tag).stream()
                    .map(Holder::value)
                    .map(Block::defaultBlockState)
                    .forEach(stateList::add);
            
            if (component.material().id().equals(BMTags.Blocks.PILLARS.location())) {
                if (stateList.isEmpty()) {
                    stateList.add(Blocks.STONE_BRICKS.defaultBlockState());
                }
            }
            
            if (component.material().id().equals(BMTags.Blocks.RUNES.location())) {
                if (component.isUpgrade()) {
                    stateList.remove(BMBlocks.RUNE_BLANK.block().get().defaultBlockState());
                } else {
                    stateList = List.of(BMBlocks.RUNE_BLANK.block().get().defaultBlockState());
                }
            }
        } else {
            Block block = blockRegistry.getOrThrow(ResourceKey.create(Registries.BLOCK, component.material().id()));
            stateList.add(block.defaultBlockState());
        }
        
        return stateList.isEmpty() ? List.of(Blocks.BEDROCK.defaultBlockState()) : stateList;
    }
}
