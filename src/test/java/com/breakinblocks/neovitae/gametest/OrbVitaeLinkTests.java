package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.OrbFillingLinkBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

public final class OrbVitaeLinkTests {

    private OrbVitaeLinkTests() {}

    private static AraVitaeTile placeAltar(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ARA_VITAE.block().get().defaultBlockState());
        AraVitaeTile altar = helper.getBlockEntity(pos, AraVitaeTile.class);
        if (altar == null) {
            helper.fail("Expected AraVitaeTile at " + pos);
        }
        return altar;
    }

    private static OrbFillingLinkBlockEntity placeOrbLink(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos, NVBlocks.ORB_FILLING_LINK.block().get().defaultBlockState());
        OrbFillingLinkBlockEntity link = helper.getBlockEntity(pos, OrbFillingLinkBlockEntity.class);
        if (link == null) {
            helper.fail("Expected OrbFillingLinkBlockEntity at " + pos);
        }
        return link;
    }

    public static void register(NVTestRegistrar r) {
        r.add("orb_link/fills_network_from_altar", 120, helper -> {
            BlockPos altarPos = new BlockPos(3, 1, 2);
            helper.setBlock(new BlockPos(3, 0, 2), Blocks.STONE.defaultBlockState());
            AraVitaeTile altar = placeAltar(helper, altarPos);
            OrbFillingLinkBlockEntity link = placeOrbLink(helper, new BlockPos(1, 1, 2));

            helper.runAfterDelay(10, () -> {
                if (altar == null || link == null) return;
                altar.addSacrificeEV(5000, false);
                int tankBaseline = altar.getMainTank();
                UUID id = UUID.fromString("00000000-0000-0000-0000-0000000000aa");

                ItemStack orb = new ItemStack(NVItems.ORB_WEAK.get());
                orb.set(NVDataComponents.BINDING, new Binding(id, "test"));
                link.inv.setStackInSlot(OrbFillingLinkBlockEntity.ORB_SLOT, orb);

                helper.succeedWhen(() -> {
                    helper.assertTrue(link.isLinked(), "Orb link should bind to the nearby altar");
                    Anima after = AnimaHelper.getAnima(id);
                    int ev = after == null ? 0 : after.getCurrentEV();
                    helper.assertTrue(!(ev <= 0), "Orb link should fill the owner network from altar EV; net=" + ev);
                    helper.assertTrue(!(altar.getMainTank() >= tankBaseline), "Altar EV should have drained; tank=" + altar.getMainTank() + " baseline=" + tankBaseline);
                    helper.assertTrue(!(link.getComparatorSignal() <= 0), "Comparator should rise with network fill; signal=" + link.getComparatorSignal());
                });
            });
        });
    }
}
