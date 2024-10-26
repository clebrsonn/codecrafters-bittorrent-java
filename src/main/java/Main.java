import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    String command = args[0];//"info";//args[0];
      Object decoded;

      switch (command) {
          case "decode" -> {
              //  Uncomment this block to pass the first stage
              String bencodedValue = args[1];


              var inputStream = new ByteArrayInputStream(bencodedValue.getBytes(StandardCharsets.UTF_8));

              decoded = new BencodeDecode(inputStream, false).decode();

              System.out.println(gson.toJson(decoded));
          }
          case "info" -> {
              DigestUtil digestUtil = new DigestUtil();
              var file = digestUtil.readFile(args[1]);
              BencodeDecode bencodeDecode = new BencodeDecode(file, true);
              decoded = bencodeDecode.decode();
              final var torrent = Torrent.of((TreeMap<String, Object>) decoded);

              System.out.println("Tracker URL: " + torrent.announce());
              System.out.println("Length: " + torrent.info().length());

              System.out.println("Info Hash: " + new String(torrent.info().hash(), StandardCharsets.ISO_8859_1));
              System.out.println("Piece Length: " + torrent.info().pieceLength());

              System.out.println("Piece Hashes:");

              torrent.info().pieces().forEach(piece -> System.out.println(DigestUtil.bytesToHex(piece)));

          }
          case "peers" -> {
              DigestUtil digestUtil = new DigestUtil();
              var file = digestUtil.readFile(args[1]);
              var ben= new Bencode(true);
              var ddec= ben.decode(file.readAllBytes(), Type.DICTIONARY);
              BencodeDecode bencodeDecode = new BencodeDecode(file, false);
              decoded = bencodeDecode.decode();
              final var torrent = Torrent.of(ddec);

              System.out.println(new HttpRequests().get(torrent));

          }
          case null, default -> System.out.println("Unknown command: " + command);
      }

  }
}
