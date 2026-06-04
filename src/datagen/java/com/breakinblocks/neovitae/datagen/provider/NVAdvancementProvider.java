package com.breakinblocks.neovitae.datagen.provider;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.advancement.CatalystTransmuteTrigger;
import com.breakinblocks.neovitae.common.advancement.ImperfectRitualActivatedTrigger;
import com.breakinblocks.neovitae.common.advancement.NVCriteriaTriggers;
import com.breakinblocks.neovitae.common.advancement.RitualActivatedTrigger;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NVAdvancementProvider extends AdvancementProvider {
    public NVAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        super(output, registries, helper, List.of(new NVAdvancements()));
    }

    static class NVAdvancements implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper helper) {
            // Root
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(NVBlocks.ARA_VITAE,
                            Component.translatable("advancements.neovitae.root.title"),
                            Component.translatable("advancements.neovitae.root.description"),
                            ResourceLocation.withDefaultNamespace("textures/block/nether_bricks.png"),
                            AdvancementType.TASK, true, true, false)
                    .addCriterion("get_ara_vitae", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.ARA_VITAE))
                    .save(saver, NeoVitae.rl("neovitae/root").toString());

            // Orb Progression
            AdvancementHolder weakBloodOrb = Advancement.Builder.advancement()
                    .parent(root)
                    .display(NVItems.ORB_WEAK.get(),
                            Component.translatable("advancements.neovitae.weak_blood_orb.title"),
                            Component.translatable("advancements.neovitae.weak_blood_orb.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_WEAK.get()))
                    .save(saver, NeoVitae.rl("neovitae/weak_blood_orb").toString());

            AdvancementHolder apprenticeBloodOrb = Advancement.Builder.advancement()
                    .parent(weakBloodOrb)
                    .display(NVItems.ORB_APPRENTICE.get(),
                            Component.translatable("advancements.neovitae.apprentice_blood_orb.title"),
                            Component.translatable("advancements.neovitae.apprentice_blood_orb.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_APPRENTICE.get()))
                    .save(saver, NeoVitae.rl("neovitae/apprentice_blood_orb").toString());

            AdvancementHolder magicianBloodOrb = Advancement.Builder.advancement()
                    .parent(apprenticeBloodOrb)
                    .display(NVItems.ORB_MAGICIAN.get(),
                            Component.translatable("advancements.neovitae.magician_blood_orb.title"),
                            Component.translatable("advancements.neovitae.magician_blood_orb.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_MAGICIAN.get()))
                    .save(saver, NeoVitae.rl("neovitae/magician_blood_orb").toString());

            AdvancementHolder masterBloodOrb = Advancement.Builder.advancement()
                    .parent(magicianBloodOrb)
                    .display(NVItems.ORB_MASTER.get(),
                            Component.translatable("advancements.neovitae.master_blood_orb.title"),
                            Component.translatable("advancements.neovitae.master_blood_orb.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_MASTER.get()))
                    .save(saver, NeoVitae.rl("neovitae/master_blood_orb").toString());

            AdvancementHolder archmageBloodOrb = Advancement.Builder.advancement()
                    .parent(masterBloodOrb)
                    .display(NVItems.ORB_ARCHMAGE.get(),
                            Component.translatable("advancements.neovitae.archmage_blood_orb.title"),
                            Component.translatable("advancements.neovitae.archmage_blood_orb.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_ARCHMAGE.get()))
                    .save(saver, NeoVitae.rl("neovitae/archmage_blood_orb").toString());

            AdvancementHolder transcendentBloodOrb = Advancement.Builder.advancement()
                    .parent(archmageBloodOrb)
                    .display(NVItems.ORB_TRANSCENDENT.get(),
                            Component.translatable("advancements.neovitae.transcendent_blood_orb.title"),
                            Component.translatable("advancements.neovitae.transcendent_blood_orb.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_TRANSCENDENT.get()))
                    .save(saver, NeoVitae.rl("neovitae/transcendent_blood_orb").toString());

            // Slate Progression (branch off weak_blood_orb)
            AdvancementHolder blankSlate = Advancement.Builder.advancement()
                    .parent(weakBloodOrb)
                    .display(NVItems.TABULA_RASA.get(),
                            Component.translatable("advancements.neovitae.blank_slate.title"),
                            Component.translatable("advancements.neovitae.blank_slate.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_slate", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.TABULA_RASA.get()))
                    .save(saver, NeoVitae.rl("neovitae/blank_slate").toString());

            AdvancementHolder reinforcedSlate = Advancement.Builder.advancement()
                    .parent(blankSlate)
                    .display(NVItems.TABULA_ROBUR.get(),
                            Component.translatable("advancements.neovitae.reinforced_slate.title"),
                            Component.translatable("advancements.neovitae.reinforced_slate.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_slate", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.TABULA_ROBUR.get()))
                    .save(saver, NeoVitae.rl("neovitae/reinforced_slate").toString());

            AdvancementHolder imbuedSlate = Advancement.Builder.advancement()
                    .parent(reinforcedSlate)
                    .display(NVItems.TABULA_ANIMATA.get(),
                            Component.translatable("advancements.neovitae.imbued_slate.title"),
                            Component.translatable("advancements.neovitae.imbued_slate.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_slate", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.TABULA_ANIMATA.get()))
                    .save(saver, NeoVitae.rl("neovitae/imbued_slate").toString());

            AdvancementHolder demonicSlate = Advancement.Builder.advancement()
                    .parent(imbuedSlate)
                    .display(NVItems.TABULA_SPIRITUS.get(),
                            Component.translatable("advancements.neovitae.demonic_slate.title"),
                            Component.translatable("advancements.neovitae.demonic_slate.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_slate", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.TABULA_SPIRITUS.get()))
                    .save(saver, NeoVitae.rl("neovitae/demonic_slate").toString());

            AdvancementHolder etherealSlate = Advancement.Builder.advancement()
                    .parent(demonicSlate)
                    .display(NVItems.TABULA_AETHEREA.get(),
                            Component.translatable("advancements.neovitae.ethereal_slate.title"),
                            Component.translatable("advancements.neovitae.ethereal_slate.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("get_slate", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.TABULA_AETHEREA.get()))
                    .save(saver, NeoVitae.rl("neovitae/ethereal_slate").toString());

            // Crafting Stations (branch off weak_blood_orb)
            AdvancementHolder tabulaVitae = Advancement.Builder.advancement()
                    .parent(weakBloodOrb)
                    .display(NVBlocks.TABULA_VITAE,
                            Component.translatable("advancements.neovitae.tabula_vitae.title"),
                            Component.translatable("advancements.neovitae.tabula_vitae.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_table", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.TABULA_VITAE))
                    .save(saver, NeoVitae.rl("neovitae/tabula_vitae").toString());

            AdvancementHolder athanor = Advancement.Builder.advancement()
                    .parent(tabulaVitae)
                    .display(NVBlocks.ATHANOR_BLOCK,
                            Component.translatable("advancements.neovitae.athanor.title"),
                            Component.translatable("advancements.neovitae.athanor.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_athanor", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.ATHANOR_BLOCK))
                    .save(saver, NeoVitae.rl("neovitae/athanor").toString());

            // Incense Altar (off tabula_vitae)
            Advancement.Builder.advancement()
                    .parent(tabulaVitae)
                    .display(NVBlocks.INCENSE_ALTAR,
                            Component.translatable("advancements.neovitae.incense_altar.title"),
                            Component.translatable("advancements.neovitae.incense_altar.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_altar", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.INCENSE_ALTAR))
                    .save(saver, NeoVitae.rl("neovitae/incense_altar").toString());

            // Sigil Branch (off blank_slate)
            AdvancementHolder firstSigil = Advancement.Builder.advancement()
                    .parent(blankSlate)
                    .display(NVItems.SIGIL_DIVINATION.get(),
                            Component.translatable("advancements.neovitae.first_sigil.title"),
                            Component.translatable("advancements.neovitae.first_sigil.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_sigil", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SIGIL_DIVINATION.get()))
                    .save(saver, NeoVitae.rl("neovitae/first_sigil").toString());

            AdvancementHolder ritualDiviner = Advancement.Builder.advancement()
                    .parent(firstSigil)
                    .display(NVItems.RITUAL_DIVINER.get(),
                            Component.translatable("advancements.neovitae.ritual_diviner.title"),
                            Component.translatable("advancements.neovitae.ritual_diviner.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_diviner", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.RITUAL_DIVINER.get()))
                    .save(saver, NeoVitae.rl("neovitae/ritual_diviner").toString());

            // Master Ritual Stone (off ritual_diviner)
            AdvancementHolder masterRitualStone = Advancement.Builder.advancement()
                    .parent(ritualDiviner)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.master_ritual_stone.title"),
                            Component.translatable("advancements.neovitae.master_ritual_stone.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_stone", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.MASTER_RITUAL_STONE))
                    .save(saver, NeoVitae.rl("neovitae/master_ritual_stone").toString());

            // Imperfect Ritual (off ritual_diviner)
            Advancement.Builder.advancement()
                    .parent(ritualDiviner)
                    .display(NVBlocks.IMPERFECT_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.imperfect_ritual.title"),
                            Component.translatable("advancements.neovitae.imperfect_ritual.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("activate", NVCriteriaTriggers.IMPERFECT_RITUAL_ACTIVATED.get().createCriterion(ImperfectRitualActivatedTrigger.any()))
                    .save(saver, NeoVitae.rl("neovitae/imperfect_ritual").toString());

            // First Ritual (off master_ritual_stone)
            AdvancementHolder firstRitual = Advancement.Builder.advancement()
                    .parent(masterRitualStone)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.first_ritual.title"),
                            Component.translatable("advancements.neovitae.first_ritual.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.any()))
                    .save(saver, NeoVitae.rl("neovitae/first_ritual").toString());

            AdvancementHolder wellOfSuffering = Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.well_of_suffering.title"),
                            Component.translatable("advancements.neovitae.well_of_suffering.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:well_of_suffering")))
                    .save(saver, NeoVitae.rl("neovitae/well_of_suffering").toString());

            AdvancementHolder edgeOfHiddenRealm = Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.edge_of_hidden_realm.title"),
                            Component.translatable("advancements.neovitae.edge_of_hidden_realm.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:simple_dungeon")))
                    .save(saver, NeoVitae.rl("neovitae/edge_of_hidden_realm").toString());

            // Crystallum Fractura activation (off firstRitual)
            AdvancementHolder crystallumFractura = Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get(),
                            Component.translatable("advancements.neovitae.crystallum_fractura.title"),
                            Component.translatable("advancements.neovitae.crystallum_fractura.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:crystallum_fractura")))
                    .save(saver, NeoVitae.rl("neovitae/crystallum_fractura").toString());

            // Catalyst transmutation chain (off crystallumFractura)
            AdvancementHolder transmuteRuina = Advancement.Builder.advancement()
                    .parent(crystallumFractura)
                    .display(NVItems.SPIRITUS_RUINA_CATALYST.get(),
                            Component.translatable("advancements.neovitae.transmute_ruina.title"),
                            Component.translatable("advancements.neovitae.transmute_ruina.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("transmute", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.RUINA)))
                    .save(saver, NeoVitae.rl("neovitae/transmute_ruina").toString());

            Advancement.Builder.advancement()
                    .parent(transmuteRuina)
                    .display(NVItems.SPIRITUS_NIHILUM_CATALYST.get(),
                            Component.translatable("advancements.neovitae.transmute_nihilum.title"),
                            Component.translatable("advancements.neovitae.transmute_nihilum.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("transmute", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.NIHILUM)))
                    .save(saver, NeoVitae.rl("neovitae/transmute_nihilum").toString());

            Advancement.Builder.advancement()
                    .parent(transmuteRuina)
                    .display(NVItems.SPIRITUS_VINDICTA_CATALYST.get(),
                            Component.translatable("advancements.neovitae.transmute_vindicta.title"),
                            Component.translatable("advancements.neovitae.transmute_vindicta.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("transmute", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.VINDICTA)))
                    .save(saver, NeoVitae.rl("neovitae/transmute_vindicta").toString());

            Advancement.Builder.advancement()
                    .parent(transmuteRuina)
                    .display(NVItems.SPIRITUS_INVICTUS_CATALYST.get(),
                            Component.translatable("advancements.neovitae.transmute_invictus.title"),
                            Component.translatable("advancements.neovitae.transmute_invictus.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("transmute", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.INVICTUS)))
                    .save(saver, NeoVitae.rl("neovitae/transmute_invictus").toString());

            Advancement.Builder.advancement()
                    .parent(transmuteRuina)
                    .display(NVItems.SPIRITUS_INVICTUS_CATALYST.get(),
                            Component.translatable("advancements.neovitae.aspectum_omnia.title"),
                            Component.translatable("advancements.neovitae.aspectum_omnia.description"),
                            null, AdvancementType.CHALLENGE, true, true, true)
                    .addCriterion("ruina", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.RUINA)))
                    .addCriterion("nihilum", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.NIHILUM)))
                    .addCriterion("vindicta", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.VINDICTA)))
                    .addCriterion("invictus", NVCriteriaTriggers.CATALYST_TRANSMUTE.get().createCriterion(CatalystTransmuteTrigger.forAspect(SpiritusType.INVICTUS)))
                    .save(saver, NeoVitae.rl("neovitae/aspectum_omnia").toString());

            AdvancementHolder serenadeOfNether = Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.serenade_of_nether.title"),
                            Component.translatable("advancements.neovitae.serenade_of_nether.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:lava")))
                    .save(saver, NeoVitae.rl("neovitae/serenade_of_nether").toString());

            Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.meteor.title"),
                            Component.translatable("advancements.neovitae.meteor.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("activate", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:meteor")))
                    .save(saver, NeoVitae.rl("neovitae/meteor").toString());

            Advancement.Builder.advancement()
                    .parent(firstRitual)
                    .display(NVBlocks.MASTER_RITUAL_STONE,
                            Component.translatable("advancements.neovitae.master_of_ceremonies.title"),
                            Component.translatable("advancements.neovitae.master_of_ceremonies.description"),
                            null, AdvancementType.CHALLENGE, true, true, true)
                    .addCriterion("suffering", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:well_of_suffering")))
                    .addCriterion("dungeon", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:simple_dungeon")))
                    .addCriterion("crystal", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:crystallum_fractura")))
                    .addCriterion("lava", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:lava")))
                    .addCriterion("meteor", NVCriteriaTriggers.RITUAL_ACTIVATED.get().createCriterion(RitualActivatedTrigger.forRitual("neovitae:meteor")))
                    .save(saver, NeoVitae.rl("neovitae/master_of_ceremonies").toString());

            // Teleposer (off demonic_slate)
            Advancement.Builder.advancement()
                    .parent(demonicSlate)
                    .display(NVBlocks.TELEPOSER,
                            Component.translatable("advancements.neovitae.teleposer.title"),
                            Component.translatable("advancements.neovitae.teleposer.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_teleposer", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.TELEPOSER))
                    .save(saver, NeoVitae.rl("neovitae/teleposer").toString());

            // Spiritus Branch (off root)
            AdvancementHolder throwingDagger = Advancement.Builder.advancement()
                    .parent(root)
                    .display(NVItems.THROWING_DAGGER.get(),
                            Component.translatable("advancements.neovitae.throwing_dagger.title"),
                            Component.translatable("advancements.neovitae.throwing_dagger.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_dagger", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.THROWING_DAGGER.get()))
                    .save(saver, NeoVitae.rl("neovitae/throwing_dagger").toString());

            AdvancementHolder spiritus = Advancement.Builder.advancement()
                    .parent(throwingDagger)
                    .display(NVItems.MONSTER_SOUL_RAW.get(),
                            Component.translatable("advancements.neovitae.spiritus.title"),
                            Component.translatable("advancements.neovitae.spiritus.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_soul", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.MONSTER_SOUL_RAW.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus").toString());

            AdvancementHolder spiritusGemPetty = Advancement.Builder.advancement()
                    .parent(spiritus)
                    .display(NVItems.SPIRITUS_GEM_PETTY.get(),
                            Component.translatable("advancements.neovitae.spiritus_gem_petty.title"),
                            Component.translatable("advancements.neovitae.spiritus_gem_petty.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_gem", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SPIRITUS_GEM_PETTY.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus_gem_petty").toString());

            AdvancementHolder spiritusGemLesser = Advancement.Builder.advancement()
                    .parent(spiritusGemPetty)
                    .display(NVItems.SPIRITUS_GEM_LESSER.get(),
                            Component.translatable("advancements.neovitae.spiritus_gem_lesser.title"),
                            Component.translatable("advancements.neovitae.spiritus_gem_lesser.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_gem", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SPIRITUS_GEM_LESSER.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus_gem_lesser").toString());

            AdvancementHolder spiritusGemCommon = Advancement.Builder.advancement()
                    .parent(spiritusGemLesser)
                    .display(NVItems.SPIRITUS_GEM_COMMON.get(),
                            Component.translatable("advancements.neovitae.spiritus_gem_common.title"),
                            Component.translatable("advancements.neovitae.spiritus_gem_common.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_gem", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SPIRITUS_GEM_COMMON.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus_gem_common").toString());

            AdvancementHolder spiritusGemGreater = Advancement.Builder.advancement()
                    .parent(spiritusGemCommon)
                    .display(NVItems.SPIRITUS_GEM_GREATER.get(),
                            Component.translatable("advancements.neovitae.spiritus_gem_greater.title"),
                            Component.translatable("advancements.neovitae.spiritus_gem_greater.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_gem", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SPIRITUS_GEM_GREATER.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus_gem_greater").toString());

            Advancement.Builder.advancement()
                    .parent(spiritusGemGreater)
                    .display(NVItems.SPIRITUS_GEM_GRAND.get(),
                            Component.translatable("advancements.neovitae.spiritus_gem_grand.title"),
                            Component.translatable("advancements.neovitae.spiritus_gem_grand.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("get_gem", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SPIRITUS_GEM_GRAND.get()))
                    .save(saver, NeoVitae.rl("neovitae/spiritus_gem_grand").toString());

            // Sentient Sword (off spiritus_gem_petty)
            Advancement.Builder.advancement()
                    .parent(spiritusGemPetty)
                    .display(NVItems.SENTIENT_SWORD.get(),
                            Component.translatable("advancements.neovitae.sentient_sword.title"),
                            Component.translatable("advancements.neovitae.sentient_sword.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_sword", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SENTIENT_SWORD.get()))
                    .save(saver, NeoVitae.rl("neovitae/sentient_sword").toString());

            // Hellfire Forge (off spiritus_gem_petty)
            AdvancementHolder hellfireForge = Advancement.Builder.advancement()
                    .parent(spiritusGemPetty)
                    .display(NVBlocks.HELLFIRE_FORGE,
                            Component.translatable("advancements.neovitae.hellfire_forge.title"),
                            Component.translatable("advancements.neovitae.hellfire_forge.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_forge", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.HELLFIRE_FORGE))
                    .save(saver, NeoVitae.rl("neovitae/hellfire_forge").toString());

            // Demon Branch (off hellfire_forge)
            Advancement.Builder.advancement()
                    .parent(hellfireForge)
                    .display(NVBlocks.VAS_MALEFICUM,
                            Component.translatable("advancements.neovitae.vas_maleficum.title"),
                            Component.translatable("advancements.neovitae.vas_maleficum.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_crucible", InventoryChangeTrigger.TriggerInstance.hasItems(NVBlocks.VAS_MALEFICUM))
                    .save(saver, NeoVitae.rl("neovitae/vas_maleficum").toString());

            Advancement.Builder.advancement()
                    .parent(hellfireForge)
                    .display(NVItems.SENTIENT_PLATE.get(),
                            Component.translatable("advancements.neovitae.sentient_armor.title"),
                            Component.translatable("advancements.neovitae.sentient_armor.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_armor", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.SENTIENT_PLATE.get()))
                    .save(saver, NeoVitae.rl("neovitae/sentient_armor").toString());

            Advancement.Builder.advancement()
                    .parent(weakBloodOrb)
                    .display(NVItems.ARCANE_SCRIBE_TOOL.get(),
                            Component.translatable("advancements.neovitae.arcane_scribe.title"),
                            Component.translatable("advancements.neovitae.arcane_scribe.description"),
                            null, AdvancementType.GOAL, true, true, false)
                    .addCriterion("get_scribe", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ARCANE_SCRIBE_TOOL.get()))
                    .save(saver, NeoVitae.rl("neovitae/arcane_scribe").toString());

            AdvancementHolder demonite = Advancement.Builder.advancement()
                    .parent(hellfireForge)
                    .display(NVItems.DEMONITE_RAW.get(),
                            Component.translatable("advancements.neovitae.demonite.title"),
                            Component.translatable("advancements.neovitae.demonite.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_demonite", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.DEMONITE_RAW.get()))
                    .save(saver, NeoVitae.rl("neovitae/demonite").toString());

            Advancement.Builder.advancement()
                    .parent(demonite)
                    .display(NVItems.HELLFORGED_INGOT.get(),
                            Component.translatable("advancements.neovitae.hellforged_ingot.title"),
                            Component.translatable("advancements.neovitae.hellforged_ingot.description"),
                            null, AdvancementType.CHALLENGE, true, true, false)
                    .addCriterion("get_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.HELLFORGED_INGOT.get()))
                    .save(saver, NeoVitae.rl("neovitae/hellforged_ingot").toString());

            // Self-sacrifice progression (off root)
            AdvancementHolder selfSacrifice = Advancement.Builder.advancement()
                    .parent(root)
                    .display(NVItems.ORB_WEAK.get(),
                            Component.translatable("advancements.neovitae.self_sacrifice.title"),
                            Component.translatable("advancements.neovitae.self_sacrifice.description"),
                            null, AdvancementType.TASK, true, false, false)
                    .addCriterion("get_orb", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.ORB_WEAK.get()))
                    .save(saver, NeoVitae.rl("neovitae/self_sacrifice").toString());


            // Epic Hidden Achievement (off transcendent_blood_orb)
            Advancement.Builder.advancement()
                    .parent(transcendentBloodOrb)
                    .display(NVItems.BLOOD_SWEAT_AND_TEARS.get(),
                            Component.translatable("advancements.neovitae.blood_sweat_and_tears.title"),
                            Component.translatable("advancements.neovitae.blood_sweat_and_tears.description"),
                            null, AdvancementType.CHALLENGE, true, true, true)
                    .addCriterion("get_record", InventoryChangeTrigger.TriggerInstance.hasItems(NVItems.BLOOD_SWEAT_AND_TEARS.get()))
                    .save(saver, NeoVitae.rl("neovitae/blood_sweat_and_tears").toString());
        }
    }
}
