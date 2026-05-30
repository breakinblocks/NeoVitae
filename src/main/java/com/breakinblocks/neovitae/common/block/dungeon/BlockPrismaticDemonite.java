package com.breakinblocks.neovitae.common.block.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import com.breakinblocks.neovitae.common.material.MaterialDefinition;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockPrismaticDemonite extends Block {

    public static final int DEPLETE_THRESHOLD = 20;

    public BlockPrismaticDemonite() {
        super(BlockBehaviour.Properties.of()
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.emptyList();
    }

    public static ItemStack getRandomRawOre(Level level) {
        List<ItemStack> candidates = new ArrayList<>();
        for (MaterialDefinition mat : MaterialRegistry.getAllMaterials()) {
            String rawTag = mat.getRawTag();
            if (rawTag == null) continue;

            ResourceLocation tagId = ResourceLocation.parse(rawTag);
            TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
            var holders = BuiltInRegistries.ITEM.getTag(tag);
            if (holders.isPresent()) {
                holders.get().forEach(holder -> candidates.add(new ItemStack(holder.value())));
            }
        }

        if (candidates.isEmpty()) return ItemStack.EMPTY;
        return candidates.get(level.random.nextInt(candidates.size())).copy();
    }
}
