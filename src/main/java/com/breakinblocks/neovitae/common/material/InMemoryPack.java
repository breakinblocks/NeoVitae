package com.breakinblocks.neovitae.common.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class InMemoryPack implements PackResources {

    private static final String PACK_META_JSON = "{\"pack\":{\"description\":\"NeoVitae Generated Materials\",\"pack_format\":34}}";

    private final PackLocationInfo locationInfo;
    private final Map<PackType, Map<Identifier, byte[]>> resources = new EnumMap<>(PackType.class);

    public InMemoryPack(PackLocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        for (PackType type : PackType.values()) {
            resources.put(type, new LinkedHashMap<>());
        }
    }

    public void putJson(PackType type, Identifier location, String json) {
        resources.get(type).put(location, json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length == 1 && "pack.mcmeta".equals(elements[0])) {
            byte[] data = PACK_META_JSON.getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(data);
        }
        return null;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionType<T> type) {
        JsonObject root = JsonParser.parseString(PACK_META_JSON).getAsJsonObject();
        String key = type.name();
        if (root.has(key)) {
            return type.codec()
                    .parse(com.mojang.serialization.JsonOps.INSTANCE, root.getAsJsonObject(key))
                    .result()
                    .orElse(null);
        }
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, Identifier location) {
        byte[] data = resources.get(packType).get(location);
        if (data == null) return null;
        return () -> new ByteArrayInputStream(data);
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput output) {
        resources.get(packType).forEach((location, data) -> {
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(path + "/")) {
                output.accept(location, () -> new ByteArrayInputStream(data));
            }
        });
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        Set<String> namespaces = new HashSet<>();
        resources.get(type).keySet().forEach(loc -> namespaces.add(loc.getNamespace()));
        return namespaces;
    }

    @Override
    public void close() {
    }
}
