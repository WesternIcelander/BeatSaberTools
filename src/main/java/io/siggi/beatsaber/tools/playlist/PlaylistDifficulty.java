package io.siggi.beatsaber.tools.playlist;

import com.google.gson.JsonObject;

import static io.siggi.beatsaber.tools.util.BSTUtil.asString;

public class PlaylistDifficulty {
    public String characteristic;
    public String name;

    public static PlaylistDifficulty fromJson(JsonObject object) {
        PlaylistDifficulty difficulty = new PlaylistDifficulty();
        difficulty.characteristic = asString(object.get("characteristic"));
        difficulty.name = asString(object.get("name"));
        return difficulty;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        if (characteristic != null) object.addProperty("characteristic", characteristic);
        if (name != null) object.addProperty("name", name);
        return object;
    }
}
