package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.altar.IAraVitae;
import com.breakinblocks.neovitae.api.capability.NVCapabilities;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.AthanorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.BloodBatteryBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiritCacheBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.OrbFluidCapability;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public final class CapabilityTests {

    private CapabilityTests() {}

    private static final BlockPos FLOOR = new BlockPos(3, 0, 2);
    private static final BlockPos TARGET = new BlockPos(3, 1, 2);

    public static void register(NVTestRegistrar r) {
        r.add("caps/hellfire_forge_item", 40, helper -> {
            helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
            helper.setBlock(TARGET, NVBlocks.HELLFIRE_FORGE.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                ResourceHandler<ItemResource> handler = helper.getLevel()
                        .getCapability(Capabilities.Item.BLOCK, helper.absolutePos(TARGET), null);
                if (handler == null) {
                    helper.fail("HellfireForge did not expose Item.BLOCK capability");
                    return;
                }

                ItemResource redstone = ItemResource.of(new ItemStack(Items.REDSTONE));
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = handler.insert(0, redstone, 4, tx);
                    if (inserted != 4) {
                        helper.fail("Expected to insert 4 redstone, got " + inserted);
                        return;
                    }
                    tx.commit();
                }

                HellfireForgeBlockEntity forge = helper.getBlockEntity(TARGET, HellfireForgeBlockEntity.class);
                if (forge == null) {
                    helper.fail("Expected HellfireForgeBlockEntity");
                    return;
                }
                ItemStack slot0 = forge.inv.getStackInSlot(0);
                if (!slot0.is(Items.REDSTONE) || slot0.getCount() != 4) {
                    helper.fail("BE slot 0 should hold 4 redstone after cap insert, has " + slot0);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("caps/athanor_fluid", 40, helper -> {
            helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
            helper.setBlock(TARGET, NVBlocks.ATHANOR_BLOCK.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                ResourceHandler<FluidResource> fluids = helper.getLevel()
                        .getCapability(Capabilities.Fluid.BLOCK, helper.absolutePos(TARGET), null);
                if (fluids == null) {
                    helper.fail("Athanor did not expose Fluid.BLOCK capability");
                    return;
                }

                int inserted;
                try (Transaction tx = Transaction.openRoot()) {
                    inserted = fluids.insert(0, FluidResource.of(Fluids.WATER), 1000, tx);
                    tx.commit();
                }
                if (inserted <= 0) {
                    helper.fail("Athanor should accept water via fluid cap, inserted=" + inserted);
                    return;
                }

                AthanorBlockEntity athanor = helper.getBlockEntity(TARGET, AthanorBlockEntity.class);
                if (athanor == null) {
                    helper.fail("Expected AthanorBlockEntity");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("caps/blood_battery_energy", 40, helper -> {
            helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
            helper.setBlock(TARGET, NVBlocks.BLOOD_BATTERY.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                EnergyHandler energy = helper.getLevel()
                        .getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(TARGET), null);
                if (energy == null) {
                    helper.fail("BloodBattery did not expose Energy.BLOCK capability");
                    return;
                }

                int received;
                try (Transaction tx = Transaction.openRoot()) {
                    received = energy.insert(500, tx);
                    tx.commit();
                }
                if (received <= 0) {
                    helper.fail("BloodBattery should accept energy, received=" + received);
                    return;
                }

                BloodBatteryBlockEntity battery = helper.getBlockEntity(TARGET, BloodBatteryBlockEntity.class);
                if (battery == null) {
                    helper.fail("Expected BloodBatteryBlockEntity");
                    return;
                }
                if (battery.getEnergyStored() != received) {
                    helper.fail("BE stored energy " + battery.getEnergyStored()
                            + " does not match cap-inserted " + received);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("caps/ara_vitae_all_three", 40, helper -> {
            helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
            helper.setBlock(TARGET, NVBlocks.ARA_VITAE.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                BlockPos abs = helper.absolutePos(TARGET);
                ResourceHandler<ItemResource> items = helper.getLevel()
                        .getCapability(Capabilities.Item.BLOCK, abs, null);
                ResourceHandler<FluidResource> fluids = helper.getLevel()
                        .getCapability(Capabilities.Fluid.BLOCK, abs, null);
                IAraVitae altar = helper.getLevel()
                        .getCapability(NVCapabilities.ARA_VITAE, abs, null);
                AraVitaeTile tile = helper.getBlockEntity(TARGET, AraVitaeTile.class);

                if (items == null) helper.fail("AraVitae missing Item.BLOCK");
                else if (fluids == null) helper.fail("AraVitae missing Fluid.BLOCK");
                else if (altar == null) helper.fail("AraVitae missing NVCapabilities.ARA_VITAE");
                else if (tile == null) helper.fail("Expected AraVitaeTile");
                else helper.succeed();
            });
        });

        r.add("caps/spirit_cache_vanilla_wrapper", 40, helper -> {
            helper.setBlock(FLOOR, Blocks.STONE.defaultBlockState());
            helper.setBlock(TARGET, NVBlocks.SPIRIT_CACHE.block().get().defaultBlockState());

            helper.runAfterDelay(2, () -> {
                ResourceHandler<ItemResource> handler = helper.getLevel()
                        .getCapability(Capabilities.Item.BLOCK, helper.absolutePos(TARGET), null);
                if (handler == null) {
                    helper.fail("SpiritCache did not expose Item.BLOCK via VanillaContainerWrapper");
                    return;
                }

                ItemResource diamond = ItemResource.of(new ItemStack(Items.DIAMOND));
                try (Transaction tx = Transaction.openRoot()) {
                    int inserted = handler.insert(0, diamond, 3, tx);
                    if (inserted != 3) {
                        helper.fail("Expected 3 diamonds inserted, got " + inserted);
                        return;
                    }
                    tx.commit();
                }

                SpiritCacheBlockEntity cache = helper.getBlockEntity(TARGET, SpiritCacheBlockEntity.class);
                if (cache == null) {
                    helper.fail("Expected SpiritCacheBlockEntity");
                    return;
                }
                ItemStack slot0 = cache.getItem(0);
                if (!slot0.is(Items.DIAMOND) || slot0.getCount() != 3) {
                    helper.fail("VanillaContainerWrapper did not bridge to Container, slot 0 = " + slot0);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("caps/orb_fluid_item", 40, helper -> {
            helper.runAfterDelay(2, () -> {
                ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
                ItemAccess access = ItemAccess.forStack(orb).oneByOne();
                ResourceHandler<FluidResource> fluids = access.getCapability(Capabilities.Fluid.ITEM);
                if (fluids == null) {
                    helper.fail("Weak blood orb did not expose Fluid.ITEM capability");
                    return;
                }
                if (!(fluids instanceof OrbFluidCapability)) {
                    helper.fail("Expected OrbFluidCapability, got " + fluids.getClass().getSimpleName());
                    return;
                }

                FluidResource essentia = FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get());
                int inserted;
                try (Transaction tx = Transaction.openRoot()) {
                    inserted = fluids.insert(0, essentia, 200, tx);
                    tx.commit();
                }
                if (inserted <= 0) {
                    helper.fail("OrbFluidCapability refused essentia vitae insert");
                    return;
                }

                ItemStack updated = access.getResource().toStack(1);
                var content = updated.get(NVDataComponents.ORB_FLUID.get());
                if (content == null || content.isEmpty() || content.getAmount() != inserted) {
                    helper.fail("ORB_FLUID component not written back, content=" + content);
                    return;
                }
                helper.succeed();
            });
        });
    }
}
