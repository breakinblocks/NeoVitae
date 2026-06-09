package com.breakinblocks.neovitae.client.hud;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.neoforged.fml.loading.FMLPaths;
import com.breakinblocks.neovitae.NeoVitae;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class ElementRegistry {

    private static final File CONFIG = FMLPaths.CONFIGDIR.get().resolve(NeoVitae.MODID).resolve("hud_elements.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();

    private static final Map<ResourceLocation, HUDElement> HUD_ELEMENTS = new LinkedHashMap<>();
    private static final Map<HUDElement, ResourceLocation> REVERSE = new LinkedHashMap<>();
    private static final Map<ResourceLocation, ElementInfo> ELEMENT_INFO = new LinkedHashMap<>();

    private ElementRegistry() {
    }

    public static void registerHandler(ResourceLocation key, HUDElement element, Vec2 defaultPosition) {
        HUD_ELEMENTS.put(key, element);
        REVERSE.put(element, key);
        ELEMENT_INFO.put(key, new ElementInfo(defaultPosition, getRandomColor()));
    }

    public static List<HUDElement> getElements() {
        return ImmutableList.copyOf(HUD_ELEMENTS.values());
    }

    public static ResourceLocation getKey(HUDElement element) {
        return REVERSE.get(element);
    }

    public static int getColor(ResourceLocation element) {
        ElementInfo info = ELEMENT_INFO.get(element);
        return info == null ? 0x80FFFFFF : info.getBoxColor();
    }

    public static Vec2 getPosition(ResourceLocation element) {
        return ELEMENT_INFO.get(element).getPosition();
    }

    public static void setPosition(ResourceLocation element, Vec2 point) {
        ElementInfo info = ELEMENT_INFO.get(element);
        if (info != null) {
            info.setPosition(point);
        }
    }

    public static void resetPositions() {
        ELEMENT_INFO.values().forEach(ElementInfo::resetPosition);
    }

    public static void render(GuiGraphics guiGraphics, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getDebugOverlay().showDebugScreen()) {
            return;
        }
        Window window = mc.getWindow();
        for (HUDElement element : HUD_ELEMENTS.values()) {
            if (!element.shouldRender(mc)) {
                continue;
            }
            Vec2 position = ELEMENT_INFO.get(getKey(element)).getPosition();
            int xPos = (int) (window.getGuiScaledWidth() * position.x);
            int yPos = (int) (window.getGuiScaledHeight() * position.y);
            element.draw(guiGraphics, partialTicks, xPos, yPos);
        }
    }

    public static void save() {
        Map<String, float[]> toWrite = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, ElementInfo> entry : ELEMENT_INFO.entrySet()) {
            Vec2 pos = entry.getValue().getPosition();
            toWrite.put(entry.getKey().toString(), new float[]{pos.x, pos.y});
        }
        try {
            CONFIG.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG)) {
                GSON.toJson(toWrite, writer);
            }
        } catch (Exception e) {
            NeoVitae.LOGGER.error("Failed to save HUD element positions", e);
        }
    }

    public static void readConfig() {
        if (!CONFIG.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(CONFIG)) {
            Map<String, float[]> loaded = GSON.fromJson(reader, new TypeToken<Map<String, float[]>>() {
            }.getType());
            if (loaded == null) {
                return;
            }
            loaded.forEach((key, value) -> {
                ElementInfo info = ELEMENT_INFO.get(ResourceLocation.parse(key));
                if (info != null && value != null && value.length == 2) {
                    info.setPosition(new Vec2(value[0], value[1]));
                }
            });
        } catch (Exception e) {
            NeoVitae.LOGGER.error("Failed to read HUD element positions", e);
        }
    }

    public static int getRandomColor() {
        float r = RANDOM.nextFloat() / 2F + 0.5F;
        float g = RANDOM.nextFloat() / 2F + 0.5F;
        float b = RANDOM.nextFloat() / 2F + 0.5F;
        return new Color(r, g, b, 0.5F).getRGB();
    }
}
