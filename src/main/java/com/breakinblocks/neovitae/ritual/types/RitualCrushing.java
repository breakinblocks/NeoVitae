package com.breakinblocks.neovitae.ritual.types;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of the Crusher - Destroys blocks and collects their drops via loot tables.
 *
 * <p>Demon Will effects:
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

        if (!(ctx.level() instanceof ServerLevel serverLevel)) return;

        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        double rawWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DEFAULT);
        double steadfastWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.STEADFAST);
        double destructiveWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE);

        boolean hasRaw = rawWill >= MIN_DEFAULT;
        boolean doSilk = steadfastWill >= MIN_STEADFAST;
        boolean doFortune = destructiveWill >= MIN_DESTRUCTIVE;

        // Silk touch overrides fortune
        if (doSilk) {
            doFortune = false;
        }

        refreshTime = hasRaw ? Math.max(1, 40 - (int) (rawWill / 5)) : 40;

        ItemStack toolStack = new ItemStack(Items.NETHERITE_PICKAXE);
        if (doFortune) {
            Holder<Enchantment> fortune = serverLevel.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            toolStack.enchant(fortune, 3);
        } else if (doSilk) {
            Holder<Enchantment> silkTouch = serverLevel.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
            toolStack.enchant(silkTouch, 1);
        }

        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, masterPos).getFirst();
        BlockEntity inv = ctx.level().getBlockEntity(chestPos);
        boolean hasInv = inv != null && Utils.getNumberOfFreeSlots(inv, Direction.DOWN) >= 1;

        FakePlayer fakePlayer = new FakePlayer(serverLevel, new GameProfile(owner, "[NeoVitae]"));

        double silkWillUsed = 0;
        double fortuneWillUsed = 0;

        // --- CRUSH: Process 1 block per tick ---
        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, CRUSH_RANGE, masterPos);
        boolean crushed = false;

        for (BlockPos pos : positions) {
            if (crushed) break;

            BlockState state = ctx.level().getBlockState(pos);

            // Skip air, liquids, unbreakable blocks, and ritual stones
            if (state.isAir()) continue;
            FluidState fluidState = state.getFluidState();
            if (!fluidState.isEmpty() && state.getBlock().defaultBlockState().isAir()) continue;
            float destroySpeed = state.getDestroySpeed(ctx.level(), pos);
            if (destroySpeed < 0) continue; // Unbreakable
            if (state.getBlock() instanceof com.breakinblocks.neovitae.common.block.BlockRitualStone) continue;
            if (state.getBlock() instanceof com.breakinblocks.neovitae.common.block.BlockMasterRitualStone) continue;

            // Check will costs before crushing
            if (doSilk && (steadfastWill - silkWillUsed) < WILL_PER_SILK) {
                doSilk = false;
                // Rebuild tool without silk touch
                toolStack = new ItemStack(Items.NETHERITE_PICKAXE);
            }
            if (doFortune && (destructiveWill - fortuneWillUsed) < WILL_PER_FORTUNE) {
                doFortune = false;
                toolStack = new ItemStack(Items.NETHERITE_PICKAXE);
            }

            // Check block protection
            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) continue;

            LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, pos.getCenter())
                    .withParameter(LootContextParams.BLOCK_STATE, state)
                    .withParameter(LootContextParams.TOOL, toolStack)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, ctx.level().getBlockEntity(pos))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);

            List<ItemStack> blockDrops = state.getDrops(lootBuilder);

            // Break the block without natural drops
            ctx.level().destroyBlock(pos, false);
            crushed = true;

            // Consume will for enchant effects
            if (doSilk) silkWillUsed += WILL_PER_SILK;
            if (doFortune) fortuneWillUsed += WILL_PER_FORTUNE;

            for (ItemStack dropStack : blockDrops) {
                if (hasInv) {
                    dropStack = Utils.insertStackIntoTile(dropStack, inv, Direction.DOWN);
                }
                if (!dropStack.isEmpty()) {
                    Utils.spawnStackAtBlock(ctx.level(), masterPos, Direction.UP, dropStack);
                }
            }
        }

        if (silkWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.STEADFAST, silkWillUsed);
        }
        if (fortuneWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE, fortuneWillUsed);
        }

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
