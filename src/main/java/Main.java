import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    String command = args[0];//"info";//args[0];
      Object decoded;

      if("decode".equals(command)) {
      //  Uncomment this block to pass the first stage
        String bencodedValue = args[1];


        var inputStream= new ByteArrayInputStream(bencodedValue.getBytes(StandardCharsets.UTF_8));

        decoded = new BencodeDecode(inputStream, false).decode();

        System.out.println(gson.toJson(decoded));

    }else if("info".equals(command)) {
        DigestUtil digestUtil = new DigestUtil();
        var file= digestUtil.readFile(args[1]);//"./sample.torrent");//args[1]);
          BencodeDecode bencodeDecode=new BencodeDecode(file, true);
        decoded = bencodeDecode.decode();
        final var torrent = Torrent.of((TreeMap<String, Object>) decoded);

        System.out.println("Tracker URL: " + torrent.announce());
        System.out.println("Length: " + torrent.info().length());
        //Bencode bencode = new Bencode(true);

          System.out.println("Info Hash: " + new String(torrent.info().hash(), StandardCharsets.ISO_8859_1));
          System.out.println("Piece Length: " + torrent.info().pieceLength());
          //pieceHashes.forEach(piece -> System.out.println(DigestUtil.hexToSha1(piece)));

          System.out.println("Piece Hashes:" );

          torrent.info().pieces().forEach(piece -> System.out.println(DigestUtil.bytesToHex(piece)));

    }else if("peers".equals(command)){
          DigestUtil digestUtil = new DigestUtil();
          var file= digestUtil.readFile(args[1]);//"./sample.torrent");//args[1]);
          BencodeDecode bencodeDecode=new BencodeDecode(file, false);
          decoded = bencodeDecode.decode();
          final var torrent = Torrent.of((TreeMap<String, Object>) decoded);

          byte[] sha1Hash= DigestUtil.toSha1(torrent.info().hash());

          System.out.println(new HttpRequests().get(torrent.announce(), Map.ofEntries(

                  Map.entry("info_hash",URLEncoder.encode(new String(sha1Hash, StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1)),
                  Map.entry("peer_id",  "cbsc1234567890v4f5t6"),
                  Map.entry("port",  "6881"),
                  Map.entry("uploaded",  "0"),
                  Map.entry("downloaded",  "0"),
                  Map.entry("left",  ""+torrent.info().length()),
                  Map.entry("compact",  "1")
          )));

    }else {
      System.out.println("Unknown command: " + command);
    }

  }

    private static String toURLEncoded(byte[] hash) {
        StringBuilder encoded = new StringBuilder();

        for (byte b : hash) {
            // Para bytes não imprimíveis, usamos %HH
            encoded.append(String.format("%%%02X", b));
        }

        return encoded.toString();
    }

}
