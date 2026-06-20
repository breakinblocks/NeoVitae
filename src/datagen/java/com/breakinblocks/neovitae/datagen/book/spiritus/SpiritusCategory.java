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
                "___b___c_y_j___m_n_A_______",
                "___d_z_o___k_______O_______",
                "___e___p___l_______B_______",
                "___f___q___x_______________",
                "___g_i_t_u_v_w_____________",
                "___h_______________________",
                "_________r_s_______________"
        };
    }

    @Override
    protected void generateEntries() {
        var spiritus = this.add(new SpiritusEntry(this).generate('a'));

        var throwingDaggers = this.add(new ThrowingDaggersEntry(this).generate('b'));
        throwingDaggers.withParent(this.parent(spiritus));
        throwingDaggers.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        throwingDaggers.hideWhileLocked(false);

        var hellfireForge = this.add(new HellfireForgeEntry(this).generate('c'));
        hellfireForge.withParent(this.parent(spiritus));
        hellfireForge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        hellfireForge.hideWhileLocked(false);

        var spiritusGems = this.add(new SpiritusGemsEntry(this).generate('d'));
        spiritusGems.withParent(this.parent(throwingDaggers));
        spiritusGems.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/throwing_daggers"));
        spiritusGems.hideWhileLocked(false);

        var aura = this.add(new AuraEntry(this).generate('e'));
        aura.withParent(this.parent(spiritusGems));
        aura.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus_gems"));
        aura.hideWhileLocked(false);

        var crystallizedSpiritus = this.add(new CrystallizedSpiritusEntry(this).generate('f'));
        crystallizedSpiritus.withParent(this.parent(aura));
        crystallizedSpiritus.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aura"));
        crystallizedSpiritus.hideWhileLocked(false);

        var aspectedSpiritus = this.add(new AspectedSpiritusEntry(this).generate('g'));
        aspectedSpiritus.withParent(this.parent(crystallizedSpiritus));
        aspectedSpiritus.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/crystallized_spiritus"));
        aspectedSpiritus.hideWhileLocked(false);

        var auraGauge = this.add(new AuraGaugeEntry(this).generate('h'));
        auraGauge.withParent(this.parent(aspectedSpiritus));
        auraGauge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aspected_spiritus"));
        auraGauge.hideWhileLocked(false);

        var willCatalysts = this.add(new SpiritusCatalystsEntry(this).generate('i'));
        willCatalysts.withParent(this.parent(aspectedSpiritus));
        willCatalysts.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/aspected_spiritus"));
        willCatalysts.hideWhileLocked(false);

        var sentientSword = this.add(new SentientSwordEntry(this).generate('j'));
        sentientSword.withParent(this.parent(spiritus));
        sentientSword.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        sentientSword.hideWhileLocked(false);

        var sentientTools = this.add(new SentientToolsEntry(this).generate('k'));
        sentientTools.withParent(this.parent(sentientSword));
        sentientTools.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/sentient_sword"));

        var lexVitae = this.add(new LexVitaeEntry(this).generate('l'));
        lexVitae.withParent(this.parent(sentientTools));
        lexVitae.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/sentient_tools"));
        sentientTools.hideWhileLocked(false);

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

        var upgrades = this.add(new UpgradesEntry(this).generate('x'));
        upgrades.withParent(this.parent(routingNodes));
        upgrades.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/routing_nodes"));
        upgrades.hideWhileLocked(false);

        var bloodMending = this.add(new BloodMendingEntry(this).generate('y'));
        bloodMending.withParent(this.parent(hellfireForge));
        bloodMending.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/hellfire_forge"));
        bloodMending.hideWhileLocked(false);

        var spiritusInfusion = this.add(new SpiritusInfusionEntry(this).generate('z'));
        spiritusInfusion.withParent(this.parent(spiritusGems));
        spiritusInfusion.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus_gems"));
        spiritusInfusion.hideWhileLocked(false);

        var athanor = this.add(new AthanorEntry(this).generate('A'));
        athanor.withParent(this.parent(spiritus));
        athanor.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/spiritus"));
        athanor.hideWhileLocked(false);

        var oreProcessing = this.add(new OreProcessingEntry(this).generate('O'));
        oreProcessing.withParent(this.parent(athanor));
        oreProcessing.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/athanor"));
        oreProcessing.hideWhileLocked(false);

        var bloodstoneBricks = this.add(new BloodstoneBricksEntry(this).generate('B'));
        bloodstoneBricks.withParent(this.parent(oreProcessing));
        bloodstoneBricks.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:spiritus/ore_processing"));
        bloodstoneBricks.hideWhileLocked(false);
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
