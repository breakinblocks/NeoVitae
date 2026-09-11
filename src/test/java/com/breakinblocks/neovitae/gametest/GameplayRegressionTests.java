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
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTest;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public final class GameplayRegressionTests {

    private static void link(TeleposerBlockEntity tile, ServerLevel target, BlockPos pos) {
        ItemStack focus = new ItemStack(NVItems.TELEPOSER_FOCUS.get());
        Binding binding = new Binding(UUID.randomUUID(), "Regression Test");
        focus.set(NVDataComponents.BINDING, binding);
        NVItems.TELEPOSER_FOCUS.get().setStoredPos(focus, pos);
        NVItems.TELEPOSER_FOCUS.get().setWorld(focus, target);
        AnimaHelper.getAnima(binding).add(AnimaTicket.create(100000), 100000);
        tile.inv.setStackInSlot(0, focus);
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 80)
    public void powered_master_stops_routing(GameTestHelper h) {
        RoutingNodeTests.RoutingTestContext network;
        try {
            Method m = RoutingNodeTests.class.getDeclaredMethod("setupLinearNetwork", GameTestHelper.class);
            m.setAccessible(true);
            network = (RoutingNodeTests.RoutingTestContext) m.invoke(null, h);
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
        MasterRoutingNodeBlockEntity master = ((MasterRoutingNodeBlockEntity) h.getBlockEntity(network.master()));
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void crystal_catalyst_initializes_conversion(GameTestHelper h) {
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
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 20)
    public void crystal_catalyst_expires(GameTestHelper h) {
        BlockPos pos = new BlockPos(2, 1, 2);
        h.setBlock(pos.below(), Blocks.STONE);
        h.setBlock(pos, NVBlocks.RAW_SPIRITUS_CRYSTAL.block().get());
        h.getLevel().getChunkAt(h.absolutePos(pos)).setData(NVDataAttachments.SPIRITUS_CHUNK.get(),
                new SpiritusChunk(100, 0, 0, 0, 0));
        SpiritusCrystalBlockEntity crystal = ((SpiritusCrystalBlockEntity) h.getBlockEntity(pos));
        crystal.applyCatalyst(0.000001, 10, 25);
        crystal.internalCounter = 19;
        SpiritusCrystalBlockEntity.tick(h.getLevel(), h.absolutePos(pos), h.getBlockState(pos), crystal);
        h.assertTrue(crystal.injectedSpiritus == 0 && crystal.appliedConversionRate == 0
                && crystal.speedModifier == 1, "Exhausted catalyst must reset conversion and speed");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void athanor_last_durability_persisted(GameTestHelper h) {
        BlockPos relative = new BlockPos(2, 1, 2);
        h.setBlock(relative, NVBlocks.ATHANOR_BLOCK.block().get());
        AthanorBlockEntity tile = ((AthanorBlockEntity) h.getBlockEntity(relative));
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void xp_level_boundaries(GameTestHelper h) {
        int[][] cases = {{-1, 0}, {0, 0}, {6, 0}, {7, 1}, {352, 16}, {393, 16},
                {394, 17}, {1507, 31}, {1627, 31}, {1628, 32}, {1000, 26}, {Integer.MAX_VALUE, 21863}};
        for (int[] entry : cases) {
            h.assertTrue(ExperienceTomeItem.getLevelForXp(entry[0]) == entry[1],
                    "Wrong level for " + entry[0] + " XP");
        }
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void xp_deposit_respects_tome_capacity(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.giveExperiencePoints(10);
        ItemStack tome = new ItemStack(NVItems.EXPERIENCE_TOME.get());
        ExperienceTomeItem.addXpToTome(tome, Integer.MAX_VALUE - 1);
        h.assertTrue(ExperienceTomeItem.depositLevels(player, tome, -1) == 1,
                "Deposit must stop at the tome's remaining capacity");
        h.assertTrue(ExperienceTomeItem.getStoredXp(tome) == Integer.MAX_VALUE && player.totalExperience == 9,
                "Overflowing deposit must preserve the remaining player XP");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void holding_persists_bound_destination(GameTestHelper h) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void holding_persists_air_use(GameTestHelper h) {
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
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void holding_persists_tick_deactivation(GameTestHelper h) {
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
        holding.getItem().inventoryTick(holding, h.getLevel(), player, 0, true);
        h.assertTrue(!NVItems.SIGIL_FAST_MINER.get().getActivated(ItemSigilHolding.getInternalInventory(holding).get(0)),
                "Contained sigil must remain deactivated after failing its upkeep payment");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void teleposer_equal_coordinates_other_dimension(GameTestHelper h) {
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
    }

    @GameTest(template = "empty_24x5x24", timeoutTicks = 40)
    public void teleposer_entity_crosses_dimension(GameTestHelper h) {
        BlockPos relative = new BlockPos(2, 1, 2);
        BlockPos source = h.absolutePos(relative);
        BlockPos target = source.offset(10, 0, 0).atY(80);
        ServerLevel other = h.getLevel().getServer().getLevel(Level.NETHER);
        h.setBlock(relative, NVBlocks.TELEPOSER.block().get());
        h.setBlock(relative.above(), Blocks.AIR);
        other.setBlockAndUpdate(target, NVBlocks.TELEPOSER.block().get().defaultBlockState());
        other.setBlockAndUpdate(target.above(), Blocks.AIR.defaultBlockState());
        TeleposerBlockEntity tile = (TeleposerBlockEntity) h.getBlockEntity(relative);
        link(tile, other, target);
        var pig = EntityType.PIG.create(h.getLevel());
        pig.setPos(source.getX() + 0.5, source.getY() + 1, source.getZ() + 0.5);
        h.getLevel().addFreshEntity(pig);
        var passenger = EntityType.CHICKEN.create(h.getLevel());
        passenger.setPos(pig.position());
        h.getLevel().addFreshEntity(passenger);
        passenger.startRiding(pig, true);
        Map<UUID, Entity> arrivals = new HashMap<>();
        // The destination need not be ticking for a dimension transfer to succeed.
        // Observe the actual entities joining it, including the restored passenger.
        Consumer<EntityJoinLevelEvent> track = event -> {
            UUID id = event.getEntity().getUUID();
            if (event.getLevel() == other && (id.equals(pig.getUUID()) || id.equals(passenger.getUUID()))) {
                arrivals.put(id, event.getEntity());
            }
        };
        NeoForge.EVENT_BUS.addListener(track);
        try {
            tile.initiateTeleport();
            Entity moved = arrivals.get(pig.getUUID());
            Entity rider = arrivals.get(passenger.getUUID());
            h.assertTrue(moved != null && moved.level() == other, "Pig must join the destination dimension");
            h.assertTrue(rider != null && rider.getVehicle() == moved, "Mounted passenger must follow the pig");
            h.assertTrue(moved.position().distanceTo(new Vec3(target.getX() + 0.5, target.getY() + 1,
                    target.getZ() + 0.5)) < 0.01, "Arrival must preserve the offset from the teleposer");
        } finally {
            NeoForge.EVENT_BUS.unregister(track);
            pig.discard();
            passenger.discard();
            arrivals.values().forEach(Entity::discard);
            other.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
        }
        h.succeed();
    }
}
