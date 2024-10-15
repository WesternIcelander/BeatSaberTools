package io.siggi.beatsaber.tools.files;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.ZipFile;

public abstract class SongContainer implements Closeable {

    public abstract File getContainerFile();

    public abstract InputStream getFile(String name) throws IOException;

    public abstract long getFileSize(String name);

    public abstract Collection<String> getAllFiles();

    public static SongContainer open(File file) throws IOException {
        if (file.getName().equals("Info.dat")) {
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
