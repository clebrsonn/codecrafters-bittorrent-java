import org.apache.http.client.utils.URLEncodedUtils;
import tracker.Announceable;
import utils.DigestUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public record Magnet(byte[] hash,String name, String url) implements Announceable {
    private static String SCHEMA= "magnet:";
    private static String SCHEMA_HASH= "urn:btih:";

    @Override
    public String getTrackerURL() {
        return url;
    }

    @Override
    public byte[] getInfoHash() {
        return hash;
    }

    @Override
    public Long getInfoLength() {
        return 0L;
    }

    public static Magnet of(String link){
        assert link.startsWith(SCHEMA);
        link= link.substring(SCHEMA.length()+1);
        final String[] exactTopic = new String[1];
        final String[] displayName = {null};
        final String[] addressTracker = {null};

        URLEncodedUtils.parse(link, StandardCharsets.UTF_8).forEach(pair ->{
            if(pair.getName().equals("xt")){
                exactTopic[0] = pair.getValue().substring(SCHEMA_HASH.length());
            }if(pair.getName().equals("dn")){
                displayName[0] = pair.getValue();
            }if(pair.getName().equals("tr")){
                addressTracker[0] = pair.getValue();
            }


        });

        //magnet:?xt=urn:btih:ad42ce8109f54c99613ce38f9b4d87e70f24a165&dn=magnet1.gif&tr=http%3A%2F%2Fbittorrent-test-tracker.codecrafters.io%2Fannounce
        return new Magnet(HexFormat.of().parseHex(exactTopic[0]), displayName[0], addressTracker[0]);
    }
}
