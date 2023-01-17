package orquest.test;

import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TestUtils {

    private TestUtils() {
    }

    public static String readFile(String path) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + path).toPath(), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
