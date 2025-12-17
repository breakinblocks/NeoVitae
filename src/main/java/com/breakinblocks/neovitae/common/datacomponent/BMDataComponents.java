package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.BMRegistries;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import java.util.function.Function;

public class BMDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Binding>> BINDING = DATA_COMPONENTS.registerComponentType("binding", builder -> builder.persistent(Binding.BASIC_CODEC).networkSynchronized(Binding.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INCENSE = DATA_COMPONENTS.registerComponentType("incense", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> DEMON_WILL_AMOUNT = DATA_COMPONENTS.registerComponentType("will_amount", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EnumWillType>> DEMON_WILL_TYPE = DATA_COMPONENTS.registerComponentType("will_type", builder -> builder.persistent(EnumWillType.CODEC).networkSynchronized(EnumWillType.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ARC_CHANCE = DATA_COMPONENTS.registerComponentType("arc_chance", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ARC_SPEED = DATA_COMPONENTS.registerComponentType("arc_speed", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CONTAINER_TIER = DATA_COMPONENTS.registerComponentType("container_tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DATA_COMPONENTS.registerComponentType("fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_CONTENT = DATA_COMPONENTS.registerComponentType("energy_content", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<Item>>> REQUIRED_SET = DATA_COMPONENTS.registerComponentType("required_set", builder -> builder.persistent(TagKey.codec(Registries.ITEM)));

    public static final Codec<Object2FloatOpenHashMap<Holder<LivingUpgrade>>> UPGRADE_HOLDER_CODEC = Codec.unboundedMap(RegistryFixedCodec.create(BMRegistries.Keys.LIVING_UPGRADES), Codec.FLOAT).xmap(Object2FloatOpenHashMap::new, Function.identity());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradeLimits>> LIMITS = DATA_COMPONENTS.registerComponentType("limits", builder -> builder.persistent(UpgradeLimits.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LivingStats>> UPGRADES = DATA_COMPONENTS.registerComponentType("upgrades", builder -> builder.persistent(LivingStats.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_MAX_UPGRADE_POINTS = DATA_COMPONENTS.registerComponentType("max_upgrade_points", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_UPGRADE_POINTS = DATA_COMPONENTS.registerComponentType("current_upgrade_points", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FULL_SET_MARKER = DATA_COMPONENTS.registerComponentType("full_set_marker", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    // doesnt work with stream codec, doesnt work without
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradeTome>> UPGRADE_TOME_DATA = DATA_COMPONENTS.registerComponentType("upgrade_tome_data", builder -> builder.persistent(UpgradeTome.CODEC).networkSynchronized(UpgradeTome.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Object2FloatOpenHashMap<Holder<LivingUpgrade>>>> STORED_UPGRADES = DATA_COMPONENTS.registerComponentType("stored_upgrades", builder -> builder.persistent(UPGRADE_HOLDER_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> UPGRADE_SCRAP = DATA_COMPONENTS.registerComponentType("upgrade_scrap", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PREVIOUS_DAMAGE = DATA_COMPONENTS.registerComponentType("previous_damage", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    // Anointment data component (replaces NBT-based anointment_holder)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AnointmentHolder>> ANOINTMENT_HOLDER = DATA_COMPONENTS.registerComponentType("anointment_holder", builder -> builder.persistent(AnointmentHolder.CODEC).networkSynchronized(AnointmentHolder.STREAM_CODEC));

    // Teleposer data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> TELEPOSER_POS = DATA_COMPONENTS.registerComponentType("teleposer_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> TELEPOSER_DIMENSION = DATA_COMPONENTS.registerComponentType("teleposer_dimension", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    // Ritual Diviner data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CURRENT_RITUAL = DATA_COMPONENTS.registerComponentType("current_ritual", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DIVINER_DIRECTION = DATA_COMPONENTS.registerComponentType("diviner_direction", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DIVINER_ACTIVATED = DATA_COMPONENTS.registerComponentType("diviner_activated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> DIVINER_STORED_POS = DATA_COMPONENTS.registerComponentType("diviner_stored_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    // Ritual Reader data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> READER_STATE = DATA_COMPONENTS.registerComponentType("reader_state", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> READER_RANGE_KEY = DATA_COMPONENTS.registerComponentType("reader_range_key", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> READER_CORNER1 = DATA_COMPONENTS.registerComponentType("reader_corner1", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    // Sigil data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SIGIL_ACTIVATED = DATA_COMPONENTS.registerComponentType("sigil_activated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SIGIL_UNUSABLE = DATA_COMPONENTS.registerComponentType("sigil_unusable", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<SigilType>>> SIGIL_TYPE = DATA_COMPONENTS.registerComponentType("sigil_type", builder -> builder.persistent(SigilType.HOLDER_CODEC).networkSynchronized(SigilType.HOLDER_STREAM_CODEC));

    // Experience Tome data component (Tome of Peritia)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_XP = DATA_COMPONENTS.registerComponentType("stored_xp", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    // Sentient tool data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DAMAGE = DATA_COMPONENTS.registerComponentType("sentient_sword_damage", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DRAIN = DATA_COMPONENTS.registerComponentType("sentient_sword_drain", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_STATIC_DROP = DATA_COMPONENTS.registerComponentType("sentient_sword_static_drop", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DROP = DATA_COMPONENTS.registerComponentType("sentient_sword_drop", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_TOOL_SPEED = DATA_COMPONENTS.registerComponentType("sentient_tool_speed", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    // Flask effect data component (for alchemy flasks with custom duration modifiers)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FlaskEffects>> FLASK_EFFECTS = DATA_COMPONENTS.registerComponentType("flask_effects", builder -> builder.persistent(FlaskEffects.CODEC).networkSynchronized(FlaskEffects.STREAM_CODEC));

    // Ghost item (filter) data components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GHOST_STACK_SIZE = DATA_COMPONENTS.registerComponentType("ghost_stack_size", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FilterInventory>> FILTER_INVENTORY = DATA_COMPONENTS.registerComponentType("filter_inventory", builder -> builder.persistent(FilterInventory.CODEC).networkSynchronized(FilterInventory.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_BLACKLIST = DATA_COMPONENTS.registerComponentType("filter_blacklist", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FILTER_TAG = DATA_COMPONENTS.registerComponentType("filter_tag", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<NestedFilterInventory>> NESTED_FILTERS = DATA_COMPONENTS.registerComponentType("nested_filters", builder -> builder.persistent(NestedFilterInventory.CODEC).networkSynchronized(NestedFilterInventory.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
