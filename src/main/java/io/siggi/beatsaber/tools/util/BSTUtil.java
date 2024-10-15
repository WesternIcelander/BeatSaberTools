package io.siggi.beatsaber.tools.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.siggi.beatsaber.tools.playlist.Playlist;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BSTUtil {

    public static final Map<Type, TypeAdapter<?>> typeAdapters;

    static {
        Gson gson = new Gson();
        Map<Type, TypeAdapter<?>> map = new HashMap<>();
        typeAdapters = Collections.unmodifiableMap(map);
        map.put(Playlist.class, new TypeAdapter<Playlist>() {
            @Override
            public Playlist read(JsonReader reader) throws IOException {
                JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                return Playlist.fromJson(object);
            }

            @Override
            public void write(JsonWriter writer, Playlist playlist) throws IOException {
                gson.toJson(playlist.toJson(), writer);
            }
        });
    }

    public static String asString(JsonElement element) {
        if (element == null) {
            return null;
        }
        return element.getAsString();
    }

    public static double asDouble(JsonElement element, double defaultIfNull) {
        if (element == null) return defaultIfNull;
        return element.getAsDouble();
    }

    public static GsonBuilder register(GsonBuilder builder) {
        Gson gson = new Gson();
        for (Map.Entry<Type, TypeAdapter<?>> entry : typeAdapters.entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        return builder;
    }
}
