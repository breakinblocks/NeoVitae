package com.breakinblocks.neovitae.util.helper;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockWithItemRegister {
    private final DeferredRegister.Blocks BLOCK;
    private final DeferredRegister.Items ITEM;

    public BlockWithItemRegister(DeferredRegister.Blocks block, DeferredRegister.Items item) {
        BLOCK = block;
        ITEM = item;
    }

    public <B extends Block, I extends BlockItem> BlockWithItemHolder<B, I> register(
            String name,
            Function<BlockBehaviour.Properties, B> block,
            BlockBehaviour.Properties blockProperties,
            BiFunction<B, Item.Properties, I> item,
            Item.Properties itemProperties) {
        DeferredBlock<B> regBlock = BLOCK.registerBlock(name, block, (Supplier<BlockBehaviour.Properties>) () -> blockProperties);
        itemProperties.useBlockDescriptionPrefix();
        DeferredItem<I> regItem = ITEM.registerItem(name, props -> item.apply(regBlock.get(), props), (Supplier<Item.Properties>) () -> itemProperties);
        return new BlockWithItemHolder<>(regBlock, regItem);
    }

    public <B extends Block> BlockWithItemHolder<B, BlockItem> register(
            String name,
            Function<BlockBehaviour.Properties, B> block,
            BlockBehaviour.Properties blockProperties) {
        return register(name, block, blockProperties, BlockItem::new, new Item.Properties());
    }

    public <B extends Block> BlockWithItemHolder<B, BlockItem> register(
            String name,
            Function<BlockBehaviour.Properties, B> block,
            BlockBehaviour.Properties blockProperties,
            Item.Properties itemProperties) {
        return register(name, block, blockProperties, BlockItem::new, itemProperties);
    }

    public <B extends Block, I extends BlockItem> BlockWithItemHolder<B, I> register(
            String name,
            Function<BlockBehaviour.Properties, B> block,
            BlockBehaviour.Properties blockProperties,
            BiFunction<B, Item.Properties, I> item) {
        return register(name, block, blockProperties, item, new Item.Properties());
    }

    public BlockWithItemHolder<Block, BlockItem> register(
            String name,
            BlockBehaviour.Properties blockProperties,
            Item.Properties itemProperties) {
        return register(name, Block::new, blockProperties, BlockItem::new, itemProperties);
    }

    public BlockWithItemHolder<Block, BlockItem> register(
            String name,
            BlockBehaviour.Properties blockProperties) {
        return register(name, Block::new, blockProperties, BlockItem::new, new Item.Properties());
    }

    public <I extends BlockItem> BlockWithItemHolder<Block, I> register(
            String name,
            BlockBehaviour.Properties blockProperties,
            BiFunction<Block, Item.Properties, I> item,
            Item.Properties itemProperties) {
        return register(name, Block::new, blockProperties, item, itemProperties);
    }

    public <I extends BlockItem> BlockWithItemHolder<Block, I> register(
            String name,
            BlockBehaviour.Properties blockProperties,
            BiFunction<Block, Item.Properties, I> item) {
        return register(name, Block::new, blockProperties, item, new Item.Properties());
    }
}
