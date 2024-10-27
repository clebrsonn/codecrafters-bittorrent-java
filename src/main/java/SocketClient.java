import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SocketClient {
    private static final byte[] PROTOCOL_BYTES= "BitTorrent protocol".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] PADDING_8= new byte[8];


    public byte[] connect(Socket socket, Torrent torrent){
        try (var in = socket.getInputStream();
             var out = socket.getOutputStream();

        ){
            byte[] peerId = new byte[20];
            new SecureRandom().nextBytes(peerId);

            System.out.println("Connected to server.");
            // Open input and output streams
            final var infoHash = torrent.info().hash();
            // Exchange data

            byte[] handshakeMessage = buildHandshakeMessage(infoHash, peerId);

            // Envia a mensagem de handshake
            out.write(handshakeMessage);
            out.flush();

            // Recebe a resposta do handshake
            byte[] response = new byte[68];  // O tamanho esperado da resposta é 68 bytes
            int bytesRead = in.read(response);

            if (bytesRead == 68) {
                // Extrai o peer_id recebido (últimos 20 bytes da resposta)
                byte[] receivedPeerId = new byte[20];
                System.arraycopy(response, 48, receivedPeerId, 0, 20);

                return receivedPeerId;
            } else {
                System.out.println("Resposta inesperada. Tamanho lido: " + bytesRead);
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] buildHandshakeMessage(byte[] infoHash, byte[] peerId) {

        final var reserved = PADDING_8;

        byte[] protocolBytes = PROTOCOL_BYTES;
        byte pstrlen = (byte) protocolBytes.length;

        byte[] handshake = new byte[49 + protocolBytes.length];
        handshake[0] = pstrlen;
        System.arraycopy(protocolBytes, 0, handshake, 1, protocolBytes.length);
        System.arraycopy(reserved, 0, handshake, 1 + protocolBytes.length, reserved.length);
        System.arraycopy(infoHash, 0, handshake, 1 + protocolBytes.length + reserved.length, infoHash.length);
        System.arraycopy(peerId, 0, handshake, 1 + protocolBytes.length + reserved.length + infoHash.length, peerId.length);

        return handshake;
    }

}
