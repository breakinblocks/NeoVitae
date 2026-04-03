package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

public class DungeonsCategory extends CategoryProvider {

    public DungeonsCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "______l____________",
                "__k_______r________",
                "______d____________",
                "__e___t____________",
                "______s____________",
                "__a___m___p________",
                "___________________",
                "______D____________",
                "__B___F___C________",
                "______T____________",
        };
    }

    @Override
    protected void generateEntries() {
        var lobby = this.add(new LobbyEntry(this).generate('l'));
        var endlessRealm = this.add(new EndlessRealmEntry(this).generate('r'));
        var keys = this.add(new KeysEntry(this).generate('k'));
        var demonStone = this.add(new DemonStoneEntry(this).generate('d'));
        var demonite = this.add(new DemoniteEntry(this).generate('e'));
        var dungeonEye = this.add(new DungeonEyeEntry(this).generate('s'));
        var alternator = this.add(new DungeonAlternatorEntry(this).generate('a'));
        var tauFruit = this.add(new TauFruitEntry(this).generate('t'));
        var mimics = this.add(new MimicsEntry(this).generate('m'));
        var spikeTrap = this.add(new SpikeTrapEntry(this).generate('p'));

        // Daemonium section
        var daemoniumOverview = this.add(new DaemoniumOverviewEntry(this).generate('D'));
        var bestiary = this.add(new DemonBestiaryEntry(this).generate('B'));
        var drops = this.add(new DemonDropsEntry(this).generate('F'));
        var crafting = this.add(new DemonCraftingEntry(this).generate('C'));
        var food = this.add(new DemonFoodEntry(this).generate('T'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/dungeons_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/dungeons_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/dungeons_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Dungeon Delving";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.SIMPLE_KEY.get());
    }

    @Override
    public String categoryId() {
        return "dungeons";
    }
}
