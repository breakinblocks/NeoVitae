package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
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

        var slates = this.add(new SlatesEntry(this).generate('c'));
        slates.withParent(this.parent(bloodAltar));

        var redstone = this.add(new RedstoneAutomationEntry(this).generate('d'));
        redstone.withParent(this.parent(bloodAltar));

        var selfSacrifice = this.add(new SelfSacrificeRuneEntry(this).generate('e'));
        selfSacrifice.withParent(this.parent(bloodAltar));

        var sacrifice = this.add(new SacrificeRuneEntry(this).generate('f'));
        sacrifice.withParent(this.parent(bloodAltar));

        var orbRune = this.add(new OrbRuneEntry(this).generate('g'));
        orbRune.withParent(this.parent(soulNetwork));

        var speed = this.add(new SpeedRuneEntry(this).generate('h'));
        speed.withParent(this.parent(bloodAltar));

        var acceleration = this.add(new AccelerationRuneEntry(this).generate('i'));
        acceleration.withParent(this.parent(bloodAltar));

        var charging = this.add(new ChargingRuneEntry(this).generate('j'));
        charging.withParent(this.parent(bloodAltar));

        var dislocation = this.add(new DislocationRuneEntry(this).generate('k'));
        dislocation.withParent(this.parent(bloodAltar));

        var capacity = this.add(new CapacityRuneEntry(this).generate('l'));
        capacity.withParent(this.parent(bloodAltar));

        var augCapacity = this.add(new AugCapacityRuneEntry(this).generate('m'));
        augCapacity.withParent(this.parent(capacity));
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
