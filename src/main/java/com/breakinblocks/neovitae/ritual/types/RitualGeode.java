package com.breakinblocks.neovitae.ritual.types;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;
import com.breakinblocks.neovitae.will.WorldDemonWillHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of the Geode's Bounty - Harvests mature crystal clusters and accelerates budding block growth.
 *
 * <p>Uses tag-based detection for cross-mod compatibility:
 * <ul>
 *   <li>{@code neovitae:geode_harvestable} - Mature clusters to harvest (amethyst, certus, etc.)</li>
 *   <li>{@code neovitae:geode_acceleratable} - Budding blocks to accelerate (includes c:budding_blocks)</li>
 * </ul>
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Store harvested drops into adjacent inventory</li>
 *   <li><b>Steadfast</b> - Harvest with Silk Touch</li>
 *   <li><b>Destructive</b> - Harvest with Fortune III</li>
 *   <li><b>Corrosive</b> - Extra random ticks on budding blocks</li>
 *   <li><b>Vengeful</b> - Damage nearby mobs, bonus acceleration ticks per mob hurt</li>
 * </ul>
 */
public class RitualGeode extends Ritual {

    public static final String HARVEST_RANGE = "harvest";
    public static final String ACCELERATION_RANGE = "acceleration";
    public static final String CHEST_RANGE = "chest";
    public static final String HARM_RANGE = "harm";

    // Minimum will thresholds to activate each feature
    private static final double MIN_DEFAULT = 10.0;
    private static final double MIN_DESTRUCTIVE = 10.0;
    private static final double MIN_STEADFAST = 10.0;
    private static final double MIN_CORROSIVE = 10.0;
    private static final double MIN_VENGEFUL = 10.0;

    // Will cost per operation
    private static final double WILL_PER_SILK = 0.5;
    private static final double WILL_PER_FORTUNE = 0.5;
    private static final double WILL_PER_GROWTH = 0.5;
    private static final double WILL_PER_STORE = 0.5;
    private static final double WILL_PER_HARM = 2.0;

    private static final int HURT_DAMAGE = 2;
    private static final int MAX_HARM = 10;

