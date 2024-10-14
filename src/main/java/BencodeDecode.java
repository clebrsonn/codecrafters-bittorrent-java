import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeDecode {
    private final PushbackInputStream input;
    private final Boolean useBytes;

    public BencodeDecode(InputStream inputStream, final Boolean useBytes) {
        this.input = new PushbackInputStream(inputStream, 1);// Buffer de 1 byte para pushback
        this.useBytes = useBytes;
    }

    public Object decode() throws IOException {
        int prefix = this.input.read();
        if (Character.isDigit(prefix)) {
            return useBytes ? decodeStringByte(prefix) : decodeString(prefix);
        } else if (prefix == 'i') {
            return decodeNumber();
        } else if (prefix == 'l') {
            return decodeList();
        } else if (prefix == 'd') {
            return decodeMap();
        }else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

    private List<Object> decodeList() throws IOException {
        List<Object> list = new ArrayList<>();
        while (true) {
            int next = this.input.read();
            if (next == 'e') {
                break; // Fim da lista
            } else {
                this.input.unread(next); // Reverte o byte lido
                list.add(decode());
            }
        }
        return list;
    }

    private Map<String, Object> decodeMap() throws IOException {
        Map<String, Object> decodedMap = new TreeMap<>();

        while (true) {
            int next = this.input.read();
            if (next == 'e') {
                break; // Fim do dicionário
            } else {
                this.input.unread(next);
                // Decodificar chave (que sempre será uma string)
                String key = decodeString(this.input.read());
                // Decodificar valor (que pode ser qualquer tipo)
                Object value = decode();
                decodedMap.put(key, value);
            }
        }
        return decodedMap;
    }

    private Long decodeNumber() throws IOException {
        StringBuilder number = new StringBuilder();
        int b;
        while ((b = this.input.read()) != 'e') {
            number.append((char) b);
        }
        return Long.parseLong(number.toString());
    }

    private byte[] decodeStringByte(int firstDigit) throws IOException{
        StringBuilder lengthStr = new StringBuilder();
        lengthStr.append((char) firstDigit);
        int b;
        while ((b = this.input.read()) != ':') {
            lengthStr.append((char) b);
        }
        int length = Integer.parseInt(lengthStr.toString());

        // Lê os bytes da string
        byte[] bytes = new byte[length];
        this.input.read(bytes); // Lê diretamente os bytes da string
        return bytes;
    }
    private String decodeString(int firstDigit) throws IOException {

        return new String(decodeStringByte(firstDigit), Charset.defaultCharset());
    }

    public static List<byte[]> decodePieces(byte[] piecesBytes) {
        final int SHA1_LENGTH = 20;
        List<byte[]> pieces = new ArrayList<>();

        if (piecesBytes.length % SHA1_LENGTH != 0) {
            throw new IllegalArgumentException("Tamanho do campo `pieces` não é múltiplo de 20 bytes.");
        }

        for (int i = 0; i < piecesBytes.length; i += SHA1_LENGTH) {
            byte[] pieceHash = new byte[SHA1_LENGTH];
            System.arraycopy(piecesBytes, i, pieceHash, 0, SHA1_LENGTH);
            pieces.add(pieceHash);
        }

        return pieces;
    }
}
