import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class TorrentInputStream {

    public FileInputStream readFile(final String fileName) throws IOException {
        return new FileInputStream(fileName);
    }

    public static String hexToSha1(final byte[] hash) throws NoSuchAlgorithmException {

        MessageDigest digest2 = MessageDigest.getInstance("SHA-1");

        var bytes= digest2.digest(hash);

        return bytesToHex(bytes);

    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
