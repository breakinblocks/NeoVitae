package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.TabulaVitaeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class SoulNetworkTests {

    private static final UUID TEST_UUID = UUID.fromString("4ecf6284-b1e8-45bb-b2b3-151c95c3b10f");
    private static final UUID TEST_UUID_2 = UUID.fromString("4ecf6284-b1e8-45bb-b2b3-151c95c3b10e");
    private static final Binding TEST_BINDING = new Binding(TEST_UUID, "TestPlayer");
    private static final Binding TEST_BINDING_2 = new Binding(TEST_UUID_2, "TestPlayer2");

    private static SoulNetwork getOrCreateNetwork() {
        return SoulNetworkHelper.getSoulNetwork(TEST_UUID);
    }

    private static ItemStack createBoundOrb(int lpToAdd) {
        ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
        orb.set(NVDataComponents.BINDING, TEST_BINDING);
        SoulNetwork network = getOrCreateNetwork();
        if (lpToAdd > 0) {
            network.add(SoulTicket.create(lpToAdd), 1000000);
        }
        return orb;
    }

    // ==================== Soul Network ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void soulNetworkCreatesForUUID(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            SoulNetwork network = getOrCreateNetwork();
            if (network == null) {
                helper.fail("Soul network should auto-create for UUID");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void soulNetworkAddAndSyphon(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            SoulNetwork network = getOrCreateNetwork();
            network.add(SoulTicket.create(5000), 100000);

            int before = network.getCurrentEssence();
            if (before < 5000) {
                helper.fail("Network should have at least 5000 LP, has " + before);
                return;
            }

            int syphoned = network.syphon(SoulTicket.create(2000));
            if (syphoned != 2000) {
                helper.fail("Should syphon 2000 LP, got " + syphoned);
                return;
            }

            int after = network.getCurrentEssence();
            if (after != before - 2000) {
                helper.fail("Expected " + (before - 2000) + " LP after syphon, got " + after);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void soulNetworkCannotOverSyphon(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            SoulNetwork network = getOrCreateNetwork();
            // Reset to known state
            while (network.getCurrentEssence() > 0) {
                network.syphon(SoulTicket.create(network.getCurrentEssence()));
            }
            network.add(SoulTicket.create(100), 100000);

            int syphoned = network.syphon(SoulTicket.create(500));
            if (syphoned > 100) {
                helper.fail("Should not syphon more than available, got " + syphoned);
                return;
            }
            helper.succeed();
        });
    }

    // ==================== Ara Vitae Orb Filling ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 300)
    public void altarFillsBoundOrb(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = (AraVitaeTile) helper.getBlockEntity(new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (altar == null) { helper.fail("No altar"); return; }

            // Reset network LP
            SoulNetwork network = getOrCreateNetwork();
            while (network.getCurrentEssence() > 0) {
                network.syphon(SoulTicket.create(network.getCurrentEssence()));
            }

            altar.addSacrificeLP(5000, false);
            altar.inv.setStackInSlot(0, createBoundOrb(0));

            int lpBefore = network.getCurrentEssence();

            helper.runAfterDelay(250, () -> {
                int lpAfter = network.getCurrentEssence();
                if (lpAfter <= lpBefore) {
                    helper.fail("Orb filling should add LP to soul network (before=" + lpBefore + " after=" + lpAfter + ")");
                    return;
                }
                helper.succeed();
            });
        });
    }

    // ==================== Tabula Vitae Crafting ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void alchemyTableCraftsWithBoundOrb(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
        TabulaVitaeBlockEntity table = (TabulaVitaeBlockEntity) helper.getBlockEntity(new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (table == null) { helper.fail("No table"); return; }

            // Flint recipe: gravel + flint = 2 flint, tier 0, 50 LP syphon, 20 ticks
            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, createBoundOrb(10000));

            helper.runAfterDelay(60, () -> {
                ItemStack output = table.inv.getStackInSlot(TabulaVitaeBlockEntity.OUTPUT_SLOT);
                if (output.isEmpty()) {
                    helper.fail("Alchemy table should have crafted flint (burnTime=" + table.burnTime + ", ticksReq=" + table.ticksRequired + ")");
                    return;
                }
                if (!output.is(Items.FLINT) || output.getCount() != 2) {
                    helper.fail("Expected 2 flint, got " + output);
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 100)
    public void alchemyTableSyphonsLP(GameTestHelper helper) {
        helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 2), NVBlocks.TABULA_VITAE.block().get().defaultBlockState());
        TabulaVitaeBlockEntity table = (TabulaVitaeBlockEntity) helper.getBlockEntity(new BlockPos(3, 1, 2));

        helper.runAfterDelay(5, () -> {
            if (table == null) { helper.fail("No table"); return; }

            // Use a separate UUID to avoid interference from concurrent tests
            SoulNetwork network2 = SoulNetworkHelper.getSoulNetwork(TEST_UUID_2);
            network2.add(SoulTicket.create(10000), 100000);

            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            orb.set(NVDataComponents.BINDING, TEST_BINDING_2);

            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, orb);

            int lpBefore = network2.getCurrentEssence();

            helper.runAfterDelay(50, () -> {
                int lpAfter = network2.getCurrentEssence();
                if (lpAfter >= lpBefore) {
                    helper.fail("Alchemy table should syphon LP (before=" + lpBefore + " after=" + lpAfter + ")");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
