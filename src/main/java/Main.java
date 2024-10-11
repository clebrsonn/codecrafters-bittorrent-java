import com.google.gson.Gson;

import java.util.Arrays;
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
          decoded = new BencodeDecode().decode(bencodedValue);
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
        System.out.println(gson.toJson(decoded));


    }else if("info".equals(command)) {
        TorrentInputStream torrentInputStream= new TorrentInputStream();
        byte[] file= torrentInputStream.readFile(args[1]);
        decoded = new BencodeDecode().decode(Arrays.toString(file));
//            Tracker URL: http://bittorrent-test-tracker.codecrafters.io/announce
//            Length: 92063
        System.out.println(gson.toJson(decoded));
    } else {
      System.out.println("Unknown command: " + command);
    }

  }


  
}
