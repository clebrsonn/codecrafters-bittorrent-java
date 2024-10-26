import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class DigestUtil {

    public FileInputStream readFile(final String fileName) throws IOException {
        return new FileInputStream(fileName);
    }

    @SneakyThrows
    public static byte[] toSha1(final byte[] hash)  {
        if(hash == null){
            return null;
        }

        MessageDigest digest2 = MessageDigest.getInstance("SHA-1");

        return digest2.digest(hash);

    }

    public static String hexToSha1(final byte[] hash){
        return bytesToHex(toSha1(hash));
    }

    @SneakyThrows
    public static byte[] shaInfo(final Object infoRoot) {
        final var infoOutputStream = new ByteArrayOutputStream();
        new BencodeEncode(infoOutputStream).encode(infoRoot);

        return toSha1(infoOutputStream.toByteArray());
    }

    public static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

}