    public RitualGeode() {
        super("geode", 0, 10000, "ritual." + NeoVitae.MODID + ".geode");
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(ACCELERATION_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-3, -3, -3), 7, 7, 7));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(HARM_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));

        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 3000, 15, 15);
        setMaximumVolumeAndDistanceOfRange(ACCELERATION_RANGE, 500, 15, 15);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 15, 15);
        setMaximumVolumeAndDistanceOfRange(HARM_RANGE, 3000, 15, 15);
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

        // Query demon will
        double rawWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DEFAULT);
        double steadfastWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.STEADFAST);
        double destructiveWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE);
        double corrosiveWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.CORROSIVE);
        double vengefulWill = WorldDemonWillHandler.getCurrentWill(ctx.level(), masterPos, EnumWillType.VENGEFUL);

        // Determine available features
        boolean doStore = rawWill >= MIN_DEFAULT;
        boolean doFortune = destructiveWill >= MIN_DESTRUCTIVE;
        boolean doSilk = steadfastWill >= MIN_STEADFAST;
        boolean doAccel = corrosiveWill >= MIN_CORROSIVE;
        boolean doHarm = vengefulWill >= MIN_VENGEFUL;

        // Silk touch overrides fortune
        if (doSilk) {
            doFortune = false;
        }

        // Build the tool for loot context
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

        // Check for storage inventory
        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, masterPos).getFirst();
        BlockEntity inv = ctx.level().getBlockEntity(chestPos);
        boolean hasInv = inv != null && Utils.getNumberOfFreeSlots(inv, Direction.DOWN) >= 1;
        doStore = doStore && hasInv;

        // Create fake player for loot context (fixes drops for blocks that require THIS_ENTITY like certus quartz)
        FakePlayer fakePlayer = new FakePlayer(serverLevel, new GameProfile(owner, "[NeoVitae Geode]"));

        // Track will consumption
        double fortuneWillUsed = 0;
        double silkWillUsed = 0;
        double storeWillUsed = 0;

        // --- HARVEST: Break mature clusters and collect drops ---
        List<BlockPos> harvestPositions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, masterPos);
        int totalCost = 0;

        for (BlockPos harvestPos : harvestPositions) {
            if (totalCost + getRefreshCost() > ctx.currentEssence()) break;

            BlockState state = ctx.level().getBlockState(harvestPos);
            if (!state.is(NVTags.Blocks.GEODE_HARVESTABLE)) continue;

            // Check will costs before harvesting
            if (doFortune && (destructiveWill - fortuneWillUsed) < WILL_PER_FORTUNE) continue;
            if (doSilk && (steadfastWill - silkWillUsed) < WILL_PER_SILK) continue;

            // Check block protection
            if (!BlockProtectionHelper.canBreakBlock(ctx.level(), harvestPos, owner)) continue;

            // Get drops with proper loot context
            LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, harvestPos.getCenter())
                    .withParameter(LootContextParams.BLOCK_STATE, state)
                    .withParameter(LootContextParams.TOOL, toolStack)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, ctx.level().getBlockEntity(harvestPos))
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, fakePlayer);

            List<ItemStack> blockDrops = state.getDrops(lootBuilder);

            // Break the block without natural drops (we handle them ourselves)
            ctx.level().destroyBlock(harvestPos, false);
            totalCost += getRefreshCost();

            // Consume will for enchant effects
            if (doFortune) fortuneWillUsed += WILL_PER_FORTUNE;
            if (doSilk) silkWillUsed += WILL_PER_SILK;

            // Handle drops: store in inventory or spawn in world
            for (ItemStack dropStack : blockDrops) {
                if (doStore && (rawWill - storeWillUsed) >= WILL_PER_STORE) {
                    dropStack = Utils.insertStackIntoTile(dropStack, inv, Direction.DOWN);
                    storeWillUsed += WILL_PER_STORE;
                }
                if (!dropStack.isEmpty()) {
                    Block.popResource(ctx.level(), harvestPos, dropStack);
                }
            }
        }

        // --- HARM: Damage nearby mobs (vengeful will) ---
        int harmTicks = 0;
        double harmWillUsed = 0;

        if (doHarm) {
            List<LivingEntity> mobs = RitualHelper.getEntitiesInRange(ctx, this, HARM_RANGE, LivingEntity.class,
                    mob -> mob.isAlive() && !(mob instanceof net.minecraft.world.entity.player.Player));

            for (LivingEntity mob : mobs) {
                if (harmTicks >= MAX_HARM) break;
                if ((vengefulWill - harmWillUsed) < WILL_PER_HARM) break;

                if (mob.hurt(ctx.level().damageSources().source(NVDamageSources.RITUAL), HURT_DAMAGE)) {
                    harmTicks++;
                    harmWillUsed += WILL_PER_HARM;
                }
            }
        }

        // --- ACCELERATION: Speed up budding block growth ---
        double accelWillUsed = 0;
        List<BlockPos> accelPositions = RitualHelper.getRangePositions(ctx.master(), this, ACCELERATION_RANGE, masterPos);

        for (BlockPos accelPos : accelPositions) {
            BlockState state = ctx.level().getBlockState(accelPos);
            if (!state.is(NVTags.Blocks.GEODE_ACCELERATABLE)) continue;

            // Bonus ticks from harming mobs (vengeful synergy)
            for (int i = 0; i < harmTicks; i++) {
                state.randomTick(serverLevel, accelPos, ctx.level().getRandom());
            }

            // Extra ticks from corrosive will
            if (doAccel && (corrosiveWill - accelWillUsed) >= WILL_PER_GROWTH) {
                state.randomTick(serverLevel, accelPos, ctx.level().getRandom());
                state.randomTick(serverLevel, accelPos, ctx.level().getRandom());
                accelWillUsed += WILL_PER_GROWTH;
            }
        }

        // Drain consumed will
        if (storeWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.DEFAULT, storeWillUsed);
        }
        if (fortuneWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.DESTRUCTIVE, fortuneWillUsed);
        }
        if (silkWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.STEADFAST, silkWillUsed);
        }
        if (accelWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.CORROSIVE, accelWillUsed);
        }
        if (harmWillUsed > 0) {
            WorldDemonWillHandler.drainWillFromChunk(ctx.level(), masterPos, EnumWillType.VENGEFUL, harmWillUsed);
        }

        // Syphon LP
        ctx.syphon(totalCost > 0 ? totalCost : getRefreshCost());
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 50;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualGeode();
    }
}
