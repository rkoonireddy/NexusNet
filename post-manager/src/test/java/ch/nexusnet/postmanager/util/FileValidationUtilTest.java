package ch.nexusnet.postmanager.util;

import ch.nexusnet.postmanager.exception.UnsupportedFileTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileValidationUtilTest {

    private MockMultipartFile validFile;
    private MockMultipartFile invalidFile;

    // File that is not what it claims to be e.g. a text file with a .jpeg extension
    private MockMultipartFile dishonestFile;

    @BeforeEach
    void setUp() throws IOException {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);
        invalidFile = new MockMultipartFile("file", "filename.txt", "text/plain", "some text".getBytes());
        dishonestFile = new MockMultipartFile("file", "filename.jpeg", "image/jpeg", "some text".getBytes());
    }

    @Test
    void shouldNotThrowExceptionWhenFileTypeIsValid() throws UnsupportedFileTypeException {
        assertDoesNotThrow(() -> FileValidationUtil.validateFileType(validFile));
    }

    @Test
    void shouldThrowExceptionWhenFileTypeIsInvalid() {
        assertThrows(UnsupportedFileTypeException.class, () -> FileValidationUtil.validateFileType(invalidFile));
    }

    @Test
    void shouldThrowExceptionWhenFileTypeIsDishonest() {
        assertThrows(UnsupportedFileTypeException.class, () -> FileValidationUtil.validateFileType(dishonestFile));
    }

    @Test
    void shouldThrowExceptionWhenFileIsNull() {
        assertThrows(NullPointerException.class, () -> FileValidationUtil.validateFileType(null));
    }
}