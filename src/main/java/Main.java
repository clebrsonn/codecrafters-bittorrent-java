import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.dampcake.bencode.Bencode; //- available if you need it!

public class Main {
  private static final Gson gson = new Gson();

  public static void main(String[] args) throws Exception {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    //System.out.println("Logs from your program will appear here!");
    String command = args[0];
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
        var file= torrentInputStream.readFile(args[1]);
          BencodeDecode bencodeDecode=new BencodeDecode(file);
        decoded = bencodeDecode.decode();

        System.out.println("Tracker URL: " + (char)((TreeMap<String, Object>) decoded).get("announce"));
        System.out.println("Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("length"));
        //Bencode bencode = new Bencode(true);

//          System.out.println("Info Hash: " + TorrentInputStream.byteArray2Hex(bencode.encode(
//                  (HashMap<String, Object>) bencode.decode(file, Type.DICTIONARY).get("info"))
//      ));

          System.out.println("Info Hash: " + TorrentInputStream.hexToSha1(
                  (byte[]) ((TreeMap<?, ?>) ((TreeMap<String, Object>) decoded).get("info")).get("piece"))
          );
          System.out.println("Piece Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("piece length"));
          List<byte[]> pieceHashes =bencodeDecode.decodePieces((byte[]) ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("pieces"));

          System.out.println("Piece Hashes: " + pieceHashes);

    } else {
      System.out.println("Unknown command: " + command);
    }

  }


  
}
