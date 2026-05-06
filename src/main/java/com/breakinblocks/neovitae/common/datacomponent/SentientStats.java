package com.breakinblocks.neovitae.common.datacomponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.component.DataComponentGetter;

public record SentientStats(Object2FloatOpenHashMap<Holder<SentientUpgrade>> upgrades) implements TooltipProvider {
    public static final Codec<SentientStats> CODEC =
    Codec.unboundedMap(RegistryFixedCodec.create(NVRegistries.Keys.SENTIENT_UPGRADES), Codec.FLOAT)
            .xmap(Object2FloatOpenHashMap::new, Function.identity())
            .xmap(SentientStats::new, SentientStats::upgrades);

    public static final SentientStats EMPTY = new SentientStats(new Object2FloatOpenHashMap<>());

    public Object2FloatMap.FastEntrySet<Holder<SentientUpgrade>> object2FloatEntrySet() {
        return upgrades.object2FloatEntrySet();
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter componentGetter) {
        HolderSet<SentientUpgrade> order = getOrder(context.registries());
        for (Holder<SentientUpgrade> holder : order) {
            if (this.upgrades.containsKey(holder)) {
                float exp = this.upgrades.getFloat(holder);
                tooltipAdder.accept(SentientHelper.getTooltip(holder, exp, tooltipFlag.hasShiftDown()));
            }
        }

        for (Object2FloatMap.Entry<Holder<SentientUpgrade>> entry : this.upgrades.object2FloatEntrySet()) {
            if (!order.contains(entry.getKey()) && !entry.getKey().is(NVTags.Sentient.TOOLTIP_HIDE)) {
                tooltipAdder.accept(SentientHelper.getTooltip(entry.getKey(), entry.getFloatValue(), tooltipFlag.hasShiftDown()));
            }
        }
    }

    private static HolderSet<SentientUpgrade> getOrder(HolderLookup.Provider registries) {
        if (registries != null) {
            Optional<HolderSet.Named<SentientUpgrade>> optional = registries.lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).get(NVTags.Sentient.TOOLTIP_ORDER);
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return HolderSet.empty();
    }
}
