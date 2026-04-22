package com.breakinblocks.neovitae.common.event;

import net.neoforged.bus.api.IEventBus;

/**
 * Chunk-NBT legacy id rewriter (currently disabled for 26.1 port).
 *
 * <p>In 1.21.1 this class intercepted {@code ChunkDataEvent.Load} and rewrote
 * raw {@code CompoundTag} palette entries / block-entity ids in place so
 * previously-placed legacy blocks (e.g. {@code neovitae:item_routing_node})
 * survived rename-refactors.
 *
 * <p>NeoForge 26.1 closes that door: {@code ChunkDataEvent.Load#getData()}
 * now returns {@code SerializableChunkData}, and {@code CompoundTag.getList}
 * / {@code .getString} / {@code .contains} no longer accept the old
 * {@code (String, int)} typed overloads, instead returning {@code Optional}
 * values keyed by codec. The in-place-mutation model is therefore no longer
 * supported. See {@code migration_alterations.md} — "Missing-mapping chunk
 * rewriter disabled".
 *
 * <p>Future work: reimplement either via {@code DataFixerUpper} fixers
 * registered on the NeoForge bus, or via a {@code ChunkSerializer}-level
 * mixin, or by leaning on vanilla DFU once a target schema is declared.
 */
public final class NVMissingMappings {

    private NVMissingMappings() {}

    public static void register(IEventBus modBus) {
        // No-op under 26.1: see class javadoc.
    }
}
