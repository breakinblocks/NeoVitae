package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.types.RitualEnchantedVitae;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EnchantedVitaeHaltTests {

    private EnchantedVitaeHaltTests() {}

    private static final UUID OWNER = UUID.fromString("d7c9f1a2-4b3e-4c5d-8e9f-0a1b2c3d4e5f");
    private static final BlockPos MASTER = new BlockPos(2, 1, 2);

    private static class StubMaster implements IMasterRitualStone {
        private final Level level;
        private final BlockPos pos;
        final List<Component> messages = new ArrayList<>();
        boolean stopped = false;

        StubMaster(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public BlockPos getBlockPos() {
            return pos;
        }

        @Override
        public UUID getOwner() {
            return OWNER;
        }

        @Override
        public void setOwner(UUID owner) {
        }

        @Override
        public Ritual getCurrentRitual() {
            return null;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public Direction getDirection() {
            return Direction.NORTH;
        }

        @Override
        public boolean isInverted() {
            return false;
        }

        @Override
        public int getCooldown() {
            return 0;
        }

        @Override
        public void setCooldown(int cooldown) {
        }

        @Override
        public long getRunningTime() {
            return 0;
        }

        @Override
        public boolean activateRitual(Ritual ritual, Player player, int crystalLevel) {
            return true;
        }

        @Override
        public void performRitual() {
        }

        @Override
        public void stopRitual(Ritual.BreakType breakType) {
            stopped = true;
        }

        @Override
        public boolean checkStructure(Ritual ritual) {
            return true;
        }

        @Override
        public AreaDescriptor getBlockRange(String key) {
            return null;
        }

        @Override
        public Map<String, AreaDescriptor> getBlockRanges() {
            return new HashMap<>();
        }

        @Override
        public void setBlockRange(String key, AreaDescriptor descriptor) {
        }

        @Override
        public void setBlockRanges(Map<String, AreaDescriptor> ranges) {
        }

        @Override
        public SpiritusType getActiveSpiritusAspect() {
            return SpiritusType.RAW;
        }

        @Override
        public void setActiveSpiritusAspect(SpiritusType type) {
        }

        @Override
        public void provideInformationOfRitualToPlayer(Player player) {
        }

        @Override
        public void provideInformationOfRangeToPlayer(Player player, String key) {
        }

        @Override
        public void provideInformationOfOffsetToPlayer(Player player, AreaDescriptor.Rectangle descriptor) {
        }

        @Override
        public void notifyOwner(Component message) {
            messages.add(message);
        }
    }

    private static Holder<Enchantment> ench(GameTestHelper helper, ResourceKey<Enchantment> key) {
        return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
    }

    private static ItemStack book(GameTestHelper helper, ResourceKey<Enchantment> key, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(ench(helper, key), level);
        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
        return stack;
    }

    private static ItemEntity drop(GameTestHelper helper, ItemStack stack) {
        BlockPos abs = helper.absolutePos(MASTER.above());
        ItemEntity entity = new ItemEntity(helper.getLevel(),
                abs.getX() + 0.5, abs.getY() + 0.2, abs.getZ() + 0.5, stack);
        entity.setNoGravity(true);
        entity.setDeltaMovement(0, 0, 0);
        helper.getLevel().addFreshEntity(entity);
        return entity;
    }

    private static void fundOwner() {
        Anima anima = AnimaHelper.getAnima(OWNER);
        if (anima.getCurrentEV() < 500000) {
            anima.add(AnimaTicket.create(1000000), 10000000);
        }
    }

    private static void run(RitualEnchantedVitae ritual, StubMaster master, int times) {
        for (int i = 0; i < times; i++) {
            ritual.performRitual(master);
        }
    }

    public static void register(NVTestRegistrar r) {
        r.add("enchanted_vitae/halted_rite_only_resumes_when_the_item_is_offered_again", 100, helper -> {
            fundOwner();
            helper.setBlock(MASTER, Blocks.STONE);
            StubMaster master = new StubMaster(helper.getLevel(), helper.absolutePos(MASTER));
            RitualEnchantedVitae ritual = new RitualEnchantedVitae();

            ItemEntity pickaxe = drop(helper, new ItemStack(Items.DIAMOND_PICKAXE));
            ItemEntity fortune = drop(helper, book(helper, Enchantments.FORTUNE, 3));
            ItemEntity silkTouch = drop(helper, book(helper, Enchantments.SILK_TOUCH, 1));
            ItemEntity efficiency = drop(helper, book(helper, Enchantments.EFFICIENCY, 5));

            ritual.performRitual(master);
            if (master.messages.isEmpty()) {
                helper.fail("The conflicting pair should have been reported to the owner");
                return;
            }
            if (!silkTouch.hasGlowingTag() || !fortune.hasGlowingTag()) {
                helper.fail("Both conflicting books should be glowing");
                return;
            }
            if (efficiency.hasGlowingTag()) {
                helper.fail("A book that is not part of the conflict should not glow");
                return;
            }

            silkTouch.discard();
            efficiency.discard();

            run(ritual, master, 60);
            if (!EnchantmentHelper.getEnchantmentsForCrafting(pickaxe.getItem()).isEmpty()) {
                helper.fail("A halted rite must not bind anything just because the conflict was removed");
                return;
            }

            ItemEntity efficiencyAgain = drop(helper, book(helper, Enchantments.EFFICIENCY, 5));
            run(ritual, master, 60);
            if (!EnchantmentHelper.getEnchantmentsForCrafting(pickaxe.getItem()).isEmpty()) {
                helper.fail("Adding a book back must not resume a halted rite on its own");
                return;
            }

            ItemStack carried = pickaxe.getItem();
            pickaxe.discard();
            master.messages.clear();
            ItemEntity reoffered = drop(helper, carried);

            run(ritual, master, 60);

            ItemEnchantments bound = EnchantmentHelper.getEnchantmentsForCrafting(reoffered.getItem());
            if (bound.getLevel(ench(helper, Enchantments.FORTUNE)) != 3) {
                helper.fail("Fortune III should have been bound after the item was offered again");
                return;
            }
            if (bound.getLevel(ench(helper, Enchantments.EFFICIENCY)) != 5) {
                helper.fail("Efficiency V should have been bound after the item was offered again");
                return;
            }
            if (master.messages.isEmpty()) {
                helper.fail("The rite should have announced what it was about to bind");
                return;
            }
            if (efficiencyAgain.getItem().isEmpty()) {
                helper.fail("The books should not have been consumed");
                return;
            }
            helper.succeed();
        });

        r.add("enchanted_vitae/idle_rite_stays_active", 100, helper -> {
            fundOwner();
            helper.setBlock(MASTER, Blocks.STONE);
            StubMaster master = new StubMaster(helper.getLevel(), helper.absolutePos(MASTER));
            RitualEnchantedVitae ritual = new RitualEnchantedVitae();

            drop(helper, new ItemStack(Items.DIAMOND_SWORD));

            run(ritual, master, 60);

            if (master.stopped) {
                helper.fail("A rite with nothing to bind should keep waiting, not shut itself off");
                return;
            }
            helper.succeed();
        });

        r.add("enchanted_vitae/clean_run_binds_without_being_offered_twice", 100, helper -> {
            fundOwner();
            helper.setBlock(MASTER, Blocks.STONE);
            StubMaster master = new StubMaster(helper.getLevel(), helper.absolutePos(MASTER));
            RitualEnchantedVitae ritual = new RitualEnchantedVitae();

            ItemEntity sword = drop(helper, new ItemStack(Items.DIAMOND_SWORD));
            drop(helper, book(helper, Enchantments.SHARPNESS, 5));

            run(ritual, master, 60);

            if (sword.getItem().getEnchantmentLevel(ench(helper, Enchantments.SHARPNESS)) != 5) {
                helper.fail("A run with no conflict should bind on its own");
                return;
            }
            if (!master.messages.isEmpty()) {
                helper.fail("A clean run should not announce anything, got " + master.messages.size() + " messages");
                return;
            }
            if (!master.stopped) {
                helper.fail("The rite should stop itself once it has bound the enchantments");
                return;
            }
            helper.succeed();
        });
    }
}
