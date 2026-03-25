package com.breakinblocks.neovitae.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.altar.AltarCategory;
import net.minecraft.resources.ResourceLocation;

public class NVBookProvider extends SingleBookSubProvider {

    public NVBookProvider(ModonomiconLanguageProvider lang) {
        super("guide", NeoVitae.MODID, lang);
    }

    @Override
    protected void registerDefaultMacros() {
        // Color macros matching the Patchouli book
        this.registerDefaultMacro("$blood", "AA0000");
        this.registerDefaultMacro("$water", "0000AA");
        this.registerDefaultMacro("$air", "AAAA00");
        this.registerDefaultMacro("$fire", "AA0000");
        this.registerDefaultMacro("$earth", "00AA00");
        this.registerDefaultMacro("$dusk", "9400D3");
        this.registerDefaultMacro("$raw", "36C6C6");
        this.registerDefaultMacro("$steadfast", "0000AA");
        this.registerDefaultMacro("$destructive", "AAAA00");
        this.registerDefaultMacro("$vengeful", "AA0000");
        this.registerDefaultMacro("$corrosive", "00AA00");
    }

    @Override
    protected void generateCategories() {
        this.add(new AltarCategory(this).generate());
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return super.additionalSetup(book)
                .withGenerateBookItem(true)
                .withModel(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "book"))
                .withCreativeTab(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "main"));
    }

    @Override
    protected String bookName() {
        return "Sanguine Scientiem";
    }

    @Override
    protected String bookTooltip() {
        return "Neo Vitae Edition";
    }
}
