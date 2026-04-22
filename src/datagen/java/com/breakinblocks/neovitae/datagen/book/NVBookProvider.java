package com.breakinblocks.neovitae.datagen.book;

import com.klikli_dev.modonomicon.api.datagen.ModonomiconLanguageProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.alchemy_array.AlchemyArraysCategory;
import com.breakinblocks.neovitae.datagen.book.tabula_vitae.TabulaVitaeCategory;
import com.breakinblocks.neovitae.datagen.book.altar.AltarCategory;
import com.breakinblocks.neovitae.datagen.book.spiritus.SpiritusCategory;
import com.breakinblocks.neovitae.datagen.book.dungeons.DungeonsCategory;
import com.breakinblocks.neovitae.datagen.book.rituals.RitualsCategory;
import com.breakinblocks.neovitae.datagen.book.utility.UtilityCategory;
import net.minecraft.resources.Identifier;

public class NVBookProvider extends SingleBookSubProvider {

    public NVBookProvider(ModonomiconLanguageProvider lang) {
        super("guide", NeoVitae.MODID, lang);
    }

    @Override
    protected void registerDefaultMacros() {
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
        this.add(new AlchemyArraysCategory(this).generate());
        this.add(new TabulaVitaeCategory(this).generate());
        this.add(new AltarCategory(this).generate());
        this.add(new SpiritusCategory(this).generate());
        this.add(new DungeonsCategory(this).generate());
        this.add(new RitualsCategory(this).generate());
        this.add(new UtilityCategory(this).generate());
    }

    @Override
    protected BookModel additionalSetup(BookModel book) {
        return super.additionalSetup(book)
                .withGenerateBookItem(false)
                .withCustomBookItem(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "guide_book"))
                .withCreativeTab(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "main"))
                .withBookContentTexture(NeoVitae.rl("textures/gui/book_content.png"))
                .withFrameTexture(NeoVitae.rl("textures/gui/book_frame.png"))
                .withBookOverviewTexture(NeoVitae.rl("textures/gui/book_overview.png"))
                .withCraftingTexture(NeoVitae.rl("textures/gui/crafting_textures.png"));
    }

    @Override
    protected String bookName() {
        return "Scriptura Vitae";
    }

    @Override
    protected String bookTooltip() {
        return "A Grimoire of Vitaemancy";
    }
}
