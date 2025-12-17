package com.breakinblocks.neovitae.common.tag;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.BMRegistries;

import java.util.function.Consumer;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class TagsCache {

    /**
     * Thread-safe: volatile ensures visibility of writes across threads.
     * These are written by the event handler on one thread and read from others.
     */
    private static volatile HolderSet<LivingUpgrade> TOOLTIP_ORDER = null;
    private static volatile HolderSet<LivingUpgrade> UPGRADE_SCRAPPABLE = null;

    public static HolderSet<LivingUpgrade> getUpgradeTooltipOrder() throws IllegalStateException {
        if (TOOLTIP_ORDER == null) {
            throw new IllegalStateException("TOOLTIP_ORDER is null");
        }

        return TOOLTIP_ORDER;
    }

    public static HolderSet<LivingUpgrade> getUpgradeScrappable() {
        if (UPGRADE_SCRAPPABLE == null) {
            throw new IllegalStateException("UPGRADE_SCRAPPABLE is null");
        }

        return UPGRADE_SCRAPPABLE;
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        // if this is the solution Im gonna be a LITTLE bit PISSED. it. was. FK-
        if (!event.shouldUpdateStaticData()) {
            return;
        }
        RegistryAccess access = event.getRegistryAccess();
        datapackHelper(
                access,
                BMRegistries.Keys.LIVING_UPGRADES,
                registry -> TOOLTIP_ORDER = registry.getOrCreateTag(BMTags.Living.TOOLTIP_ORDER)
        );
        datapackHelper(
                access,
                BMRegistries.Keys.LIVING_UPGRADES,
                registry -> UPGRADE_SCRAPPABLE = registry.getOrCreateTag(BMTags.Living.IS_SCRAPPABLE)
        );
    }

    private static <T> void datapackHelper(RegistryAccess registries, ResourceKey<Registry<T>> key, Consumer<Registry<T>> ifPresent) {
        registries.registry(key).ifPresentOrElse(ifPresent, () -> NeoVitae.LOGGER.error("TagsUpdatedEvent doesnt have tags for \"{}\"", key));
    }
}
