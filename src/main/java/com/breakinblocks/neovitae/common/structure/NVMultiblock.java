package com.breakinblocks.neovitae.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.*;
import java.util.function.Predicate;
import java.util.ArrayList;

/**
 * NeoVitae multiblock management.
 * Handles Ara Vitae tier validation and structure scanning.
 */
public class NVMultiblock {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(NVMultiblock::onServerStarted);
        eventBus.addListener(NVMultiblock::onServerStopped);
    }

    public static Identifier[] TIER_KEYS = new Identifier[]{};
    public static AltarTier[] TIER_LIST = new AltarTier[]{};
    public static MultiblockValidator[] TIER_VALIDATORS = new MultiblockValidator[]{};

    public static void onServerStarted(ServerStartedEvent event) {
        NeoVitae.LOGGER.info("NVMultiblock.onServerStarted: Loading altar tier definitions...");

        Iterable<Holder<AltarTier>> tagHolders = event.getServer().registryAccess()
                .lookupOrThrow(NVRegistries.Keys.ALTAR_TIER_KEY)
                .getTagOrEmpty(NVTags.Tiers.VALID_TIERS);
        List<Holder<AltarTier>> tierList = new ArrayList<>();
        tagHolders.forEach(tierList::add);

        NeoVitae.LOGGER.info("NVMultiblock: Found {} altar tiers in VALID_TIERS tag", tierList.size());
        
        Identifier[] keys = new Identifier[tierList.size()];
        AltarTier[] tiers = new AltarTier[tierList.size()];
        MultiblockValidator[] validators = new MultiblockValidator[tierList.size()];

        for (Holder<AltarTier> holder : tierList) {
            int tier = holder.value().tier();
            keys[tier] = holder.getKey().identifier();
            tiers[tier] = holder.value();
            
            MultiblockValidator.Builder builder = MultiblockValidator.builder();
            Registry<Block> blockRegistry = event.getServer().registryAccess().lookupOrThrow(Registries.BLOCK);
            
            for (AltarComponent component : tiers[tier].components()) {
                Predicate<BlockState> matcher = createMatcher(component, blockRegistry);
                if (component.optional()) {
                    Predicate<BlockState> required = matcher;
                    matcher = state -> state.isAir() || !state.getFluidState().isEmpty() || !state.canOcclude() || required.test(state);
                }
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
        TIER_KEYS = new Identifier[]{};
        TIER_LIST = new AltarTier[]{};
        TIER_VALIDATORS = new MultiblockValidator[]{};
    }

    private static Predicate<BlockState> createMatcher(AltarComponent component, Registry<Block> blockRegistry) {
        if (component.material().tag()) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, component.material().id());

            if (component.material().id().equals(NVTags.Blocks.PILLARS.location())) {
                List<Block> tagBlocks = new ArrayList<>();
                blockRegistry.getTagOrEmpty(tag).forEach(h -> tagBlocks.add(h.value()));
                if (tagBlocks.isEmpty()) {
                    return BlockState::canOcclude;
                }
            }

            return state -> state.is(tag);
        } else {
            Block block = blockRegistry.getValueOrThrow(ResourceKey.create(Registries.BLOCK, component.material().id()));
            return state -> state.is(block);
        }
    }

    public static List<BlockState> getDisplayStates(AltarComponent component, RegistryAccess registries) {
        Registry<Block> blockRegistry = registries.lookupOrThrow(Registries.BLOCK);
        List<BlockState> stateList = new ArrayList<>();
        
        if (component.material().tag()) {
            TagKey<Block> tag = TagKey.create(Registries.BLOCK, component.material().id());
            for (Holder<Block> h : blockRegistry.getTagOrEmpty(tag)) {
                stateList.add(h.value().defaultBlockState());
            }

            if (component.material().id().equals(NVTags.Blocks.PILLARS.location())) {
                if (stateList.isEmpty()) {
                    stateList.add(Blocks.STONE_BRICKS.defaultBlockState());
                }
            }

            if (component.material().id().equals(NVTags.Blocks.RUNES.location())) {
                if (component.isUpgrade()) {
                    stateList.remove(NVBlocks.RUNE_BLANK.block().get().defaultBlockState());
                } else {
                    stateList = List.of(NVBlocks.RUNE_BLANK.block().get().defaultBlockState());
                }
            }
        } else {
            Block block = blockRegistry.getValueOrThrow(ResourceKey.create(Registries.BLOCK, component.material().id()));
            stateList.add(block.defaultBlockState());
        }
        
        return stateList.isEmpty() ? List.of(Blocks.BEDROCK.defaultBlockState()) : stateList;
    }
}
