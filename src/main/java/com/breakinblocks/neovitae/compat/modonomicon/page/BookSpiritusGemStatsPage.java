package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.RenderedBookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer;
import com.klikli_dev.modonomicon.data.BookPageType;
import com.klikli_dev.modonomicon.registry.BookPageTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class BookSpiritusGemStatsPage extends BookPage {

    public static final Identifier ID = NVPageTypes.SPIRITUS_GEM_STATS;
    public static final MapCodec<BookSpiritusGemStatsPage> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BookTextHolder.CODEC.optionalFieldOf("title", BookTextHolder.EMPTY).forGetter(BookSpiritusGemStatsPage::getTitle),
                    Codec.STRING.optionalFieldOf("id", "").forGetter(BookPage::getId),
                    BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
            ).apply(instance, BookSpiritusGemStatsPage::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSpiritusGemStatsPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookSpiritusGemStatsPage::getTitle,
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookSpiritusGemStatsPage::new
    );
    public static final BookPageType<BookSpiritusGemStatsPage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    private BookTextHolder title;

    public BookSpiritusGemStatsPage(BookTextHolder title, String id, BookCondition condition) {
        super(id, condition);
        this.title = title;
    }

    public BookTextHolder getTitle() {
        return title;
    }

    public boolean hasTitle() {
        return !title.isEmpty();
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);
        if (!title.hasComponent()) {
            title = new RenderedBookTextHolder(title, textRenderer.render(title.getString()));
        }
    }

    @Override
    public BookPageType<?> type() {
        return TYPE;
    }

    @Override
    public boolean matchesQuery(String query, Level level) {
        return false;
    }
}
