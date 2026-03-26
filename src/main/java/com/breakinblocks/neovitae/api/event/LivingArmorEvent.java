package com.breakinblocks.neovitae.api.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Events related to Living Armor upgrades.
 */
public abstract class LivingArmorEvent extends Event {
    private final Player wearer;
    private final ItemStack armorPiece;

    protected LivingArmorEvent(Player wearer, ItemStack armorPiece) {
        this.wearer = wearer;
        this.armorPiece = armorPiece;
    }

    /**
     * Gets the player wearing the Living Armor.
     */
    public Player getWearer() {
        return wearer;
    }

    /**
     * Gets the Living Armor chest piece.
     */
    public ItemStack getArmorPiece() {
        return armorPiece;
    }

    /**
     * Fired when an upgrade is about to gain experience.
     * Cancel to prevent the experience gain.
     */
    public static class ExperienceGain extends LivingArmorEvent implements ICancellableEvent {
        private final ResourceLocation upgradeId;
        private float experience;

        public ExperienceGain(Player wearer, ItemStack armorPiece, ResourceLocation upgradeId, float experience) {
            super(wearer, armorPiece);
            this.upgradeId = upgradeId;
            this.experience = experience;
        }

        /**
         * Gets the upgrade that is gaining experience.
         */
        public ResourceLocation getUpgradeId() {
            return upgradeId;
        }

        /**
         * Gets the amount of experience being gained.
         */
        public float getExperience() {
            return experience;
        }

        /**
         * Sets the amount of experience to be gained.
         */
        public void setExperience(float experience) {
            this.experience = experience;
        }
    }

    /**
     * Fired when an upgrade levels up.
     * Not cancellable - use for notification purposes.
     */
    public static class LevelUp extends LivingArmorEvent {
        private final ResourceLocation upgradeId;
        private final int previousLevel;
        private final int newLevel;

        public LevelUp(Player wearer, ItemStack armorPiece, ResourceLocation upgradeId, int previousLevel, int newLevel) {
            super(wearer, armorPiece);
            this.upgradeId = upgradeId;
            this.previousLevel = previousLevel;
            this.newLevel = newLevel;
        }

        /**
         * Gets the upgrade that leveled up.
         */
        public ResourceLocation getUpgradeId() {
            return upgradeId;
        }

        /**
         * Gets the previous level before leveling up.
         */
        public int getPreviousLevel() {
            return previousLevel;
        }

        /**
         * Gets the new level after leveling up.
         */
        public int getNewLevel() {
            return newLevel;
        }
    }
}
