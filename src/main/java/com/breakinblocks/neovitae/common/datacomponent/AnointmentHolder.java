package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.anointment.Anointment;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;

import java.util.ArrayList;
import java.util.List;

/**
 * Data component for storing anointments on items (charges, weapons, tools).
 * Replaces the old NBT-based anointment_holder system.
 */
public record AnointmentHolder(List<AnointmentEntry> anointments) {

    public static final Codec<AnointmentHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(AnointmentEntry.CODEC).fieldOf("anointments").forGetter(AnointmentHolder::anointments)
    ).apply(instance, AnointmentHolder::new));

    public static final StreamCodec<ByteBuf, AnointmentHolder> STREAM_CODEC = StreamCodec.composite(
            AnointmentEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            AnointmentHolder::anointments,
            AnointmentHolder::new
    );

    public static AnointmentHolder empty() {
        return new AnointmentHolder(List.of());
    }

    public static AnointmentHolder single(ResourceLocation key, int level, int maxDamage) {
        return new AnointmentHolder(List.of(new AnointmentEntry(key, level, 0, maxDamage)));
    }

    public static AnointmentHolder single(String key, int level, int maxDamage) {
        return single(ResourceLocation.parse(key), level, maxDamage);
    }

    public boolean isEmpty() {
        return anointments.isEmpty();
    }

    public boolean hasAnointment(ResourceLocation key) {
        return anointments.stream().anyMatch(a -> a.key().equals(key));
    }

    public int getAnointmentLevel(ResourceLocation key) {
        for (AnointmentEntry entry : anointments) {
            if (entry.key().equals(key)) {
                return entry.level();
            }
        }
        return 0;
    }

    public int getAnointmentLevel(Anointment anointment) {
        return getAnointmentLevel(anointment.getKey());
    }

    public AnointmentHolder withDamage(ResourceLocation key, int damage) {
        List<AnointmentEntry> newList = new ArrayList<>();
        for (AnointmentEntry entry : anointments) {
            if (entry.key().equals(key)) {
                newList.add(new AnointmentEntry(entry.key(), entry.level(), entry.damage() + damage, entry.maxDamage()));
            } else {
                newList.add(entry);
            }
        }
        return new AnointmentHolder(newList);
    }

    /**
     * Consume durability for anointments that trigger on attack.
     * Returns a new holder with updated durabilities and expired anointments removed.
     */
    public AnointmentHolder consumeOnAttack() {
        List<AnointmentEntry> newList = new ArrayList<>();
        for (AnointmentEntry entry : anointments) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());
            if (anoint.consumeOnAttack()) {
                AnointmentEntry damaged = new AnointmentEntry(entry.key(), entry.level(), entry.damage() + 1, entry.maxDamage());
                if (!damaged.isExpired()) {
                    newList.add(damaged);
                }
            } else {
                newList.add(entry);
            }
        }
        return new AnointmentHolder(newList);
    }

    /**
     * Consume durability for anointments that trigger on harvest.
     * Returns a new holder with updated durabilities and expired anointments removed.
     */
    public AnointmentHolder consumeOnHarvest() {
        List<AnointmentEntry> newList = new ArrayList<>();
        for (AnointmentEntry entry : anointments) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());
            if (anoint.consumeOnHarvest()) {
                AnointmentEntry damaged = new AnointmentEntry(entry.key(), entry.level(), entry.damage() + 1, entry.maxDamage());
                if (!damaged.isExpired()) {
                    newList.add(damaged);
                }
            } else {
                newList.add(entry);
            }
        }
        return new AnointmentHolder(newList);
    }

    /**
     * Consume durability for anointments that trigger on use finish.
     * Returns a new holder with updated durabilities and expired anointments removed.
     */
    public AnointmentHolder consumeOnUseFinish() {
        List<AnointmentEntry> newList = new ArrayList<>();
        for (AnointmentEntry entry : anointments) {
            Anointment anoint = AnointmentRegistrar.get(entry.key());
            if (anoint.consumeOnUseFinish()) {
                AnointmentEntry damaged = new AnointmentEntry(entry.key(), entry.level(), entry.damage() + 1, entry.maxDamage());
                if (!damaged.isExpired()) {
                    newList.add(damaged);
                }
            } else {
                newList.add(entry);
            }
        }
        return new AnointmentHolder(newList);
    }

    /**
     * Consume durability for a specific anointment.
     * Returns a new holder with updated durability and the anointment removed if expired.
     */
    public AnointmentHolder consumeAnointment(ResourceLocation key) {
        List<AnointmentEntry> newList = new ArrayList<>();
        for (AnointmentEntry entry : anointments) {
            if (entry.key().equals(key)) {
                AnointmentEntry damaged = new AnointmentEntry(entry.key(), entry.level(), entry.damage() + 1, entry.maxDamage());
                if (!damaged.isExpired()) {
                    newList.add(damaged);
                }
            } else {
                newList.add(entry);
            }
        }
        return new AnointmentHolder(newList);
    }

    /**
     * A single anointment entry with its key, level, damage, and max damage.
     */
    public record AnointmentEntry(ResourceLocation key, int level, int damage, int maxDamage) {

        public static final Codec<AnointmentEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("key").forGetter(AnointmentEntry::key),
                Codec.INT.fieldOf("level").forGetter(AnointmentEntry::level),
                Codec.INT.fieldOf("damage").forGetter(AnointmentEntry::damage),
                Codec.INT.fieldOf("max_damage").forGetter(AnointmentEntry::maxDamage)
        ).apply(instance, AnointmentEntry::new));

        public static final StreamCodec<ByteBuf, AnointmentEntry> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, AnointmentEntry::key,
                ByteBufCodecs.INT, AnointmentEntry::level,
                ByteBufCodecs.INT, AnointmentEntry::damage,
                ByteBufCodecs.INT, AnointmentEntry::maxDamage,
                AnointmentEntry::new
        );

        public boolean isExpired() {
            return damage >= maxDamage;
        }

        public int remainingUses() {
            return maxDamage - damage;
        }
    }
}
