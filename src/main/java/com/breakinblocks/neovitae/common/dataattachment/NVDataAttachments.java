package com.breakinblocks.neovitae.common.dataattachment;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
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
            "incense", () -> AttachmentType.builder(() -> 0D).serialize(Codec.DOUBLE).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<ResourceLocation, Double>>> SENTIENT_ADDITIONAL = ATTACHMENT_TYPES.register("sentient_cooldown", () -> AttachmentType.<Map<ResourceLocation, Double>>builder(() -> new HashMap<>()).serialize(Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE).xmap(HashMap::new, Function.identity())).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SpiritusChunk>> SPIRITUS_CHUNK = ATTACHMENT_TYPES.register(
            "spiritus_chunk", () -> AttachmentType.builder(SpiritusChunk::new).serialize(SpiritusChunk.CODEC).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DeadPetStorage>> DEAD_PET_STORAGE = ATTACHMENT_TYPES.register(
            "dead_pet_storage", () -> AttachmentType.builder(() -> DeadPetStorage.EMPTY)
                    .serialize(DeadPetStorage.CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DungeonExitData>> DUNGEON_EXIT = ATTACHMENT_TYPES.register(
            "dungeon_exit", () -> AttachmentType.builder(() -> DungeonExitData.EMPTY)
                    .serialize(DungeonExitData.CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
