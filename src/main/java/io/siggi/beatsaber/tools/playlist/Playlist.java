package io.siggi.beatsaber.tools.playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.siggi.beatsaber.tools.util.BSTUtil.asString;

public class Playlist {
    public String playlistTitle;
    public String playlistAuthor;
    public String playlistDescription;
    public JsonObject customData;
    public final List<PlaylistSong> songs = new ArrayList<>();
    public String image;

    public static Playlist fromJson(JsonObject object) {
        Playlist playlist = new Playlist();
        playlist.playlistTitle = asString(object.get("playlistTitle"));
        playlist.playlistAuthor = asString(object.get("playlistAuthor"));
        playlist.playlistDescription = asString(object.get("playlistDescription"));
        playlist.customData = object.getAsJsonObject("customData");
        playlist.image = asString(object.get("image"));

        JsonArray songsArray = object.getAsJsonArray("songs");
        if (songsArray != null) {
            for (JsonElement songElement : songsArray) {
                playlist.songs.add(PlaylistSong.fromJson(songElement.getAsJsonObject()));
            }
        }

        return playlist;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        if (playlistTitle != null) object.addProperty("playlistTitle", playlistTitle);
        if (playlistAuthor != null) object.addProperty("playlistAuthor", playlistAuthor);
        if (playlistDescription != null) object.addProperty("playlistDescription", playlistDescription);
        if (customData != null) object.add("customData", customData);
        JsonArray array = new JsonArray();
        for (PlaylistSong song : songs) {
            array.add(song.toJson());
        }
        object.add("songs", array);
        if (image != null) object.addProperty("image", image);
        return object;
    }
}
