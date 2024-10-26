import java.nio.charset.StandardCharsets;
import java.util.Map;

public record Torrent(String announce, TorrentInfo info) {

    public static Torrent of(Map<String, Object> root) {
        final var announce = (String) root.get("announce");
        final var info = TorrentInfo.of((Map<String, Object>) root.get("info"));

        return new Torrent(announce, info);
    }
}
