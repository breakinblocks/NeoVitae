package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectLiquifiedExperience;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.TeleposerBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public final class GameplayRegressionTests {
    private GameplayRegressionTests() {}

    private static void convert(String method, ResourceHandler<ItemResource> items,
                                ResourceHandler<FluidResource> fluids) {
        try {
            Method m = AlchemyArrayEffectLiquifiedExperience.class.getDeclaredMethod(method,
                    ResourceHandler.class, ResourceHandler.class, Fluid.class, int.class);
            m.setAccessible(true);
            m.invoke(null, items, fluids, NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get(), 20);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void link(TeleposerBlockEntity tile, ServerLevel target, BlockPos pos) {
        ItemStack focus = new ItemStack(NVItems.TELEPOSER_FOCUS.get());
        Binding binding = new Binding(UUID.randomUUID(), "Regression Test");
        focus.set(NVDataComponents.BINDING, binding);
        NVItems.TELEPOSER_FOCUS.get().setStoredPos(focus, pos);
        NVItems.TELEPOSER_FOCUS.get().setWorld(focus, target);
        AnimaHelper.getAnima(binding).add(AnimaTicket.create(100000), 100000);
        tile.inv.setStackInSlot(0, focus);
    }

    public static void register(NVTestRegistrar r) {
        r.addIsolated("regression/powered_master_stops_routing", 80, h -> {
            RoutingNodeTests.RoutingTestContext network;
            try {
                Method m = RoutingNodeTests.class.getDeclaredMethod("setupLinearNetwork", GameTestHelper.class);
                m.setAccessible(true);
                network = (RoutingNodeTests.RoutingTestContext) m.invoke(null, h);
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            MasterRoutingNodeBlockEntity master = h.getBlockEntity(network.master(), MasterRoutingNodeBlockEntity.class);
            try {
                var signal = MasterRoutingNodeBlockEntity.class.getDeclaredField("currentInput");
                signal.setAccessible(true);
                signal.setInt(master, 15);
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            if (master.isConnectionEnabled(h.absolutePos(network.input()))) throw new IllegalStateException("Master signal setup failed");
            if (master.isConnectedViaGraph(h.absolutePos(network.input()))) {
                h.fail("Routing graph reports a path through master despite master.isConnectionEnabled=false");
                return;
            }
            h.assertTrue(!master.isConnectedViaGraph(master.getBlockPos()), "Powered master must reject its own position too");
            h.succeed();
        });
        r.add("regression/crystal_catalyst_initializes_conversion", 20, h -> {
            SpiritusCrystalBlockEntity crystal = new SpiritusCrystalBlockEntity(BlockPos.ZERO,
                    NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get().defaultBlockState());
            crystal.applyCatalyst(200, 10, 25);
            if (crystal.appliedConversionRate != 25) {
                h.fail("First catalyst left conversion rate at " + crystal.appliedConversionRate + ", expected 25");
                return;
            }
            crystal.applyCatalyst(100, 5, 30);
            h.assertTrue(crystal.appliedConversionRate == 25 && crystal.speedModifier == 10
                    && crystal.injectedSpiritus == 300, "Additional catalysts must retain the best rates and add reserves");
            h.succeed();
        });
        r.addIsolated("regression/crystal_catalyst_expires", 20, h -> {
            BlockPos pos = new BlockPos(2, 1, 2);
            h.setBlock(pos.below(), Blocks.STONE);
            h.setBlock(pos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get());
            h.getLevel().getChunkAt(h.absolutePos(pos)).setData(NVDataAttachments.SPIRITUS_CHUNK.get(),
                    new SpiritusChunk(100, 0, 0, 0, 0));
            SpiritusCrystalBlockEntity crystal = h.getBlockEntity(pos, SpiritusCrystalBlockEntity.class);
            crystal.applyCatalyst(0.000001, 10, 25);
            crystal.internalCounter = 19;
            SpiritusCrystalBlockEntity.tick(h.getLevel(), h.absolutePos(pos), h.getBlockState(pos), crystal);
            h.assertTrue(crystal.injectedSpiritus == 0 && crystal.appliedConversionRate == 0
                    && crystal.speedModifier == 1, "Exhausted catalyst must reset conversion and speed");
            h.succeed();
        });
        r.add("regression/athanor_last_durability_persisted", 20, h -> {
            BlockPos relative = new BlockPos(2, 1, 2);
            h.setBlock(relative, NVBlocks.ATHANOR_BLOCK.block().get());
            AthanorBlockEntity tile = h.getBlockEntity(relative, AthanorBlockEntity.class);
            ItemStack cell = new ItemStack(NVItems.PRIMITIVE_FURNACE_CELL.get());
            cell.setDamageValue(cell.getMaxDamage() - 1);
            tile.athanorInv.setStackInSlot(AthanorBlockEntity.TOOL_SLOT, cell);
            try {
                Method m = AthanorBlockEntity.class.getDeclaredMethod("damageTool");
                m.setAccessible(true);
                m.invoke(tile);
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            ItemStack saved = tile.athanorInv.getStackInSlot(AthanorBlockEntity.TOOL_SLOT);
            if (!saved.isEmpty() && saved.getDamageValue() < saved.getMaxDamage()) {
                h.fail("Final durability point not consumed: damage=" + saved.getDamageValue() + "/" + saved.getMaxDamage());
                return;
            }
            h.succeed();
        });
        r.add("regression/xp_transfer_atomic", 20, h -> {
            ItemStacksResourceHandler items = new ItemStacksResourceHandler(1) {
                @Override public boolean isValid(int slot, ItemResource resource) { return false; }
            };
            ItemStack tome = new ItemStack(NVItems.EXPERIENCE_TOME.get());
            ExperienceTomeItem.addXpToTome(tome, 10);
            items.set(0, ItemResource.of(tome), 1);
            FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(1, 10000);
            convert("drainTomes", items, fluids);
            int xp = ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1));
            if (xp * 20 + fluids.getAmountAsInt(0) != 200) {
                h.fail("Conservation failed: tome=" + xp + " XP, tank=" + fluids.getAmountAsInt(0) + " mB (started with 10 XP)");
                return;
            }
            h.succeed();
        });
        r.add("regression/xp_fraction_preserved", 20, h -> {
            ItemStacksResourceHandler items = new ItemStacksResourceHandler(1);
            items.set(0, ItemResource.of(new ItemStack(NVItems.EXPERIENCE_TOME.get())), 1);
            FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(1, 10000);
            fluids.set(0, FluidResource.of(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get()), 39);
            convert("fillTomes", items, fluids);
            int xp = ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1));
            if (xp != 1 || fluids.getAmountAsInt(0) != 19) {
                h.fail("Conservation failed: tome=" + xp + " XP, tank=" + fluids.getAmountAsInt(0) + " mB (started with 39 mB)");
                return;
            }
            h.succeed();
        });
        r.add("regression/xp_reverse_transfer_atomic", 20, h -> {
            ItemStacksResourceHandler items = new ItemStacksResourceHandler(1) {
                @Override public boolean isValid(int slot, ItemResource resource) { return false; }
            };
            items.set(0, ItemResource.of(new ItemStack(NVItems.EXPERIENCE_TOME.get())), 1);
            FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(1, 10000);
            fluids.set(0, FluidResource.of(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get()), 200);
            convert("fillTomes", items, fluids);
            h.assertTrue(ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1)) == 0
                    && fluids.getAmountAsInt(0) == 200, "Rejected tome replacement must roll back fluid extraction");
            h.succeed();
        });
        r.add("regression/xp_round_trip_with_partial_tank", 20, h -> {
            ItemStacksResourceHandler items = new ItemStacksResourceHandler(1);
            ItemStack tome = new ItemStack(NVItems.EXPERIENCE_TOME.get());
            ExperienceTomeItem.addXpToTome(tome, 10);
            items.set(0, ItemResource.of(tome), 1);
            FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(1, 39);
            convert("drainTomes", items, fluids);
            h.assertTrue(ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1)) == 9
                    && fluids.getAmountAsInt(0) == 20, "Only a whole XP point should fit in a 39 mB tank");
            convert("fillTomes", items, fluids);
            h.assertTrue(ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1)) == 10
                    && fluids.getAmountAsInt(0) == 0, "Round trip must restore the original XP");
            h.succeed();
        });
        r.add("regression/xp_fraction_across_tanks", 20, h -> {
            ItemStacksResourceHandler items = new ItemStacksResourceHandler(1);
            items.set(0, ItemResource.of(new ItemStack(NVItems.EXPERIENCE_TOME.get())), 1);
            FluidStacksResourceHandler fluids = new FluidStacksResourceHandler(2, 10000);
            FluidResource xpFluid = FluidResource.of(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get());
            fluids.set(0, xpFluid, 19);
            fluids.set(1, xpFluid, 20);
            convert("fillTomes", items, fluids);
            h.assertTrue(ExperienceTomeItem.getStoredXp(items.getResource(0).toStack(1)) == 1
                    && fluids.getAmountAsInt(0) + fluids.getAmountAsInt(1) == 19,
                    "Split tanks must preserve fractional XP");
            h.succeed();
        });
        r.add("regression/xp_level_boundaries", 20, h -> {
            int[][] cases = {{-1, 0}, {0, 0}, {6, 0}, {7, 1}, {352, 16}, {393, 16},
                    {394, 17}, {1507, 31}, {1627, 31}, {1628, 32}, {1000, 26}, {Integer.MAX_VALUE, 21863}};
            for (int[] entry : cases) {
                h.assertTrue(ExperienceTomeItem.getLevelForXp(entry[0]) == entry[1],
                        "Wrong level for " + entry[0] + " XP");
            }
            h.succeed();
        });
        r.add("regression/xp_deposit_respects_tome_capacity", 20, h -> {
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            player.giveExperiencePoints(10);
            ItemStack tome = new ItemStack(NVItems.EXPERIENCE_TOME.get());
            ExperienceTomeItem.addXpToTome(tome, Integer.MAX_VALUE - 1);
            h.assertTrue(ExperienceTomeItem.depositLevels(player, tome, -1) == 1,
                    "Deposit must stop at the tome's remaining capacity");
            h.assertTrue(ExperienceTomeItem.getStoredXp(tome) == Integer.MAX_VALUE && player.totalExperience == 9,
                    "Overflowing deposit must preserve the remaining player XP");
            h.succeed();
        });
        r.add("regression/holding_persists_bound_destination", 20, h -> {
            BlockPos relative = new BlockPos(2, 1, 2);
            BlockPos target = h.absolutePos(relative);
            h.setBlock(relative, NVBlocks.TELEPOSER.block().get());
            var player = h.makeMockPlayer(GameType.CREATIVE);
            ItemStack holding = new ItemStack(NVItems.SIGIL_HOLDING.get());
            ItemStack inner = new ItemStack(NVItems.SIGIL_TELEPOSITION.get());
            inner.set(NVDataComponents.BINDING, new Binding(player.getUUID(), "Regression Test"));
            NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
            inventory.set(0, inner);
            ((ItemSigilHolding) holding.getItem()).saveInventory(holding, inventory);
            player.setItemInHand(InteractionHand.MAIN_HAND, holding);
            holding.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false)));
            BlockPos saved = ItemSigilHolding.getInternalInventory(holding).get(0).get(NVDataComponents.TELEPOSER_POS);
            if (!target.equals(saved)) {
                h.fail("Stored sigil destination was lost: " + saved);
                return;
            }
            h.succeed();
        });
        r.add("regression/holding_persists_air_use", 20, h -> {
            var player = h.makeMockPlayer(GameType.CREATIVE);
            player.setPos(Vec3.atCenterOf(h.absolutePos(new BlockPos(2, 2, 2))));
            player.setXRot(-90);
            ItemStack holding = new ItemStack(NVItems.SIGIL_HOLDING.get());
            ItemStack inner = new ItemStack(NVItems.SIGIL_BLOOD_LIGHT.get());
            inner.set(NVDataComponents.BINDING, new Binding(player.getUUID(), "Regression Test"));
            inner.set(NVDataComponents.BLOOD_LIGHT_RAINBOW, true);
            inner.remove(NVDataComponents.BLOOD_LIGHT_COLOR);
            NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
            inventory.set(0, inner);
            NVItems.SIGIL_HOLDING.get().saveInventory(holding, inventory);
            player.setItemInHand(InteractionHand.MAIN_HAND, holding);
            holding.getItem().use(h.getLevel(), player, InteractionHand.MAIN_HAND);
            h.assertTrue(ItemSigilHolding.getInternalInventory(holding).get(0).has(NVDataComponents.BLOOD_LIGHT_COLOR),
                    "Rainbow color chosen during air use must be saved to the contained sigil");
            h.assertTrue(player.getMainHandItem() == holding, "Air use must retain the holding item in hand");
            h.succeed();
        });
        r.add("regression/holding_persists_tick_deactivation", 20, h -> {
            var player = RitualHelper.createRitualFakePlayer(h.getLevel(), UUID.randomUUID(),
                    "Regression Fake Player", h.absolutePos(BlockPos.ZERO));
            ItemStack holding = new ItemStack(NVItems.SIGIL_HOLDING.get());
            ItemStack inner = new ItemStack(NVItems.SIGIL_FAST_MINER.get());
            Binding owner = new Binding(UUID.randomUUID(), "Regression Test");
            inner.set(NVDataComponents.BINDING, owner);
            AnimaHelper.getAnima(owner).set(AnimaTicket.create(0), Integer.MAX_VALUE);
            NVItems.SIGIL_FAST_MINER.get().setActivatedState(inner, true);
            NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
            inventory.set(0, inner);
            NVItems.SIGIL_HOLDING.get().saveInventory(holding, inventory);
            player.tickCount = 0;
            holding.getItem().inventoryTick(holding, h.getLevel(), player, EquipmentSlot.MAINHAND);
            h.assertTrue(!NVItems.SIGIL_FAST_MINER.get().getActivated(ItemSigilHolding.getInternalInventory(holding).get(0)),
                    "Contained sigil must remain deactivated after failing its upkeep payment");
            h.succeed();
        });
        r.add("regression/teleposer_equal_coordinates_other_dimension", 20, h -> {
            BlockPos relative = new BlockPos(2, 1, 2);
            BlockPos source = h.absolutePos(relative).atY(80);
            ServerLevel other = h.getLevel().getServer().getLevel(Level.NETHER);
            h.getLevel().setBlockAndUpdate(source, NVBlocks.TELEPOSER.block().get().defaultBlockState());
            h.getLevel().setBlockAndUpdate(source.above(), Blocks.DIAMOND_BLOCK.defaultBlockState());
            other.setBlockAndUpdate(source, NVBlocks.TELEPOSER.block().get().defaultBlockState());
            other.setBlockAndUpdate(source.above(), Blocks.AIR.defaultBlockState());
            if (!(other.getBlockEntity(source) instanceof TeleposerBlockEntity)) throw new IllegalStateException("Target setup failed");
            TeleposerBlockEntity tile = (TeleposerBlockEntity) h.getLevel().getBlockEntity(source);
            link(tile, other, source);
            tile.initiateTeleport();
            boolean moved = other.getBlockState(source.above()).is(Blocks.DIAMOND_BLOCK);
            other.setBlockAndUpdate(source, Blocks.AIR.defaultBlockState());
            other.setBlockAndUpdate(source.above(), Blocks.AIR.defaultBlockState());
            h.getLevel().setBlockAndUpdate(source, Blocks.AIR.defaultBlockState());
            h.getLevel().setBlockAndUpdate(source.above(), Blocks.AIR.defaultBlockState());
            if (!moved) { h.fail("Cross-dimension transfer rejected for equal BlockPos"); return; }
            h.succeed();
        });
        r.addIsolated("regression/teleposer_entity_crosses_dimension", 80, h -> {
            BlockPos relative = new BlockPos(2, 1, 2);
            BlockPos source = h.absolutePos(relative);
            BlockPos target = source.offset(10, 0, 0).atY(80);
            ServerLevel other = h.getLevel().getServer().getLevel(Level.NETHER);
            h.setBlock(relative, NVBlocks.TELEPOSER.block().get());
            h.setBlock(relative.above(), Blocks.AIR);
            other.setBlockAndUpdate(target, NVBlocks.TELEPOSER.block().get().defaultBlockState());
            other.setBlockAndUpdate(target.above(), Blocks.AIR.defaultBlockState());
            if (!(other.getBlockEntity(target) instanceof TeleposerBlockEntity)) throw new IllegalStateException("Target setup failed");
            TeleposerBlockEntity tile = h.getBlockEntity(relative, TeleposerBlockEntity.class);
            link(tile, other, target);
            boolean forced = other.setChunkForced(target.getX() >> 4, target.getZ() >> 4, true);
            // Entity queries only see tracked chunks. Let the remote chunk become
            // entity-ticking before populating either side of the transfer.
            h.runAfterDelay(10, () -> {
                var pig = EntityType.PIG.create(h.getLevel(), EntitySpawnReason.COMMAND);
                pig.setPos(source.getX() + 0.5, source.getY() + 1, source.getZ() + 0.5);
                h.getLevel().addFreshEntity(pig);
                UUID id = pig.getUUID();
                var passenger = EntityType.CHICKEN.create(h.getLevel(), EntitySpawnReason.COMMAND);
                passenger.setPos(pig.position());
                h.getLevel().addFreshEntity(passenger);
                passenger.startRiding(pig, true, false);
                UUID passengerId = passenger.getUUID();
                var returning = EntityType.COW.create(other, EntitySpawnReason.COMMAND);
                returning.setPos(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5);
                other.addFreshEntity(returning);
                UUID returningId = returning.getUUID();
                tile.initiateTeleport();
                boolean moved = other.getEntity(id) != null;
                boolean passengerMoved = other.getEntity(passengerId) != null
                        && other.getEntity(passengerId).getVehicle() == other.getEntity(id);
                boolean returned = h.getLevel().getEntity(returningId) != null;
                boolean positioned = moved && other.getEntity(id).position()
                        .distanceTo(new Vec3(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5)) < 0.01;
                String actual = pig.level().dimension().identifier() + " position=" + pig.position()
                        + " removed=" + pig.isRemoved() + " passengerMoved=" + passengerMoved + " returned=" + returned;
                pig.discard();
                passenger.discard();
                returning.discard();
                if (other.getEntity(id) != null) other.getEntity(id).discard();
                if (other.getEntity(passengerId) != null) other.getEntity(passengerId).discard();
                if (h.getLevel().getEntity(returningId) != null) h.getLevel().getEntity(returningId).discard();
                other.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
                if (forced) other.setChunkForced(target.getX() >> 4, target.getZ() >> 4, false);
                if (!moved) { h.fail("Pig stayed in " + actual + " instead of entering Nether"); return; }
                h.assertTrue(passengerMoved && positioned && returned,
                        "Teleposer must preserve mounts and offsets and transfer entities in both directions");
                h.succeed();
            });
        });
    }
}
