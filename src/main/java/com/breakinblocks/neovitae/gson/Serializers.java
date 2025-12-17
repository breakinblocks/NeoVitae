package com.breakinblocks.neovitae.gson;

import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;

/**
 * Custom Gson serializers for Blood Magic data types.
 */
public final class Serializers {

    private Serializers() {}

    public static final SerializerBase<Direction> FACING_SERIALIZER = new SerializerBase<>() {
        @Override
        public Class<Direction> getType() {
            return Direction.class;
        }

        @Override
        public Direction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Direction.byName(json.getAsString());
        }

        @Override
        public JsonElement serialize(Direction src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }
    };

    public static final SerializerBase<ResourceLocation> RESOURCELOCATION_SERIALIZER = new SerializerBase<>() {
        @Override
        public Class<ResourceLocation> getType() {
            return ResourceLocation.class;
        }

        @Override
        public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                String domain = obj.get("domain").getAsString();
                String path = obj.get("path").getAsString();
                return ResourceLocation.fromNamespaceAndPath(domain, path);
            }
            return ResourceLocation.parse(json.getAsString());
        }

        @Override
        public JsonElement serialize(ResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("domain", src.getNamespace());
            object.addProperty("path", src.getPath());
            return object;
        }
    };

    public static final SerializerBase<ItemStack> ITEMMETA_SERIALIZER = new SerializerBase<>() {
        @Override
        public Class<ItemStack> getType() {
            return ItemStack.class;
        }

        @Override
        public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            ResourceLocation registryName = context.deserialize(
                    json.getAsJsonObject().get("registryName").getAsJsonObject(), ResourceLocation.class);
            return new ItemStack(BuiltInRegistries.ITEM.get(registryName));
        }

        @Override
        public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("registryName", context.serialize(BuiltInRegistries.ITEM.getKey(src.getItem())));
            jsonObject.addProperty("meta", src.getDamageValue());
            return jsonObject;
        }
    };

    public static final SerializerBase<BlockPos> BLOCKPOS_SERIALIZER = new SerializerBase<>() {
        @Override
        public Class<BlockPos> getType() {
            return BlockPos.class;
        }

        @Override
        public BlockPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int z = obj.get("z").getAsInt();
            return new BlockPos(x, y, z);
        }

        @Override
        public JsonElement serialize(BlockPos src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("x", src.getX());
            object.addProperty("y", src.getY());
            object.addProperty("z", src.getZ());
            return object;
        }
    };

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(FACING_SERIALIZER.getType(), FACING_SERIALIZER)
            .registerTypeAdapter(RESOURCELOCATION_SERIALIZER.getType(), RESOURCELOCATION_SERIALIZER)
            .registerTypeAdapter(ITEMMETA_SERIALIZER.getType(), ITEMMETA_SERIALIZER)
            .registerTypeAdapter(BLOCKPOS_SERIALIZER.getType(), BLOCKPOS_SERIALIZER)
            .create();
}
