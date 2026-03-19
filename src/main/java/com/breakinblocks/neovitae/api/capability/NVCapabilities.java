package com.breakinblocks.neovitae.api.capability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;

/**
 * NeoVitae capability definitions.
 *
 * <p>Capabilities allow blocks and entities to expose functionality to other mods
 * in a standardized way. NeoVitae provides the following capabilities:</p>
 *
 * <ul>
 *   <li>{@link #BLOOD_ALTAR} - Access to Blood Altar functionality</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Get blood altar capability from a block position
 * IBloodAltar altar = level.getCapability(NVCapabilities.BLOOD_ALTAR, pos, null);
 * if (altar != null) {
 *     int blood = altar.getCurrentBlood();
 *     int capacity = altar.getCapacity();
 *     int tier = altar.getTier();
 * }
 * }</pre>
 *
 * <h2>Capability Registration</h2>
 * <p>These capabilities are automatically registered by NeoVitae during
 * the RegisterCapabilitiesEvent. Addon mods can query them via
 * {@code level.getCapability()} without any additional setup.</p>
 */
public final class NVCapabilities {

    private NVCapabilities() {} // Prevent instantiation

    /**
     * Capability for accessing Blood Altar functionality.
     *
     * <p>Provides read access to altar state including:</p>
     * <ul>
     *   <li>Current blood level and capacity</li>
     *   <li>Crafting progress and speed</li>
     *   <li>Altar tier</li>
     *   <li>Rune bonuses (capacity, speed, etc.)</li>
     * </ul>
     *
     * <p>The context parameter (Direction) can be used to specify which
     * side of the altar is being accessed, though the default altar
     * implementation returns the same data regardless of side.</p>
     */
    public static final BlockCapability<IBloodAltar, @Nullable Direction> BLOOD_ALTAR =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "blood_altar"),
                    IBloodAltar.class
            );
}
