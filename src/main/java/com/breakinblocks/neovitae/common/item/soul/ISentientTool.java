package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.List;

/**
 * Interface for sentient tools that scale with spiritus.
 * Implemented by SentientSwordItem, SentientAxeItem, SentientPickaxeItem,
 * SentientShovelItem, and SentientScytheItem.
 *
 * <p>This interface defines the contract for will-powered tools that:
 * <ul>
 *   <li>Consume spiritus from the player's inventory</li>
 *   <li>Scale damage/effects based on available will</li>
 *   <li>Apply special effects based on will type</li>
 *   <li>Drop spiritus from killed enemies</li>
 * </ul>
 */
public interface ISentientTool {

    /**
     * Gets the damage values added at each power level for the given will type.
     * Each tool type has different base damage scaling.
     *
     * @param type the will type
     * @return array of damage bonuses indexed by power level (0-6)
     */
    double[] getDamageForSpiritusType(SpiritusType type);

    /**
     * Calculates the extra damage based on will type and power level.
     *
     * @param type the current will type
     * @param spiritusBracket the power level (0-6)
     * @return the bonus damage to add
     */
    default double getExtraDamage(SpiritusType type, int spiritusBracket) {
        if (spiritusBracket < 0) return 0;
        double[] damages = getDamageForSpiritusType(type);
        if (spiritusBracket >= damages.length) return damages[damages.length - 1];
        return damages[spiritusBracket];
    }

    /**
     * Recalculates tool powers based on the player's current spiritus.
     *
     * @param stack the tool item stack
     * @param world the level
     * @param player the player holding the tool
     */
    void recalculatePowers(ItemStack stack, Level world, Player player);

    /**
     * Gets random spiritus drops when an entity is killed.
     *
     * @param killedEntity the killed entity
     * @param attackingEntity the attacker
     * @param stack the tool used
     * @param looting the looting enchantment level
     * @return list of soul item stacks to drop
     */
    default List<ItemStack> getRandomSpiritusDrop(LivingEntity killedEntity, LivingEntity attackingEntity,
            ItemStack stack, int looting) {
        return SentientToolHelper.getRandomSpiritusDrop(killedEntity, attackingEntity, stack, looting);
    }

    /**
     * Gets the tooltip description translation key for this tool.
     *
     * @return the translation key suffix (e.g., "sentientSword" for "tooltip.neovitae.sentientSword.desc")
     */
    String getTooltipKey();
}
