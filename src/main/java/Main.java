import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

            decoded = new BencodeDecode(inputStream).decode();
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(gson.toJson(decoded));
    }else if("info".equals(command)) {
        TorrentInputStream torrentInputStream= new TorrentInputStream();
        var file= torrentInputStream.readFile(args[1]);//"./sample.torrent");//args[1]);
          BencodeDecode bencodeDecode=new BencodeDecode(file);
        decoded = bencodeDecode.decode();

        System.out.println("Tracker URL: " + new String((byte[]) ((TreeMap<String, Object>) decoded).get("announce"), StandardCharsets.UTF_8));
        System.out.println("Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("length"));
        Bencode bencode = new Bencode(true);

          System.out.println("Info Hash: " + TorrentInputStream.hexToSha1(bencode.encode(
                  (Map<String, Object>) bencode.decode(torrentInputStream.readFile(args[1]).readAllBytes(), Type.DICTIONARY).get("info"))
      ));
          var outputStream = new ByteArrayOutputStream();
          new BencodeEncode(outputStream).encodeDic(new TreeMap<>((TreeMap<String, Object>) ((TreeMap<String, Object>) decoded).get("info")));

          System.out.println("Info Hash2: " + TorrentInputStream.hexToSha1(
                  outputStream.toByteArray())
          );
          System.out.println("Piece Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("piece length"));
          List<byte[]> pieceHashes =bencodeDecode.decodePieces((byte[]) ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("pieces"));
          pieceHashes.forEach(piece -> System.out.println(TorrentInputStream.hexToSha1(piece)));

          System.out.println("Piece Hashes:" );

          pieceHashes.forEach(piece -> System.out.println(TorrentInputStream.hexToSha1(piece)));

    } else {
      System.out.println("Unknown command: " + command);
    }

  }


  
}
