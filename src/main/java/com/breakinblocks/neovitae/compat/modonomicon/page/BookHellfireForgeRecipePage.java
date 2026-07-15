package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.klikli_dev.modonomicon.book.page.BookRecipePage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class BookHellfireForgeRecipePage extends BookRecipePage<ForgeRecipe> {

    public static final Identifier ID = NVPageTypes.HELLFIRE_FORGE;
    public static final MapCodec<BookHellfireForgeRecipePage> CODEC = codec(BookHellfireForgeRecipePage::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BookHellfireForgeRecipePage> STREAM_CODEC = streamCodec(BookHellfireForgeRecipePage::new);
    public static final BookPageType<BookHellfireForgeRecipePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    public BookHellfireForgeRecipePage(JsonDataHolder data) {
        super(data);
    }

    public BookHellfireForgeRecipePage(NetworkDataHolder data) {
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
