import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TorrentInputStream {

    public byte[] readFile(final String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }
}
