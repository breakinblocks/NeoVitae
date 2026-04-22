package com.breakinblocks.neovitae.common.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import java.util.Optional;

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
        if (!event.shouldUpdateStaticData()) {
            return;
        }
        HolderLookup.Provider lookup = event.getLookupProvider();
        lookupTag(lookup, NVTags.Living.TOOLTIP_ORDER)
                .ifPresentOrElse(set -> TOOLTIP_ORDER = set,
                        () -> NeoVitae.LOGGER.error("TagsUpdatedEvent missing {}", NVTags.Living.TOOLTIP_ORDER.location()));
        lookupTag(lookup, NVTags.Living.IS_SCRAPPABLE)
                .ifPresentOrElse(set -> UPGRADE_SCRAPPABLE = set,
                        () -> NeoVitae.LOGGER.error("TagsUpdatedEvent missing {}", NVTags.Living.IS_SCRAPPABLE.location()));
    }

    private static Optional<HolderSet.Named<LivingUpgrade>> lookupTag(HolderLookup.Provider provider, TagKey<LivingUpgrade> tag) {
        return provider.lookup(NVRegistries.Keys.LIVING_UPGRADES).flatMap(lookup -> lookup.get(tag));
    }
}
