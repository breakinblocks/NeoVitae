package com.breakinblocks.neovitae.common.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import java.util.Optional;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class TagsCache {

    /**
     * Thread-safe: volatile ensures visibility of writes across threads.
     * These are written by the event handler on one thread and read from others.
     */
    private static volatile HolderSet<SentientUpgrade> TOOLTIP_ORDER = null;
    private static volatile HolderSet<SentientUpgrade> UPGRADE_SCRAPPABLE = null;

    public static HolderSet<SentientUpgrade> getUpgradeTooltipOrder() throws IllegalStateException {
        if (TOOLTIP_ORDER == null) {
            throw new IllegalStateException("TOOLTIP_ORDER is null");
        }

        return TOOLTIP_ORDER;
    }

    public static HolderSet<SentientUpgrade> getUpgradeScrappable() {
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
        lookupTag(lookup, NVTags.Sentient.TOOLTIP_ORDER)
                .ifPresentOrElse(set -> TOOLTIP_ORDER = set,
                        () -> NeoVitae.LOGGER.error("TagsUpdatedEvent missing {}", NVTags.Sentient.TOOLTIP_ORDER.location()));
        lookupTag(lookup, NVTags.Sentient.IS_SCRAPPABLE)
                .ifPresentOrElse(set -> UPGRADE_SCRAPPABLE = set,
                        () -> NeoVitae.LOGGER.error("TagsUpdatedEvent missing {}", NVTags.Sentient.IS_SCRAPPABLE.location()));
    }

    private static Optional<HolderSet.Named<SentientUpgrade>> lookupTag(HolderLookup.Provider provider, TagKey<SentientUpgrade> tag) {
        return provider.lookup(NVRegistries.Keys.SENTIENT_UPGRADES).flatMap(lookup -> lookup.get(tag));
    }
}
