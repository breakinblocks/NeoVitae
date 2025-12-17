package com.breakinblocks.neovitae.anointment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;

import java.util.*;
import java.util.function.Consumer;

/**
 * Defines an anointment type that can be applied to weapons/tools.
 * Each anointment has a unique key, consumption triggers, and optional effect providers.
 */
public class Anointment {
    public static final Anointment DUMMY = new Anointment(NeoVitae.rl("dummy"));

    private final ResourceLocation key;
    private final Set<ResourceLocation> incompatible;
    private final Map<String, Bonus> bonuses;
    private String translationKey = null;

    private boolean consumeOnAttack = false;
    private boolean consumeOnUseFinish = false;
    private boolean consumeOnHarvest = false;

    private IDamageProvider damageProvider;

    public Anointment(ResourceLocation key) {
        this.key = key;
        this.incompatible = new HashSet<>();
        this.bonuses = new HashMap<>();
    }

    // Builder methods
    public Anointment withBonusSet(String id, Consumer<List<Number>> modifiers) {
        List<Number> values = new ArrayList<>();
        modifiers.accept(values);
        bonuses.put(id, new Bonus(id, values));
        return this;
    }

    public Anointment addIncompatibility(ResourceLocation... keys) {
        Collections.addAll(incompatible, keys);
        return this;
    }

    public Anointment setConsumeOnAttack() {
        this.consumeOnAttack = true;
        return this;
    }

    public Anointment setConsumeOnUseFinish() {
        this.consumeOnUseFinish = true;
        return this;
    }

    public Anointment setConsumeOnHarvest() {
        this.consumeOnHarvest = true;
        return this;
    }

    public Anointment withDamageProvider(IDamageProvider provider) {
        this.damageProvider = provider;
        return this;
    }

    // Getters
    public ResourceLocation getKey() {
        return key;
    }

    public boolean consumeOnAttack() {
        return consumeOnAttack;
    }

    public boolean consumeOnUseFinish() {
        return consumeOnUseFinish;
    }

    public boolean consumeOnHarvest() {
        return consumeOnHarvest;
    }

    public IDamageProvider getDamageProvider() {
        return damageProvider;
    }

    public boolean isCompatible(ResourceLocation otherKey) {
        return !incompatible.contains(otherKey);
    }

    public Number getBonusValue(String id, int level) {
        List<Number> modifiers = bonuses.getOrDefault(id, Bonus.DEFAULT).modifiers;
        if (modifiers.isEmpty() || level == 0) {
            return 0;
        }
        return level <= modifiers.size() ? modifiers.get(level - 1) : modifiers.get(modifiers.size() - 1);
    }

    public String getTranslationKey() {
        if (translationKey == null) {
            translationKey = "anointment." + key.getNamespace() + "." + key.getPath();
        }
        return translationKey;
    }

    @Override
    public String toString() {
        return key.toString();
    }

    @FunctionalInterface
    public interface IDamageProvider {
        double getAdditionalDamage(Player player, ItemStack weapon, double damage, AnointmentHolder holder, LivingEntity attacked, Anointment anoint, int level);
    }

    public static class Bonus {
        private static final Bonus DEFAULT = new Bonus("null", Collections.emptyList());

        private final String id;
        private final List<Number> modifiers;

        public Bonus(String id, List<Number> modifiers) {
            this.id = id;
            this.modifiers = modifiers;
        }

        public String getId() {
            return id;
        }
    }
}
