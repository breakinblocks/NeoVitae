package com.breakinblocks.neovitae.common.material;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class MaterialDefinition {

    private String name;
    private String color;
    private List<String> stages;
    @SerializedName("smelt_to")
    private String smeltTo;
    @SerializedName("smelt_xp")
    private float smeltXp;
    @SerializedName("ore_tag")
    private String oreTag;
    @SerializedName("raw_tag")
    private String rawTag;
    @SerializedName("ingot_tag")
    private String ingotTag;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("id_overrides")
    private Map<String, String> idOverrides;
    @SerializedName("alias_tags")
    private Map<String, List<String>> aliasTags;
    @SerializedName("dust_yield_from_ore")
    private int dustYieldFromOre = 3;
    @SerializedName("extra_input_items")
    private List<String> extraInputItems;
    @SerializedName("extra_ore_items")
    private List<String> extraOreItems;
    @SerializedName("extra_raw_items")
    private List<String> extraRawItems;
    @SerializedName("generative")
    private Boolean generative;
    @SerializedName("gen_ore")
    private String genOre;
    @SerializedName("gen_raw")
    private String genRaw;

    public MaterialDefinition() {}

    public MaterialDefinition(String name, String color, List<String> stages,
                               String smeltTo, float smeltXp,
                               String oreTag, String rawTag, String ingotTag,
                               String displayName, Map<String, String> idOverrides) {
        this.name = name;
        this.color = color;
        this.stages = stages;
        this.smeltTo = smeltTo;
        this.smeltXp = smeltXp;
        this.oreTag = oreTag;
        this.rawTag = rawTag;
        this.ingotTag = ingotTag;
        this.displayName = displayName;
        this.idOverrides = idOverrides;
    }

    public String getName() { return name; }
    public List<String> getStages() { return stages; }
    public String getSmeltTo() { return smeltTo; }
    public float getSmeltXp() { return smeltXp; }
    public String getOreTag() { return oreTag; }
    public String getRawTag() { return rawTag; }
    public String getIngotTag() { return ingotTag; }
    public String getDisplayName() { return displayName != null ? displayName : capitalize(name); }

    public int getColorInt() {
        String hex = color.startsWith("#") ? color.substring(1) : color.startsWith("0x") ? color.substring(2) : color;
        return Integer.parseInt(hex, 16);
    }

    public boolean hasStage(String stage) {
        return stages != null && stages.contains(stage);
    }

    public String getItemId(String stage) {
        if (idOverrides != null && idOverrides.containsKey(stage)) {
            return idOverrides.get(stage);
        }
        return name + "_" + stage;
    }

    public List<String> getAliasTags(String stage) {
        if (aliasTags == null) return List.of();
        return aliasTags.getOrDefault(stage, List.of());
    }

    public MaterialDefinition withAliasTags(Map<String, List<String>> aliasTags) {
        this.aliasTags = aliasTags;
        return this;
    }

    public int getDustYieldFromOre() { return dustYieldFromOre; }

    public List<String> getExtraInputItems() {
        return extraInputItems != null ? extraInputItems : List.of();
    }

    public MaterialDefinition withDustYieldFromOre(int dustYieldFromOre) {
        this.dustYieldFromOre = dustYieldFromOre;
        return this;
    }

    public MaterialDefinition withExtraInputItems(List<String> extraInputItems) {
        this.extraInputItems = extraInputItems;
        return this;
    }

    public List<String> getExtraOreItems() {
        return extraOreItems != null ? extraOreItems : List.of();
    }

    public List<String> getExtraRawItems() {
        return extraRawItems != null ? extraRawItems : List.of();
    }

    public MaterialDefinition withExtraOreItems(List<String> extraOreItems) {
        this.extraOreItems = extraOreItems;
        return this;
    }

    public MaterialDefinition withExtraRawItems(List<String> extraRawItems) {
        this.extraRawItems = extraRawItems;
        return this;
    }

    public boolean isGenerative() {
        return generative == null || generative;
    }

    public String getGenOre() {
        return genOre;
    }

    public String getGenRaw() {
        return genRaw;
    }

    public MaterialDefinition withGenerative(boolean generative) {
        this.generative = generative;
        return this;
    }

    public MaterialDefinition withGenOre(String genOre) {
        this.genOre = genOre;
        return this;
    }

    public MaterialDefinition withGenRaw(String genRaw) {
        this.genRaw = genRaw;
        return this;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] parts = s.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return sb.toString();
    }
}
