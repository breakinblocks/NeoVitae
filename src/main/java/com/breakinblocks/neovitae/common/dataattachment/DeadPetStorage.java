package com.breakinblocks.neovitae.common.dataattachment;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public record DeadPetStorage(List<CompoundTag> pets) {

    public static final DeadPetStorage EMPTY = new DeadPetStorage(List.of());

    public static final Codec<DeadPetStorage> CODEC = CompoundTag.CODEC.listOf()
            .xmap(DeadPetStorage::new, DeadPetStorage::pets);

    public DeadPetStorage addPet(CompoundTag petData) {
        List<CompoundTag> newList = new ArrayList<>(pets);
        newList.add(petData);
        return new DeadPetStorage(newList);
    }

    public DeadPetStorage clear() {
        return EMPTY;
    }
}
