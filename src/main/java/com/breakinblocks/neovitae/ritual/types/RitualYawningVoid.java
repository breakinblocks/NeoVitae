package com.breakinblocks.neovitae.ritual.types;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.WillState;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Yawning of the Void - Block quarry ritual that destroys blocks and collects drops.
 * Scans blocks sequentially with a persistent position tracker.
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Destroy blocks and collect drops into chest or spawn in world</li>
 *   <li><b>Steadfast</b> - Replace mode: places blocks from quarry area into the placement area</li>
 *   <li><b>Corrosive</b> - Filter mode: only destroys blocks matching items in the chest inventory</li>
 * </ul>
 *
 * <p>This is a Dusk tier ritual. Processes 1 block per tick with persistent scan position.
 */
public class RitualYawningVoid extends Ritual {

    public static final String QUARRY_RANGE = "quarryRange";
    public static final String CHEST_RANGE = "chestRange";
    public static final String PLACEMENT_RANGE = "placementRange";

    private static final double MIN_DEFAULT = 10.0;
    private static final double MIN_STEADFAST = 10.0;
    private static final double MIN_CORROSIVE = 10.0;

    private static final double WILL_PER_REPLACE = 0.5;
    private static final double WILL_PER_FILTER = 0.2;

    // Persistent scanning position
    private int currentX = 0;
    private int currentY = 0;
    private int currentZ = 0;
    private boolean scanInitialized = false;

    public RitualYawningVoid() {
        super("yawning_void", 1, 10000, "ritual." + NeoVitae.MODID + ".yawning_void");
        addBlockRange(QUARRY_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(PLACEMENT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -10, -5), 11, 5, 11));

        setMaximumVolumeAndDistanceOfRange(QUARRY_RANGE, 5000, 20, 20);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
        setMaximumVolumeAndDistanceOfRange(PLACEMENT_RANGE, 5000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        if (!(ctx.level() instanceof ServerLevel serverLevel)) return;

        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        WillState will = RitualHelper.queryWill(ctx.level(), masterPos, Math.min(MIN_DEFAULT, Math.min(MIN_STEADFAST, MIN_CORROSIVE)));

        boolean hasRaw = will.hasDefault();
        boolean doReplace = will.hasSteadfast();
        boolean doFilter = will.hasCorrosive();

        double steadfastWillUsed = 0;

        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, masterPos).getFirst();
        BlockEntity chestTile = ctx.level().getBlockEntity(chestPos);
        IItemHandler chestHandler = chestTile != null ? Utils.getInventory(chestTile, Direction.DOWN) : null;

        List<BlockPos> quarryPositions = RitualHelper.getRangePositions(ctx.master(), this, QUARRY_RANGE, masterPos);
        if (quarryPositions.isEmpty()) return;

        if (!scanInitialized) {
            BlockPos firstPos = quarryPositions.getFirst();
            currentX = firstPos.getX();
            currentY = firstPos.getY();
            currentZ = firstPos.getZ();
            scanInitialized = true;
        }

        FakePlayer fakePlayer = new FakePlayer(serverLevel, new GameProfile(owner, "[NeoVitae]"));
        ItemStack toolStack = new ItemStack(Items.NETHERITE_PICKAXE);

        // Build filter list from chest (for corrosive mode)
        java.util.Set<net.minecraft.world.item.Item> filterItems = new java.util.HashSet<>();
        if (doFilter && chestHandler != null) {
            for (int i = 0; i < chestHandler.getSlots(); i++) {
                ItemStack filterStack = chestHandler.getStackInSlot(i);
                if (!filterStack.isEmpty()) {
                    filterItems.add(filterStack.getItem());
                }
            }
            // If no filter items, disable filter mode
            if (filterItems.isEmpty()) doFilter = false;
        }

        // --- QUARRY: Process 1 block ---
        boolean processed = false;
        int attempts = 0;
        int maxAttempts = quarryPositions.size(); // Prevent infinite loop

