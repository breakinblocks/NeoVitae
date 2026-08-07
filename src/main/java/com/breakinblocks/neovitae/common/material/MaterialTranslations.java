package com.breakinblocks.neovitae.common.material;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.breakinblocks.neovitae.NeoVitae;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MaterialTranslations {

    private static final String RESOURCE = "/assets/" + NeoVitae.MODID + "/material_names.json";
    private static final Gson GSON = new Gson();

    private static final Map<String, Locale> LOCALES = load();

    private record Locale(String format, Map<String, String> stages, Map<String, String> materials) {}

    private MaterialTranslations() {}

    public static Set<String> locales() {
        return LOCALES.keySet();
    }

    @Nullable
    public static String nameFor(String locale, String material, String stage) {
        Locale entry = LOCALES.get(locale);
        if (entry == null) return null;

        String materialName = entry.materials().get(material);
        String stageName = entry.stages().get(stage);
        if (materialName == null || stageName == null) return null;

        return String.format(entry.format(), materialName, stageName);
    }

    private static Map<String, Locale> load() {
        try (InputStream in = MaterialTranslations.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                return Collections.emptyMap();
            }
            JsonObject root = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonObject.class);
            Map<String, Locale> out = new LinkedHashMap<>();
            for (String locale : root.keySet()) {
                JsonObject obj = root.getAsJsonObject(locale);
                String format = obj.has("format") ? obj.get("format").getAsString() : "%s %s";
                out.put(locale, new Locale(format, readMap(obj, "stages"), readMap(obj, "materials")));
            }
            return out;
        } catch (Exception e) {
            NeoVitae.LOGGER.error("[MaterialRegistry] Failed to load material name translations", e);
            return Collections.emptyMap();
        }
    }

    private static Map<String, String> readMap(JsonObject parent, String member) {
        Map<String, String> out = new LinkedHashMap<>();
        if (parent.has(member)) {
            JsonObject obj = parent.getAsJsonObject(member);
            for (String key : obj.keySet()) {
                out.put(key, obj.get(key).getAsString());
            }
        }
        return out;
    }
}
