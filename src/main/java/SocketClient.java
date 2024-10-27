import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SocketClient {
    private static final byte[] PROTOCOL_BYTES= "BitTorrent protocol".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] PADDING_8= new byte[8];


    public byte[] connect(Socket socket, Torrent torrent) throws IOException {
        try (var in = socket.getInputStream();
             var out = socket.getOutputStream();

        ) {

            final int handshakeMessageSize = 1 + 19 + 8 + 20 + 20;
            final ByteBuffer payloadBuffer =
                    ByteBuffer.allocate(handshakeMessageSize);
            payloadBuffer.put((byte) 19)
                    .put(PROTOCOL_BYTES)
                    .put(PADDING_8)
                    .put(torrent.info().hash())
                    .put("00112233445566778899".getBytes());
            out.write(payloadBuffer.array());
            final byte[] handshakeResponse = new byte[handshakeMessageSize];
            in.read(handshakeResponse);
            final byte[] peerIdResponse = new byte[20];
            final ByteBuffer wrap = ByteBuffer.wrap(handshakeResponse);
            wrap.position(48);
            wrap.get(peerIdResponse, 0, 20);

            // Envia a mensagem de handshake

            return peerIdResponse;
        }
    }
}