        while (!processed && attempts < maxAttempts) {
            attempts++;

            // Find next valid position
            BlockPos targetPos = new BlockPos(currentX, currentY, currentZ);
            advanceScanPosition(quarryPositions);

            // Validate the position is in range
            if (!quarryPositions.contains(targetPos)) continue;

            BlockState state = ctx.level().getBlockState(targetPos);

            // Skip air, liquids, unbreakable blocks
            if (state.isAir()) continue;
            if (!state.getFluidState().isEmpty() && state.getBlock().defaultBlockState().isAir()) continue;
            float destroySpeed = state.getDestroySpeed(ctx.level(), targetPos);
            if (destroySpeed < 0) continue; // Unbreakable
            if (state.getBlock() instanceof com.breakinblocks.neovitae.common.block.BlockRitualStone) continue;
            if (state.getBlock() instanceof com.breakinblocks.neovitae.common.block.BlockMasterRitualStone) continue;

            // Check block protection
            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), targetPos, owner)) continue;

            // CORROSIVE: Filter mode - only destroy blocks matching items in chest
            if (doFilter) {
                // Get the block's drop to check against filter
                LootParams.Builder filterLootBuilder = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, targetPos.getCenter())
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withParameter(LootContextParams.TOOL, toolStack)
                        .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);
                List<ItemStack> filterDrops = state.getDrops(filterLootBuilder);

                boolean matchesFilter = false;
                for (ItemStack drop : filterDrops) {
                    if (filterItems.contains(drop.getItem())) {
                        matchesFilter = true;
                        break;
                    }
                }
                // Also check if the block item itself matches
                ItemStack blockItem = new ItemStack(state.getBlock().asItem());
                if (!blockItem.isEmpty() && filterItems.contains(blockItem.getItem())) {
                    matchesFilter = true;
                }

                if (!matchesFilter) continue;
                will.use(EnumWillType.CORROSIVE, WILL_PER_FILTER);
            }

            // STEADFAST: Replace mode - place block in placement area instead of dropping
            if (doReplace && (will.getSteadfast() - steadfastWillUsed) >= WILL_PER_REPLACE) {
                ItemStack blockAsItem = new ItemStack(state.getBlock().asItem());
                if (!blockAsItem.isEmpty()) {
                    // Try to place in the placement area
                    boolean placed = tryPlaceInRange(ctx, masterPos, state);
                    if (placed) {
                        ctx.level().destroyBlock(targetPos, false);
                        will.use(EnumWillType.STEADFAST, WILL_PER_REPLACE);
                        steadfastWillUsed += WILL_PER_REPLACE;
                        processed = true;
                        continue;
                    }
                }
                // Fall through to normal destroy if placement failed
            }

            LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, targetPos.getCenter())
                    .withParameter(LootContextParams.BLOCK_STATE, state)
                    .withParameter(LootContextParams.TOOL, toolStack)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, ctx.level().getBlockEntity(targetPos))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);

            List<ItemStack> blockDrops = state.getDrops(lootBuilder);
            ctx.level().destroyBlock(targetPos, false);
            processed = true;

            for (ItemStack dropStack : blockDrops) {
                if (chestTile != null) {
                    dropStack = Utils.insertStackIntoTile(dropStack, chestTile, Direction.DOWN);
                }
                if (!dropStack.isEmpty()) {
                    Utils.spawnStackAtBlock(ctx.level(), masterPos, Direction.UP, dropStack);
                }
            }
        }

        will.drain(ctx.level(), masterPos);

        if (processed) {
            ctx.syphon(getRefreshCost());
        }
    }

    /**
     * Advances the scan position to the next block in the quarry range.
     */
    private void advanceScanPosition(List<BlockPos> quarryPositions) {
        if (quarryPositions.isEmpty()) return;

        // Find current index
        BlockPos current = new BlockPos(currentX, currentY, currentZ);
        int index = quarryPositions.indexOf(current);

        // Move to next position, wrapping around
        int nextIndex = (index + 1) % quarryPositions.size();
        BlockPos next = quarryPositions.get(nextIndex);
        currentX = next.getX();
        currentY = next.getY();
        currentZ = next.getZ();
    }

    /**
     * Tries to place a block state in the placement range at the first available air position.
     */
    private boolean tryPlaceInRange(RitualContext ctx, BlockPos masterPos, BlockState stateToPlace) {
        List<BlockPos> placementPositions = RitualHelper.getRangePositions(ctx.master(), this, PLACEMENT_RANGE, masterPos);
        for (BlockPos placePos : placementPositions) {
            BlockState existing = ctx.level().getBlockState(placePos);
            if (existing.isAir()) {
                ctx.level().setBlock(placePos, stateToPlace, Block.UPDATE_ALL);
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        currentX = tag.getInt("currentX");
        currentY = tag.getInt("currentY");
        currentZ = tag.getInt("currentZ");
        scanInitialized = tag.getBoolean("scanInitialized");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putInt("currentX", currentX);
        tag.putInt("currentY", currentY);
        tag.putInt("currentZ", currentZ);
        tag.putBoolean("scanInitialized", scanInitialized);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 5;
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".will.steadfast"),
                Component.translatable(getTranslationKey() + ".will.corrosive")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.AIR);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualYawningVoid();
    }
}
