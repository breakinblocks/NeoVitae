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

public class BookLivingUpgradeTablePage extends BookPage {

    private BookTextHolder title;
    private BookTextHolder text;

    public BookLivingUpgradeTablePage(BookTextHolder title, BookTextHolder text, String anchor, BookCondition condition) {
        super(anchor, condition);
        this.title = title;
        this.text = text;
    }

    public static BookLivingUpgradeTablePage fromJson(JsonObject json, HolderLookup.Provider provider) {
        var title = json.has("title")
                ? new BookTextHolder(GsonHelper.getAsString(json, "title"))
                : BookTextHolder.EMPTY;
        var text = json.has("text")
                ? new BookTextHolder(GsonHelper.getAsString(json, "text"))
                : BookTextHolder.EMPTY;
        var anchor = GsonHelper.getAsString(json, "anchor", "");
        var condition = json.has("condition")
                ? BookCondition.fromJson(json.getAsJsonObject("condition"), provider)
                : new BookNoneCondition();
        return new BookLivingUpgradeTablePage(title, text, anchor, condition);
    }

    public static BookLivingUpgradeTablePage fromNetwork(RegistryFriendlyByteBuf buffer) {
        var title = BookTextHolder.fromNetwork(buffer);
        var text = BookTextHolder.fromNetwork(buffer);
        var anchor = buffer.readUtf();
        var condition = BookCondition.fromNetwork(buffer);
        return new BookLivingUpgradeTablePage(title, text, anchor, condition);
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buffer) {
        title.toNetwork(buffer);
        text.toNetwork(buffer);
        buffer.writeUtf(getAnchor());
        getCondition().toNetwork(buffer);
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
    public ResourceLocation getType() {
        return NVPageTypes.LIVING_UPGRADE_TABLE;
    }

    @Override
    public boolean matchesQuery(String query) {
        return false;
    }
}
