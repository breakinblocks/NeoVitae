package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.sentientdowngrade.SentientDowngradeRecipe;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookSentientDowngradeRecipePage extends BookNVRecipePage<SentientDowngradeRecipe> {

    public static final Identifier ID = NVPageTypes.SENTIENT_DOWNGRADE;
    public static final MapCodec<BookSentientDowngradeRecipePage> CODEC = codec(BookSentientDowngradeRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSentientDowngradeRecipePage> STREAM_CODEC = streamCodec(BookSentientDowngradeRecipePage::new);
    public static final BookPageType<BookSentientDowngradeRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookSentientDowngradeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookSentientDowngradeRecipePage(NetworkDataHolder data) {
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
