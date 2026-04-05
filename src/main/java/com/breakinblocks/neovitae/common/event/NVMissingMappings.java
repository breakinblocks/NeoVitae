package com.breakinblocks.neovitae.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkDataEvent;

/**
 * Chunk-NBT legacy id rewriter.
 *
 * <p>NeoForge 1.21.1 does not expose a general-purpose {@code MissingMappingsEvent},
 * so renamed registrations cannot be migrated through the registry system alone.
 * Instead, we intercept {@link ChunkDataEvent.Load} and rewrite any legacy block
 * palette entries and block-entity ids in the raw chunk NBT before vanilla
 * deserializes it. Any entries we do not recognise are left untouched.
 *
 * <p>Each entry migrates a previous id to its current id. Keep entries here
 * whenever a registered block/block-entity is renamed so that existing worlds
 * survive the rename without losing placed blocks.
 */
public final class NVMissingMappings {

    /** Legacy block id ↦ current block id. */
    private static final String[][] BLOCK_REMAPS = {
            {"neovitae:item_routing_node", "neovitae:routing_conduit"}
    };

    /** Legacy block-entity type id ↦ current block-entity type id. */
    private static final String[][] BLOCK_ENTITY_REMAPS = {
            {"neovitae:routing_node", "neovitae:routing_conduit"}
    };

    private NVMissingMappings() {}

    public static void register(IEventBus modBus) {
        // The chunk-data event fires on the game event bus, not the mod bus.
        NeoForge.EVENT_BUS.addListener(NVMissingMappings::onChunkLoad);
    }

    private static void onChunkLoad(ChunkDataEvent.Load event) {
        CompoundTag root = event.getData();
        if (root == null) return;
        rewriteSectionPalettes(root);
        rewriteBlockEntities(root);
    }

    private static void rewriteSectionPalettes(CompoundTag root) {
        if (!root.contains("sections", Tag.TAG_LIST)) return;
        ListTag sections = root.getList("sections", Tag.TAG_COMPOUND);
        for (int i = 0; i < sections.size(); i++) {
            CompoundTag section = sections.getCompound(i);
            if (!section.contains("block_states", Tag.TAG_COMPOUND)) continue;
            CompoundTag blockStates = section.getCompound("block_states");
            if (!blockStates.contains("palette", Tag.TAG_LIST)) continue;
            ListTag palette = blockStates.getList("palette", Tag.TAG_COMPOUND);
            for (int p = 0; p < palette.size(); p++) {
                CompoundTag entry = palette.getCompound(p);
                String name = entry.getString("Name");
                String remapped = lookup(BLOCK_REMAPS, name);
                if (remapped != null) {
                    entry.putString("Name", remapped);
                }
            }
        }
    }

    private static void rewriteBlockEntities(CompoundTag root) {
        if (!root.contains("block_entities", Tag.TAG_LIST)) return;
        ListTag entities = root.getList("block_entities", Tag.TAG_COMPOUND);
        for (int i = 0; i < entities.size(); i++) {
            CompoundTag be = entities.getCompound(i);
            String id = be.getString("id");
            String remapped = lookup(BLOCK_ENTITY_REMAPS, id);
            if (remapped != null) {
                be.putString("id", remapped);
            }
        }
    }

    private static String lookup(String[][] table, String key) {
        for (String[] row : table) {
            if (row[0].equals(key)) {
                return row[1];
            }
        }
        return null;
    }
}
