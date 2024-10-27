package utils;

import bencode.BencodeEncode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class DigestUtil {

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

    public static byte[] shaInfo(final Object infoRoot) {
        final var infoOutputStream = new ByteArrayOutputStream();
        try {
            new BencodeEncode().write(infoRoot,infoOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return toSha1(infoOutputStream.toByteArray());
    }

    public static String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes);
    }

}
