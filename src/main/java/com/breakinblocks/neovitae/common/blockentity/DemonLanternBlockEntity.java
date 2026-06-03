package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.SpiritusChunk;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DemonLanternBlockEntity extends BlockEntity {

    private static final int AMPLIFY_RADIUS = 8;
    private static final int AMPLIFY_BELOW = 4;
    private static final float SPAWN_CHANCE = 0.05f;
    private static final double DRAIN_CHANCE = 0.0005;
    private static final int MAX_NEARBY_MONSTERS = 40;
    private static final int UPKEEP_INTERVAL = 20;
    private static final int RESCAN_INTERVAL = 200;
    private static final int BASE_INTERVAL = 100;
    private static final double MAX_SPEED_SPIRITUS = 500.0;

    private int activationTimer = 0;
    private int upkeepTimer = UPKEEP_INTERVAL;
    private int rescanTimer = 0;
    private boolean fueled = false;
    private Binding binding = Binding.EMPTY;
    private List<BlockPos> spawnSpots = null;

    public DemonLanternBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.DEMON_LANTERN_TYPE.get(), pos, state);
    }

    public void setBinding(Binding binding) {
        this.binding = binding != null ? binding : Binding.EMPTY;
        setChanged();
    }

    public Binding getBinding() {
        return binding;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DemonLanternBlockEntity tile) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (level.hasNeighborSignal(pos)) {
            tile.activationTimer = 0;
            tile.upkeepTimer = UPKEEP_INTERVAL;
            return;
        }
        if (tile.binding == null || tile.binding.isEmpty()) return;

        if (++tile.upkeepTimer >= UPKEEP_INTERVAL) {
            tile.upkeepTimer = 0;
            tile.fueled = tile.payUpkeep(NeoVitae.SERVER_CONFIG.DEMON_LANTERN_UPKEEP.get());
        }
        if (!tile.fueled) {
            tile.activationTimer = 0;
            return;
        }

        if (tile.spawnSpots == null || --tile.rescanTimer <= 0) {
            tile.spawnSpots = tile.scanSpawnSpots(serverLevel, pos);
            tile.rescanTimer = RESCAN_INTERVAL;
        }

        double total = WorldSpiritusHandler.getTotalSpiritus(level, pos);
        if (++tile.activationTimer >= activationInterval(total)) {
            tile.activationTimer = 0;
            tile.activate(serverLevel, pos);
        }
    }

    /**
     * Ticks between activations: once per {@link #BASE_INTERVAL} ticks at zero spiritus,
     * scaling linearly down to once per tick at {@link #MAX_SPEED_SPIRITUS} total spiritus.
     */
    private static int activationInterval(double totalSpiritus) {
        double t = Math.min(Math.max(totalSpiritus, 0.0), MAX_SPEED_SPIRITUS);
        int interval = (int) Math.round(BASE_INTERVAL - (BASE_INTERVAL - 1) * (t / MAX_SPEED_SPIRITUS));
        return Math.max(1, interval);
    }

    private boolean payUpkeep(int upkeep) {
        if (upkeep <= 0) return true;
        Anima anima = AnimaHelper.getAnima(binding);
        if (anima == null || anima.getCurrentEV() < upkeep) return false;
        anima.syphon(AnimaTicket.create(upkeep));
        return true;
    }

    private void activate(ServerLevel level, BlockPos pos) {
        if (level.random.nextDouble() < DRAIN_CHANCE) {
            consumeRandomSpiritus(level, pos);
        }
        amplifySpawn(level, pos);
    }

    private void consumeRandomSpiritus(ServerLevel level, BlockPos pos) {
        SpiritusChunk chunk = WorldSpiritusHandler.getSpiritusChunk(level, pos);
        List<SpiritusType> available = new ArrayList<>();
        for (SpiritusType type : SpiritusType.values()) {
            if (chunk.getSpiritus(type) >= 1.0) available.add(type);
        }
        if (available.isEmpty()) return;
        SpiritusType chosen = available.get(level.random.nextInt(available.size()));
        WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, chosen, 1.0);
    }

    private void amplifySpawn(ServerLevel level, BlockPos center) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) return;
        if (spawnSpots == null || spawnSpots.isEmpty()) return;

        RandomSource rand = level.random;

        AABB area = new AABB(center).inflate(AMPLIFY_RADIUS);
        int capacity = MAX_NEARBY_MONSTERS - level.getEntitiesOfClass(Monster.class, area).size();
        if (capacity <= 0) return;

        int spawned = 0;
        // One spawn attempt per cached spawnable block, gated by a per-block chance.
        for (BlockPos spot : spawnSpots) {
            if (spawned >= capacity) break;
            if (rand.nextFloat() >= SPAWN_CHANCE) continue;
            if (trySpawnMonster(level, spot, rand)) spawned++;
        }
    }

    private boolean trySpawnMonster(ServerLevel level, BlockPos spawnPos, RandomSource rand) {
        Holder<Biome> biome = level.getBiome(spawnPos);
        WeightedRandomList<MobSpawnSettings.SpawnerData> mobs =
                biome.value().getMobSettings().getMobs(MobCategory.MONSTER);
        Optional<MobSpawnSettings.SpawnerData> picked = mobs.getRandom(rand);
        if (picked.isEmpty()) return false;

        EntityType<?> type = picked.get().type;
        if (type.getCategory() != MobCategory.MONSTER) return false;

        Entity entity = type.create(level);
        if (!(entity instanceof Mob mob)) {
            if (entity != null) entity.discard();
            return false;
        }

        mob.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, rand.nextFloat() * 360f, 0f);
        // Spawner-style: ignore the light/darkness rule so it works as a mob farm, but
        // still reject positions where the mob would be stuck inside blocks.
        if (!mob.checkSpawnObstruction(level)) {
            mob.discard();
            return false;
        }

        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWNER, null);
        level.addFreshEntityWithPassengers(mob);
        return true;
    }

    /**
     * Scans the amplification region once and caches every block a mob could stand on
     * (solid floor below, open feet and head). Solid/underground and open-air blocks are
     * dropped so per-tick spawning only ever iterates viable positions.
     */
    private List<BlockPos> scanSpawnSpots(ServerLevel level, BlockPos center) {
        List<BlockPos> spots = new ArrayList<>();
        int r2 = AMPLIFY_RADIUS * AMPLIFY_RADIUS;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -AMPLIFY_RADIUS; dx <= AMPLIFY_RADIUS; dx++) {
            for (int dz = -AMPLIFY_RADIUS; dz <= AMPLIFY_RADIUS; dz++) {
                if (dx * dx + dz * dz > r2) continue;
                for (int dy = 0; dy >= -AMPLIFY_BELOW; dy--) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (isSpawnable(level, cursor)) spots.add(cursor.immutable());
                }
            }
        }
        return spots;
    }

    private boolean isSpawnable(ServerLevel level, BlockPos pos) {
        if (level.getBlockState(pos).blocksMotion()) return false;
        if (level.getBlockState(pos.above()).blocksMotion()) return false;
        return level.getBlockState(pos.below()).blocksMotion();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (binding != null && !binding.isEmpty()) {
            Binding.BASIC_CODEC.encodeStart(NbtOps.INSTANCE, binding).result()
                    .ifPresent(nbt -> tag.put("binding", nbt));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("binding")) {
            binding = Binding.BASIC_CODEC.parse(NbtOps.INSTANCE, tag.getCompound("binding"))
                    .result().orElse(Binding.EMPTY);
        } else {
            binding = Binding.EMPTY;
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.binding = componentInput.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (binding != null && !binding.isEmpty()) {
            components.set(NVDataComponents.BINDING, binding);
        }
    }
}
