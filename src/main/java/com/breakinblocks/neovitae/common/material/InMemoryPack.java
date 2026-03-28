package com.breakinblocks.neovitae.common.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class InMemoryPack implements PackResources {

    private static final String PACK_META_JSON = "{\"pack\":{\"description\":\"NeoVitae Generated Materials\",\"pack_format\":34}}";

    private final PackLocationInfo locationInfo;
    private final Map<PackType, Map<ResourceLocation, byte[]>> resources = new EnumMap<>(PackType.class);

    public InMemoryPack(PackLocationInfo locationInfo) {
        this.locationInfo = locationInfo;
        for (PackType type : PackType.values()) {
            resources.put(type, new LinkedHashMap<>());
        }
    }

    public void putJson(PackType type, ResourceLocation location, String json) {
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
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        JsonObject root = JsonParser.parseString(PACK_META_JSON).getAsJsonObject();
        String key = deserializer.getMetadataSectionName();
        if (root.has(key)) {
            return deserializer.fromJson(root.getAsJsonObject(key));
        }
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
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
