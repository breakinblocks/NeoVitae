package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.NVRituals;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SentientRitualTests {

    private SentientRitualTests() {}

    private static final BlockPos MRS_POS = new BlockPos(12, 1, 12);

    private static MasterRitualStoneBlockEntity placeMrs(GameTestHelper helper, UUID owner, Ritual ritual) {
        helper.setBlock(MRS_POS.below(), Blocks.STONE.defaultBlockState());
        helper.setBlock(MRS_POS, NVBlocks.MASTER_RITUAL_STONE.block().get().defaultBlockState());
        List<RitualComponent> components = new ArrayList<>();
        ritual.gatherComponents(components::add);
        for (RitualComponent component : components) {
            helper.setBlock(MRS_POS.offset(component.offset()), runeBlock(component.runeType()).defaultBlockState());
        }
        MasterRitualStoneBlockEntity mrs = helper.getBlockEntity(MRS_POS, MasterRitualStoneBlockEntity.class);
        if (mrs == null) {
            helper.fail("Expected MasterRitualStoneBlockEntity at " + MRS_POS);
            return null;
        }
        Anima anima = AnimaHelper.getAnima(owner);
        anima.add(AnimaTicket.create(1000000), 10000000);
        mrs.forceActivateRitual(ritual, null);
        mrs.setOwner(owner);
        return mrs;
    }

    private static Block runeBlock(EnumRuneType type) {
        return switch (type) {
            case BLANK -> NVBlocks.BLANK_RITUAL_STONE.block().get();
            case WATER -> NVBlocks.WATER_RITUAL_STONE.block().get();
            case FIRE -> NVBlocks.FIRE_RITUAL_STONE.block().get();
            case EARTH -> NVBlocks.EARTH_RITUAL_STONE.block().get();
            case AIR -> NVBlocks.AIR_RITUAL_STONE.block().get();
            case TENEBRAE -> NVBlocks.TENEBRAE_RITUAL_STONE.block().get();
            case DEUS -> NVBlocks.DEUS_RITUAL_STONE.block().get();
        };
    }

    private static Player standingWearer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(NVItems.SENTIENT_PLATE.get()));
        Vec3 standAt = helper.absoluteVec(new Vec3(MRS_POS.getX() + 0.5, MRS_POS.getY() + 1, MRS_POS.getZ() + 0.5));
        player.setPos(standAt.x, standAt.y, standAt.z);
        helper.getLevel().addFreshEntity(player);
        return player;
    }

    private static void throwCatalyst(GameTestHelper helper, Item item) {
        Vec3 at = helper.absoluteVec(new Vec3(10.5, 2.2, 10.5));
        ItemEntity entity = new ItemEntity(helper.getLevel(), at.x, at.y, at.z, new ItemStack(item));
        entity.setDeltaMovement(Vec3.ZERO);
        helper.getLevel().addFreshEntity(entity);
    }

    private static Holder<SentientUpgrade> upgradeHolder(GameTestHelper helper, String path) {
        return helper.getLevel().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                .getOrThrow(ResourceKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, NeoVitae.rl(path)));
    }

    public static void register(NVTestRegistrar r) {
        r.addIsolated("sentient_ritual/penance_applies_downgrade_from_thrown_catalyst", 200, helper -> {
            UUID owner = UUID.randomUUID();
            MasterRitualStoneBlockEntity mrs = placeMrs(helper, owner, NVRituals.PENANCE.get());
            if (mrs == null) return;

            Player player = standingWearer(helper);
            throwCatalyst(helper, Items.GLASS_BOTTLE);

            helper.runAfterDelay(100, () -> {
                ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
                SentientStats stats = worn.get(NVDataComponents.UPGRADES);
                Holder<SentientUpgrade> quenched = upgradeHolder(helper, "quenched");
                if (stats == null || stats.upgrades().getOrDefault(quenched, 0f) < 1f) {
                    helper.fail("Penance should inscribe Quenched from a thrown glass bottle, stats=" + stats
                            + " active=" + mrs.isActive());
                    return;
                }
                Integer usedPoints = worn.get(NVDataComponents.CURRENT_UPGRADE_POINTS);
                if (usedPoints == null || usedPoints != -100) {
                    helper.fail("Quenched should free 100 points (used=-100), got " + usedPoints);
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("sentient_ritual/penance_ignores_items_without_recipe", 200, helper -> {
            UUID owner = UUID.randomUUID();
            MasterRitualStoneBlockEntity mrs = placeMrs(helper, owner, NVRituals.PENANCE.get());
            if (mrs == null) return;

            Player player = standingWearer(helper);
            throwCatalyst(helper, Items.COBBLESTONE);

            helper.runAfterDelay(60, () -> {
                ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
                SentientStats stats = worn.get(NVDataComponents.UPGRADES);
                if (stats != null && !stats.upgrades().isEmpty()) {
                    helper.fail("A non-catalyst item should not inscribe anything, stats=" + stats);
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("sentient_ritual/evolve_adds_capacity_then_deactivates", 200, helper -> {
            UUID owner = UUID.randomUUID();
            MasterRitualStoneBlockEntity mrs = placeMrs(helper, owner, NVRituals.ARMOUR_EVOLVE.get());
            if (mrs == null) return;

            Player player = standingWearer(helper);

            helper.runAfterDelay(100, () -> {
                ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
                Integer maxPoints = worn.get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS);
                if (maxPoints == null || maxPoints != 200) {
                    helper.fail("One evolution should raise capacity to 200, got " + maxPoints);
                    return;
                }
                if (mrs.isActive()) {
                    helper.fail("Evolution ritual should deactivate after evolving");
                    return;
                }
                helper.succeed();
            });
        });

        r.addIsolated("sentient_ritual/evolve_refuses_armor_at_max_capacity", 200, helper -> {
            UUID owner = UUID.randomUUID();
            MasterRitualStoneBlockEntity mrs = placeMrs(helper, owner, NVRituals.ARMOUR_EVOLVE.get());
            if (mrs == null) return;

            Player player = standingWearer(helper);
            ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
            worn.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, 500);

            helper.runAfterDelay(100, () -> {
                Integer maxPoints = player.getItemBySlot(EquipmentSlot.CHEST).get(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS);
                if (maxPoints == null || maxPoints != 500) {
                    helper.fail("Capacity should stay at the 500 cap, got " + maxPoints);
                    return;
                }
                if (mrs.isActive()) {
                    helper.fail("Evolution ritual should deactivate when the armor is already at the cap");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
