package ch.nexusnet.postmanager.util;

import ch.nexusnet.postmanager.aws.s3.config.AllowedFileType;
import ch.nexusnet.postmanager.exception.UnsupportedFileTypeException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileValidationUtil {

    private static final Tika tika = new Tika();

    private FileValidationUtil() {
    }

    /**
     * Validates the filet ype of a given file.
     *
     * @param file The file to be validated.
     * @throws IOException                  If an I/O error occurs.
     * @throws UnsupportedFileTypeException If the file type is not supported.
     */
    public static void validateFileType(MultipartFile file) throws IOException, UnsupportedFileTypeException {
        String detectedType = tika.detect(file.getInputStream());
        List<String> allowedMimeTypes = Arrays.stream(AllowedFileType.values())
                .map(AllowedFileType::getMimeType)
                .toList();
        if (!allowedMimeTypes.contains(detectedType)) {
            throw new UnsupportedFileTypeException("File type" + detectedType + " not supported. Allowed file types: " + allowedMimeTypes + ".");
        }
    }
}