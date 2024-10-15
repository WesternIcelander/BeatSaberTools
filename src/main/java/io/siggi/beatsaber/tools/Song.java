package io.siggi.beatsaber.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.siggi.beatsaber.tools.files.SongContainer;
import io.siggi.tools.Digests;
import io.siggi.tools.Hex;
import io.siggi.tools.io.IO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.siggi.beatsaber.tools.util.BSTUtil.asString;

public class Song {
    private final SongContainer container;
    private final Map<String, byte[]> dataCache = new HashMap<>();
    private final Map<String, JsonElement> jsonFiles = new HashMap<>();
    private JsonObject infoJson = null;
    private String infoJsonName = null;
    private String hash;

    public Song(SongContainer container) {
        this.container = container;
    }

    public SongContainer getContainer() {
        return container;
    }

    public String getSongName() {
        return asString(getInfoJson().get("_songName"));
    }

    public String getSongSubName() {
        return asString(getInfoJson().get("_songSubName"));
    }

    public String getSongAuthor() {
        return asString(getInfoJson().get("_songAuthorName"));
    }

    public String getLevelAuthor() {
        return asString(getInfoJson().get("_levelAuthorName"));
    }

    public String getHash() {
        // Hash is determined by concatenating all .dat files starting with Info.dat
        // followed by the other dat files in the order they appear in Info.dat,
        // and then running it through SHA-1, then converting it to uppercase hex.
        if (hash == null) {
            try {
                List<String> datFiles = new ArrayList<>();
                JsonObject info = getInfoJson();
                datFiles.add(infoJsonName);
                if (info == null) return null;
                JsonArray beatmapSets = info.getAsJsonArray("_difficultyBeatmapSets");
                for (JsonElement beatmapSetElement : beatmapSets) {
                    JsonObject beatmapSet = beatmapSetElement.getAsJsonObject();
                    JsonArray beatmaps = beatmapSet.getAsJsonArray("_difficultyBeatmaps");
                    for (JsonElement beatmapElement : beatmaps) {
                        JsonObject beatmap = beatmapElement.getAsJsonObject();
                        String fileName = beatmap.get("_beatmapFilename").getAsString();
                        datFiles.add(fileName);
                    }
                }
                MessageDigest sha1 = Digests.sha1();
                byte[] buffer = new byte[4096];
                for (String datFile : datFiles) {
                    try (InputStream fileContent = container.getFile(datFile)) {
                        int c;
                        while ((c = fileContent.read(buffer, 0, buffer.length)) != -1) {
                            sha1.update(buffer, 0, c);
                        }
                    }
                }
                hash = Hex.toHex(sha1.digest()).toUpperCase();
            } catch (Exception e) {
                return null;
            }
        }
        return hash;
    }

    private byte[] getData(String file) {
        byte[] data = dataCache.get(file);
        if (data == null) {
            long fileSize = container.getFileSize(file);
            if (fileSize <= 0L || fileSize >= 0x1000000) return null;
            try (InputStream in = container.getFile(file)) {
                data = new byte[(int) fileSize];
                IO.readFully(in, data);
            } catch (Exception e) {
                return null;
            }
        }
        return data;
    }

    private <T extends JsonElement> T getJson(String file) {
        JsonElement jsonElement = jsonFiles.get(file);
        if (jsonElement == null) {
            byte[] data = getData(file);
            if (data == null) return null;
            jsonElement = JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(data)));
            jsonFiles.put(file, jsonElement);
        }
        return (T) jsonElement;
    }

    public JsonObject getInfoJson() {
        if (infoJson == null) {
            infoJson = getJson("Info.dat");
            if (infoJson == null) {
                Collection<String> allFiles = container.getAllFiles();
                for (String file : allFiles) {
                    if (file.equalsIgnoreCase("Info.dat")) {
                        infoJson = getJson(file);
                        if (infoJson != null) {
                            infoJsonName = file;
                            break;
                        }
                    }
                }
            } else {
                infoJsonName = "Info.dat";
            }
        }
        return infoJson;
    }
}
