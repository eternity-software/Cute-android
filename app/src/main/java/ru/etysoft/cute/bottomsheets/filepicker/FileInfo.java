package ru.etysoft.cute.bottomsheets.filepicker;

public class FileInfo {
    private final String name;
    private final String filePath;

    public FileInfo(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }
}
