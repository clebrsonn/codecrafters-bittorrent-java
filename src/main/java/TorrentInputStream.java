import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HexFormat;

public class TorrentInputStream {

    public FileInputStream readFile(final String fileName) throws IOException {
        return new FileInputStream(fileName);
    }

    public static byte[] toSha1(final byte[] hash)  {
        if(hash == null){
            return null;
        }

        try {
            MessageDigest digest2 = MessageDigest.getInstance("SHA-1");

            return digest2.digest(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hexToSha1(final byte[] hash){
        return bytesToHex(toSha1(hash));
    }

    public static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

}
