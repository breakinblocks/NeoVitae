package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.flask.FlaskRecipe;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookFlaskRecipePage extends BookRecipePage<FlaskRecipe> {

    public static final Identifier ID = NVPageTypes.FLASK;
    public static final MapCodec<BookFlaskRecipePage> CODEC = codec(BookFlaskRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookFlaskRecipePage> STREAM_CODEC = streamCodec(BookFlaskRecipePage::new);
    public static final BookPageType<BookFlaskRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookFlaskRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookFlaskRecipePage(NetworkDataHolder data) {
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
