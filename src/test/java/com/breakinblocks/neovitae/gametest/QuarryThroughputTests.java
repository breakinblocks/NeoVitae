package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.spiritus.SpiritusHandler;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.EnumFillMode;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.types.RitualMagnetism;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class QuarryThroughputTests {

    private QuarryThroughputTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("quarry/moves_thirty_ores_per_refresh", 40, QuarryThroughputTests::quarryMovesThirtyOresPerRefresh);
        r.add("quarry/ruina_voids_filler_but_keeps_other_blocks", 40, QuarryThroughputTests::ruinaVoidsFillerButKeepsOtherBlocks);
    }

    private static final UUID OWNER = UUID.fromString("5d0c2e61-7f3a-4b8e-9c21-0e6b4a3f8d12");
    private static final BlockPos MASTER = new BlockPos(2, 5, 2);
    private static final BlockPos FOUNDATION = MASTER.below();
    private static final BlockPos CHEST = MASTER.above();

    static class StubMaster implements IMasterRitualStone {
        private final Level level;
        private final BlockPos pos;
        private final UUID owner;
        private final Map<String, AreaDescriptor> ranges = new HashMap<>();

        StubMaster(Level level, BlockPos pos, UUID owner) {
            this.level = level;
            this.pos = pos;
            this.owner = owner;
        }

        @Override public Level getLevel() { return level; }
        @Override public BlockPos getBlockPos() { return pos; }
        @Override public UUID getOwner() { return owner; }
        @Override public void setOwner(UUID owner) {}
        @Override public Ritual getCurrentRitual() { return null; }
        @Override public boolean isActive() { return true; }
        @Override public Direction getDirection() { return Direction.NORTH; }
        @Override public boolean isInverted() { return false; }
        @Override public int getCooldown() { return 0; }
        @Override public void setCooldown(int cooldown) {}
        @Override public long getRunningTime() { return 0; }
        @Override public boolean activateRitual(Ritual ritual, Player player, int crystalLevel) { return true; }
        @Override public void performRitual() {}
        @Override public void stopRitual(Ritual.BreakType breakType) {}
        @Override public boolean checkStructure(Ritual ritual) { return true; }
        @Override public AreaDescriptor getBlockRange(String key) { return ranges.get(key); }
        @Override public Map<String, AreaDescriptor> getBlockRanges() { return ranges; }
        @Override public void setBlockRange(String key, AreaDescriptor descriptor) { ranges.put(key, descriptor); }
        @Override public void setBlockRanges(Map<String, AreaDescriptor> r) { ranges.clear(); ranges.putAll(r); }
        @Override public SpiritusType getActiveSpiritusAspect() { return SpiritusType.RAW; }
        @Override public void setActiveSpiritusAspect(SpiritusType type) {}
        @Override public EnumFillMode getFillMode() { return EnumFillMode.SOLID; }
        @Override public void provideInformationOfRitualToPlayer(Player player) {}
        @Override public void provideInformationOfRangeToPlayer(Player player, String key) {}
        @Override public void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor) {}
        @Override public void notifyOwner(Component message) {}
    }

    private static StubMaster quarry(GameTestHelper helper) {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 200000) {
            anima.add(AnimaTicket.create(1000000), 10000000);
        }
        helper.setBlock(FOUNDATION, Blocks.STONE);
        helper.setBlock(CHEST, Blocks.CHEST);
        return new StubMaster(helper.getLevel(), helper.absolutePos(MASTER), OWNER);
    }

    private static int chestCount(GameTestHelper helper, Item item) {
        int total = 0;
        ChestBlockEntity chest = helper.getBlockEntity(CHEST, ChestBlockEntity.class);
        if (chest != null) {
            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                if (chest.getItem(slot).is(item)) total += chest.getItem(slot).getCount();
            }
        }
        return total;
    }

    private static void quarryMovesThirtyOresPerRefresh(GameTestHelper helper) {
        StubMaster master = quarry(helper);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                helper.setBlock(new BlockPos(x, 3, z), Blocks.IRON_ORE);
            }
        }
        for (int i = 0; i < 15; i++) {
            helper.setBlock(new BlockPos(i % 5, 2, i / 5), Blocks.IRON_ORE);
        }

        new RitualMagnetism().performRitual(master);

        int moved = chestCount(helper, Items.IRON_ORE);
        if (moved != 30) {
            helper.fail("Expected 30 ores moved in one refresh, got " + moved);
            return;
        }
        int left = 0;
        for (int i = 0; i < 15; i++) {
            if (helper.getBlockState(new BlockPos(i % 5, 2, i / 5)).is(Blocks.IRON_ORE)) left++;
        }
        if (left != 10) {
            helper.fail("Expected 10 ores left on the second layer, got " + left);
            return;
        }
        helper.succeed();
    }

    private static void ruinaVoidsFillerButKeepsOtherBlocks(GameTestHelper helper) {
        StubMaster master = quarry(helper);
        SpiritusHandler.INSTANCE.fillSpiritusToAmount(helper.getLevel(), helper.absolutePos(MASTER), SpiritusType.RUINA, 50);

        BlockPos ore = new BlockPos(2, 3, 2);
        BlockPos stone = new BlockPos(1, 3, 2);
        BlockPos cobble = new BlockPos(3, 3, 2);
        BlockPos netherrack = new BlockPos(2, 3, 1);
        BlockPos bricks = new BlockPos(2, 3, 3);
        helper.setBlock(ore, Blocks.IRON_ORE);
        helper.setBlock(stone, Blocks.STONE);
        helper.setBlock(cobble, Blocks.COBBLESTONE);
        helper.setBlock(netherrack, Blocks.NETHERRACK);
        helper.setBlock(bricks, Blocks.STONE_BRICKS);

        new RitualMagnetism().performRitual(master);

        helper.assertBlockPresent(Blocks.AIR, ore);
        helper.assertBlockPresent(Blocks.AIR, stone);
        helper.assertBlockPresent(Blocks.AIR, cobble);
        helper.assertBlockPresent(Blocks.AIR, netherrack);
        helper.assertBlockPresent(Blocks.STONE_BRICKS, bricks);
        helper.assertBlockPresent(Blocks.STONE, FOUNDATION);
        if (chestCount(helper, Items.IRON_ORE) != 1) {
            helper.fail("Ore should still be collected while Ruina is active");
            return;
        }
        helper.succeed();
    }
}
