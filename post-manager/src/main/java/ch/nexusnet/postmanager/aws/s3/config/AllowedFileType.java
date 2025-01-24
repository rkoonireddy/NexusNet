package ch.nexusnet.postmanager.aws.s3.config;

import lombok.Getter;

@Getter
public enum AllowedFileType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    SVG("image/svg+xml"),

    MP3("audio/mpeg"),

    MP4("video/mp4"),
    AVI("video/x-msvideo"),
    MOV("video/quicktime"),

    PDF("application/pdf");


    private final String mimeType;

    AllowedFileType(String mimeType) {
        this.mimeType = mimeType;
    }
}