package io.siggi.beatsaber.tools.playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.siggi.beatsaber.tools.util.BSTUtil.asString;

public class PlaylistSong {
    public String key;
    public String songName;
    public String levelAuthorName;
    public String hash;
    public String levelid;
    public final List<PlaylistDifficulty> difficulties = new ArrayList<>();
    public JsonObject customData;

    public static PlaylistSong fromJson(JsonObject object) {
        PlaylistSong song = new PlaylistSong();
        song.key = asString(object.get("key"));
        song.songName = asString(object.get("songName"));
        song.levelAuthorName = asString(object.get("levelAuthorName"));
        song.hash = asString(object.get("hash"));
        song.levelid = asString(object.get("levelid"));
        JsonArray difficulties = object.getAsJsonArray("difficulties");
        if (difficulties != null) {
            for (JsonElement difficultyElement : difficulties) {
                song.difficulties.add(PlaylistDifficulty.fromJson(difficultyElement.getAsJsonObject()));
            }
        }
        song.customData = object.getAsJsonObject("customData");
        return song;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        if (key != null) object.addProperty("key", key);
        if (songName != null) object.addProperty("songName", songName);
        if (levelAuthorName != null) object.addProperty("levelAuthorName", levelAuthorName);
        if (hash != null) object.addProperty("hash", hash);
        if (levelid != null) object.addProperty("levelid", levelid);
        if (!difficulties.isEmpty()) {
            JsonArray difficultiesArray = new JsonArray();
            object.add("difficulties", difficultiesArray);
        }
        if (customData != null) {
            object.add("customData", customData);
        }
        return object;
    }
}
