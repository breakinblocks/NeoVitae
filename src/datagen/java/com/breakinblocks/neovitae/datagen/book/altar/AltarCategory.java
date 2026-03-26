package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class AltarCategory extends CategoryProvider {

    public AltarCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_______________d___",
                "_______b_c_________",
                "___a_______________",
                "_______e_f_________",
                "_______________g___",
                "_h_i_j_k_l_m______"
        };
    }

    @Override
    protected void generateEntries() {
        var bloodAltar = this.add(new BloodAltarEntry(this).generate('a'));
        var soulNetwork = this.add(new SoulNetworkEntry(this).generate('b'));
        soulNetwork.withParent(this.parent(bloodAltar));
        soulNetwork.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        soulNetwork.hideWhileLocked(false);

        var slates = this.add(new SlatesEntry(this).generate('c'));
        slates.withParent(this.parent(bloodAltar));
        slates.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        slates.hideWhileLocked(false);

        var redstone = this.add(new RedstoneAutomationEntry(this).generate('d'));
        redstone.withParent(this.parent(bloodAltar));
        redstone.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        redstone.hideWhileLocked(false);

        var selfSacrifice = this.add(new SelfSacrificeRuneEntry(this).generate('e'));
        selfSacrifice.withParent(this.parent(bloodAltar));
        selfSacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        selfSacrifice.hideWhileLocked(false);

        var sacrifice = this.add(new SacrificeRuneEntry(this).generate('f'));
        sacrifice.withParent(this.parent(bloodAltar));
        sacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        sacrifice.hideWhileLocked(false);

        var orbRune = this.add(new OrbRuneEntry(this).generate('g'));
        orbRune.withParent(this.parent(soulNetwork));
        orbRune.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/soul_network"));
        orbRune.hideWhileLocked(false);

        var speed = this.add(new SpeedRuneEntry(this).generate('h'));
        speed.withParent(this.parent(bloodAltar));
        speed.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        speed.hideWhileLocked(false);

        var acceleration = this.add(new AccelerationRuneEntry(this).generate('i'));
        acceleration.withParent(this.parent(bloodAltar));
        acceleration.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        acceleration.hideWhileLocked(false);

        var charging = this.add(new ChargingRuneEntry(this).generate('j'));
        charging.withParent(this.parent(bloodAltar));
        charging.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        charging.hideWhileLocked(false);

        var dislocation = this.add(new DislocationRuneEntry(this).generate('k'));
        dislocation.withParent(this.parent(bloodAltar));
        dislocation.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        dislocation.hideWhileLocked(false);

        var capacity = this.add(new CapacityRuneEntry(this).generate('l'));
        capacity.withParent(this.parent(bloodAltar));
        capacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/blood_altar"));
        capacity.hideWhileLocked(false);

        var augCapacity = this.add(new AugCapacityRuneEntry(this).generate('m'));
        augCapacity.withParent(this.parent(capacity));
        augCapacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_capacity"));
        augCapacity.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Blood Altars";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVBlocks.BLOOD_ALTAR.asItem());
    }

    @Override
    public String categoryId() {
        return "altar";
    }
}
