package io.siggi.beatsaber.tools.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SongContainerFolder extends SongContainer {
    private final File directory;

    public SongContainerFolder(File directory) {
        this.directory = directory;
    }

    @Override
    public File getContainerFile() {
        return directory;
    }

    @Override
    public InputStream getFile(String name) throws IOException {
        if (name.replace("\\", "/").contains("../")) {
            throw new FileNotFoundException();
        }
        return new FileInputStream(new File(directory, name));
    }

    @Override
    public long getFileSize(String name) {
        if (name.replace("\\", "/").contains("../")) {
            return 0L;
        }
        return new File(directory, name).length();
    }

    @Override
    public void close() {
    }
}
