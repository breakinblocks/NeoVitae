// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.anointment;

import net.minecraft.resources.Identifier;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all anointment types.
 * Anointments are temporary weapon/tool coatings that provide special effects.
 */
public class AnointmentRegistrar {
    public static final Map<Identifier, Anointment> ANOINTMENT_MAP = new HashMap<>();

    public static final Anointment MELEE_DAMAGE = register(new Anointment(NeoVitae.rl("melee_damage"))
            .withBonusSet("damage", list -> {
                list.add(2.0);
                list.add(4.0);
                list.add(6.0);
            })
            .withDamageProvider((player, weapon, damage, holder, attacked, anoint, level) ->
                    anoint.getBonusValue("damage", level).doubleValue())
            .setConsumeOnAttack()
            .appliesTo(NVTags.Items.ANOINTABLE_MELEE)
            .addIncompatibility(NeoVitae.rl("holy_water")));

    public static final Anointment SILK_TOUCH = register(new Anointment(NeoVitae.rl("silk_touch"))
            .setConsumeOnHarvest()
            .appliesTo(NVTags.Items.ANOINTABLE_MINING)
            .addIncompatibility(NeoVitae.rl("fortune")));

    public static final Anointment FORTUNE = register(new Anointment(NeoVitae.rl("fortune"))
            .withBonusSet("level", list -> {
                list.add(1);
                list.add(2);
                list.add(3);
            })
            .setConsumeOnHarvest()
            .appliesTo(NVTags.Items.ANOINTABLE_MINING)
            .addIncompatibility(NeoVitae.rl("silk_touch")));

    public static final Anointment HOLY_WATER = register(new Anointment(NeoVitae.rl("holy_water"))
            .withBonusSet("damage", list -> {
                list.add(4.0);
                list.add(8.0);
                list.add(12.0);
            })
            .withDamageProvider((player, weapon, damage, holder, attacked, anoint, level) -> {
                if (attacked.isInvertedHealAndHarm()) {
                    return anoint.getBonusValue("damage", level).doubleValue();
                }
                return 0;
            })
            .setConsumeOnAttack()
            .appliesTo(NVTags.Items.ANOINTABLE_MELEE)
            .addIncompatibility(NeoVitae.rl("melee_damage")));

    public static final Anointment HIDDEN_KNOWLEDGE = register(new Anointment(NeoVitae.rl("hidden_knowledge"))
            .withBonusSet("exp", list -> {
                list.add(2.0);
                list.add(4.0);
                list.add(6.0);
            })
            .appliesTo(NVTags.Items.ANOINTABLE_MINING));

    public static final Anointment QUICK_DRAW = register(new Anointment(NeoVitae.rl("quick_draw"))
            .withBonusSet("speed", list -> {
                list.add(0.25);
                list.add(0.50);
                list.add(0.75);
            })
            .setConsumeOnUseFinish()
            .appliesTo(NVTags.Items.ANOINTABLE_BOWS));

    public static final Anointment LOOTING = register(new Anointment(NeoVitae.rl("looting"))
            .withBonusSet("level", list -> {
                list.add(1);
                list.add(2);
                list.add(3);
            })
            .setConsumeOnAttack()
            .appliesTo(NVTags.Items.ANOINTABLE_MELEE));

    public static final Anointment BOW_POWER = register(new Anointment(NeoVitae.rl("bow_power"))
            .withBonusSet("damage", list -> {
                list.add(1.0);
                list.add(2.0);
                list.add(3.0);
                list.add(5.0);
            })
            .setConsumeOnUseFinish()
            .appliesTo(NVTags.Items.ANOINTABLE_BOWS));

    public static final Anointment SPIRITUS_DRAIN = register(new Anointment(NeoVitae.rl("spiritus_drain"))
            .withBonusSet("bonus", list -> {
                list.add(0.5);
            })
            .setConsumeOnAttack()
            .appliesTo(NVTags.Items.ANOINTABLE_WEAPONS));

    public static final Anointment SMELTING = register(new Anointment(NeoVitae.rl("smelting"))
            .setConsumeOnHarvest()
            .appliesTo(NVTags.Items.ANOINTABLE_MINING));

    public static final Anointment VOIDING = register(new Anointment(NeoVitae.rl("voiding"))
            .setConsumeOnHarvest()
            .appliesTo(NVTags.Items.ANOINTABLE_MINING));

    public static final Anointment BOW_VELOCITY = register(new Anointment(NeoVitae.rl("bow_velocity"))
            .withBonusSet("velocity", list -> {
                list.add(0.25);
                list.add(0.50);
                list.add(0.75);
            })
            .setConsumeOnUseFinish()
            .appliesTo(NVTags.Items.ANOINTABLE_BOWS));

    public static final Anointment WEAPON_REPAIR = register(new Anointment(NeoVitae.rl("repairing"))
            .withBonusSet("exp", list -> {
                list.add(1.0);
                list.add(2.0);
                list.add(3.0);
            })
            .setConsumeOnUseFinish()
            .appliesTo(NVTags.Items.ANOINTABLE_WEAPONS));

    private static Anointment register(Anointment anointment) {
        ANOINTMENT_MAP.put(anointment.getKey(), anointment);
        return anointment;
    }

    public static Anointment get(Identifier key) {
        return ANOINTMENT_MAP.getOrDefault(key, Anointment.DUMMY);
    }

    public static Anointment get(String key) {
        return get(Identifier.parse(key));
    }

    public static void init() {
        // Called to ensure static initialization runs
        NeoVitae.LOGGER.info("Registered {} anointments", ANOINTMENT_MAP.size());
    }
}
