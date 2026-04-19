package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.compat.modonomicon.NVPageTypes;
import com.google.gson.JsonObject;
import com.klikli_dev.modonomicon.book.BookTextHolder;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;
import com.klikli_dev.modonomicon.book.conditions.BookNoneCondition;
import com.klikli_dev.modonomicon.book.page.BookPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BookBloodOrbStatsPage extends BookPage {

    private BookTextHolder title;

    public BookBloodOrbStatsPage(BookTextHolder title, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title = title;
    }

    public static BookBloodOrbStatsPage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var title = json.has("title")
                ? new BookTextHolder(net.minecraft.network.chat.Component.translatable(GsonHelper.getAsString(json, "title")))
                : BookTextHolder.EMPTY;
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookBloodOrbStatsPage(title, anchor, condition);
    }

    public static BookBloodOrbStatsPage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookBloodOrbStatsPage(title, anchor, condition);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        title.toNetwork(buffer);
        buffer.writeUtf(getAnchor());
        BookCondition.toNetwork(getCondition(), buffer);
    }

    public BookTextHolder getTitle() {
        return title;
    }

    public boolean hasTitle() {
        return !title.isEmpty();
    }

    @Override
    public void prerenderMarkdown(com.klikli_dev.modonomicon.client.gui.book.markdown.BookTextRenderer textRenderer) {
        super.prerenderMarkdown(textRenderer);
        if (!title.hasComponent()) {
            title = new com.klikli_dev.modonomicon.book.RenderedBookTextHolder(title, textRenderer.render(title.getString()));
        }
    }

    @Override
    public ResourceLocation getType() {
        return NVPageTypes.BLOOD_ORB_STATS;
    }

    @Override
    public boolean matchesQuery(String query) {
        return false;
    }
}
