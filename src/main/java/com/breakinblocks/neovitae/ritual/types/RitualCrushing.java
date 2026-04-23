package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.block.BlockMasterRitualStone;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.SpiritusState;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of the Crusher - Destroys blocks and collects their drops via loot tables.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduces refresh time (40 ticks scaling down with will)</li>
 *   <li><b>Steadfast</b> - Harvest with Silk Touch</li>
 *   <li><b>Destructive</b> - Harvest with Fortune III</li>
 * </ul>
 *
 * <p>Silk Touch overrides Fortune when both are present. Drops are inserted into
 * an adjacent chest if available, otherwise spawned in the world.
 * Processes 1 block per ritual tick, matching original NeoVitae behavior.
 */
public class RitualCrushing extends Ritual {

    public static final String CRUSH_RANGE = "crushRange";
    public static final String CHEST_RANGE = "chestRange";

    private static final double MIN_DEFAULT = 10.0;
    private static final double MIN_STEADFAST = 10.0;
    private static final double MIN_DESTRUCTIVE = 10.0;

    private static final double WILL_PER_SILK = 0.5;
    private static final double WILL_PER_FORTUNE = 0.5;

    private int refreshTime = 40;

    public RitualCrushing() {
        super("crushing", 0, 2500, "ritual." + NeoVitae.MODID + ".crushing");
        addBlockRange(CRUSH_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, -1, 0), 1, 1, 1));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(CRUSH_RANGE, 64, 10, 10);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        ServerLevel serverLevel = ctx.serverLevel();

        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        SpiritusState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_DEFAULT);

        boolean doSilk = will.hasSteadfast();
        boolean doFortune = will.hasDestructive();

        // Silk touch overrides fortune
        if (doSilk) {
            doFortune = false;
        }

        refreshTime = scaleByRawWill(will, 40, 1, 5);

        ItemStack toolStack = RitualHelper.createMiningTool(serverLevel, doFortune, doSilk);

        RitualHelper.ChestOutput chest = RitualHelper.resolveChestOutput(ctx, this, CHEST_RANGE);
        BlockEntity inv = chest.tile();
        boolean hasInv = chest.hasFreeSlot();

        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(serverLevel, owner, "NeoVitae");

        double silkWillUsed = 0;
        double fortuneWillUsed = 0;

        // --- CRUSH: Process 1 block per tick ---
        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, CRUSH_RANGE, masterPos);
        boolean crushed = false;

        for (BlockPos pos : positions) {
            if (crushed) break;

            BlockState state = ctx.level().getBlockState(pos);

            // Skip air, pure liquid blocks, unbreakable blocks, and ritual stones
            if (state.isAir()) continue;
            if (state.getBlock() instanceof LiquidBlock) continue;
            float destroySpeed = state.getDestroySpeed(ctx.level(), pos);
            if (destroySpeed < 0) continue; // Unbreakable
            if (state.getBlock() instanceof BlockRitualStone) continue;
            if (state.getBlock() instanceof BlockMasterRitualStone) continue;

            if (doSilk && (will.getSteadfast() - silkWillUsed) < WILL_PER_SILK) {
                doSilk = false;
                toolStack = RitualHelper.createMiningTool(serverLevel, false, false);
            }
            if (doFortune && (will.getDestructive() - fortuneWillUsed) < WILL_PER_FORTUNE) {
                doFortune = false;
                toolStack = RitualHelper.createMiningTool(serverLevel, false, false);
            }

            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) continue;

            List<ItemStack> blockDrops = RitualHelper.getBlockDrops(serverLevel, state, pos, toolStack, fakePlayer);

            ctx.level().destroyBlock(pos, false);
            crushed = true;

            RitualHelper.chanceStream(ctx.level(), 15, () ->
                    StreamPresets.arcaneBolt(masterPos, pos).build()
                            .sendToNearby(ctx.serverLevel(), masterPos, 64));

            if (doSilk) silkWillUsed += WILL_PER_SILK;
            if (doFortune) fortuneWillUsed += WILL_PER_FORTUNE;

            RitualHelper.distributeDrops(blockDrops, hasInv ? inv : null,
                    stack -> Utils.spawnStackAtBlock(ctx.level(), masterPos, Direction.UP, stack));
        }

        RitualHelper.drainWill(will, ctx.level(), masterPos,
                0, 0, fortuneWillUsed, 0, silkWillUsed);

        if (crushed) {
            ctx.syphon(getRefreshCost());
        }
    }

    @Override
    public int getRefreshTime() {
        return refreshTime;
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".will.default"),
                Component.translatable(getTranslationKey() + ".will.steadfast"),
                Component.translatable(getTranslationKey() + ".will.destructive")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addRune(components, 3, 0, 0, EnumRuneType.FIRE);
        addRune(components, -3, 0, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, 3, EnumRuneType.FIRE);
        addRune(components, 0, 0, -3, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrushing();
    }
}
