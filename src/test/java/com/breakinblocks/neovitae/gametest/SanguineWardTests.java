package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.entity.BloodShieldEntity;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

public final class SanguineWardTests {

    private SanguineWardTests() {}

    private static final BlockPos PLAYER_POS = new BlockPos(2, 1, 3);

    private static Player wardedPlayer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 pos = helper.absoluteVec(Vec3.atCenterOf(PLAYER_POS));
        player.setPos(pos.x, pos.y, pos.z);
        player.setYRot(0.0f);
        player.yHeadRot = 0.0f;
        player.setHealth(player.getMaxHealth());

        ItemStack orb = new ItemStack(NVItems.ORB_APPRENTICE.get());
        orb.set(NVDataComponents.BINDING, new Binding(player.getUUID(), "WardTestPlayer"));
        player.setItemInHand(InteractionHand.OFF_HAND, orb);

        Anima anima = AnimaHelper.getAnima(player.getUUID());
        anima.add(AnimaTicket.create(100000), 1000000);
        return player;
    }

    private static Zombie attackerAt(GameTestHelper helper, Player player, double offsetZ) {
        Zombie zombie = EntityType.ZOMBIE.create(helper.getLevel(), EntitySpawnReason.TRIGGERED);
        zombie.snapTo(player.getX(), player.getY(), player.getZ() + offsetZ, 0.0f, 0.0f);
        helper.getLevel().addFreshEntity(zombie);
        return zombie;
    }

    private static Arrow arrowAt(GameTestHelper helper, Player player, double offsetZ) {
        Arrow arrow = new Arrow(helper.getLevel(), player.getX(), player.getY() + 1, player.getZ() + offsetZ,
                new ItemStack(Items.ARROW), null);
        helper.getLevel().addFreshEntity(arrow);
        return arrow;
    }

    public static void register(NVTestRegistrar r) {
        r.add("sanguine_ward/blocks_projectiles_from_the_front", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, true);
                Arrow arrow = arrowAt(helper, player, 2.0);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().arrow(arrow, null), 4.0f);

                BloodOrbItem.setShieldActive(player, false);
                if (player.getHealth() < before) {
                    helper.fail("Active ward should block arrows from the front");
                    return;
                }
                if (arrow.isAlive()) {
                    helper.fail("A blocked projectile should be discarded");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sanguine_ward/drops_tridents_instead_of_destroying", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, true);

                ThrownTrident trident = new ThrownTrident(helper.getLevel(), player, new ItemStack(Items.TRIDENT));
                trident.setPos(player.getX(), player.getY() + 1, player.getZ() + 2.0);
                helper.getLevel().addFreshEntity(trident);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().trident(trident, null), 8.0f);

                BloodOrbItem.setShieldActive(player, false);
                if (player.getHealth() < before) {
                    helper.fail("Active ward should block a trident");
                    return;
                }
                if (!trident.isAlive()) {
                    helper.fail("A trident must survive the ward so it can be picked up");
                    return;
                }
                double distance = trident.position().subtract(player.position()).horizontalDistance();
                if (Math.abs(distance - BloodShieldEntity.SHIELD_DISTANCE) > 0.5) {
                    helper.fail("Trident should be set down at the ward, distance=" + distance);
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sanguine_ward/drops_throwing_daggers_instead_of_destroying", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, true);

                EntityThrowingDagger dagger = new EntityThrowingDagger(helper.getLevel(),
                        player.getX(), player.getY() + 1, player.getZ() + 2.0,
                        new ItemStack(NVItems.THROWING_DAGGER.get()));
                helper.getLevel().addFreshEntity(dagger);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().thrown(dagger, null), 4.0f);

                BloodOrbItem.setShieldActive(player, false);
                if (player.getHealth() < before) {
                    helper.fail("Active ward should block a throwing dagger");
                    return;
                }
                if (!dagger.isAlive()) {
                    helper.fail("A throwing dagger must survive the ward so it can be picked up");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sanguine_ward/ignores_projectiles_from_behind", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, true);
                Arrow arrow = arrowAt(helper, player, -2.0);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().arrow(arrow, null), 4.0f);

                BloodOrbItem.setShieldActive(player, false);
                if (player.getHealth() >= before) {
                    helper.fail("Ward should not block arrows from behind");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sanguine_ward/does_not_block_melee", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, true);
                Zombie zombie = attackerAt(helper, player, 2.0);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().mobAttack(zombie), 4.0f);

                BloodOrbItem.setShieldActive(player, false);
                if (player.getHealth() >= before) {
                    helper.fail("The ward wards off ranged attacks only; melee must still land");
                    return;
                }
                helper.succeed();
            });
        });

        r.add("sanguine_ward/inactive_ward_does_not_block", 80, helper -> {
            helper.runAfterDelay(1, () -> {
                Player player = wardedPlayer(helper);
                BloodOrbItem.setShieldActive(player, false);
                Arrow arrow = arrowAt(helper, player, 2.0);

                float before = player.getHealth();
                player.hurt(helper.getLevel().damageSources().arrow(arrow, null), 4.0f);

                if (player.getHealth() >= before) {
                    helper.fail("An inactive ward must not block arrows");
                    return;
                }
                helper.succeed();
            });
        });
    }
}
