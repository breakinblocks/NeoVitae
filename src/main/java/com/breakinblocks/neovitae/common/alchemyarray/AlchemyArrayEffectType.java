package com.breakinblocks.neovitae.common.alchemyarray;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Enum of all alchemy array effect types.
 * Each type knows how to create its corresponding effect instance.
 */
public enum AlchemyArrayEffectType implements StringRepresentable {
    CRAFTING("crafting", () -> new AlchemyArrayEffectCrafting(ItemStack.EMPTY)),
    BINDING("binding", () -> new AlchemyArrayEffectBinding(ItemStack.EMPTY)),
    BOUNCE("bounce", AlchemyArrayEffectBounce::new),
    SPIKE("spike", AlchemyArrayEffectSpike::new),
    UPDRAFT("updraft", AlchemyArrayEffectUpdraft::new),
    MOVEMENT("movement", AlchemyArrayEffectMovement::new),
    DAY("day", AlchemyArrayEffectDay::new),
    NIGHT("night", AlchemyArrayEffectNight::new);

    public static final Codec<AlchemyArrayEffectType> CODEC = StringRepresentable.fromEnum(AlchemyArrayEffectType::values);
    public static final StreamCodec<ByteBuf, AlchemyArrayEffectType> STREAM_CODEC = ByteBufCodecs.idMapper(
            i -> AlchemyArrayEffectType.values()[i], AlchemyArrayEffectType::ordinal);

    private final String name;
    private final Supplier<AlchemyArrayEffect> factory;

    AlchemyArrayEffectType(String name, Supplier<AlchemyArrayEffect> factory) {
        this.name = name;
        this.factory = factory;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /**
     * Creates a new effect instance for this type.
     * For crafting/binding types, you should use createCraftingEffect instead.
     */
    public AlchemyArrayEffect createEffect() {
        return factory.get();
    }

    /**
     * Creates a crafting or binding effect with the given output.
     */
    public AlchemyArrayEffect createCraftingEffect(ItemStack output) {
        return switch (this) {
            case CRAFTING -> new AlchemyArrayEffectCrafting(output);
            case BINDING -> new AlchemyArrayEffectBinding(output);
            default -> factory.get();
        };
    }

    public static AlchemyArrayEffectType byName(String name) {
        for (AlchemyArrayEffectType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return CRAFTING;
    }
}
