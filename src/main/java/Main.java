import bencode.BencodeDecode;
import com.google.gson.Gson;
import torrent.Torrent;
import tracker.AnnounceResponse;
import tracker.HttpRequests;
import utils.DigestUtil;

import java.io.*;
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
              try (final var peer = Peer.connect(new Socket(address[0], Integer.parseInt(address[1])), torrent)) {
                  System.out.printf("Peer ID: %s%n", DigestUtil.bytesToHex(peer.getId()));
              }

          }
          case "download_piece"->{
              final var torrent = load(args[3]);
              AnnounceResponse returned= new HttpRequests().get(torrent);
              String outputPath = args[2]; // Ex: /tmp/test-piece-0
              int pieceIndex = Integer.parseInt(args[4]);

              try (
                      final var peer = Peer.connect(returned.peers().getFirst(), torrent);
                      final var fileOutputStream = new FileOutputStream(new File(outputPath));
              ) {
                  final var data = peer.downloadPiece(torrent.info(), pieceIndex);
                  fileOutputStream.write(data);
              }

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
