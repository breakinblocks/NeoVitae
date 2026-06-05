package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookTabulaVitaeRecipePage extends BookRecipePage<TabulaVitaeRecipe> {

    public static final Identifier ID = NVPageTypes.TABULA_VITAE;
    public static final MapCodec<BookTabulaVitaeRecipePage> CODEC = codec(BookTabulaVitaeRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookTabulaVitaeRecipePage> STREAM_CODEC = streamCodec(BookTabulaVitaeRecipePage::new);
    public static final BookPageType<BookTabulaVitaeRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookTabulaVitaeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookTabulaVitaeRecipePage(NetworkDataHolder data) {
        super(data);
    }

    @Override
    public BookPageType<?> type() {
        return TYPE;
    }
}
