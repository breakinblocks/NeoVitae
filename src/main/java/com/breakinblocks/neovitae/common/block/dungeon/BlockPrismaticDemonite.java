package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockPrismaticDemonite extends Block {

    public static final int DEPLETE_THRESHOLD = 20;

    public BlockPrismaticDemonite(BlockBehaviour.Properties props) {
        super(props
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    public static ItemStack getRandomRawOre(Level level) {
        List<ItemStack> candidates = new ArrayList<>();
        for (MaterialDefinition mat : MaterialRegistry.getGenerativeMaterials()) {
            String preferredRaw = mat.getGenRaw();
            if (preferredRaw != null && !preferredRaw.isEmpty()) {
                Identifier rawId = Identifier.tryParse(preferredRaw);
                if (rawId != null) {
                    BuiltInRegistries.ITEM.get(rawId)
                            .map(Holder.Reference::value)
                            .ifPresent(item -> candidates.add(new ItemStack(item)));
                }
                continue;
            }

            String rawTag = mat.getRawTag();
            if (rawTag == null) continue;

            Identifier tagId = Identifier.parse(rawTag);
            TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
            for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                candidates.add(new ItemStack(holder.value()));
            }
        }

        if (candidates.isEmpty()) return ItemStack.EMPTY;
        return candidates.get(level.getRandom().nextInt(candidates.size())).copy();
    }
}
