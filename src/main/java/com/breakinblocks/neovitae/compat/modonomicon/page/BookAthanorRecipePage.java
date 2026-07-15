package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookAthanorRecipePage extends BookRecipePage<AthanorRecipe> {

    public static final Identifier ID = NVPageTypes.ATHANOR;
    public static final MapCodec<BookAthanorRecipePage> CODEC = codec(BookAthanorRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookAthanorRecipePage> STREAM_CODEC = streamCodec(BookAthanorRecipePage::new);
    public static final BookPageType<BookAthanorRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookAthanorRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAthanorRecipePage(NetworkDataHolder data) {
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
