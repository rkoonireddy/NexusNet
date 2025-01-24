package ch.nexusnet.usermanager.aws.s3.filetypes;

public enum S3FileType {
    PROFILE_PICTURE("image/jpeg"),
    RESUME("application/pdf");

    public final String label;

    S3FileType(String label) {
        this.label = label;
    }

    public static S3FileType getByLabel(String label) {
    for (S3FileType fileType : S3FileType.values()) {
        if (fileType.label.equals(label)) {
            return fileType;
        }
    }
    return null;
}
}
