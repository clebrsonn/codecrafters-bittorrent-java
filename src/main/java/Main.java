import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.dampcake.bencode.Bencode; //- available if you need it!

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


        try {
            var inputStream= new ByteArrayInputStream(bencodedValue.getBytes(StandardCharsets.UTF_8));

            decoded = new BencodeDecode(inputStream, false).decode();
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(gson.toJson(decoded));

    }else if("info".equals(command)) {
        TorrentInputStream torrentInputStream= new TorrentInputStream();
        var file= torrentInputStream.readFile(args[1]);//"./sample.torrent");//args[1]);
          BencodeDecode bencodeDecode=new BencodeDecode(file, true);
        decoded = bencodeDecode.decode();

        System.out.println("Tracker URL: " + new String((byte[]) ((TreeMap<String, Object>) decoded).get("announce"), StandardCharsets.UTF_8));
        System.out.println("Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("length"));
        //Bencode bencode = new Bencode(true);

          var outputStream = new ByteArrayOutputStream();
          new BencodeEncode(outputStream).encodeDic(new TreeMap<>((TreeMap<String, Object>) ((TreeMap<String, Object>) decoded).get("info")));

          System.out.println("Info Hash: " + TorrentInputStream.hexToSha1(
                  outputStream.toByteArray())
          );
          System.out.println("Piece Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("piece length"));
          List<byte[]> pieceHashes =bencodeDecode.decodePieces((byte[]) ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("pieces"));
          //pieceHashes.forEach(piece -> System.out.println(TorrentInputStream.hexToSha1(piece)));

          System.out.println("Piece Hashes:" );

          pieceHashes.forEach(piece -> System.out.println(TorrentInputStream.bytesToHex(piece)));

    }else if("peers".equals(command)){
          TorrentInputStream torrentInputStream= new TorrentInputStream();
          var file= torrentInputStream.readFile(args[1]);//"./sample.torrent");//args[1]);
          Bencode bencode = new Bencode(true);
          //BencodeDecode bencodeDecode=new BencodeDecode(file, false);
          //decoded = bencodeDecode.decode();
          var outputStream = new ByteArrayOutputStream();
          decoded = bencode.decode(file.readAllBytes(), Type.DICTIONARY);
          //new BencodeEncode(outputStream).encodeDic(new TreeMap<>((TreeMap<String, Object>) ((TreeMap<String, Object>) decoded).get("info")));
          byte[] sha1Hash= TorrentInputStream.toSha1(bencode.encode((Map<?, ?>) ((Map<?, ?>) decoded).get("info")));

          String base64UrlSafe = Base64.getUrlEncoder().withoutPadding().encodeToString(sha1Hash);
          ByteBuffer buffer = (ByteBuffer) ((Map<?, ?>)  decoded).get("announce");
          byte[] bytes = new byte[buffer.remaining()];
          buffer.get(bytes);


          System.out.println(new HttpRequests().get( new String(bytes , StandardCharsets.ISO_8859_1), Map.ofEntries(

                  Map.entry("info_hash",URLEncoder.encode(base64UrlSafe, StandardCharsets.ISO_8859_1)),
                  Map.entry("peer_id",  "cbc-1234567890v4f5t6"),
                  Map.entry("port",  "6881"),
                  Map.entry("uploaded",  "0"),
                  Map.entry("downloaded",  "0"),
                  Map.entry("left",  ""+((Map<String, Object>)((Map<String, Object>) decoded).get("info")).get("length")),
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
