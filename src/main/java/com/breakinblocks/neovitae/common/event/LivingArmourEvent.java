package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;

public abstract class LivingArmourEvent extends Event {

    private final Player wearer;
    private final Holder<LivingUpgrade> upgrade;
    private LivingArmourEvent(Player wearer, Holder<LivingUpgrade> upgrade) {
        this.wearer = wearer;
        this.upgrade = upgrade;
    }

    public Player getWearer() {
        return this.wearer;
    }

    public Holder<LivingUpgrade> getUpgrade() {
        return this.upgrade;
    }

    public static class ExpGain extends LivingArmourEvent {

        private final float startingAmount;
        private final boolean fromTome;
        private float currentAmount;
        public ExpGain(Player wearer, Holder<LivingUpgrade>upgrade, float amount, boolean fromTome) {
            super(wearer, upgrade);
            this.startingAmount = amount;
            this.currentAmount = amount;
            this.fromTome = fromTome;
        }

        public boolean isTomeExp() {
            return this.fromTome;
        }

        public float getStartingAmount() {
            return this.startingAmount;
        }

        public float getCurrentAmount() {
            return this.currentAmount;
        }

        public void setCurrentAmount(float amount) {
            this.currentAmount = amount;
        }
    }

    /**
     * Fired when a living upgrade levels up.
     * This event is not cancelable.
     */
    public static class LevelUp extends LivingArmourEvent {

        private final int previousLevel;
        private final int newLevel;

        public LevelUp(Player wearer, Holder<LivingUpgrade> upgrade, int previousLevel, int newLevel) {
            super(wearer, upgrade);
            this.previousLevel = previousLevel;
            this.newLevel = newLevel;
        }

        /**
         * @return The level before this level up occurred
         */
        public int getPreviousLevel() {
            return this.previousLevel;
        }

        /**
         * @return The new level after leveling up
         */
        public int getNewLevel() {
            return this.newLevel;
        }
    }
}
