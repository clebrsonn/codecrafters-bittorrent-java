import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.SplittableRandom;
// import com.dampcake.bencode.Bencode; - available if you need it!

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

        System.out.println("Tracker URL: " + ((HashMap<String, Object>) decoded).get("announce"));
        System.out.println("Length: " + ((HashMap<String, Object>)((HashMap<String, Object>) decoded).get("info")).get("length"));
    } else {
      System.out.println("Unknown command: " + command);
    }

  }


  
}
