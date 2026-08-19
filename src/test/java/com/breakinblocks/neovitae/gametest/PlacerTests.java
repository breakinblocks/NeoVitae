package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.api.ritual.AreaCursor;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.ritual.EnumFillMode;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.types.RitualPlacer;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class PlacerTests {

    private static final BlockPos MASTER = BlockPos.ZERO;

    private static final UUID OWNER = UUID.fromString("b31e77c4-9a2f-4d18-8c60-51ae2f0d9b77");

    private static class StubMaster implements IMasterRitualStone {
        private final Level level;
        private final BlockPos pos;
        private final Map<String, AreaDescriptor> ranges = new HashMap<>();
        private EnumFillMode fillMode = EnumFillMode.SOLID;

        StubMaster(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override public Level getLevel() { return level; }
        @Override public BlockPos getBlockPos() { return pos; }
        @Override public UUID getOwner() { return OWNER; }
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
        @Override public EnumFillMode getFillMode() { return fillMode; }
        @Override public void provideInformationOfRitualToPlayer(Player player) {}
        @Override public void provideInformationOfRangeToPlayer(Player player, String key) {}
        @Override public void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor) {}
        @Override public void notifyOwner(Component message) {}
    }

    private static void fundOwner() {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 200000) {
            anima.add(AnimaTicket.create(1000000), 10000000);
        }
    }

    private static StubMaster masonAt(GameTestHelper helper, BlockPos relPos, AreaDescriptor range, EnumFillMode mode) {
        fundOwner();
        StubMaster master = new StubMaster(helper.getLevel(), helper.absolutePos(relPos));
        master.setBlockRange(RitualPlacer.PLACER_RANGE, range);
        master.fillMode = mode;
        return master;
    }

    private static void chestOf(GameTestHelper helper, BlockPos relPos, ItemStack contents) {
        helper.setBlock(relPos, Blocks.CHEST);
        if (helper.getBlockEntity(relPos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, contents);
        }
    }

    private static int countPlaced(GameTestHelper helper, BlockPos relOrigin, AreaDescriptor range, Block block) {
        int found = 0;
        for (BlockPos p : range.getContainedPositions(helper.absolutePos(relOrigin))) {
            if (helper.getLevel().getBlockState(p).is(block)) found++;
        }
        return found;
    }

    private static long savedCursor(RitualPlacer ritual) {
        CompoundTag tag = new CompoundTag();
        ritual.writeToNBT(tag);
        return tag.contains("fillCursor") ? tag.getLong("fillCursor") : -1L;
    }

    private static AreaCursor cursor(int sizeX, int sizeY, int sizeZ) {
        AreaDescriptor.Rectangle box = new AreaDescriptor.Rectangle(
                new BlockPos(-(sizeX / 2), 0, -(sizeZ / 2)), sizeX, sizeY, sizeZ);
        return AreaCursor.of(box, MASTER);
    }

    private static int countAccepted(AreaCursor cursor, EnumFillMode mode) {
        int count = 0;
        for (long i = 0; i < cursor.volume(); i++) {
            if (cursor.accepts(i, mode)) count++;
        }
        return count;
    }

    private static void expectCount(GameTestHelper helper, AreaCursor cursor, EnumFillMode mode, int expected) {
        int actual = countAccepted(cursor, mode);
        if (actual != expected) {
            helper.fail(mode + " should accept " + expected + " positions, got " + actual);
        }
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void fillModesOnACube(GameTestHelper helper) {
        AreaCursor c = cursor(5, 5, 5);
        if (c.volume() != 125) {
            helper.fail("A 5x5x5 box should hold 125 positions, got " + c.volume());
            return;
        }
        expectCount(helper, c, EnumFillMode.SOLID, 125);
        expectCount(helper, c, EnumFillMode.HOLLOW, 98);
        expectCount(helper, c, EnumFillMode.FLOOR, 25);
        expectCount(helper, c, EnumFillMode.ROOF, 25);
        expectCount(helper, c, EnumFillMode.WALLS, 80);
        expectCount(helper, c, EnumFillMode.FRAME, 44);
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void fillModesOnAFlatSlab(GameTestHelper helper) {
        AreaCursor c = cursor(5, 1, 5);
        expectCount(helper, c, EnumFillMode.SOLID, 25);
        expectCount(helper, c, EnumFillMode.HOLLOW, 25);
        expectCount(helper, c, EnumFillMode.FRAME, 16);
        expectCount(helper, c, EnumFillMode.WALLS, 16);
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void floorAndRoofAreSingleLayers(GameTestHelper helper) {
        AreaCursor c = cursor(5, 5, 5);
        for (long i = 0; i < c.volume(); i++) {
            if (c.accepts(i, EnumFillMode.FLOOR) && c.at(i).getY() != 0) {
                helper.fail("FLOOR accepted a position off the bottom layer: " + c.at(i));
                return;
            }
            if (c.accepts(i, EnumFillMode.ROOF) && c.at(i).getY() != 4) {
                helper.fail("ROOF accepted a position off the top layer: " + c.at(i));
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void cursorVisitsEveryPositionExactlyOnce(GameTestHelper helper) {
        AreaCursor c = cursor(7, 3, 5);
        if (c.volume() != 105) {
            helper.fail("Expected a volume of 105, got " + c.volume());
            return;
        }
        Set<BlockPos> seen = new HashSet<>();
        for (long i = 0; i < c.volume(); i++) {
            seen.add(c.at(i));
        }
        if (seen.size() != 105) {
            helper.fail("The cursor should visit 105 distinct positions, got " + seen.size());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void cursorMatchesAHemisphereExactly(GameTestHelper helper) {
        AreaDescriptor.HemiSphere dome = new AreaDescriptor.HemiSphere(BlockPos.ZERO, 4);
        AreaCursor c = AreaCursor.of(dome, MASTER);

        Set<BlockPos> fromCursor = new HashSet<>();
        for (long i = 0; i < c.volume(); i++) {
            if (c.accepts(i, EnumFillMode.SOLID)) fromCursor.add(c.at(i));
        }
        Set<BlockPos> fromDescriptor = new HashSet<>(dome.getContainedPositions(MASTER));

        if (!fromCursor.equals(fromDescriptor)) {
            helper.fail("Cursor covered " + fromCursor.size()
                    + " positions but the hemisphere contains " + fromDescriptor.size());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void skipToJumpsWholeLayers(GameTestHelper helper) {
        AreaCursor c = cursor(9, 6, 9);
        long layer = 9L * 9L;

        long steps = 0;
        long index = 0;
        while (index < c.volume()) {
            if (!c.accepts(index, EnumFillMode.FLOOR)) {
                index = c.skipTo(index + 1, EnumFillMode.FLOOR);
            } else {
                index++;
            }
            steps++;
            if (steps > c.volume()) break;
        }
        if (steps > layer + 1) {
            helper.fail("A FLOOR lap should cost about " + layer + " steps, took " + steps);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void skipToNeverSkipsAnAcceptedPosition(GameTestHelper helper) {
        AreaCursor c = cursor(7, 5, 7);
        for (EnumFillMode mode : EnumFillMode.values()) {
            Set<Long> walked = new HashSet<>();
            long index = 0;
            while (index < c.volume()) {
                if (c.accepts(index, mode)) {
                    walked.add(index);
                    index++;
                } else {
                    index = c.skipTo(index + 1, mode);
                }
            }
            int expected = countAccepted(c, mode);
            if (walked.size() != expected) {
                helper.fail(mode + " walk found " + walked.size() + " positions but " + expected + " are accepted");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void spiritusTiersMatchThePublishedTable(GameTestHelper helper) {
        double[] raw = {0.0, 19.9, 20.0, 49.9, 50.0, 500.0};
        int[] expectedBlocks = {1, 3, 4, 7, 8, 8};
        for (int i = 0; i < raw.length; i++) {
            int actual = RitualPlacer.blocksPerRefresh(raw[i]);
            if (actual != expectedBlocks[i]) {
                helper.fail("At " + raw[i] + " raw expected " + expectedBlocks[i] + " blocks, got " + actual);
                return;
            }
        }
        int[] expectedTiers = {0, 0, 1, 1, 2, 2};
        for (int i = 0; i < raw.length; i++) {
            int actual = RitualPlacer.tierFor(raw[i]);
            if (actual != expectedTiers[i]) {
                helper.fail("At " + raw[i] + " raw expected tier " + expectedTiers[i] + ", got " + actual);
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void placesBlocksFromAnAdjacentChest(GameTestHelper helper) {
        BlockPos master = new BlockPos(3, 1, 3);
        AreaDescriptor.Rectangle range = new AreaDescriptor.Rectangle(new BlockPos(-1, 1, -1), 3, 1, 3);
        StubMaster stub = masonAt(helper, master, range, EnumFillMode.SOLID);
        chestOf(helper, master.above(), new ItemStack(Blocks.STONE, 64));

        RitualPlacer ritual = new RitualPlacer();
        for (int i = 0; i < 40; i++) ritual.performRitual(stub);

        int placed = countPlaced(helper, master, range, Blocks.STONE);
        if (placed <= 0) {
            helper.fail("The Mason should have placed stone from the chest above it");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void floorModeOnlyFillsTheBottomLayer(GameTestHelper helper) {
        BlockPos master = new BlockPos(3, 1, 3);
        AreaDescriptor.Rectangle range = new AreaDescriptor.Rectangle(new BlockPos(-1, 1, -1), 3, 3, 3);
        StubMaster stub = masonAt(helper, master, range, EnumFillMode.FLOOR);
        chestOf(helper, master.below(), new ItemStack(Blocks.STONE, 64));

        RitualPlacer ritual = new RitualPlacer();
        for (int i = 0; i < 60; i++) ritual.performRitual(stub);

        int bottomY = helper.absolutePos(master).getY() + 1;
        for (BlockPos p : range.getContainedPositions(helper.absolutePos(master))) {
            if (helper.getLevel().getBlockState(p).is(Blocks.STONE) && p.getY() != bottomY) {
                helper.fail("FLOOR mode placed a block off the bottom layer at " + p);
                return;
            }
        }
        if (countPlaced(helper, master, range, Blocks.STONE) <= 0) {
            helper.fail("FLOOR mode placed nothing at all");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 120)
    public void skipsBlocksThatCannotSurvive(GameTestHelper helper) {
        BlockPos master = new BlockPos(3, 1, 3);
        AreaDescriptor.Rectangle range = new AreaDescriptor.Rectangle(new BlockPos(-1, 2, -1), 3, 2, 3);
        StubMaster stub = masonAt(helper, master, range, EnumFillMode.SOLID);
        chestOf(helper, master.above(), new ItemStack(Blocks.TORCH, 32));

        RitualPlacer ritual = new RitualPlacer();
        for (int i = 0; i < 40; i++) ritual.performRitual(stub);

        if (countPlaced(helper, master, range, Blocks.TORCH) > 0) {
            helper.fail("A torch cannot survive floating in the air and must not be placed");
            return;
        }
        if (helper.getBlockEntity(master.above()) instanceof ChestBlockEntity chest
                && chest.getItem(0).getCount() != 32) {
            helper.fail("Torches must not be consumed when they cannot be placed, "
                    + chest.getItem(0).getCount() + " left of 32");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 200)
    public void cursorResumesInsteadOfRestarting(GameTestHelper helper) {
        BlockPos master = new BlockPos(2, 1, 2);
        BlockPos absMaster = helper.absolutePos(master);
        AreaDescriptor.Rectangle range = new AreaDescriptor.Rectangle(new BlockPos(-7, 1, -7), 15, 5, 15);

        for (BlockPos p : range.getContainedPositions(absMaster)) {
            helper.getLevel().setBlockAndUpdate(p, Blocks.STONE.defaultBlockState());
        }

        StubMaster stub = masonAt(helper, master, range, EnumFillMode.SOLID);
        chestOf(helper, master.below(), new ItemStack(Blocks.STONE, 64));

        RitualPlacer ritual = new RitualPlacer();
        ritual.performRitual(stub);
        long afterOne = savedCursor(ritual);
        ritual.performRitual(stub);
        ritual.performRitual(stub);
        long afterThree = savedCursor(ritual);

        if (afterOne <= 0) {
            helper.fail("The first refresh should have swept forward, cursor sat at " + afterOne);
            return;
        }
        if (afterThree < afterOne * 2) {
            helper.fail("Three refreshes must sweep about three times as far as one; a cursor that "
                    + "restarts each refresh would stall. Got " + afterOne + " then " + afterThree);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 30)
    public void cursorDoesNotMaterialiseThePositionList(GameTestHelper helper) {
        AreaDescriptor.Rectangle box = new AreaDescriptor.Rectangle(new BlockPos(-20, 1, -20), 41, 20, 41);
        AreaCursor c = AreaCursor.of(box, MASTER);
        for (long i = 0; i < Math.min(4096, c.volume()); i++) {
            c.accepts(i, EnumFillMode.SOLID);
            c.at(i);
        }
        if (box.hasCachedPositions()) {
            helper.fail("Walking a range with AreaCursor must not build the cached position list");
            return;
        }
        List<BlockPos> ignored = box.getContainedPositions(MASTER);
        if (!box.hasCachedPositions() || ignored.isEmpty()) {
            helper.fail("getContainedPositions should still populate the cache when called directly");
            return;
        }
        helper.succeed();
    }
}
