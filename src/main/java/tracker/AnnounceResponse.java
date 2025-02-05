package tracker;

import utils.NetworkUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record AnnounceResponse(long interval,
                               List<InetSocketAddress> peers
){
    public static AnnounceResponse of(Map<String, Object> root) {

        var interval = (Long) root.get("interval");
        if (interval == null) {
            interval = (long) root.get("mininterval");
        }

        final var peers = new ArrayList<InetSocketAddress>();
        peers.addAll(NetworkUtils.parseV4SocketAddresses((String) root.get("peers")));
        peers.addAll(NetworkUtils.parseV6SocketAddresses((String) root.get("peers6")));

        // peers.removeIf((x) -> x.getPort() == selfPort);
        // peers.removeIf((x) -> x.getAddress() instanceof Inet4Address);
        System.out.println(peers);

        return new AnnounceResponse(interval, peers);
    }

}
