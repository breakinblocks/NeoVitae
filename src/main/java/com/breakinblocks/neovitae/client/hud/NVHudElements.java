package com.breakinblocks.neovitae.client.hud;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.hud.element.ElementDivinedInformation;
import com.breakinblocks.neovitae.client.hud.element.HoldingElement;
import com.breakinblocks.neovitae.client.hud.element.SpiritusAuraElement;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.IncenseAltarBlockEntity;
import com.breakinblocks.neovitae.util.helper.NumeralHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public final class NVHudElements {

    private static boolean registered = false;

    private NVHudElements() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        ElementRegistry.registerHandler(NeoVitae.rl("altar_divination"),
                new ElementDivinedInformation<AraVitaeTile>(2, ElementDivinedInformation.SigilGate.SIMPLE, AraVitaeTile.class) {
                    @Override
                    public void gatherInformation(Consumer<Function<AraVitaeTile, Component>> rows) {
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.tier", NumeralHelper.toRoman(a.getTier() + 1)));
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.ev", a.getCurrentBlood(), a.getCapacity()));
                    }
                }, new Vec2(0.01f, 0.30f));

        ElementRegistry.registerHandler(NeoVitae.rl("altar_seer"),
                new ElementDivinedInformation<AraVitaeTile>(5, ElementDivinedInformation.SigilGate.ADVANCED, AraVitaeTile.class) {
                    @Override
                    public void gatherInformation(Consumer<Function<AraVitaeTile, Component>> rows) {
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.tier", NumeralHelper.toRoman(a.getTier() + 1)));
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.ev", a.getCurrentBlood(), a.getCapacity()));
                        rows.accept(a -> a.isActive()
                                ? Component.translatable("hud.neovitae.altar.progress", a.getCraftingProgress(), a.getLiquidRequired())
                                : Component.translatable("hud.neovitae.altar.inactive"));
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.consumption", a.getConsumptionRate()));
                        rows.accept(a -> Component.translatable("hud.neovitae.altar.charge", a.getChargingTank()));
                    }
                }, new Vec2(0.01f, 0.30f));

        ElementRegistry.registerHandler(NeoVitae.rl("incense_altar"),
                new ElementDivinedInformation<IncenseAltarBlockEntity>(2, ElementDivinedInformation.SigilGate.EITHER, IncenseAltarBlockEntity.class) {
                    @Override
                    public void gatherInformation(Consumer<Function<IncenseAltarBlockEntity, Component>> rows) {
                        rows.accept(a -> Component.translatable("hud.neovitae.incense.tranquility", (int) a.getTranquility()));
                        rows.accept(a -> Component.translatable("hud.neovitae.incense.bonus", (int) (a.getIncenseAddition() * 100)));
                    }
                }, new Vec2(0.01f, 0.55f));

        ElementRegistry.registerHandler(NeoVitae.rl("spiritus_aura"), new SpiritusAuraElement(), new Vec2(0.005f, 0.01f));
        ElementRegistry.registerHandler(NeoVitae.rl("holding"), new HoldingElement(), new Vec2(0.72f, 0.9f));
    }
}
