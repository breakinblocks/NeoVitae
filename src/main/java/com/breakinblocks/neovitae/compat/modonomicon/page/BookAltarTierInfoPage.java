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

public class BookAltarTierInfoPage extends BookPage {

    public static final Identifier ID = NVPageTypes.ALTAR_TIER_INFO;
    public static final MapCodec<BookAltarTierInfoPage> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BookTextHolder.CODEC.optionalFieldOf("title", BookTextHolder.EMPTY).forGetter(BookAltarTierInfoPage::getTitle),
                    Codec.STRING.optionalFieldOf("id", "").forGetter(BookPage::getId),
                    BookCondition.CODEC.optionalFieldOf("condition", new BookNoneCondition()).forGetter(BookPage::getCondition)
            ).apply(instance, BookAltarTierInfoPage::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BookAltarTierInfoPage> STREAM_CODEC = StreamCodec.composite(
            BookTextHolder.STREAM_CODEC, BookAltarTierInfoPage::getTitle,
            ByteBufCodecs.STRING_UTF8, BookPage::getId,
            BookCondition.STREAM_CODEC, BookPage::getCondition,
            BookAltarTierInfoPage::new
    );
    public static final BookPageType<BookAltarTierInfoPage> TYPE = BookPageTypeRegistry.register(ID, CODEC, STREAM_CODEC);

    private BookTextHolder title;

    public BookAltarTierInfoPage(BookTextHolder title, String id, BookCondition condition) {
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
