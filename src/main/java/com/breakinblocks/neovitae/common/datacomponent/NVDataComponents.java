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
import net.minecraft.world.item.DyeColor;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;

import java.util.function.Function;

public class NVDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Binding>> BINDING = DATA_COMPONENTS.registerComponentType("binding", builder -> builder.persistent(Binding.BASIC_CODEC).networkSynchronized(Binding.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> INCENSE = DATA_COMPONENTS.registerComponentType("incense", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> LEX_ACTIVE = DATA_COMPONENTS.registerComponentType("lex_active", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LEX_RADIUS = DATA_COMPONENTS.registerComponentType("lex_radius", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LEX_MODE = DATA_COMPONENTS.registerComponentType("lex_mode", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SPIRITUS_AMOUNT = DATA_COMPONENTS.registerComponentType("spiritus_amount", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpiritusType>> SPIRITUS_TYPE = DATA_COMPONENTS.registerComponentType("spiritus_type", builder -> builder.persistent(SpiritusType.CODEC).networkSynchronized(SpiritusType.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ARC_CHANCE = DATA_COMPONENTS.registerComponentType("arc_chance", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ARC_SPEED = DATA_COMPONENTS.registerComponentType("arc_speed", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CONTAINER_TIER = DATA_COMPONENTS.registerComponentType("container_tier", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID_CONTENT = DATA_COMPONENTS.registerComponentType("fluid_content", builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AccumulatorContent>> ACCUMULATOR_CONTENT = DATA_COMPONENTS.registerComponentType("accumulator_content", builder -> builder.persistent(AccumulatorContent.CODEC).networkSynchronized(AccumulatorContent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY_CONTENT = DATA_COMPONENTS.registerComponentType("energy_content", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<Item>>> REQUIRED_SET = DATA_COMPONENTS.registerComponentType("required_set", builder -> builder.persistent(TagKey.codec(Registries.ITEM)));

    public static final Codec<Object2FloatOpenHashMap<Holder<SentientUpgrade>>> UPGRADE_HOLDER_CODEC = Codec.unboundedMap(RegistryFixedCodec.create(NVRegistries.Keys.SENTIENT_UPGRADES), Codec.FLOAT).xmap(Object2FloatOpenHashMap::new, Function.identity());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradeLimits>> LIMITS = DATA_COMPONENTS.registerComponentType("limits", builder -> builder.persistent(UpgradeLimits.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SentientStats>> UPGRADES = DATA_COMPONENTS.registerComponentType("upgrades", builder -> builder.persistent(SentientStats.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_MAX_UPGRADE_POINTS = DATA_COMPONENTS.registerComponentType("max_upgrade_points", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CURRENT_UPGRADE_POINTS = DATA_COMPONENTS.registerComponentType("current_upgrade_points", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> FULL_SET_MARKER = DATA_COMPONENTS.registerComponentType("full_set_marker", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradeTome>> UPGRADE_TOME_DATA = DATA_COMPONENTS.registerComponentType("upgrade_tome_data", builder -> builder.persistent(UpgradeTome.CODEC).networkSynchronized(UpgradeTome.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Object2FloatOpenHashMap<Holder<SentientUpgrade>>>> STORED_UPGRADES = DATA_COMPONENTS.registerComponentType("stored_upgrades", builder -> builder.persistent(UPGRADE_HOLDER_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> UPGRADE_SCRAP = DATA_COMPONENTS.registerComponentType("upgrade_scrap", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PREVIOUS_DAMAGE = DATA_COMPONENTS.registerComponentType("previous_damage", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AnointmentHolder>> ANOINTMENT_HOLDER = DATA_COMPONENTS.registerComponentType("anointment_holder", builder -> builder.persistent(AnointmentHolder.CODEC).networkSynchronized(AnointmentHolder.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> TELEPOSER_POS = DATA_COMPONENTS.registerComponentType("teleposer_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> TELEPOSER_DIMENSION = DATA_COMPONENTS.registerComponentType("teleposer_dimension", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CURRENT_RITUAL = DATA_COMPONENTS.registerComponentType("current_ritual", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DIVINER_DIRECTION = DATA_COMPONENTS.registerComponentType("diviner_direction", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DIVINER_ACTIVATED = DATA_COMPONENTS.registerComponentType("diviner_activated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> DIVINER_STORED_POS = DATA_COMPONENTS.registerComponentType("diviner_stored_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> READER_STATE = DATA_COMPONENTS.registerComponentType("reader_state", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> READER_RANGE_KEY = DATA_COMPONENTS.registerComponentType("reader_range_key", builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> READER_CORNER1 = DATA_COMPONENTS.registerComponentType("reader_corner1", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> READER_MASTER_POS = DATA_COMPONENTS.registerComponentType("reader_master_pos", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> RITUAL_CORNER1 = DATA_COMPONENTS.registerComponentType("ritual_corner1", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> RITUAL_CORNER2 = DATA_COMPONENTS.registerComponentType("ritual_corner2", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SIGIL_ACTIVATED = DATA_COMPONENTS.registerComponentType("sigil_activated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SIGIL_UNUSABLE = DATA_COMPONENTS.registerComponentType("sigil_unusable", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<SigilType>>> SIGIL_TYPE = DATA_COMPONENTS.registerComponentType("sigil_type", builder -> builder.persistent(SigilType.HOLDER_CODEC).networkSynchronized(SigilType.HOLDER_STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_XP = DATA_COMPONENTS.registerComponentType("stored_xp", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DAMAGE = DATA_COMPONENTS.registerComponentType("sentient_sword_damage", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DRAIN = DATA_COMPONENTS.registerComponentType("sentient_sword_drain", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_STATIC_DROP = DATA_COMPONENTS.registerComponentType("sentient_sword_static_drop", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_SWORD_DROP = DATA_COMPONENTS.registerComponentType("sentient_sword_drop", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SENTIENT_TOOL_SPEED = DATA_COMPONENTS.registerComponentType("sentient_tool_speed", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FlaskEffects>> FLASK_EFFECTS = DATA_COMPONENTS.registerComponentType("flask_effects", builder -> builder.persistent(FlaskEffects.CODEC).networkSynchronized(FlaskEffects.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GHOST_STACK_SIZE = DATA_COMPONENTS.registerComponentType("ghost_stack_size", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLOOD_LIGHT_BRIGHTNESS = DATA_COMPONENTS.registerComponentType("blood_light_brightness", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColor>> BLOOD_LIGHT_COLOR = DATA_COMPONENTS.registerComponentType("blood_light_color", builder -> builder.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BLOOD_LIGHT_RAINBOW = DATA_COMPONENTS.registerComponentType("blood_light_rainbow", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> ORB_FLUID = DATA_COMPONENTS.registerComponentType("orb_fluid", builder -> builder.persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColor>> ALCHEMY_ARRAY_COLOR = DATA_COMPONENTS.registerComponentType("alchemy_array_color", builder -> builder.persistent(DyeColor.CODEC).networkSynchronized(DyeColor.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BLOOD_MENDING = DATA_COMPONENTS.registerComponentType("blood_mending", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> SPIRITUS_MAX = DATA_COMPONENTS.registerComponentType("spiritus_max", builder -> builder.persistent(Codec.DOUBLE).networkSynchronized(ByteBufCodecs.DOUBLE));

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
    }
}
