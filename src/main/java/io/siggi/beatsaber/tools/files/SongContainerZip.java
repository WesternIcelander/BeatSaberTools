package io.siggi.beatsaber.tools.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SongContainerZip extends SongContainer {
    private final ZipFile zipFile;

    public SongContainerZip(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    @Override
    public File getContainerFile() {
        return null;
    }

    @Override
    public InputStream getFile(String name) throws IOException {
        ZipEntry entry;
        try {
            entry = zipFile.getEntry(name);
            if (entry == null) entry = zipFile.getEntry(findCaseInsensitiveFile(name));
        } catch (IllegalStateException e) {
            throw new IOException("Already closed", e);
        }
        if (entry == null) throw new FileNotFoundException();
        return zipFile.getInputStream(entry);
    }

    @Override
    public long getFileSize(String name) {
        ZipEntry entry;
        try {
            entry = zipFile.getEntry(name);
            if (entry == null) entry = zipFile.getEntry(findCaseInsensitiveFile(name));
        } catch (IllegalStateException e) {
            return 0L;
        }
        if (entry == null) return 0L;
        return entry.getSize();
    }

    @Override
    public Collection<String> getAllFiles() {
        Set<String> items = new HashSet<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            items.add(entry.getName());
        }
        return items;
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
