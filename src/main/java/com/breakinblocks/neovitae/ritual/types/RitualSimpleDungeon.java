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
 * "Edge of the Hidden Realm" ritual - creates a simple dungeon instance.
 * This ritual activates once and creates a portal pillar to a mini-dungeon
 * in the Blood Magic dungeon dimension.
 */
public class RitualSimpleDungeon extends DungeonRitualBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RitualSimpleDungeon.class);

    public RitualSimpleDungeon() {
        super("simple_dungeon", 0, 80000, "ritual." + NeoVitae.MODID + ".simple_dungeon");
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        Level world = masterRitualStone.getWorldObj();
        BlockPos masterPos = masterRitualStone.getMasterBlockPos();

        if (world.isClientSide || !(world instanceof ServerLevel serverWorld)) {
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

        LOGGER.info("Creating mini-dungeon at {} -> dungeon controller {}", masterPos, dungeonControllerPos);

        // Create the dungeon using DungeonSynthesizer
        DungeonSynthesizer synthesizer = new DungeonSynthesizer();
        BlockPos[] positions = synthesizer.generateInitialRoom(
                ModRoomPools.MINI_DUNGEON_ENTRANCES,
                dungeonWorld.random,
                dungeonWorld,
                dungeonControllerPos
        );

        BlockPos playerSpawnPos = positions[0];
        BlockPos portalPos = positions[1];

        LOGGER.info("Dungeon generated. Player spawn: {}, Portal: {}", playerSpawnPos, portalPos);

        // Calculate positions for portal pillars
        BlockPos pillarPos = masterPos.above(2);  // Above the rune column
        BlockPos overworldPlayerPos = masterPos.relative(masterRitualStone.getDirection(), 2);

        // Perform common cleanup FIRST (replaces runes with smooth stone, removes MRS)
        performRitualCleanup(masterRitualStone, world);

        // THEN spawn portal pillars (after cleanup so they don't get overwritten)
        spawnPortalPillar(world, dungeonWorld, pillarPos, playerSpawnPos);
        spawnPortalPillar(dungeonWorld, world, portalPos, overworldPlayerPos);

        // Stop the ritual since it's a one-time effect
        masterRitualStone.stopRitual(Ritual.BreakType.DEACTIVATE);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        // Base layer - uses AIR at inner corners to distinguish from Placer ritual
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);

        // Water and fire extensions at y=0
        for (int i = -1; i <= 1; i++) {
            addRune(components, i, 0, 3, EnumRuneType.WATER);
            addRune(components, i, 0, -3, EnumRuneType.WATER);
            addRune(components, 3, 0, i, EnumRuneType.FIRE);
            addRune(components, -3, 0, i, EnumRuneType.FIRE);
        }

        // Iconic vertical column above MRS
        for (int j = 1; j <= 4; j++) {
            addRune(components, 0, j, 0, EnumRuneType.EARTH);
        }

        // Upper platform at y=4
        addParallelRunes(components, 1, 4, EnumRuneType.AIR);
        addParallelRunes(components, 2, 4, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualSimpleDungeon();
    }
}
