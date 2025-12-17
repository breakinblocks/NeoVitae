package com.breakinblocks.neovitae.api.sigil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * Interface for sigil effect implementations.
 * Each effect type defines behavior for different use contexts.
 *
 * <p>To create a custom sigil effect, implement this interface and register
 * your effect's MapCodec with Neo Vitae's sigil effect type registry.</p>
 *
 * <p>Example registration:</p>
 * <pre>{@code
 * public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
 *     DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "yourmodid");
 *
 * public static final Supplier<MapCodec<YourEffect>> YOUR_EFFECT =
 *     SIGIL_EFFECTS.register("your_effect", () -> YourEffect.CODEC);
 * }</pre>
 */
public interface ISigilEffect {
    /**
     * Dispatch codec for ISigilEffect.
     * Initialized by Neo Vitae during mod loading.
     * External mods should not need to access this directly.
     */
    DispatchCodecHolder DISPATCH_CODEC = new DispatchCodecHolder();

    /**
     * Holder class for the dispatch codec that allows lazy initialization.
     */
    class DispatchCodecHolder implements Supplier<Codec<ISigilEffect>> {
        private Supplier<Codec<ISigilEffect>> delegate;

        @Override
        public Codec<ISigilEffect> get() {
            if (delegate == null) {
                throw new IllegalStateException("ISigilEffect dispatch codec not yet initialized");
            }
            return delegate.get();
        }

        public void setDelegate(Supplier<Codec<ISigilEffect>> codec) {
            this.delegate = codec;
        }
    }

    /**
     * Returns the codec for this specific effect type.
     * Used for serialization dispatch.
     */
    MapCodec<? extends ISigilEffect> codec();

    /**
     * Called when the sigil is right-clicked in the air.
     *
     * @param level  The world
     * @param player The player using the sigil
     * @param stack  The sigil ItemStack
     * @return true if the effect was performed and LP should be consumed
     */
    default boolean useOnAir(Level level, Player player, ItemStack stack) {
        return false;
    }

    /**
     * Called when the sigil is used on a block.
     *
     * @param level    The world
     * @param player   The player using the sigil
     * @param stack    The sigil ItemStack
     * @param blockPos The position of the clicked block
     * @param side     The side of the block that was clicked
     * @param hitVec   The exact hit location
     * @return true if the effect was performed and LP should be consumed
     */
    default boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return false;
    }

    /**
     * Called when the sigil is used on an entity.
     *
     * @param level  The world
     * @param player The player using the sigil
     * @param stack  The sigil ItemStack
     * @param target The target entity
     * @return true if the effect was performed and LP should be consumed
     */
    default boolean useOnEntity(Level level, Player player, ItemStack stack, Entity target) {
        return false;
    }

    /**
     * Called every tick while the sigil is active (for toggleable sigils).
     *
     * @param level      The world
     * @param player     The player holding the sigil
     * @param stack      The sigil ItemStack
     * @param itemSlot   The inventory slot
     * @param isSelected Whether the sigil is currently selected
     */
    default void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
    }

    /**
     * Whether this effect has an active tick component (toggleable sigil).
     */
    default boolean isToggleable() {
        return false;
    }
}
