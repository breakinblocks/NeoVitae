package com.breakinblocks.neovitae.compat.enderio;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

final class EnderIOCompatImpl {

    private static final Identifier POWERED_SPAWNER = Identifier.fromNamespaceAndPath("enderio", "powered_spawner");
    private static final String SPAWNER_SOUL_CLASS = "com.enderio.enderio.foundation.souldata.SpawnerSoul";
    private static final String[] LISTENER_FIELDS = {"RELOAD_LISTENER", "SPAWNER"};

    private static boolean resolved;
    private static boolean usable;

    private static Method getMode;
    private static Method isCapacitorInstalled;
    private static Method hasEnergy;
    private static Method hasSoul;
    private static Method getBoundSoul;
    private static Method getMaxEnergyUse;
    private static Method soulEntityType;
    private static Object soulListener;
    private static Method listenerMatches;

    private EnderIOCompatImpl() {}

    static boolean isPoweredSpawner(BlockEntity be) {
        return POWERED_SPAWNER.equals(BuiltInRegistries.BLOCK.getKey(be.getBlockState().getBlock()));
    }

    static EnderIOCompat.PoweredSpawnerSnapshot readPoweredSpawner(BlockEntity be) throws Exception {
        if (!isPoweredSpawner(be) || !resolve(be.getClass())) return null;

        if (!(getMode.invoke(be) instanceof Enum<?> mode) || !"SPAWN".equals(mode.name())) return null;
        if (!(Boolean) isCapacitorInstalled.invoke(be)) return null;
        if (!(Boolean) hasEnergy.invoke(be)) return null;
        if (!(Boolean) hasSoul.invoke(be)) return null;

        Object soul = getBoundSoul.invoke(be);
        if (soul == null) return null;
        if (!(soulEntityType.invoke(soul) instanceof EntityType<?> type)) return null;

        int energyPerTick = (Integer) getMaxEnergyUse.invoke(be);
        if (energyPerTick <= 0) return null;

        int energyPerMob = lookupEnergyPerMob(type);
        if (energyPerMob <= 0) return null;

        return new EnderIOCompat.PoweredSpawnerSnapshot(type, (double) energyPerTick / energyPerMob);
    }

    private static int lookupEnergyPerMob(EntityType<?> type) throws Exception {
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (!(listenerMatches.invoke(soulListener, id) instanceof Optional<?> match) || match.isEmpty()) return 0;
        Object data = match.get();
        return (Integer) data.getClass().getMethod("power").invoke(data);
    }

    private static synchronized boolean resolve(Class<?> beClass) throws Exception {
        if (resolved) return usable;
        resolved = true;

        getMode = beClass.getMethod("getMode");
        isCapacitorInstalled = beClass.getMethod("isCapacitorInstalled");
        hasEnergy = beClass.getMethod("hasEnergy");
        hasSoul = beClass.getMethod("hasSoul");
        getBoundSoul = beClass.getMethod("getBoundSoul");
        getMaxEnergyUse = beClass.getMethod("getMaxEnergyUse");
        soulEntityType = getBoundSoul.getReturnType().getMethod("entityType");

        Class<?> spawnerSoul = Class.forName(SPAWNER_SOUL_CLASS, false, beClass.getClassLoader());
        for (String name : LISTENER_FIELDS) {
            try {
                Field field = spawnerSoul.getField(name);
                soulListener = field.get(null);
                break;
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (soulListener == null) return false;

        for (Method m : soulListener.getClass().getMethods()) {
            if (m.getName().equals("matches") && m.getParameterCount() == 1
                    && m.getParameterTypes()[0].isAssignableFrom(Identifier.class)) {
                listenerMatches = m;
                break;
            }
        }
        if (listenerMatches == null) return false;

        usable = true;
        return true;
    }
}
