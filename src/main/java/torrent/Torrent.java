package torrent;

import tracker.Announceable;

import java.util.Map;

public record Torrent(String announce, TorrentInfo info) implements Announceable {

    public static Torrent of(Map<String, Object> root) {
        final var announce = (String) root.get("announce");
        final var info = TorrentInfo.of((Map<String, Object>) root.get("info"));

        return new Torrent(announce, info);
    }

    @Override
    public String getTrackerURL() {
        return announce;
    }

    @Override
    public byte[] getInfoHash() {
        return info().hash();
    }

    @Override
    public Long getInfoLength() {
        return info().length();
    }
}
