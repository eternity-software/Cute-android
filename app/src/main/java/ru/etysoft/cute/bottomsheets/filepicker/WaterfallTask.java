package ru.etysoft.cute.bottomsheets.filepicker;

import ru.etysoft.cute.components.SmartImageView;

public class WaterfallTask {

    private SmartImageView smartImageView;
    private String imageUri;

    public SmartImageView getSmartImageView() {
        return smartImageView;
    }

    public String getImageUri() {
        return imageUri;
    }

    public WaterfallTask(SmartImageView smartImageView, String imageUri) {
        this.smartImageView = smartImageView;
        this.imageUri = imageUri;
    }
}
