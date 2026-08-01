package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookAraVitaeRecipePage extends BookNVRecipePage<AraVitaeRecipe> {

    public static final Identifier ID = NVPageTypes.ARA_VITAE;
    public static final MapCodec<BookAraVitaeRecipePage> CODEC = codec(BookAraVitaeRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookAraVitaeRecipePage> STREAM_CODEC = streamCodec(BookAraVitaeRecipePage::new);
    public static final BookPageType<BookAraVitaeRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookAraVitaeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookAraVitaeRecipePage(NetworkDataHolder data) {
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
