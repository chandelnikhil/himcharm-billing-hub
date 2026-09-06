package org.himcharm.enums;

import java.util.Arrays;

public enum CampaignImageType {
    FESTIVAL("festival"),
    BIRTHDAY("birthday"),
    ANNIVERSARY("anniversary");

    private final String pathValue;
    private final String fileBaseName;

    CampaignImageType(String pathValue) {
        this(pathValue, pathValue);
    }

    CampaignImageType(String pathValue, String fileBaseName) {
        this.pathValue = pathValue;
        this.fileBaseName = fileBaseName;
    }

    public String getPathValue() {
        return pathValue;
    }

    public String getFileBaseName() {
        return fileBaseName;
    }

    public static CampaignImageType fromPath(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Image type is required");
        }

        return Arrays.stream(values())
                .filter(type -> type.pathValue.equalsIgnoreCase(value)
                        || type.fileBaseName.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Image type must be festival, birthday, or anniversary"
                ));
    }
}
