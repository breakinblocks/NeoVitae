package com.breakinblocks.neovitae.common.dataattachment;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NVDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, NeoVitae.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> INCENSE = ATTACHMENT_TYPES.register(
            "incense", () -> AttachmentType.builder(() -> 0D).serialize(Codec.DOUBLE.fieldOf("value")).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<Identifier, Double>>> SENTIENT_ADDITIONAL = ATTACHMENT_TYPES.register(
            "sentient_cooldown",
            () -> AttachmentType.<Map<Identifier, Double>>builder(() -> new HashMap<>())
                    .serialize(Codec.unboundedMap(Identifier.CODEC, Codec.DOUBLE)
                            .<Map<Identifier, Double>>xmap(m -> new HashMap<>(m), m -> m)
                            .fieldOf("value"))
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SpiritusChunk>> SPIRITUS_CHUNK = ATTACHMENT_TYPES.register(
            "spiritus_chunk", () -> AttachmentType.builder(SpiritusChunk::new).serialize(SpiritusChunk.CODEC.fieldOf("value")).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DeadPetStorage>> DEAD_PET_STORAGE = ATTACHMENT_TYPES.register(
            "dead_pet_storage", () -> AttachmentType.builder(() -> DeadPetStorage.EMPTY)
                    .serialize(DeadPetStorage.CODEC.fieldOf("value"))
                    .copyOnDeath()
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DungeonExitData>> DUNGEON_EXIT = ATTACHMENT_TYPES.register(
            "dungeon_exit", () -> AttachmentType.builder(() -> DungeonExitData.EMPTY)
                    .serialize(DungeonExitData.CODEC.fieldOf("value"))
                    .copyOnDeath()
                    .build()
    );

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
