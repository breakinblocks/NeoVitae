package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datamap.RitualStats;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RitualEnchantedVitae extends Ritual {

    public static final String NAME = "enchanted_vitae";
    public static final String ENCHANT_RANGE = "enchantRange";

    private static final int CHARGE_TICKS = 100;
    private static final int EV_PER_LEVEL = 2000;
    private static final int MAX_RARITY_MULTIPLIER = 5;
    private static final int CONFLICT_GLOW_TICKS = 200;

    private static final double PENTACLE_RADIUS = 2.5;
    private static final int PENTACLE_SAMPLES = 12;

    private int chargeTicks;
    private int jobSignature;
    private int reportedConflict;
    private boolean haltedByConflict;
    private int haltedTargetId;
    private boolean announceNextPlan;
    private final Map<ItemEntity, Long> glowingUntil = new HashMap<>();

    public RitualEnchantedVitae() {
        super(NAME, 1, 10000, "ritual." + NeoVitae.MODID + "." + NAME);
        addBlockRange(ENCHANT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-2, 1, -2), 5, 2, 5));
        setMaximumVolumeAndDistanceOfRange(ENCHANT_RANGE, 50, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone);
        if (ctx == null) return;

        expireGlow(ctx.level().getGameTime());

        List<ItemEntity> items = RitualHelper.getEntitiesInRange(ctx, this, ENCHANT_RANGE, ItemEntity.class);

        List<ItemEntity> books = new ArrayList<>();
        ItemEntity targetEntity = null;
        int targetCount = 0;

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;
            if (stack.is(Items.ENCHANTED_BOOK)) {
                books.add(itemEntity);
            } else {
                targetEntity = itemEntity;
                targetCount += stack.getCount();
            }
        }

        clearHaltIfItemWasReoffered(targetEntity);

        if (books.isEmpty() || targetEntity == null || targetCount != 1) {
            chargeTicks = 0;
            reportedConflict = 0;
            return;
        }

        if (haltedByConflict) {
            chargeTicks = 0;
            return;
        }

        ItemStack target = targetEntity.getItem();
        List<ItemStack> bookStacks = new ArrayList<>();
        for (ItemEntity book : books) {
            bookStacks.add(book.getItem());
        }

        EnchantPlan plan = planEnchantments(target, bookStacks);

        Conflict conflict = plan.conflict();
        if (conflict != null) {
            chargeTicks = 0;
            announceNextPlan = false;
            haltHere(targetEntity);
            reportConflict(ctx, masterRitualStone, books, conflict);
            return;
        }

        if (plan.enchantments().isEmpty()) {
            chargeTicks = 0;
            return;
        }

        if (announceNextPlan) {
            announceNextPlan = false;
            masterRitualStone.notifyOwner(Component.translatable(langKey("will_bind"),
                    ComponentUtils.formatList(sortedForDisplay(plan.enchantments()),
                            entry -> Enchantment.getFullname(entry.getKey(), entry.getValue()))));
        }

        int signature = signature(target, plan.enchantments());
        if (signature != jobSignature) {
            jobSignature = signature;
            chargeTicks = 0;
        }

        int cost = costFor(plan.enchantments());
        if (ctx.currentEV() < cost) {
            chargeTicks = 0;
            masterRitualStone.notifyOwner(Component.translatable(langKey("insufficient"), cost));
            return;
        }

        chargeTicks += getRefreshTime();
        drawPentacle(ctx.serverLevel(), ctx.masterPos(), chargeTicks);

        if (chargeTicks < CHARGE_TICKS) {
            RitualHelper.chanceStream(ctx.level(), 2, () -> {
                for (ItemEntity book : books) {
                    StreamPresets.arcaneBolt(book, ctx.masterPos()).build()
                            .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);
                }
            });
            return;
        }

        applyEnchantments(target, plan.enchantments());
        targetEntity.setItem(target.copy());
        if (shouldConsumeBooks()) {
            consumeBooks(books, plan.enchantments());
        }
        ctx.syphon(cost);
        chargeTicks = 0;
        jobSignature = 0;

        StreamPresets.arcaneBolt(ctx.masterPos(), targetEntity.blockPosition()).build()
                .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);
        strikeVisualLightning(ctx.serverLevel(), ctx.masterPos());
        ctx.serverLevel().sendParticles(ParticleTypes.ENCHANT,
                ctx.masterPos().getX() + 0.5, ctx.masterPos().getY() + 1.2, ctx.masterPos().getZ() + 0.5,
                80, 1.2, 0.8, 1.2, 0.35);
        ctx.level().playSound(null, ctx.masterPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 0.7f);

        masterRitualStone.stopRitual(BreakType.DEACTIVATE);
    }

    public static EnchantPlan planEnchantments(ItemStack target, List<ItemStack> books) {
        Map<Holder<Enchantment>, Integer> offered = new HashMap<>();
        for (ItemStack book : books) {
            ItemEnchantments stored = EnchantmentHelper.getEnchantmentsForCrafting(book);
            for (Holder<Enchantment> holder : stored.keySet()) {
                offered.merge(holder, stored.getLevel(holder), Math::max);
            }
        }

        ItemEnchantments existing = EnchantmentHelper.getEnchantmentsForCrafting(target);
        List<Map.Entry<Holder<Enchantment>, Integer>> candidates = new ArrayList<>(offered.entrySet());
        candidates.sort(Comparator
                .comparingInt((Map.Entry<Holder<Enchantment>, Integer> e) -> e.getValue()).reversed()
                .thenComparing(e -> e.getKey().getRegisteredName()));

        Map<Holder<Enchantment>, Integer> plan = new HashMap<>();
        for (Map.Entry<Holder<Enchantment>, Integer> candidate : candidates) {
            Holder<Enchantment> holder = candidate.getKey();
            int level = candidate.getValue();

            if (!target.supportsEnchantment(holder)) continue;

            Holder<Enchantment> blocker = firstIncompatible(holder, existing.keySet());
            if (blocker == null) {
                blocker = firstIncompatible(holder, plan.keySet());
            }
            if (blocker != null) {
                int blockerLevel = Math.max(existing.getLevel(blocker), plan.getOrDefault(blocker, 0));
                return new EnchantPlan(Map.of(), new Conflict(blocker, blockerLevel, holder, level));
            }

            if (existing.getLevel(holder) >= level) continue;
            plan.put(holder, level);
        }
        return new EnchantPlan(plan, null);
    }

    @Nullable
    private static Holder<Enchantment> firstIncompatible(Holder<Enchantment> holder, Iterable<Holder<Enchantment>> others) {
        for (Holder<Enchantment> other : others) {
            if (holder.equals(other)) continue;
            if (!Enchantment.areCompatible(holder, other)) return other;
        }
        return null;
    }

    private boolean shouldConsumeBooks() {
        RitualStats stats = getStats();
        return stats != null && stats.consumeBooks();
    }

    public static void consumeBooks(List<ItemEntity> books, Map<Holder<Enchantment>, Integer> plan) {
        for (ItemEntity book : books) {
            if (!contributedTo(book.getItem(), plan)) continue;
            ItemStack stack = book.getItem().copy();
            stack.shrink(1);
            if (stack.isEmpty()) {
                book.discard();
            } else {
                book.setItem(stack);
            }
        }
    }

    public static boolean contributedTo(ItemStack book, Map<Holder<Enchantment>, Integer> plan) {
        ItemEnchantments stored = EnchantmentHelper.getEnchantmentsForCrafting(book);
        for (Holder<Enchantment> holder : stored.keySet()) {
            if (plan.containsKey(holder)) return true;
        }
        return false;
    }

    public static int costFor(Map<Holder<Enchantment>, Integer> plan) {
        int cost = 0;
        for (Map.Entry<Holder<Enchantment>, Integer> entry : plan.entrySet()) {
            int weight = Math.max(1, entry.getKey().value().getWeight());
            int rarity = Math.min(MAX_RARITY_MULTIPLIER, Math.max(1, 10 / weight));
            cost += entry.getValue() * EV_PER_LEVEL * rarity;
        }
        return cost;
    }

    public static void applyEnchantments(ItemStack target, Map<Holder<Enchantment>, Integer> plan) {
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(target));
        plan.forEach(mutable::set);
        EnchantmentHelper.setEnchantments(target, mutable.toImmutable());
    }

    private void haltHere(ItemEntity targetEntity) {
        haltedByConflict = true;
        haltedTargetId = targetEntity.getId();
        jobSignature = 0;
    }

    private void clearHaltIfItemWasReoffered(@Nullable ItemEntity targetEntity) {
        if (!haltedByConflict) return;
        if (targetEntity != null && targetEntity.getId() == haltedTargetId) return;
        haltedByConflict = false;
        reportedConflict = 0;
        announceNextPlan = true;
    }

    private static List<Map.Entry<Holder<Enchantment>, Integer>> sortedForDisplay(Map<Holder<Enchantment>, Integer> plan) {
        List<Map.Entry<Holder<Enchantment>, Integer>> sorted = new ArrayList<>(plan.entrySet());
        sorted.sort(Comparator.comparing(entry -> entry.getKey().getRegisteredName()));
        return sorted;
    }

    private void reportConflict(RitualContext ctx, IMasterRitualStone master, List<ItemEntity> books, Conflict conflict) {
        int signature = conflict.first().getRegisteredName().hashCode() * 31
                + conflict.second().getRegisteredName().hashCode();
        if (signature == reportedConflict) return;
        reportedConflict = signature;

        master.notifyOwner(Component.translatable(langKey("conflict"),
                Enchantment.getFullname(conflict.first(), conflict.firstLevel()),
                Enchantment.getFullname(conflict.second(), conflict.secondLevel())));

        long until = ctx.level().getGameTime() + CONFLICT_GLOW_TICKS;
        for (ItemEntity book : books) {
            ItemEnchantments stored = EnchantmentHelper.getEnchantmentsForCrafting(book.getItem());
            if (stored.getLevel(conflict.first()) > 0 || stored.getLevel(conflict.second()) > 0) {
                book.setGlowingTag(true);
                glowingUntil.put(book, until);
            }
        }
    }

    private void expireGlow(long now) {
        glowingUntil.entrySet().removeIf(entry -> {
            if (now < entry.getValue()) return false;
            ItemEntity book = entry.getKey();
            if (book.isAlive()) {
                book.setGlowingTag(false);
            }
            return true;
        });
    }

    private static String langKey(String suffix) {
        return "ritual." + NeoVitae.MODID + "." + NAME + "." + suffix;
    }

    private static int signature(ItemStack target, Map<Holder<Enchantment>, Integer> plan) {
        int hash = target.getItem().hashCode();
        for (Map.Entry<Holder<Enchantment>, Integer> entry : plan.entrySet()) {
            hash += entry.getKey().getRegisteredName().hashCode() * 31 + entry.getValue();
        }
        return hash;
    }

    private void drawPentacle(ServerLevel level, BlockPos masterPos, int ticks) {
        double cx = masterPos.getX() + 0.5;
        double cy = masterPos.getY() + 1.1;
        double cz = masterPos.getZ() + 0.5;
        double phase = ticks * 0.06;
        double step = Math.PI * 2 / 5;

        for (int point = 0; point < 5; point++) {
            double startAngle = phase + point * step;
            double endAngle = phase + ((point + 2) % 5) * step;
            double sx = cx + Math.cos(startAngle) * PENTACLE_RADIUS;
            double sz = cz + Math.sin(startAngle) * PENTACLE_RADIUS;
            double ex = cx + Math.cos(endAngle) * PENTACLE_RADIUS;
            double ez = cz + Math.sin(endAngle) * PENTACLE_RADIUS;

            for (int i = 0; i <= PENTACLE_SAMPLES; i++) {
                double t = i / (double) PENTACLE_SAMPLES;
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        sx + (ex - sx) * t, cy, sz + (ez - sz) * t,
                        1, 0.0, 0.0, 0.0, 0.0);
            }

            level.sendParticles(ParticleTypes.ENCHANT, sx, cy + 0.4, sz, 3, 0.05, 0.2, 0.05, 0.02);
        }
    }

    private void strikeVisualLightning(ServerLevel level, BlockPos masterPos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt == null) return;
        bolt.snapTo(masterPos.getX() + 0.5, masterPos.getY(), masterPos.getZ() + 0.5);
        bolt.setVisualOnly(true);
        level.addFreshEntity(bolt);
    }

    @Override
    public void stopRitual(IMasterRitualStone masterRitualStone, BreakType breakType) {
        haltedByConflict = false;
        announceNextPlan = false;
        for (ItemEntity book : glowingUntil.keySet()) {
            if (book.isAlive()) {
                book.setGlowingTag(false);
            }
        }
        glowingUntil.clear();
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 2, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 2, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 3, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 0, EnumRuneType.TENEBRAE);

        addParallelRunes(components, 3, 1, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 1, EnumRuneType.TENEBRAE);

        addRune(components, 0, 2, -3, EnumRuneType.WATER);
        addRune(components, 3, 2, 0, EnumRuneType.FIRE);
        addRune(components, 0, 2, 3, EnumRuneType.EARTH);
        addRune(components, -3, 2, 0, EnumRuneType.AIR);
        addRune(components, 3, 2, -3, EnumRuneType.TENEBRAE);
        addRune(components, 3, 2, 3, EnumRuneType.TENEBRAE);
        addRune(components, -3, 2, 3, EnumRuneType.TENEBRAE);
        addRune(components, -3, 2, -3, EnumRuneType.TENEBRAE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualEnchantedVitae();
    }

    public record Conflict(Holder<Enchantment> first, int firstLevel, Holder<Enchantment> second, int secondLevel) {
    }

    public record EnchantPlan(Map<Holder<Enchantment>, Integer> enchantments, @Nullable Conflict conflict) {
    }
}
