package com.breakinblocks.neovitae.ritual.types;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.compat.enderio.EnderIOCompat;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RitualTormentNexus extends Ritual {

    public static final String NAME = "torment_nexus";
    public static final String EFFECT_RANGE = "effect";
    public static final String ALTAR_RANGE = "altar";
    public static final String CHEST_RANGE = "chestRange";

    private static final Set<GlobalPos> SUPPRESSED_SPAWNERS = ConcurrentHashMap.newKeySet();
    private static volatile boolean LISTENER_REGISTERED = false;

    private static final int RESCAN_INTERVAL_REFRESHES = 10;

    private final Map<BlockPos, Double> vanillaSpawnerAccumulators = new LinkedHashMap<>();
    private final Map<BlockPos, Double> trialSpawnerAccumulators = new LinkedHashMap<>();
    private final Map<BlockPos, Double> trialRewardAccumulators = new LinkedHashMap<>();
    private final Map<BlockPos, Double> poweredSpawnerAccumulators = new LinkedHashMap<>();
    private BlockPos altarOffsetPos = null;
    private int refreshesSinceScan = 0;

    public RitualTormentNexus() {
        super(NAME, 1, 25000, "ritual." + NeoVitae.MODID + "." + NAME);
        addBlockRange(EFFECT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -5, -5), 11, 11, 11));
        addBlockRange(ALTAR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -10, -5), 11, 21, 11));
        setMaximumVolumeAndDistanceOfRange(EFFECT_RANGE, 0, 32, 32);
        setMaximumVolumeAndDistanceOfRange(ALTAR_RANGE, 0, 15, 15);
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
        ensureSpawnSuppressionListener();
    }

    private static void ensureSpawnSuppressionListener() {
        if (LISTENER_REGISTERED) return;
        synchronized (RitualTormentNexus.class) {
            if (LISTENER_REGISTERED) return;
            NeoForge.EVENT_BUS.addListener(RitualTormentNexus::onFinalizeSpawn);
            LISTENER_REGISTERED = true;
        }
    }

    private static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        if (SUPPRESSED_SPAWNERS.isEmpty()) return;
        EntitySpawnReason st = event.getSpawnType();
        if (st != EntitySpawnReason.SPAWNER && st != EntitySpawnReason.TRIAL_SPAWNER) return;
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (!(level instanceof ServerLevel sl)) return;
        ResourceKey<Level> dim = sl.dimension();
        BlockPos at = entity.blockPosition();
        for (GlobalPos gp : SUPPRESSED_SPAWNERS) {
            if (!gp.dimension().equals(dim)) continue;
            BlockPos sp = gp.pos();
            if (Math.abs(sp.getX() - at.getX()) <= 8
                    && Math.abs(sp.getZ() - at.getZ()) <= 8
                    && Math.abs(sp.getY() - at.getY()) <= 6) {
                event.setSpawnCancelled(true);
                event.setCanceled(true);
                for (Entity passenger : entity.getIndirectPassengers()) {
                    if (passenger instanceof Mob rider) {
                        rider.setSpawnCancelled(true);
                    } else {
                        passenger.discard();
                    }
                }
                return;
            }
        }
    }

    @Override
    public boolean activateRitual(IMasterRitualStone master, Player player, UUID owner) {
        Level level = master.getLevel();
        if (level == null || level.isClientSide()) return true;
        applyConfiguredRange(master);
        scanArea((ServerLevel) level, master);
        return true;
    }

    private void applyConfiguredRange(IMasterRitualStone master) {
        int hr = NeoVitae.SERVER_CONFIG.TORMENT_NEXUS_HORIZONTAL_RANGE.get();
        int vr = NeoVitae.SERVER_CONFIG.TORMENT_NEXUS_VERTICAL_RANGE.get();
        if (master.getBlockRange(EFFECT_RANGE) == null) {
            master.setBlockRange(EFFECT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-hr, -vr, -hr), hr * 2 + 1, vr * 2 + 1, hr * 2 + 1));
        }
    }

    @Override
    public void stopRitual(IMasterRitualStone master, BreakType breakType) {
        Level level = master.getLevel();
        if (level instanceof ServerLevel sl) {
            ResourceKey<Level> dim = sl.dimension();
            for (BlockPos pos : vanillaSpawnerAccumulators.keySet()) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, pos));
            }
            for (BlockPos pos : trialSpawnerAccumulators.keySet()) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, pos));
            }
            for (BlockPos pos : poweredSpawnerAccumulators.keySet()) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, pos));
            }
        }
        vanillaSpawnerAccumulators.clear();
        trialSpawnerAccumulators.clear();
        trialRewardAccumulators.clear();
        poweredSpawnerAccumulators.clear();
    }

    private void scanArea(ServerLevel level, IMasterRitualStone master) {
        BlockPos masterPos = master.getBlockPos();
        AreaDescriptor range = RitualHelper.getEffectiveRange(master, this, EFFECT_RANGE);
        if (range == null) return;
        ResourceKey<Level> dim = level.dimension();

        Map<BlockPos, Double> nextVanilla = new LinkedHashMap<>();
        Map<BlockPos, Double> nextTrial = new LinkedHashMap<>();
        Map<BlockPos, Double> nextPowered = new LinkedHashMap<>();
        for (BlockPos pos : range.getContainedPositions(masterPos)) {
            BlockEntity be = level.getBlockEntity(pos);
            BlockPos imm = pos.immutable();
            if (be instanceof SpawnerBlockEntity) {
                nextVanilla.put(imm, vanillaSpawnerAccumulators.getOrDefault(imm, 0.0));
            } else if (be instanceof TrialSpawnerBlockEntity) {
                nextTrial.put(imm, trialSpawnerAccumulators.getOrDefault(imm, 0.0));
            } else if (be != null && EnderIOCompat.isPoweredSpawner(be)) {
                nextPowered.put(imm, poweredSpawnerAccumulators.getOrDefault(imm, 0.0));
            }
        }

        for (BlockPos p : vanillaSpawnerAccumulators.keySet()) {
            if (!nextVanilla.containsKey(p)) SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
        }
        for (BlockPos p : trialSpawnerAccumulators.keySet()) {
            if (!nextTrial.containsKey(p)) SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
        }
        for (BlockPos p : poweredSpawnerAccumulators.keySet()) {
            if (!nextPowered.containsKey(p)) SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
        }

        vanillaSpawnerAccumulators.clear();
        vanillaSpawnerAccumulators.putAll(nextVanilla);
        trialSpawnerAccumulators.clear();
        trialSpawnerAccumulators.putAll(nextTrial);
        trialRewardAccumulators.keySet().retainAll(nextTrial.keySet());
        poweredSpawnerAccumulators.clear();
        poweredSpawnerAccumulators.putAll(nextPowered);

        for (BlockPos p : vanillaSpawnerAccumulators.keySet()) SUPPRESSED_SPAWNERS.add(GlobalPos.of(dim, p));
        for (BlockPos p : trialSpawnerAccumulators.keySet()) SUPPRESSED_SPAWNERS.add(GlobalPos.of(dim, p));
        for (BlockPos p : poweredSpawnerAccumulators.keySet()) SUPPRESSED_SPAWNERS.add(GlobalPos.of(dim, p));
    }

    @Override
    public void performRitual(IMasterRitualStone master) {
        RitualContext ctx = RitualHelper.createContext(master);
        if (ctx == null) return;
        ServerLevel level = ctx.serverLevel();
        BlockPos masterPos = ctx.masterPos();

        if (vanillaSpawnerAccumulators.isEmpty() && trialSpawnerAccumulators.isEmpty()) {
            scanArea(level, master);
            refreshesSinceScan = 0;
        } else if (++refreshesSinceScan >= RESCAN_INTERVAL_REFRESHES) {
            scanArea(level, master);
            refreshesSinceScan = 0;
        }

        prunePresence(level);

        int evPerKill = NeoVitae.SERVER_CONFIG.TORMENT_NEXUS_EV_PER_KILL.get();
        int evModPercent = NeoVitae.SERVER_CONFIG.TORMENT_NEXUS_EV_MODIFIER_PERCENT.get();
        int maxEvPerOperation = NeoVitae.SERVER_CONFIG.TORMENT_NEXUS_MAX_EV_PER_OPERATION.get();
        int refreshTicks = getRefreshTime();

        AraVitaeTile altar = findAltar(ctx);

        UUID owner = ctx.master().getOwner() != null ? ctx.master().getOwner() : UUID.randomUUID();
        FakePlayer fakePlayer = RitualHelper.createRitualFakePlayer(level, owner, "TormentNexus");
        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, masterPos).orElse(masterPos.above());
        BlockEntity chestBE = level.getBlockEntity(chestPos);
        ResourceHandler<ItemResource> chestInv = chestBE != null ? level.getCapability(Capabilities.Item.BLOCK, chestPos, null) : null;

        long totalKills = 0;
        long pendingXp = 0;
        long evCharged = 0;
        boolean ranOutOfEv = false;

        for (BlockPos pos : new ArrayList<>(vanillaSpawnerAccumulators.keySet())) {
            if (!(level.getBlockEntity(pos) instanceof SpawnerBlockEntity vs)) {
                vanillaSpawnerAccumulators.remove(pos);
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(level.dimension(), pos));
                continue;
            }
            VanillaSpawnerSnapshot snap = readVanillaSpawner(vs, level.registryAccess());
            if (snap == null) continue;
            double cycles = vanillaSpawnerAccumulators.getOrDefault(pos, 0.0) + (refreshTicks / Math.max(1.0, snap.averageDelay()));
            int wholeCycles = (int) cycles;
            vanillaSpawnerAccumulators.put(pos, cycles - wholeCycles);
            if (wholeCycles <= 0) continue;
            for (int c = 0; c < wholeCycles; c++) {
                for (int n = 0; n < snap.spawnCount(); n++) {
                    int charge = maxEvPerOperation > 0 ? (int) Math.max(0, Math.min(evPerKill, maxEvPerOperation - evCharged)) : evPerKill;
                    if (charge > 0 && ctx.currentEV() < charge) { ranOutOfEv = true; break; }
                    if (snap.entityType() == null) continue;
                    KillResult kr = simulateKill(level, snap.entityType(), pos, fakePlayer, evModPercent, chestInv);
                    if (charge > 0) {
                        ctx.syphon(charge);
                        evCharged += charge;
                    }
                    if (altar != null && kr.ev > 0) altar.addSacrificeEV(kr.ev, true);
                    pendingXp += kr.xp;
                    totalKills++;
                }
                if (ranOutOfEv) break;
            }
            if (ranOutOfEv) break;
            level.sendParticles(ParticleTypes.SOUL, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.02);
        }

        if (!ranOutOfEv) {
            for (BlockPos pos : new ArrayList<>(trialSpawnerAccumulators.keySet())) {
                if (!(level.getBlockEntity(pos) instanceof TrialSpawnerBlockEntity ts)) {
                    trialSpawnerAccumulators.remove(pos);
                    trialRewardAccumulators.remove(pos);
                    SUPPRESSED_SPAWNERS.remove(GlobalPos.of(level.dimension(), pos));
                    continue;
                }
                TrialSnapshot snap = readTrialSpawner(ts, level.getRandom());
                if (snap == null || snap.entityType() == null) continue;
                double rate = snap.simultaneousMobs() / Math.max(1.0, snap.ticksBetweenSpawn());
                double cycles = trialSpawnerAccumulators.getOrDefault(pos, 0.0) + refreshTicks * rate;
                int wholeKills = (int) cycles;
                trialSpawnerAccumulators.put(pos, cycles - wholeKills);
                for (int n = 0; n < wholeKills; n++) {
                    int charge = maxEvPerOperation > 0 ? (int) Math.max(0, Math.min(evPerKill, maxEvPerOperation - evCharged)) : evPerKill;
                    if (charge > 0 && ctx.currentEV() < charge) { ranOutOfEv = true; break; }
                    EntityType<?> et = snap.entityType();
                    KillResult kr = simulateKill(level, et, pos, fakePlayer, evModPercent, chestInv);
                    if (charge > 0) {
                        ctx.syphon(charge);
                        evCharged += charge;
                    }
                    if (altar != null && kr.ev > 0) altar.addSacrificeEV(kr.ev, true);
                    pendingXp += kr.xp;
                    totalKills++;
                }
                if (ranOutOfEv) break;

                double rewardProgress = trialRewardAccumulators.getOrDefault(pos, 0.0) + refreshTicks;
                int rewards = (int) (rewardProgress / snap.cooldownTicks());
                trialRewardAccumulators.put(pos, rewardProgress - (double) rewards * snap.cooldownTicks());
                for (int i = 0; i < rewards; i++) {
                    ejectTrialReward(level, snap, level.getRandom(), chestInv);
                }

                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.02);
            }
        }

        if (!ranOutOfEv) {
            for (BlockPos pos : new ArrayList<>(poweredSpawnerAccumulators.keySet())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be == null || !EnderIOCompat.isPoweredSpawner(be)) {
                    poweredSpawnerAccumulators.remove(pos);
                    SUPPRESSED_SPAWNERS.remove(GlobalPos.of(level.dimension(), pos));
                    continue;
                }
                EnderIOCompat.PoweredSpawnerSnapshot snap = EnderIOCompat.readPoweredSpawner(be);
                if (snap == null || snap.entityType() == null) continue;

                double cycles = poweredSpawnerAccumulators.getOrDefault(pos, 0.0) + refreshTicks * snap.mobsPerTick();
                int wholeKills = (int) cycles;
                poweredSpawnerAccumulators.put(pos, cycles - wholeKills);
                for (int n = 0; n < wholeKills; n++) {
                    int charge = maxEvPerOperation > 0 ? (int) Math.max(0, Math.min(evPerKill, maxEvPerOperation - evCharged)) : evPerKill;
                    if (charge > 0 && ctx.currentEV() < charge) { ranOutOfEv = true; break; }
                    KillResult kr = simulateKill(level, snap.entityType(), pos, fakePlayer, evModPercent, chestInv);
                    if (charge > 0) {
                        ctx.syphon(charge);
                        evCharged += charge;
                    }
                    if (altar != null && kr.ev > 0) altar.addSacrificeEV(kr.ev, true);
                    pendingXp += kr.xp;
                    totalKills++;
                }
                if (ranOutOfEv) break;
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.02);
            }
        }

        if (pendingXp > 0 && chestInv != null) {
            depositXpIntoTome(chestInv, (int) Math.min(pendingXp, Integer.MAX_VALUE));
        }

        if (totalKills > 0) {
            emitRitualVisuals(level, masterPos, totalKills);
        }
        emitAmbientHaunt(level, masterPos);
    }

    private void prunePresence(ServerLevel level) {
        ResourceKey<Level> dim = level.dimension();
        var vit = vanillaSpawnerAccumulators.entrySet().iterator();
        while (vit.hasNext()) {
            BlockPos p = vit.next().getKey();
            if (!(level.getBlockEntity(p) instanceof SpawnerBlockEntity)) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
                vit.remove();
            }
        }
        var tit = trialSpawnerAccumulators.entrySet().iterator();
        while (tit.hasNext()) {
            BlockPos p = tit.next().getKey();
            if (!(level.getBlockEntity(p) instanceof TrialSpawnerBlockEntity)) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
                trialRewardAccumulators.remove(p);
                tit.remove();
            }
        }
        var pit = poweredSpawnerAccumulators.entrySet().iterator();
        while (pit.hasNext()) {
            BlockPos p = pit.next().getKey();
            BlockEntity be = level.getBlockEntity(p);
            if (be == null || !EnderIOCompat.isPoweredSpawner(be)) {
                SUPPRESSED_SPAWNERS.remove(GlobalPos.of(dim, p));
                pit.remove();
            }
        }
    }

    private AraVitaeTile findAltar(RitualContext ctx) {
        RitualHelper.AltarSearchResult result = RitualHelper.findAltar(ctx, this, ALTAR_RANGE, altarOffsetPos);
        altarOffsetPos = result.offset();
        return result.altar();
    }

    private record VanillaSpawnerSnapshot(EntityType<?> entityType, int spawnCount, double averageDelay) {}

    private VanillaSpawnerSnapshot readVanillaSpawner(SpawnerBlockEntity be, HolderLookup.Provider provider) {
        TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        be.getSpawner().save(out);
        CompoundTag tag = out.buildResult();
        int min = tag.getShortOr("MinSpawnDelay", (short) 200);
        int max = tag.getShortOr("MaxSpawnDelay", (short) 800);
        if (min <= 0 && max <= 0) { min = 200; max = 800; }
        int count = tag.getShortOr("SpawnCount", (short) 4);
        if (count <= 0) count = 4;
        double avg = (Math.max(1, min) + Math.max(min, max)) / 2.0;
        EntityType<?> et = tag.getCompound("SpawnData")
                .flatMap(sd -> sd.getCompound("entity"))
                .flatMap(e -> e.read("id", Identifier.CODEC))
                .flatMap(id -> BuiltInRegistries.ENTITY_TYPE.getOptional(id))
                .orElse(null);
        return new VanillaSpawnerSnapshot(et, count, avg);
    }

    private record TrialSnapshot(EntityType<?> entityType, int ticksBetweenSpawn, float simultaneousMobs,
                                 int cooldownTicks, WeightedList<ResourceKey<LootTable>> rewardTables) {}

    private TrialSnapshot readTrialSpawner(TrialSpawnerBlockEntity be, RandomSource rng) {
        TrialSpawnerConfig cfg = be.getTrialSpawner().activeConfig();
        WeightedList<SpawnData> potentials = cfg.spawnPotentialsDefinition();
        EntityType<?> picked = potentials.getRandom(rng).map(this::entityFromSpawnData).orElse(null);
        if (picked == null) {
            for (Weighted<SpawnData> w : potentials.unwrap()) {
                EntityType<?> e = entityFromSpawnData(w.value());
                if (e != null) { picked = e; break; }
            }
        }
        return new TrialSnapshot(picked, Math.max(1, cfg.ticksBetweenSpawn()), Math.max(1f, cfg.simultaneousMobs()),
                Math.max(1, be.getTrialSpawner().getTargetCooldownLength()), cfg.lootTablesToEject());
    }

    private void ejectTrialReward(ServerLevel level, TrialSnapshot snap, RandomSource rng,
            ResourceHandler<ItemResource> chestInv) {
        if (chestInv == null || snap.rewardTables() == null) return;
        ResourceKey<LootTable> key = snap.rewardTables().getRandom(rng).orElse(null);
        if (key == null) return;
        LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
        if (table == LootTable.EMPTY) return;
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        for (ItemStack drop : table.getRandomItems(params)) {
            if (!drop.isEmpty()) Utils.insertItemStacked(chestInv, drop, false);
        }
    }

    private EntityType<?> entityFromSpawnData(SpawnData sd) {
        return sd.entityToSpawn()
                .read("id", Identifier.CODEC)
                .flatMap(id -> BuiltInRegistries.ENTITY_TYPE.getOptional(id))
                .orElse(null);
    }

    private record KillResult(int ev, int xp) {}

    private KillResult simulateKill(ServerLevel level, EntityType<?> type, BlockPos at, FakePlayer fakePlayer, int evModPercent, ResourceHandler<ItemResource> chestInv) {
        Entity proto = type.create(level, EntitySpawnReason.SPAWNER);
        if (!(proto instanceof LivingEntity living)) {
            if (proto != null) proto.discard();
            return new KillResult(0, 0);
        }
        try {
            living.snapTo(at.getX() + 0.5, at.getY(), at.getZ() + 0.5, 0f, 0f);
            float maxHealth = living.getMaxHealth();
            int baseEv = EntitySacrificeHelper.calculateEV(living, maxHealth);
            int ev = (int) Math.max(0L, ((long) baseEv * evModPercent) / 100L);

            int xp = 0;
            if (living instanceof Mob mob) {
                xp = mob.getExperienceReward(level, fakePlayer);
            }

            if (chestInv != null) {
                try {
                    LootParams params = new LootParams.Builder(level)
                            .withParameter(LootContextParams.THIS_ENTITY, living)
                            .withParameter(LootContextParams.ORIGIN, living.position())
                            .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().source(NVDamageSources.RITUAL, fakePlayer))
                            .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer)
                            .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer)
                            .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, fakePlayer)
                            .create(LootContextParamSets.ENTITY);
                    ResourceKey<LootTable> lootKey = living.getLootTable().orElse(null);
                    if (lootKey != null) {
                        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootKey);
                        if (table != LootTable.EMPTY) {
                            for (ItemStack drop : table.getRandomItems(params)) {
                                if (!drop.isEmpty()) Utils.insertItemStacked(chestInv, drop, false);
                            }
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            return new KillResult(ev, xp);
        } finally {
            living.discard();
        }
    }

    private void depositXpIntoTome(ResourceHandler<ItemResource> inv, int xp) {
        for (int i = 0; i < inv.size(); i++) {
            ItemResource resource = inv.getResource(i);
            if (resource.isEmpty() || !(resource.getItem() instanceof ExperienceTomeItem)) {
                continue;
            }
            ItemStack updated = resource.toStack(1);
            ExperienceTomeItem.addXpToTome(updated, xp);
            try (Transaction tx = Transaction.openRoot()) {
                if (inv.extract(i, resource, 1, tx) == 1
                        && inv.insert(i, ItemResource.of(updated), 1, tx) == 1) {
                    tx.commit();
                }
            }
            return;
        }
    }

    private void emitRitualVisuals(ServerLevel level, BlockPos masterPos, long kills) {
        double cx = masterPos.getX() + 0.5;
        double cy = masterPos.getY() + 1.2;
        double cz = masterPos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.SOUL, cx, cy, cz, (int) Math.min(20, 4 + kills), 0.5, 0.5, 0.5, 0.02);
        RandomSource rng = level.getRandom();
        if (rng.nextInt(8) == 0) {
            level.playSound(null, masterPos, SoundEvents.VEX_AMBIENT, SoundSource.BLOCKS, 0.55f, 0.7f + rng.nextFloat() * 0.4f);
        }
    }

    private void emitAmbientHaunt(ServerLevel level, BlockPos masterPos) {
        RandomSource rng = level.getRandom();
        if (rng.nextInt(10) == 0) {
            level.playSound(null, masterPos, SoundEvents.GHAST_HURT, SoundSource.BLOCKS,
                    0.12f, 0.5f + rng.nextFloat() * 0.3f);
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        altarOffsetPos = RitualHelper.readAltarOffset(tag);
        vanillaSpawnerAccumulators.clear();
        trialSpawnerAccumulators.clear();
        readAccumulatorList(tag, "VanillaSpawners", vanillaSpawnerAccumulators);
        readAccumulatorList(tag, "TrialSpawners", trialSpawnerAccumulators);
        trialRewardAccumulators.clear();
        readAccumulatorList(tag, "TrialRewards", trialRewardAccumulators);
        poweredSpawnerAccumulators.clear();
        readAccumulatorList(tag, "PoweredSpawners", poweredSpawnerAccumulators);
    }

    private static void readAccumulatorList(CompoundTag tag, String key, Map<BlockPos, Double> out) {
        ListTag list = tag.getList(key).orElse(null);
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i).orElse(null);
            if (e == null) continue;
            BlockPos p = e.read("pos", BlockPos.CODEC).orElse(null);
            if (p != null) out.put(p, e.getDoubleOr("acc", 0.0));
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        RitualHelper.writeAltarOffset(tag, altarOffsetPos);
        tag.put("VanillaSpawners", writeAccumulatorList(vanillaSpawnerAccumulators));
        tag.put("TrialSpawners", writeAccumulatorList(trialSpawnerAccumulators));
        tag.put("TrialRewards", writeAccumulatorList(trialRewardAccumulators));
        tag.put("PoweredSpawners", writeAccumulatorList(poweredSpawnerAccumulators));
    }

    private static ListTag writeAccumulatorList(Map<BlockPos, Double> source) {
        ListTag list = new ListTag();
        for (var e : source.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.store("pos", BlockPos.CODEC, e.getKey());
            t.putDouble("acc", e.getValue());
            list.add(t);
        }
        return list;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addRune(components, -2, -1, -3, EnumRuneType.DUSK);
        addRune(components, -2, -1, 3, EnumRuneType.DUSK);
        addRune(components, -1, -1, -1, EnumRuneType.DUSK);
        addRune(components, -1, -1, 1, EnumRuneType.DUSK);
        addRune(components, 1, -1, -1, EnumRuneType.DUSK);
        addRune(components, 1, -1, 1, EnumRuneType.DUSK);
        addRune(components, 2, -1, -3, EnumRuneType.DUSK);
        addRune(components, 2, -1, 3, EnumRuneType.DUSK);
        addRune(components, -3, 0, -1, EnumRuneType.DUSK);
        addRune(components, -3, 0, 0, EnumRuneType.EARTH);
        addRune(components, -3, 0, 1, EnumRuneType.DUSK);
        addRune(components, -2, 0, -2, EnumRuneType.DUSK);
        addRune(components, -2, 0, 0, EnumRuneType.EARTH);
        addRune(components, -2, 0, 2, EnumRuneType.DUSK);
        addRune(components, -1, 0, -3, EnumRuneType.DUSK);
        addRune(components, -1, 0, 0, EnumRuneType.WATER);
        addRune(components, -1, 0, 3, EnumRuneType.DUSK);
        addRune(components, 0, 0, -3, EnumRuneType.FIRE);
        addRune(components, 0, 0, -2, EnumRuneType.FIRE);
        addRune(components, 0, 0, -1, EnumRuneType.AIR);
        addRune(components, 0, 0, 1, EnumRuneType.FIRE);
        addRune(components, 0, 0, 2, EnumRuneType.AIR);
        addRune(components, 0, 0, 3, EnumRuneType.AIR);
        addRune(components, 1, 0, -3, EnumRuneType.DUSK);
        addRune(components, 1, 0, 0, EnumRuneType.EARTH);
        addRune(components, 1, 0, 3, EnumRuneType.DUSK);
        addRune(components, 2, 0, -2, EnumRuneType.DUSK);
        addRune(components, 2, 0, 0, EnumRuneType.WATER);
        addRune(components, 2, 0, 2, EnumRuneType.DUSK);
        addRune(components, 3, 0, -1, EnumRuneType.DUSK);
        addRune(components, 3, 0, 0, EnumRuneType.WATER);
        addRune(components, 3, 0, 1, EnumRuneType.DUSK);
        addRune(components, -3, 1, 0, EnumRuneType.EARTH);
        addRune(components, -1, 1, -1, EnumRuneType.DUSK);
        addRune(components, -1, 1, 1, EnumRuneType.DUSK);
        addRune(components, 0, 1, -3, EnumRuneType.FIRE);
        addRune(components, 0, 1, 3, EnumRuneType.AIR);
        addRune(components, 1, 1, -1, EnumRuneType.DUSK);
        addRune(components, 1, 1, 1, EnumRuneType.DUSK);
        addRune(components, 3, 1, 0, EnumRuneType.WATER);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualTormentNexus();
    }
}
