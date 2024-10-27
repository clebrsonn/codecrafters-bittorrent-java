import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    String command = args[0];//"info";//args[0];
      Object decoded;

      String bencodedValue = args[1];

      switch (command) {
          case "decode" -> {


              var inputStream = new ByteArrayInputStream(bencodedValue.getBytes(StandardCharsets.UTF_8));

              decoded = new BencodeDecode(inputStream).parse();

              System.out.println(gson.toJson(decoded));
          }
          case "info" -> {
              final var torrent = load(bencodedValue);

              System.out.println("Tracker URL: " + torrent.announce());
              System.out.println("Length: " + torrent.info().length());

              System.out.println("Info Hash: " + DigestUtil.bytesToHex(torrent.info().hash()));
              System.out.println("Piece Length: " + torrent.info().pieceLength());

              System.out.println("Piece Hashes:");

              torrent.info().pieces().forEach(piece -> System.out.println(DigestUtil.bytesToHex(piece)));

          }
          case "peers" -> {
              final var torrent = load(bencodedValue);
              AnnounceResponse returned= new HttpRequests().get(torrent);

              System.out.println(returned.peers());

          }
          case "handshake" -> {
              final var torrent = load(bencodedValue);
              final var address= args[2].split(":");
              System.out.println(new String(new SocketClient().connect(new Socket(address[0], Integer.parseInt(address[1])), torrent)));
          }
          case null, default -> System.out.println("Unknown command: " + command);
      }

  }

  private static Torrent load(final String path) throws IOException {
      var file = new FileInputStream(path);
          final var decoded = new BencodeDecode(file).parse();

          return Torrent.of((Map<String, Object>) decoded);
  }
}
