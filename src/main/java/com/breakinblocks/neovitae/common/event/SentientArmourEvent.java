package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;

public abstract class SentientArmourEvent extends Event {

    private final Player wearer;
    private final Holder<SentientUpgrade> upgrade;
    private SentientArmourEvent(Player wearer, Holder<SentientUpgrade> upgrade) {
        this.wearer = wearer;
        this.upgrade = upgrade;
    }

    public Player getWearer() {
        return this.wearer;
    }

    public Holder<SentientUpgrade> getUpgrade() {
        return this.upgrade;
    }

    public static class ExpGain extends SentientArmourEvent {

        private final float startingAmount;
        private final boolean fromTome;
        private float currentAmount;
        public ExpGain(Player wearer, Holder<SentientUpgrade>upgrade, float amount, boolean fromTome) {
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

    public static class LevelUp extends SentientArmourEvent {

        private final int previousLevel;
        private final int newLevel;

        public LevelUp(Player wearer, Holder<SentientUpgrade> upgrade, int previousLevel, int newLevel) {
            super(wearer, upgrade);
            this.previousLevel = previousLevel;
            this.newLevel = newLevel;
        }

        public int getPreviousLevel() {
            return this.previousLevel;
        }

        public int getNewLevel() {
            return this.newLevel;
        }
    }
}
