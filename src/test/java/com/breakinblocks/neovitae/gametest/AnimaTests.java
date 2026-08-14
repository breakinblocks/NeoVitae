package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AnimaTests {

    private static final UUID TEST_UUID = UUID.fromString("4ecf6284-b1e8-45bb-b2b3-151c95c3b10f");
    private static final UUID TEST_UUID_2 = UUID.fromString("4ecf6284-b1e8-45bb-b2b3-151c95c3b10e");
    private static final Binding TEST_BINDING = new Binding(TEST_UUID, "TestPlayer");
    private static final Binding TEST_BINDING_2 = new Binding(TEST_UUID_2, "TestPlayer2");

    private static Anima getOrCreateNetwork() {
        return AnimaHelper.getAnima(TEST_UUID);
    }

    private static ItemStack createBoundOrb(int lpToAdd) {
        ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
        orb.set(NVDataComponents.BINDING, TEST_BINDING);
        Anima anima = getOrCreateNetwork();
        if (lpToAdd > 0) {
            anima.add(AnimaTicket.create(lpToAdd), 1000000);
        }
        return orb;
    }

    // ==================== Anima ====================

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void animaCreatesForUUID(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Anima anima = getOrCreateNetwork();
            if (anima == null) {
                helper.fail("Anima should auto-create for UUID");
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void animaAddAndSyphon(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Anima anima = getOrCreateNetwork();
            anima.add(AnimaTicket.create(5000), 100000);

            int before = anima.getCurrentEV();
            if (before < 5000) {
                helper.fail("Anima should have at least 5000 EV, has " + before);
                return;
            }

            int syphoned = anima.syphon(AnimaTicket.create(2000));
            if (syphoned != 2000) {
                helper.fail("Should syphon 2000 EV, got " + syphoned);
                return;
            }

            int after = anima.getCurrentEV();
            if (after != before - 2000) {
                helper.fail("Expected " + (before - 2000) + " EV after syphon, got " + after);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void animaCannotOverSyphon(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Anima anima = getOrCreateNetwork();
            while (anima.getCurrentEV() > 0) {
                anima.syphon(AnimaTicket.create(anima.getCurrentEV()));
            }
            anima.add(AnimaTicket.create(100), 100000);

            int syphoned = anima.syphon(AnimaTicket.create(500));
            if (syphoned > 100) {
                helper.fail("Should not syphon more than available, got " + syphoned);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void bindingDecodedFromNbtStillReportsUnbound(GameTestHelper helper) {
        helper.runAfterDelay(1, () -> {
            Tag encoded = Binding.BASIC_CODEC.encodeStart(NbtOps.INSTANCE, Binding.EMPTY).getOrThrow();
            Binding decoded = Binding.BASIC_CODEC.parse(NbtOps.INSTANCE, encoded).getOrThrow();

            if (decoded == Binding.EMPTY) {
                helper.fail("Decoded binding should be a distinct instance for this test to mean anything");
                return;
            }
            if (!decoded.isEmpty()) {
                helper.fail("A binding decoded from an unbound one must still report as unbound");
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

            Anima anima = getOrCreateNetwork();
            while (anima.getCurrentEV() > 0) {
                anima.syphon(AnimaTicket.create(anima.getCurrentEV()));
            }

            altar.addSacrificeEV(5000, false);
            altar.inv.setStackInSlot(0, createBoundOrb(0));

            int lpBefore = anima.getCurrentEV();

            helper.runAfterDelay(250, () -> {
                int lpAfter = anima.getCurrentEV();
                if (lpAfter <= lpBefore) {
                    helper.fail("Orb filling should add EV to anima (before=" + lpBefore + " after=" + lpAfter + ")");
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

            Anima anima2 = AnimaHelper.getAnima(TEST_UUID_2);
            anima2.add(AnimaTicket.create(10000), 100000);

            ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
            orb.set(NVDataComponents.BINDING, TEST_BINDING_2);

            table.inv.setStackInSlot(0, new ItemStack(Items.GRAVEL));
            table.inv.setStackInSlot(1, new ItemStack(Items.FLINT));
            table.inv.setStackInSlot(TabulaVitaeBlockEntity.ORB_SLOT, orb);

            int lpBefore = anima2.getCurrentEV();

            helper.runAfterDelay(50, () -> {
                int lpAfter = anima2.getCurrentEV();
                if (lpAfter >= lpBefore) {
                    helper.fail("Alchemy table should syphon EV (before=" + lpBefore + " after=" + lpAfter + ")");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
