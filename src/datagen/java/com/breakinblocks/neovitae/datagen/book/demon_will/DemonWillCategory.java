package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

public class DemonWillCategory extends CategoryProvider {

    public DemonWillCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________a_____________",
                "___b___c___j_l_m_n_________",
                "___d___o___k_______________",
                "___e___p___________________",
                "___f___q___x_______________",
                "___g_i_t_u_v_w_____________",
                "___h_______________________",
                "_________r_s_______________"
        };
    }

    @Override
    protected void generateEntries() {
        var demonWill = this.add(new DemonWillEntry(this).generate('a'));

        var soulSnare = this.add(new SoulSnareEntry(this).generate('b'));
        soulSnare.withParent(this.parent(demonWill));

        var hellfireForge = this.add(new HellfireForgeEntry(this).generate('c'));
        hellfireForge.withParent(this.parent(demonWill));

        var tartaricGems = this.add(new TartaricGemsEntry(this).generate('d'));
        tartaricGems.withParent(this.parent(soulSnare));

        var crystallizedWill = this.add(new CrystallizedWillEntry(this).generate('e'));
        crystallizedWill.withParent(this.parent(tartaricGems));

        var aspectedWill = this.add(new AspectedWillEntry(this).generate('f'));
        aspectedWill.withParent(this.parent(crystallizedWill));

        var aura = this.add(new AuraEntry(this).generate('g'));
        aura.withParent(this.parent(aspectedWill));

        var auraGauge = this.add(new AuraGaugeEntry(this).generate('h'));
        auraGauge.withParent(this.parent(aura));

        var willCatalysts = this.add(new WillCatalystsEntry(this).generate('i'));
        willCatalysts.withParent(this.parent(aspectedWill));

        var sentientSword = this.add(new SentientSwordEntry(this).generate('j'));
        sentientSword.withParent(this.parent(demonWill));

        var sentientTools = this.add(new SentientToolsEntry(this).generate('k'));
        sentientTools.withParent(this.parent(sentientSword));

        var throwingDaggers = this.add(new ThrowingDaggersEntry(this).generate('l'));
        throwingDaggers.withParent(this.parent(demonWill));

        var bloodTank = this.add(new BloodTankEntry(this).generate('m'));
        bloodTank.withParent(this.parent(demonWill));

        var explosiveCharges = this.add(new ExplosiveChargesEntry(this).generate('n'));
        explosiveCharges.withParent(this.parent(demonWill));

        var nodeRouter = this.add(new NodeRouterEntry(this).generate('o'));
        nodeRouter.withParent(this.parent(hellfireForge));

        var routingNodes = this.add(new RoutingNodesEntry(this).generate('p'));
        routingNodes.withParent(this.parent(nodeRouter));

        var standardFilter = this.add(new StandardFilterEntry(this).generate('q'));
        standardFilter.withParent(this.parent(routingNodes));

        var tagFilter = this.add(new TagFilterEntry(this).generate('r'));
        tagFilter.withParent(this.parent(standardFilter));

        var modFilter = this.add(new ModFilterEntry(this).generate('s'));
        modFilter.withParent(this.parent(standardFilter));

        var enchantFilter = this.add(new EnchantFilterEntry(this).generate('t'));
        enchantFilter.withParent(this.parent(standardFilter));

        var compositeFilter = this.add(new CompositeFilterEntry(this).generate('u'));
        compositeFilter.withParent(this.parent(standardFilter));

        var filterParts = this.add(new FilterPartsEntry(this).generate('v'));
        filterParts.withParent(this.parent(standardFilter));

        var editingFilters = this.add(new EditingFiltersEntry(this).generate('w'));
        editingFilters.withParent(this.parent(standardFilter));

        var upgrades = this.add(new UpgradesEntry(this).generate('x'));
        upgrades.withParent(this.parent(routingNodes));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/demon_will_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/demon_will_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/demon_will_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Demon Will";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.MONSTER_SOUL_RAW.get());
    }

    @Override
    public String categoryId() {
        return "demon_will";
    }
}
