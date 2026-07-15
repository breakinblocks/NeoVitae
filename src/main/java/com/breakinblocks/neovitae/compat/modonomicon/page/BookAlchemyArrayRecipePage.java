package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookAlchemyArrayRecipePage extends BookRecipePage<AlchemyArrayRecipe> {

    public static final Identifier ID = NVPageTypes.ALCHEMY_ARRAY;
    public static final MapCodec<BookAlchemyArrayRecipePage> CODEC = codec(BookAlchemyArrayRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookAlchemyArrayRecipePage> STREAM_CODEC = streamCodec(BookAlchemyArrayRecipePage::new);
    public static final BookPageType<BookAlchemyArrayRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookAlchemyArrayRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAlchemyArrayRecipePage(NetworkDataHolder data) {
        super(data);
    }

    @Override
    public BookPageType<?> type() {
        return TYPE;
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        if (getParentEntry() == null) {
            return;
        }
        super.prerenderMarkdown(textRenderer);
    }
}
