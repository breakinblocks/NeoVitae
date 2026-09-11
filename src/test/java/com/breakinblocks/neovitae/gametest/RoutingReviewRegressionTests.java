package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.api.routing.RoutingChannel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.routing.ItemRoutingChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Method;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class RoutingReviewRegressionTests {
    private static MasterRoutingNodeBlockEntity master(GameTestHelper h) {
        BlockPos pos = new BlockPos(2, 1, 2);
        h.setBlock(pos, NVBlocks.MASTER_ROUTING_NODE.block().get());
        return (MasterRoutingNodeBlockEntity) h.getBlockEntity(pos);
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 20)
    public void newEdgesMarkMasterForSaving(GameTestHelper h) {
        MasterRoutingNodeBlockEntity master = master(h);
        LevelChunk chunk = h.getLevel().getChunkAt(master.getBlockPos());
        chunk.setUnsaved(false);
        master.addConnection(master.getBlockPos().offset(32, 0, 0), master.getBlockPos().offset(33, 0, 0));
        boolean saved = chunk.isUnsaved();
        chunk.setUnsaved(true);
        h.assertTrue(saved, "Adding an edge between existing nodes must mark the master's chunk for saving");
        h.succeed();
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 40)
    public void routingDoesNotLoadRemoteChunks(GameTestHelper h) {
        MasterRoutingNodeBlockEntity master = master(h);
        BlockPos input = master.getBlockPos().offset(4096, 0, 0);
        BlockPos output = input.offset(32, 0, 0);
        h.assertTrue(!h.getLevel().hasChunk(input.getX() >> 4, input.getZ() >> 4)
                && !h.getLevel().hasChunk(output.getX() >> 4, output.getZ() >> 4), "Remote chunks must start unloaded");
        master.addNodeToList(new InputRoutingNodeBlockEntity(input, NVBlocks.INPUT_ROUTING_NODE.block().get().defaultBlockState()));
        master.addNodeToList(new OutputRoutingNodeBlockEntity(output, NVBlocks.OUTPUT_ROUTING_NODE.block().get().defaultBlockState()));
        master.addConnection(master.getBlockPos(), input);
        master.addConnection(master.getBlockPos(), output);
        try {
            Method process = MasterRoutingNodeBlockEntity.class.getDeclaredMethod("processChannel", RoutingChannel.class, Level.class);
            process.setAccessible(true);
            process.invoke(master, new ItemRoutingChannel(), h.getLevel());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        h.assertTrue(!h.getLevel().hasChunk(input.getX() >> 4, input.getZ() >> 4)
                && !h.getLevel().hasChunk(output.getX() >> 4, output.getZ() >> 4), "Routing must not synchronously load remote node chunks");
        h.assertTrue(master.graphContains(input) && master.graphContains(output), "Unloaded nodes must remain in the graph");
        h.succeed();
    }
}
