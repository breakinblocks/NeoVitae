package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.WillState;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual of Lava - generates lava in a configurable area.
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Reduced LP cost for lava placement, enables tank filling</li>
 *   <li><b>Vengeful</b> - Apply Fire Fuse to non-player mobs in fire range</li>
 *   <li><b>Steadfast</b> - Apply Fire Resistance to players in fire range</li>
 *   <li><b>Corrosive</b> - Deal fire damage to entities (skip fire-immune) in fire range</li>
 *   <li><b>Destructive</b> - Reserved (range expansion, not implemented)</li>
 * </ul>
 */
public class RitualLava extends Ritual {

    public static final String LAVA_RANGE = "lavaRange";
    public static final String TANK_RANGE = "tankRange";
    public static final String FIRE_RANGE = "fireRange";

    private static final double MIN_WILL = 0.5;
    private static final double WILL_PER_LAVA = 0.5;
    private static final double WILL_PER_TANK_FILL = 1.0;
    private static final double VENGEFUL_WILL_PER_MOB = 1.0;
    private static final double CORROSIVE_WILL_PER_HIT = 1.0;
    private static final double STEADFAST_WILL_PER_PLAYER = 0.5;
    private static final int BASE_LAVA_COST = 500;
    private static final float CORROSIVE_FIRE_DAMAGE = 2.0F;

    public RitualLava() {
        super("lava", 0, 10000, "ritual." + NeoVitae.MODID + ".lava");
        addBlockRange(LAVA_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(TANK_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(FIRE_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));

        setMaximumVolumeAndDistanceOfRange(LAVA_RANGE, 9, 3, 3);
        setMaximumVolumeAndDistanceOfRange(TANK_RANGE, 1, 10, 10);
        setMaximumVolumeAndDistanceOfRange(FIRE_RANGE, 0, 10, 10);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        WillState will = RitualHelper.queryWill(ctx.level(), masterPos, MIN_WILL);

        // LP cost per lava placement — cheaper with more raw will
        int lavaCost = will.hasDefault() ? Math.max(0, BASE_LAVA_COST - (int) will.getDefault()) : BASE_LAVA_COST;
        if (lavaCost == 0) lavaCost = 1; // Minimum 1 LP

        int maxEffects = ctx.maxOperations(lavaCost);
        int totalEffects = 0;

        double rawUsed = 0;
        double corrosiveUsed = 0;
        double vengefulUsed = 0;
        double steadfastUsed = 0;

        // --- TANK FILLING: Fill fluid tanks with lava when raw will is present ---
        if (will.hasDefault()) {
            List<BlockPos> tankPositions = RitualHelper.getRangePositions(ctx.master(), this, TANK_RANGE, masterPos);
            for (BlockPos tankPos : tankPositions) {
                if (totalEffects >= maxEffects) break;
                if ((will.getDefault() - rawUsed) < WILL_PER_TANK_FILL) break;

                IFluidHandler fluidHandler = ctx.level().getCapability(
                        Capabilities.FluidHandler.BLOCK, tankPos, null);
                if (fluidHandler != null) {
                    FluidStack lavaStack = new FluidStack(Fluids.LAVA, 1000);
                    int filled = fluidHandler.fill(lavaStack, IFluidHandler.FluidAction.SIMULATE);
                    if (filled > 0) {
                        fluidHandler.fill(lavaStack, IFluidHandler.FluidAction.EXECUTE);
                        rawUsed += WILL_PER_TANK_FILL;
                        totalEffects++;
                    }
                }
            }
        }

        // --- LAVA PLACEMENT: Place lava in empty blocks ---
        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, LAVA_RANGE, masterPos);
        for (BlockPos pos : positions) {
            if (totalEffects >= maxEffects) break;

            if (ctx.level().isEmptyBlock(pos)) {
                if (BlockProtectionHelper.tryPlaceBlock(ctx.level(), pos, Blocks.LAVA.defaultBlockState(), owner)) {
                    totalEffects++;
                    if (will.hasDefault()) {
                        rawUsed += WILL_PER_LAVA;
                    }
                }
            }
        }

        // --- FIRE EFFECTS: Apply effects to entities in fire range ---

        // Vengeful: Fire Fuse on non-player mobs
        if (will.hasVengeful()) {
            List<LivingEntity> mobs = RitualHelper.getEntitiesInRange(ctx, this, FIRE_RANGE, LivingEntity.class,
                    mob -> mob.isAlive() && !(mob instanceof Player));
            for (LivingEntity mob : mobs) {
                if ((will.getVengeful() - vengefulUsed) < VENGEFUL_WILL_PER_MOB) break;
                mob.addEffect(new MobEffectInstance(NVMobEffects.FIRE_FUSE, 100, 0, true, true));
                vengefulUsed += VENGEFUL_WILL_PER_MOB;
            }
        }

        // Steadfast: Fire Resistance on players
        if (will.hasSteadfast()) {
            List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, FIRE_RANGE, Player.class,
                    player -> player.isAlive() && !player.isSpectator());
            for (Player player : players) {
                if ((will.getSteadfast() - steadfastUsed) < STEADFAST_WILL_PER_PLAYER) break;
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
                steadfastUsed += STEADFAST_WILL_PER_PLAYER;
            }
        }

        // Corrosive: Fire damage to entities (skip fire-immune)
        if (will.hasCorrosive()) {
            List<LivingEntity> entities = RitualHelper.getEntitiesInRange(ctx, this, FIRE_RANGE, LivingEntity.class,
                    entity -> entity.isAlive() && !entity.fireImmune());
            for (LivingEntity entity : entities) {
                if ((will.getCorrosive() - corrosiveUsed) < CORROSIVE_WILL_PER_HIT) break;
                entity.hurt(ctx.level().damageSources().onFire(), CORROSIVE_FIRE_DAMAGE);
                entity.setRemainingFireTicks(60);
                corrosiveUsed += CORROSIVE_WILL_PER_HIT;
            }
        }

        if (totalEffects > 0) {
            ctx.syphon(lavaCost * totalEffects);
        }

        will.use(EnumWillType.DEFAULT, rawUsed);
        will.use(EnumWillType.CORROSIVE, corrosiveUsed);
        will.use(EnumWillType.VENGEFUL, vengefulUsed);
        will.use(EnumWillType.STEADFAST, steadfastUsed);
        will.drain(ctx.level(), masterPos);
    }

    @Override
    public int getRefreshTime() {
        return 10;
    }

    @Override
    public int getRefreshCost() {
        return 500;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualLava();
    }
}
