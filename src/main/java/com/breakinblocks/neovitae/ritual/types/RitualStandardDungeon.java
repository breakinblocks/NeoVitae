package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;
import com.breakinblocks.neovitae.structures.ModRoomPools;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.function.Consumer;

/**
 * "Pathway to the Endless Realm" ritual - creates a full procedural dungeon instance.
 * This is a higher-tier ritual that generates a larger, more complex dungeon
 * with multiple rooms and challenges.
 */
public class RitualStandardDungeon extends DungeonRitualBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RitualStandardDungeon.class);

    public RitualStandardDungeon() {
        super("standard_dungeon", 0, 150000, "ritual." + NeoVitae.MODID + ".standard_dungeon");
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        Level world = masterRitualStone.getWorldObj();
        BlockPos masterPos = masterRitualStone.getMasterBlockPos();

        if (world.isClientSide || !(world instanceof ServerLevel)) {
            return;
        }

        ServerLevel dungeonWorld = getDungeonWorld(world);
        if (dungeonWorld == null) {
            LOGGER.warn("Could not access dungeon dimension");
            masterRitualStone.stopRitual(Ritual.BreakType.DEACTIVATE);
            return;
        }

        // Get the spawn position for this dungeon (the controller position)
        BlockPos dungeonControllerPos = SoulNetworkHelper.getSpawnPositionOfDungeon();
        if (dungeonControllerPos == null) {
            LOGGER.warn("Could not get dungeon spawn position");
            masterRitualStone.stopRitual(Ritual.BreakType.DEACTIVATE);
            return;
        }

        LOGGER.info("Creating standard dungeon at {} -> dungeon controller {}", masterPos, dungeonControllerPos);

        // Create the dungeon using DungeonSynthesizer with standard dungeon entrances
        DungeonSynthesizer synthesizer = new DungeonSynthesizer();
        BlockPos[] positions = synthesizer.generateInitialRoom(
                ModRoomPools.STANDARD_DUNGEON_ENTRANCES,
                dungeonWorld.random,
                dungeonWorld,
                dungeonControllerPos
        );

        BlockPos playerSpawnPos = positions[0];
        BlockPos portalPos = positions[1];

        LOGGER.info("Dungeon generated. Player spawn: {}, Portal: {}", playerSpawnPos, portalPos);

        // Calculate positions - pillar is 4 blocks up (same as 1.20.1)
        // This works because cleanup happens first, converting the rune at (0,4,0) to smooth stone
        BlockPos pillarPos = masterPos.relative(Direction.UP, 4);
        BlockPos overworldPlayerPos = masterPos.relative(Direction.UP).relative(masterRitualStone.getDirection(), 2);

        // Perform cleanup FIRST (converts ritual stones to smooth stone)
        // This must happen before placing the pillar, otherwise the pillar would be overwritten
        performRitualCleanup(masterRitualStone, world);

        // Spawn portal pillars AFTER cleanup
        spawnPortalPillar(world, dungeonWorld, pillarPos, playerSpawnPos);
        spawnPortalPillar(dungeonWorld, world, portalPos, overworldPlayerPos);

        // Stop the ritual since it's a one-time effect
        masterRitualStone.stopRitual(Ritual.BreakType.DEACTIVATE);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        // Inner ring
        addRune(components, 0, 0, 1, EnumRuneType.FIRE);
        addRune(components, -1, 0, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, -1, EnumRuneType.WATER);
        addRune(components, 1, 0, 0, EnumRuneType.WATER);
        addRune(components, -1, 0, 1, EnumRuneType.DUSK);
        addRune(components, 1, 0, -1, EnumRuneType.DUSK);

        // Water cluster
        addRune(components, 2, 0, 2, EnumRuneType.WATER);
        addRune(components, 2, 0, 1, EnumRuneType.WATER);
        addRune(components, 1, 0, 2, EnumRuneType.WATER);

        // Fire cluster
        addRune(components, -2, 0, -2, EnumRuneType.FIRE);
        addRune(components, -2, 0, -1, EnumRuneType.FIRE);
        addRune(components, -1, 0, -2, EnumRuneType.FIRE);

        // Air cluster
        addRune(components, -3, 0, 3, EnumRuneType.AIR);
        addRune(components, -2, 0, 3, EnumRuneType.AIR);
        addRune(components, -1, 0, 3, EnumRuneType.AIR);
        addRune(components, -3, 0, 2, EnumRuneType.AIR);
        addRune(components, -3, 0, 1, EnumRuneType.AIR);

        // Earth cluster
        addRune(components, 3, 0, -3, EnumRuneType.EARTH);
        addRune(components, 2, 0, -3, EnumRuneType.EARTH);
        addRune(components, 1, 0, -3, EnumRuneType.EARTH);
        addRune(components, 3, 0, -2, EnumRuneType.EARTH);
        addRune(components, 3, 0, -1, EnumRuneType.EARTH);

        // Dusk clusters (corners)
        addRune(components, 3, 0, 3, EnumRuneType.DUSK);
        addRune(components, 3, 0, 4, EnumRuneType.DUSK);
        addRune(components, 4, 0, 3, EnumRuneType.DUSK);
        addRune(components, 2, 0, 4, EnumRuneType.DUSK);
        addRune(components, 4, 0, 2, EnumRuneType.DUSK);

        addRune(components, -3, 0, -3, EnumRuneType.DUSK);
        addRune(components, -3, 0, -4, EnumRuneType.DUSK);
        addRune(components, -4, 0, -3, EnumRuneType.DUSK);
        addRune(components, -2, 0, -4, EnumRuneType.DUSK);
        addRune(components, -4, 0, -2, EnumRuneType.DUSK);

        // Upper tier runes
        addCornerRunes(components, 3, 4, EnumRuneType.EARTH);
        addOffsetRunes(components, 2, 3, 4, EnumRuneType.DUSK);
        addOffsetRunes(components, 2, 4, 4, EnumRuneType.DUSK);
        addOffsetRunes(components, 1, 4, 4, EnumRuneType.AIR);
        addParallelRunes(components, 4, 4, EnumRuneType.AIR);

        // Central column
        addRune(components, 0, 2, 0, EnumRuneType.EARTH);
        addRune(components, 0, 3, 0, EnumRuneType.DUSK);
        addRune(components, 0, 4, 0, EnumRuneType.DUSK);
        addRune(components, 0, 5, 0, EnumRuneType.DUSK);
        addRune(components, 0, 6, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualStandardDungeon();
    }
}
