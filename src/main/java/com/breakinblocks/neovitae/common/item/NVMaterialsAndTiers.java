package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.bus.api.IEventBus;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.EnumMap;
import java.util.Map;

/**
 * Tool and armor materials for NeoVitae items.
 * In 26.1 these are plain records rather than registry entries.
 */
public class NVMaterialsAndTiers {

    public static final ResourceKey<EquipmentAsset> SENTIENT_EQUIPMENT_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(NeoVitae.MODID, "sentient"));

    // Swapped into the chestplate's EQUIPPABLE component when the Elytra upgrade is active,
    // replacing the pre-port client-side ElytraLayer texture swap (that extension point was
    // removed in 26.1).
    public static final ResourceKey<EquipmentAsset> SENTIENT_ELYTRA_EQUIPMENT_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID,
                    Identifier.fromNamespaceAndPath(NeoVitae.MODID, "sentient_elytra"));

    // Vanilla ArmorItem.Type.getDurability(base) was `base * durabilityMultiplier` with
    // Helmet=11, Chest=16, Leggings=15, Boots=13. Pre-port Sentient Armor used base=33
    // so Chest had 528 HP. Encoding multipliers into the per-slot durability map here.
    public static final ArmorMaterial SENTIENT_ARMOUR_MATERIAL = new ArmorMaterial(
            33 * 16,
            defenseMap(2, 5, 6, 2),
            9,
            SoundEvents.ARMOR_EQUIP_IRON,
            0F,
            0F,
            NVTags.Items.SENTIENT_ARMOR_REPAIR,
            SENTIENT_EQUIPMENT_ASSET);

    public static final ToolMaterial SENTIENT = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            520,
            6.0F,
            2.0F,
            50,
            NVTags.Items.SPIRITUS_CRYSTALS);

    private static Map<ArmorType, Integer> defenseMap(int helmet, int chest, int legs, int boots) {
        EnumMap<ArmorType, Integer> m = new EnumMap<>(ArmorType.class);
        m.put(ArmorType.HELMET, helmet);
        m.put(ArmorType.CHESTPLATE, chest);
        m.put(ArmorType.LEGGINGS, legs);
        m.put(ArmorType.BOOTS, boots);
        return m;
    }

    public static void register(IEventBus modBus) {
        // No-op: ArmorMaterial and ToolMaterial are plain records in 26.1 and no longer need registering.
    }
}
