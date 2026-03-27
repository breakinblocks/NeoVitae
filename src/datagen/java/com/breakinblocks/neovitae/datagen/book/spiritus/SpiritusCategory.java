package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

public class SpiritusCategory extends CategoryProvider {

    public SpiritusCategory(ModonomiconProviderBase parent) {
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
        var spiritus = this.add(new SpiritusEntry(this).generate('a'));

        var soulSnare = this.add(new SpiritusSnareEntry(this).generate('b'));
        soulSnare.withParent(this.parent(spiritus));
        soulSnare.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        soulSnare.hideWhileLocked(false);

        var hellfireForge = this.add(new HellfireForgeEntry(this).generate('c'));
        hellfireForge.withParent(this.parent(spiritus));
        hellfireForge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        hellfireForge.hideWhileLocked(false);

        var spiritusGems = this.add(new SpiritusGemsEntry(this).generate('d'));
        spiritusGems.withParent(this.parent(soulSnare));
        spiritusGems.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus_snare"));
        spiritusGems.hideWhileLocked(false);

        var crystallizedWill = this.add(new CrystallizedWillEntry(this).generate('e'));
        crystallizedWill.withParent(this.parent(spiritusGems));
        crystallizedWill.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus_gems"));
        crystallizedWill.hideWhileLocked(false);

        var aspectedWill = this.add(new AspectedWillEntry(this).generate('f'));
        aspectedWill.withParent(this.parent(crystallizedWill));
        aspectedWill.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/crystallized_will"));
        aspectedWill.hideWhileLocked(false);

        var aura = this.add(new AuraEntry(this).generate('g'));
        aura.withParent(this.parent(aspectedWill));
        aura.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aspected_will"));
        aura.hideWhileLocked(false);

        var auraGauge = this.add(new AuraGaugeEntry(this).generate('h'));
        auraGauge.withParent(this.parent(aura));
        auraGauge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aura"));
        auraGauge.hideWhileLocked(false);

        var willCatalysts = this.add(new WillCatalystsEntry(this).generate('i'));
        willCatalysts.withParent(this.parent(aspectedWill));
        willCatalysts.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aspected_will"));
        willCatalysts.hideWhileLocked(false);

        var sentientSword = this.add(new SentientSwordEntry(this).generate('j'));
        sentientSword.withParent(this.parent(spiritus));
        sentientSword.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        sentientSword.hideWhileLocked(false);

        var sentientTools = this.add(new SentientToolsEntry(this).generate('k'));
        sentientTools.withParent(this.parent(sentientSword));
        sentientTools.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/sentient_sword"));
        sentientTools.hideWhileLocked(false);

        var throwingDaggers = this.add(new ThrowingDaggersEntry(this).generate('l'));
        throwingDaggers.withParent(this.parent(spiritus));
        throwingDaggers.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        throwingDaggers.hideWhileLocked(false);

        var bloodTank = this.add(new BloodTankEntry(this).generate('m'));
        bloodTank.withParent(this.parent(spiritus));
        bloodTank.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        bloodTank.hideWhileLocked(false);

        var explosiveCharges = this.add(new ExplosiveChargesEntry(this).generate('n'));
        explosiveCharges.withParent(this.parent(spiritus));
        explosiveCharges.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        explosiveCharges.hideWhileLocked(false);

        var nodeRouter = this.add(new NodeRouterEntry(this).generate('o'));
        nodeRouter.withParent(this.parent(hellfireForge));
        nodeRouter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/hellfire_forge"));
        nodeRouter.hideWhileLocked(false);

        var routingNodes = this.add(new RoutingNodesEntry(this).generate('p'));
        routingNodes.withParent(this.parent(nodeRouter));
        routingNodes.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/node_router"));
        routingNodes.hideWhileLocked(false);

        var standardFilter = this.add(new StandardFilterEntry(this).generate('q'));
        standardFilter.withParent(this.parent(routingNodes));
        standardFilter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/routing_nodes"));
        standardFilter.hideWhileLocked(false);

        var tagFilter = this.add(new TagFilterEntry(this).generate('r'));
        tagFilter.withParent(this.parent(standardFilter));
        tagFilter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        tagFilter.hideWhileLocked(false);

        var modFilter = this.add(new ModFilterEntry(this).generate('s'));
        modFilter.withParent(this.parent(standardFilter));
        modFilter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        modFilter.hideWhileLocked(false);

        var enchantFilter = this.add(new EnchantFilterEntry(this).generate('t'));
        enchantFilter.withParent(this.parent(standardFilter));
        enchantFilter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        enchantFilter.hideWhileLocked(false);

        var compositeFilter = this.add(new CompositeFilterEntry(this).generate('u'));
        compositeFilter.withParent(this.parent(standardFilter));
        compositeFilter.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        compositeFilter.hideWhileLocked(false);

        var filterParts = this.add(new FilterPartsEntry(this).generate('v'));
        filterParts.withParent(this.parent(standardFilter));
        filterParts.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        filterParts.hideWhileLocked(false);

        var editingFilters = this.add(new EditingFiltersEntry(this).generate('w'));
        editingFilters.withParent(this.parent(standardFilter));
        editingFilters.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/standard_filter"));
        editingFilters.hideWhileLocked(false);

        var upgrades = this.add(new UpgradesEntry(this).generate('x'));
        upgrades.withParent(this.parent(routingNodes));
        upgrades.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/routing_nodes"));
        upgrades.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/spiritus_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/spiritus_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/spiritus_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Spiritus & Artifice";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.MONSTER_SOUL_RAW.get());
    }

    @Override
    public String categoryId() {
        return "spiritus";
    }
}
