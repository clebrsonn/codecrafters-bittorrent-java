import com.dampcake.bencode.Type;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.TreeMap;

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
          decoded = new BencodeDecode().decode(bencodedValue.getBytes(StandardCharsets.UTF_8));
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(gson.toJson(decoded));
    }else if("info".equals(command)) {
        TorrentInputStream torrentInputStream= new TorrentInputStream();
        byte[] file= torrentInputStream.readFile(args[1]);
        decoded = new BencodeDecode().decode(file);

        System.out.println("Tracker URL: " + ((TreeMap<String, Object>) decoded).get("announce"));
        System.out.println("Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("length"));
        //Bencode bencode = new Bencode(true);

//          System.out.println("Info Hash: " + TorrentInputStream.byteArray2Hex(bencode.encode(
//                  (HashMap<String, Object>) bencode.decode(file, Type.DICTIONARY).get("info"))
//      ));

          System.out.println("Info Hash: " + TorrentInputStream.byteArray2Hex(
                  new BencodeEncode().encode(((TreeMap<String, Object>) decoded).get("info"), BencodeType.DICTIONARY))
          );
          System.out.println("Piece Length: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("piece length"));
          System.out.println("Piece Hashes: " + ((TreeMap<String, Object>)((TreeMap<String, Object>) decoded).get("info")).get("pieces"));

    } else {
      System.out.println("Unknown command: " + command);
    }

  }


  
}
