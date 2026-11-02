package com.breakinblocks.neovitae.gametest;

import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipeInput;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.Optional;
import java.util.UUID;

public final class UnboundedFlightTests {

    private UnboundedFlightTests() {}

    private static final UUID CRAFTER_UUID = UUID.fromString("0f4d2ab3-5e6c-4f1a-9d8b-7c2e4a6b0d41");
    private static final Binding CRAFTER_BINDING = new Binding(CRAFTER_UUID, "UnboundedFlightCrafter");

    private static Holder<SentientUpgrade> unboundedFlight(GameTestHelper helper) {
        return helper.getLevel().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                .getOrThrow(ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, NeoVitae.rl("unbounded_flight")));
    }

    private static ItemStack tome(Holder<SentientUpgrade> upgrade) {
        ItemStack tome = new ItemStack(NVItems.UPGRADE_TOME.get());
        tome.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgrade, 1.0f));
        return tome;
    }

    private static Player fullSetWearer(GameTestHelper helper, int maxPoints, Vec3 relativePos) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack chest = new ItemStack(NVItems.SENTIENT_PLATE.get());
        SentientHelper.setDefaultSentient(chest, helper.getLevel().registryAccess());
        chest.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, maxPoints);
        player.setItemSlot(EquipmentSlot.CHEST, chest);
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(NVItems.SENTIENT_HELMET.get()));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(NVItems.SENTIENT_LEGGINGS.get()));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(NVItems.SENTIENT_BOOTS.get()));
        Vec3 pos = helper.absoluteVec(relativePos);
        player.setPos(pos.x, pos.y, pos.z);
        helper.getLevel().addFreshEntity(player);
        return player;
    }

    private static float upgradeExp(Player player, Holder<SentientUpgrade> upgrade) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY)
                .upgrades().getOrDefault(upgrade, 0f);
    }

    public static void register(NVTestRegistrar r) {
        r.add("unbounded_flight/curios_sockets_survive_reload", 40, UnboundedFlightTests::curiosSocketsSurviveReloadAndReequip);
        r.add("unbounded_flight/athanor_recipe_makes_deus_shard", 20, helper -> {
            ItemStack[] inputs = new ItemStack[AthanorRecipe.MAX_INPUTS];
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = i < 4 ? new ItemStack(NVItems.REVENANT_PLATING.get()) : ItemStack.EMPTY;
            }
            inputs[4] = new ItemStack(Items.NETHER_STAR);
            AthanorRecipeInput input = new AthanorRecipeInput(new ItemStack(NVItems.ORB_TRANSCENDENT.get()), inputs,
                    new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 10000));
            Optional<RecipeHolder<AthanorRecipe>> match = helper.getLevel().recipeAccess()
                    .getRecipeFor(NVRecipes.ATHANOR_TYPE.get(), input, helper.getLevel());
            helper.assertTrue(match.isPresent(), "Four Revenant Plating and a Nether Star should match an Athanor recipe");
            AthanorRecipe recipe = match.get().value();
            helper.assertTrue(recipe.getGuaranteedOutput().stream().anyMatch(t -> t.create().is(NVItems.DEUS_SHARD.get())),
                    "The recipe should make a Deus Shard, got " + recipe.getGuaranteedOutput());
            for (SpiritusType type : SpiritusType.values()) {
                helper.assertTrue(recipe.getSpiritusCosts().getOrDefault(type, 0.0) == 20.0, "Expected 20 " + type + " spiritus");
            }
            inputs[3] = ItemStack.EMPTY;
            helper.assertTrue(helper.getLevel().recipeAccess().getRecipeFor(NVRecipes.ATHANOR_TYPE.get(), input, helper.getLevel()).isEmpty(),
                    "Three Revenant Plating should not match");
            helper.succeed();
        });

        r.add("unbounded_flight/tabula_crafts_tome_from_deus_shard", 400, helper -> {
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            BlockPos tablePos = new BlockPos(3, 1, 2);
            helper.setBlock(tablePos, NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
            TabulaVitaeBlockEntity table = helper.getBlockEntity(tablePos, TabulaVitaeBlockEntity.class);
            Holder<SentientUpgrade> upgrade = unboundedFlight(helper);

            helper.runAfterDelay(1, () -> {
                Anima anima = AnimaHelper.getAnima(CRAFTER_UUID);
                while (anima.getCurrentEV() > 0) {
                    anima.syphon(AnimaTicket.create(anima.getCurrentEV()));
                }
                anima.add(AnimaTicket.create(150000), 1000000);
                int before = anima.getCurrentEV();

                ItemStack orb = new ItemStack(NVItems.ORB_TRANSCENDENT.get());
                orb.set(NVDataComponents.BINDING, CRAFTER_BINDING);

                table.inv.setStackInSlot(0, new ItemStack(Items.ELYTRA));
                table.inv.setStackInSlot(1, new ItemStack(NVItems.DEUS_SHARD.get()));
                table.inv.setStackInSlot(2, new ItemStack(NVItems.PRISMATIC_SPIRITUS_GEM.get()));
                table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, orb);

                helper.succeedWhen(() -> {
                    ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                    UpgradeTome result = output.get(NVDataComponents.UPGRADE_TOME_DATA);
                    helper.assertTrue(result != null && result.upgrade().is(upgrade.getKey()),
                            "Expected a Tome of Unbounded Flight, got " + output);
                    int spent = before - anima.getCurrentEV();
                    helper.assertTrue(spent >= 100000, "Recipe costs 100000 EV, only " + spent + " was syphoned");
                });
            });
        });

        r.add("unbounded_flight/tome_rejected_before_deus_evolution", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 3), Blocks.STONE.defaultBlockState());
            Player player = fullSetWearer(helper, 500, new Vec3(3.5, 1, 3.5));
            Holder<SentientUpgrade> upgrade = unboundedFlight(helper);

            helper.runAfterDelay(2, () -> {
                player.setItemInHand(InteractionHand.MAIN_HAND, tome(upgrade));
                InteractionResult result = player.getMainHandItem().getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                helper.assertTrue(result == InteractionResult.FAIL, "Tome should be rejected at 500 points, got " + result);
                helper.assertTrue(upgradeExp(player, upgrade) == 0f, "Armor should not hold Unbounded Flight at 500 points");
                float applied = SentientHelper.applyExp(player, upgrade, 1.0f, true);
                helper.assertTrue(applied == 0f, "Direct application should also be rejected, applied " + applied);
                helper.succeed();
            });
        });

        r.add("unbounded_flight/tome_accepted_after_deus_evolution", 60, helper -> {
            helper.setBlock(new BlockPos(3, 0, 3), Blocks.STONE.defaultBlockState());
            Player player = fullSetWearer(helper, 600, new Vec3(3.5, 1, 3.5));
            Holder<SentientUpgrade> upgrade = unboundedFlight(helper);

            helper.runAfterDelay(2, () -> {
                player.setItemInHand(InteractionHand.MAIN_HAND, tome(upgrade));
                player.getMainHandItem().getItem().use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                helper.assertTrue(upgradeExp(player, upgrade) >= 1f, "Armor at 600 points should accept the tome");
                int points = player.getItemBySlot(EquipmentSlot.CHEST).getOrDefault(NVDataComponents.CURRENT_UPGRADE_POINTS, 0);
                helper.assertTrue(points >= 20, "Unbounded Flight should cost 20 points, armor shows " + points);
                helper.succeedWhen(() -> helper.assertTrue(player.mayFly(), "Wearer should be able to fly"));
            });
        });

        r.add("unbounded_flight/drains_ev_while_flying_then_drops", 200, helper -> {
            Player player = fullSetWearer(helper, 600, new Vec3(3.5, 4, 3.5));
            Holder<SentientUpgrade> upgrade = unboundedFlight(helper);
            player.setNoGravity(true);

            helper.runAfterDelay(2, () -> {
                SentientHelper.applyExp(player, upgrade, 1.0f, true);
                Anima anima = AnimaHelper.getAnima(player);
                anima.add(AnimaTicket.create(100), 100000);

                helper.runAfterDelay(2, () -> {
                    player.getAbilities().flying = true;
                    player.onUpdateAbilities();

                    helper.succeedWhen(() -> {
                        helper.assertTrue(anima.getCurrentEV() < 5, "Flight should drain the network, " + anima.getCurrentEV() + " EV left");
                        helper.assertTrue(!player.getAbilities().flying, "Flight should end once the network is empty");
                    });
                });
            });
        });
    }
    private static void curiosSocketsSurviveReloadAndReequip(GameTestHelper helper) {
        Player player = fullSetWearer(helper, 1000, new Vec3(2, 1, 2));
        CuriosApi.getCuriosInventory(player).orElseThrow().loadDatapacks();
        Holder<SentientUpgrade> upgrade = helper.getLevel().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).getOrThrow(CuriosCompat.CURIOS_SOCKET_UPGRADE);
        SentientHelper.applyExpToCap(player, upgrade, 100000, true);
        helper.assertTrue(CuriosCompat.recalculateCuriosSlots(player) > 0, "Socket upgrade should grant slots: fullSet=" + SentientHelper.hasFullSet(player) + ", xp=" + upgradeExp(player, upgrade) + ", slots=" + CuriosApi.getCuriosInventory(player).map(inv -> inv.getCurios().keySet().toString()).orElse("no inventory"));
        ICurioStacksHandler sockets = CuriosApi.getCuriosInventory(player).orElseThrow()
                .getCurios().get(CuriosCompat.SENTIENT_ARMOUR_SOCKET_SLOT);
        sockets.update();
        int count = sockets.getSlots();
        helper.assertTrue(count > 0, "Socket slots should exist");
        helper.assertTrue(!sockets.getPermanentModifiers().isEmpty(), "Socket capacity must persist with the Curios inventory");
        TagValueOutput saved = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        sockets.serialize(saved);
        sockets.clearModifiers();
        sockets.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), saved.buildResult()));
        sockets.update();
        helper.assertTrue(sockets.getSlots() == count, "Saved sockets must load before the first equipment tick");
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        CuriosCompat.recalculateCuriosSlots(player);
        sockets.update();
        helper.assertTrue(sockets.getSlots() == 0, "Removing the set should remove bonus sockets");
        player.setItemSlot(EquipmentSlot.HEAD, helmet);
        CuriosCompat.recalculateCuriosSlots(player);
        sockets.update();
        helper.assertTrue(sockets.getSlots() == count, "Reequipping after lost modifiers must restore sockets");
        helper.succeed();
    }
}
