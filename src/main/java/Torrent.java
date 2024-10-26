import java.nio.charset.StandardCharsets;
import java.util.Map;

public record Torrent(String announce, TorrentInfo info) {

    public static Torrent of(Map<String, Object> root) {
        final var announce = new String((byte[]) root.get("announce"), StandardCharsets.ISO_8859_1);
        final var info = TorrentInfo.of((Map<String, Object>) root.get("info"));

        return new Torrent(announce, info);
    }
}
