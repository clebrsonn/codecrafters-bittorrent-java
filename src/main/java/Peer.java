import tracker.Announceable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Peer {
    private static final byte[] PROTOCOL_BYTES= "BitTorrent protocol".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] PADDING_8= new byte[8];

    private final Socket socket;
    private InputStream in;
    private OutputStream out;
    private final byte[] infoHash;
    private byte[] peerId;

    public Peer(Socket socket, byte[] infoHash) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.infoHash = infoHash;
    }


    public Peer(String ip, int port, byte[] infoHash) throws IOException {
        this(new Socket(ip, port), infoHash);
    }

    public byte[] performHandshake() throws IOException {
        var in = socket.getInputStream();
        var out = socket.getOutputStream();
        final int handshakeMessageSize = 1 + PROTOCOL_BYTES.length + PADDING_8.length + 20 + 20;
        final ByteBuffer payloadBuffer =
                ByteBuffer.allocate(handshakeMessageSize);
        payloadBuffer.put((byte) 19)
                .put(PROTOCOL_BYTES)
                .put(PADDING_8)
                .put(infoHash)
                .put("cbc12233445566778899".getBytes());
        out.write(payloadBuffer.array());
        final byte[] handshakeResponse = new byte[handshakeMessageSize];
        in.read(handshakeResponse);
        final byte[] peerIdResponse = new byte[20];
        final ByteBuffer wrap = ByteBuffer.wrap(handshakeResponse);
        wrap.position(48);
        wrap.get(peerIdResponse, 0, 20);

        // Envia a mensagem de handshake
        this.peerId= peerIdResponse;
        return peerIdResponse;
    }

    public void sendInterested() throws IOException {
        ByteBuffer interested = ByteBuffer.allocate(5);
        interested.putInt(1); // Tamanho da mensagem
        interested.put((byte) 2); // ID da mensagem interested

        out.write(interested.array());
        out.flush();
    }

    public void waitForUnchoke() throws IOException {
        byte[] response = new byte[5];
        in.read(response);

        ByteBuffer buffer = ByteBuffer.wrap(response);
        int length = buffer.getInt();
        byte messageId = buffer.get();

        if (length != 1 || messageId != 1) {
            throw new IOException("Esperado mensagem unchoke (id 1), mas recebeu outro tipo.");
        }
    }

    public void sendRequest(int index, Long offset, Long length) throws IOException {
        ByteBuffer request = ByteBuffer.allocate(17);
        request.putInt(13); // Comprimento da mensagem
        request.put((byte) 6); // ID da mensagem request
        request.putInt(index); // Índice da peça
        request.putLong(offset); // Offset do bloco
        request.putLong(length); // Comprimento do bloco

        out.write(request.array());
        out.flush();
    }

    public Block receivePiece() throws IOException {
        byte[] header = new byte[13];
        in.read(header);
        ByteBuffer buffer = ByteBuffer.wrap(header);
        int length = buffer.getInt();
        byte messageId = buffer.get();

        if (messageId != 7) {
            throw new IOException("Esperado mensagem piece (id 7), mas recebeu outro tipo.");
        }

        int pieceIndex = buffer.getInt();
        int blockOffset = buffer.getInt();

        int blockSize = length - 9; // Exclui header
        byte[] blockData = new byte[blockSize];
        in.read(blockData);

        return new Block(pieceIndex, blockOffset, blockData);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static class Block {
        private final int index;
        private final int offset;
        private final byte[] data;

        public Block(int index, int offset, byte[] data) {
            this.index = index;
            this.offset = offset;
            this.data = data;
        }

        public int getIndex() {
            return index;
        }

        public int getOffset() {
            return offset;
        }

        public byte[] getData() {
            return data;
        }
    }

}
