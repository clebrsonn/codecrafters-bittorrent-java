import torrent.TorrentInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BittorrentDownloader {
    private static final int BLOCK_SIZE = 16 * 1024; // 16 KB
    private static final int MAX_PIPELINE = 5; // Máximo de 5 blocos pendentes

    public static void downloadPiece(InetSocketAddress address, int pieceIndex, String outputPath, TorrentInfo info) throws IOException {

        // Conectar-se a um peer
        Peer peer = new Peer(new Socket(address.getAddress(), address.getPort()), info.hash()); // Exemplo fictício para estabelecer conexão

        // Fazer handshake
        peer.performHandshake();
        peer.waitForBitfield();

        // Trocar mensagens iniciais
        peer.sendInterested();
        peer.waitForUnchoke();

        // Dividir a peça em blocos e fazer download com pipelining
        List<byte[]> pieceBlocks = new ArrayList<>();
        Queue<Long> pendingOffsets = new LinkedList<>();
        for (Long offset = 0L; offset < info.length(); offset += BLOCK_SIZE) {
            pendingOffsets.add(offset);
        }

        while (!pendingOffsets.isEmpty() || !pieceBlocks.isEmpty()) {
            while (pendingOffsets.size() > 0 && pieceBlocks.size() < MAX_PIPELINE) {
                Long offset = pendingOffsets.poll();
                Long blockLength = Math.min(BLOCK_SIZE, info.length() - offset);
                peer.sendRequest(pieceIndex, offset, blockLength);
                pieceBlocks.add(new byte[Math.toIntExact(blockLength)]); // Placeholder para o bloco recebido
            }

            // Receber e processar o próximo bloco
            Peer.Block block = peer.receivePiece();
            int blockOffset = block.offset();
            int index = blockOffset / BLOCK_SIZE;
            pieceBlocks.set(index, block.data()); // Armazena o bloco recebido
        }

        // Combinar blocos em uma peça completa
        byte[] completePiece = new byte[Math.toIntExact(info.length())];
        for (int i = 0; i < pieceBlocks.size(); i++) {
            System.arraycopy(pieceBlocks.get(i), 0, completePiece, i * BLOCK_SIZE, pieceBlocks.get(i).length);
        }

        // Salvar a peça no arquivo de saída
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(completePiece);
        }
        System.out.println("Peça salva em: " + outputPath);
    }

}
