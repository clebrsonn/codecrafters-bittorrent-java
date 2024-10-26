import java.io.*;
import java.nio.charset.Charset;
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
        } else if (prefix == BencodeEncode.INTEGER_BYTE) {
            return decodeNumber();
        } else if (prefix == BencodeEncode.LIST_BYTE) {
            return decodeList();
        } else if (prefix == BencodeEncode.MAP_BYTE) {
            return decodeMap();
        }else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

    private List<Object> decodeList() throws IOException {
        List<Object> list = new ArrayList<>();
        while (true) {
            int next = this.input.read();
            if (next == BencodeEncode.END_BYTE) {
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
            if (next == BencodeEncode.END_BYTE) {
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
        while ((b = this.input.read()) != BencodeEncode.END_BYTE) {
            number.append((char) b);
        }
        return Long.parseLong(number.toString());
    }

    private byte[] decodeStringByte(int firstDigit) throws IOException{
        StringBuilder lengthStr = new StringBuilder();
        lengthStr.append((char) firstDigit);
        int b;
        while ((b = this.input.read()) != BencodeEncode.COLON_BYTE) {
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

}
