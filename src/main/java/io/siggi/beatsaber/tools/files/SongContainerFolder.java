package io.siggi.beatsaber.tools.files;

import io.siggi.tools.iterator.FileIterator;
import io.siggi.tools.iterator.PathIteratorBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SongContainerFolder extends SongContainer {
    private final File directory;

    public SongContainerFolder(File directory) {
        this.directory = directory;
    }

    @Override
    public File getContainerFile() {
        return directory;
    }

    private File getFileName(String name) {
        File f = new File(directory, name);
        if (!f.exists()) {
            f = new File(directory, findCaseInsensitiveFile(name));
        }
        return f;
    }

    @Override
    public InputStream getFile(String name) throws IOException {
        if (name.replace("\\", "/").contains("../")) {
            throw new FileNotFoundException();
        }
        return new FileInputStream(getFileName(name));
    }

    @Override
    public long getFileSize(String name) {
        if (name.replace("\\", "/").contains("../")) {
            return 0L;
        }
        return getFileName(name).length();
    }

    @Override
    public Collection<String> getAllFiles() {
        File absoluteFile = directory.getAbsoluteFile();
        String absolutePath = absoluteFile.getPath();
        if (!absolutePath.endsWith(File.separator)) absolutePath += File.separator;
        Set<String> items = new HashSet<>();
        try (FileIterator iterator = new PathIteratorBuilder(absoluteFile).includeDirectories(false).includeFiles(true).fileIterator()) {
            for (File file : iterator) {
                String path = file.getAbsolutePath();
                if (path.startsWith(absolutePath)) items.add(path.substring(absolutePath.length()).replace(File.separator, "/"));
            }
        } catch (IOException ignored) {
        }
        return items;
    }

    @Override
    public void close() {
    }
}
