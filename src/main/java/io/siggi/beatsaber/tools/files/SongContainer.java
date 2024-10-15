package io.siggi.beatsaber.tools.files;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public abstract class SongContainer implements Closeable {

    private final Map<String, String> caseInsensitiveFileMap = new HashMap<>();

    public abstract File getContainerFile();

    public abstract InputStream getFile(String name) throws IOException;

    public abstract long getFileSize(String name);

    public abstract Collection<String> getAllFiles();

    public String findCaseInsensitiveFile(String name) {
        if (caseInsensitiveFileMap.isEmpty()) {
            for (String item : getAllFiles()) {
                caseInsensitiveFileMap.put(item.toLowerCase(), item);
            }
        }
        String result = caseInsensitiveFileMap.get(name.toLowerCase());
        if (result == null) return name;
        return result;
    }

    public static SongContainer open(File file) throws IOException {
        if (file.getName().equalsIgnoreCase("Info.dat")) {
            file = file.getParentFile();
        }
        if (file.isDirectory()) {
            return new SongContainerFolder(file);
        } else {
            ZipFile zf = null;
            SongContainerZip container = null;
            try {
                zf = new ZipFile(file);
                container = new SongContainerZip(zf);
            } finally {
                if (container == null && zf != null) {
                    try {
                        zf.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return container;
        }
    }
}
