package com.breakinblocks.neovitae.api.ritual;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/**
 * Interface for imperfect rituals.
 * Imperfect rituals are simpler, one-time effects triggered by
 * placing a specific block above an imperfect ritual stone.
 *
 * <p>To create custom imperfect rituals, extend the abstract ImperfectRitual class
 * in the main package and register using NeoForge's DeferredRegister.</p>
 *
 * <p>Example registration:</p>
 * <pre>{@code
 * // Create your DeferredRegister using the Neo Vitae registry key
 * public static final DeferredRegister<ImperfectRitual> IMPERFECT_RITUALS =
 *     DeferredRegister.create(RitualRegistry.IMPERFECT_RITUAL_REGISTRY_KEY, "yourmodid");
 *
 * // Register your imperfect ritual
 * public static final DeferredHolder<ImperfectRitual, MyImperfectRitual> MY_RITUAL =
 *     IMPERFECT_RITUALS.register("my_ritual", MyImperfectRitual::new);
 * }</pre>
 */
public interface IImperfectRitual {

    /**
     * Called when a player activates this ritual by right-clicking the imperfect ritual stone.
     *
     * @param imperfectRitualStone The imperfect ritual stone being activated
     * @param player               The player activating the ritual
     * @return true if activation was successful
     */
    boolean onActivate(IImperfectRitualStone imperfectRitualStone, Player player);

    /**
     * Gets the unique identifier for this ritual.
     *
     * @return The ritual name
     */
    String getName();

    /**
     * Gets the predicate that determines which block must be placed above
     * the imperfect ritual stone to trigger this ritual.
     *
     * <p>Note: Block requirements can be overridden via DataMaps (ImperfectRitualStats).</p>
     *
     * @return The block state predicate
     */
    Predicate<BlockState> getBlockRequirement();

    /**
     * Gets the LP cost to activate this ritual.
     *
     * <p>Note: Costs can be overridden via DataMaps (ImperfectRitualStats).</p>
     *
     * @return The activation cost in LP
     */
    int getActivationCost();

    /**
     * Whether to display lightning effects on activation.
     *
     * <p>Note: This can be overridden via DataMaps (ImperfectRitualStats).</p>
     *
     * @return true to show lightning
     */
    boolean isLightShow();

    /**
     * Gets the translation key prefix for this ritual.
     *
     * @return The translation key
     */
    String getTranslationKey();
}
