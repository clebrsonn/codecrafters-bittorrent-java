import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class TorrentInputStream {

    public byte[] readFile(final String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    public static String byteArray2Hex(final byte[] hash) throws NoSuchAlgorithmException {

        MessageDigest digest2 = MessageDigest.getInstance("SHA-1");

        var bytes= digest2.digest(hash);
        var sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();

    }
}
