package com.breakinblocks.neovitae.anointment;

import net.minecraft.resources.ResourceLocation;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all anointment types.
 * Anointments are temporary weapon/tool coatings that provide special effects.
 */
public class AnointmentRegistrar {
    public static final Map<ResourceLocation, Anointment> ANOINTMENT_MAP = new HashMap<>();

    // Melee Damage - Increases melee damage, consumed on attack
    public static final Anointment MELEE_DAMAGE = register(new Anointment(NeoVitae.rl("melee_damage"))
            .withBonusSet("damage", list -> {
                list.add(2.0);  // Level 1: +2 damage
                list.add(4.0);  // Level 2: +4 damage
                list.add(6.0);  // Level 3: +6 damage
            })
            .withDamageProvider((player, weapon, damage, holder, attacked, anoint, level) ->
                    anoint.getBonusValue("damage", level).doubleValue())
            .setConsumeOnAttack()
            .addIncompatibility(NeoVitae.rl("holy_water")));

    // Silk Touch - Harvests blocks with silk touch, consumed on harvest
    public static final Anointment SILK_TOUCH = register(new Anointment(NeoVitae.rl("silk_touch"))
            .setConsumeOnHarvest()
            .addIncompatibility(NeoVitae.rl("fortune")));

    // Fortune - Increases drops, consumed on harvest
    public static final Anointment FORTUNE = register(new Anointment(NeoVitae.rl("fortune"))
            .withBonusSet("level", list -> {
                list.add(1);  // Level 1: Fortune I
                list.add(2);  // Level 2: Fortune II
                list.add(3);  // Level 3: Fortune III
            })
            .setConsumeOnHarvest()
            .addIncompatibility(NeoVitae.rl("silk_touch")));

    // Holy Water - Extra damage to undead, consumed on attack
    public static final Anointment HOLY_WATER = register(new Anointment(NeoVitae.rl("holy_water"))
            .withBonusSet("damage", list -> {
                list.add(4.0);  // Level 1: +4 damage vs undead
                list.add(8.0);  // Level 2: +8 damage vs undead
                list.add(12.0); // Level 3: +12 damage vs undead
            })
            .withDamageProvider((player, weapon, damage, holder, attacked, anoint, level) -> {
                if (attacked.isInvertedHealAndHarm()) {
                    return anoint.getBonusValue("damage", level).doubleValue();
                }
                return 0;
            })
            .setConsumeOnAttack()
            .addIncompatibility(NeoVitae.rl("melee_damage")));

    // Hidden Knowledge - Increases XP from blocks, consumed on harvest when XP drops
    public static final Anointment HIDDEN_KNOWLEDGE = register(new Anointment(NeoVitae.rl("hidden_knowledge"))
            .withBonusSet("exp", list -> {
                list.add(2.0);  // Level 1: +2 XP
                list.add(4.0);  // Level 2: +4 XP
                list.add(6.0);  // Level 3: +6 XP
            }));

    // Quick Draw - Decreases bow draw time, consumed on use finish
    public static final Anointment QUICK_DRAW = register(new Anointment(NeoVitae.rl("quick_draw"))
            .withBonusSet("speed", list -> {
                list.add(0.25); // Level 1: 25% faster
                list.add(0.50); // Level 2: 50% faster
                list.add(0.75); // Level 3: 75% faster
            })
            .setConsumeOnUseFinish());

    // Looting - Increases mob drops, consumed on attack
    public static final Anointment LOOTING = register(new Anointment(NeoVitae.rl("looting"))
            .withBonusSet("level", list -> {
                list.add(1);  // Level 1: Looting I
                list.add(2);  // Level 2: Looting II
                list.add(3);  // Level 3: Looting III
            })
            .setConsumeOnAttack());

    // Bow Power - Increases arrow damage, consumed on use finish
    public static final Anointment BOW_POWER = register(new Anointment(NeoVitae.rl("bow_power"))
            .withBonusSet("damage", list -> {
                list.add(1.0);  // Level 1: +1 damage
                list.add(2.0);  // Level 2: +2 damage
                list.add(3.0);  // Level 3: +3 damage
                list.add(5.0);  // Level 4 (strong): +5 damage
            })
            .setConsumeOnUseFinish());

    // Will Power - Increases demon will drops, consumed on attack
    public static final Anointment WILL_POWER = register(new Anointment(NeoVitae.rl("will_power"))
            .withBonusSet("bonus", list -> {
                list.add(0.5); // Level 1: +50% will drops
            })
            .setConsumeOnAttack());

    // Smelting - Auto-smelts drops, consumed on harvest
    public static final Anointment SMELTING = register(new Anointment(NeoVitae.rl("smelting"))
            .setConsumeOnHarvest());

    // Voiding - Destroys certain drops, consumed on harvest
    public static final Anointment VOIDING = register(new Anointment(NeoVitae.rl("voiding"))
            .setConsumeOnHarvest());

    // Bow Velocity - Increases arrow velocity, consumed on use finish
    public static final Anointment BOW_VELOCITY = register(new Anointment(NeoVitae.rl("bow_velocity"))
            .withBonusSet("velocity", list -> {
                list.add(0.25); // Level 1: +25% velocity
                list.add(0.50); // Level 2: +50% velocity
                list.add(0.75); // Level 3: +75% velocity
            })
            .setConsumeOnUseFinish());

    // Weapon Repair - Repairs weapon using XP, consumed on use or attack
    public static final Anointment WEAPON_REPAIR = register(new Anointment(NeoVitae.rl("repairing"))
            .withBonusSet("exp", list -> {
                list.add(1.0);  // Level 1: 1 XP worth of repair
                list.add(2.0);  // Level 2: 2 XP worth
                list.add(3.0);  // Level 3: 3 XP worth
            })
            .setConsumeOnUseFinish());

    private static Anointment register(Anointment anointment) {
        ANOINTMENT_MAP.put(anointment.getKey(), anointment);
        return anointment;
    }

    public static Anointment get(ResourceLocation key) {
        return ANOINTMENT_MAP.getOrDefault(key, Anointment.DUMMY);
    }

    public static Anointment get(String key) {
        return get(ResourceLocation.parse(key));
    }

    public static void init() {
        // Called to ensure static initialization runs
        NeoVitae.LOGGER.info("Registered {} anointments", ANOINTMENT_MAP.size());
    }
}
