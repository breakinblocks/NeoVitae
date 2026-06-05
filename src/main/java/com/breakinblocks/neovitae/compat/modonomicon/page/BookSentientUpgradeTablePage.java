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

public class BookSentientUpgradeTablePage extends BookPage {

    public static final Identifier ID = NVPageTypes.SENTIENT_UPGRADE_TABLE;
    public static final MapCodec<BookSentientUpgradeTablePage> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BookTextHolder.CODEC.optionalFieldOf("title", BookTextHolder.EMPTY).forGetter(BookSentientUpgradeTablePage::getTitle),
                    BookTextHolder.CODEC.optionalFieldOf("text", BookTextHolder.EMPTY).forGetter(BookSentientUpgradeTablePage::getText),
                    Codec.STRING.optionalFieldOf("id", "").forGetter(BookPage::getId),
                    BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
            ).apply(instance, BookSentientUpgradeTablePage::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BookSentientUpgradeTablePage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookSentientUpgradeTablePage::getTitle,
            BookTextHolder.STREAM_CODEC, BookSentientUpgradeTablePage::getText,
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookSentientUpgradeTablePage::new
    );
    public static final BookPageType<BookSentientUpgradeTablePage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    private BookTextHolder title;
    private BookTextHolder text;

    public BookSentientUpgradeTablePage(BookTextHolder title, BookTextHolder text, String id, BookCondition condition) {
        super(id, condition);
        this.title = title;
        this.text = text;
    }

    public BookTextHolder getTitle() {
        return title;
    }

    public BookTextHolder getText() {
        return text;
    }

    public boolean hasTitle() {
        return !title.isEmpty();
    }

    @Override
    public void prerenderMarkdown(BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);
        if (!text.hasComponent()) {
            text = new RenderedBookTextHolder(text, textRenderer.render(text.getString()));
        }
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
