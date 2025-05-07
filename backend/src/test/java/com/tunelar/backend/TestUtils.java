package com.tunelar.backend;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Utility class for test-related functionality, particularly for JSON serialization/deserialization.
 */
public class TestUtils {
    /**
     * Adapter for serializing and deserializing LocalDateTime objects with GSON.
     */
    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        /**
         * Formatter for LocalDateTime
         */
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(final LocalDateTime src, final Type typeOfSrc,
                final JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(final JsonElement json, final Type typeOfT,
                final JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }

    /**
     * GSON instance configured with custom type adapters
     */
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    /**
     * Converts an object to its JSON string representation.
     *
     * @param obj The object to convert to JSON
     * @return JSON string representation of the object
     */
    public static String asJsonString(final Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param <T> The type of the object
     * @param str The JSON string to parse
     * @param clazz The class of the object to return
     * @return An object of type T parsed from the JSON string
     */
    public static <T> T fromJson(final String str, final Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    /**
     * Converts a JSON string to an object of the specified type.
     * Useful for complex types like generic collections.
     *
     * @param <T> The type of the object
     * @param str The JSON string to parse
     * @param type The type of the object to return
     * @return An object of type T parsed from the JSON string
     */
    public static <T> T fromJson(final String str, final Type type) {
        return gson.fromJson(str, type);
    }
}