import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BencodeEncode {
    private final OutputStream output;

    public BencodeEncode(OutputStream output) {
        this.output = output;
    }

    public void encode(Object bencodeDecoded) throws IOException {

        switch (bencodeDecoded) {
            case String s -> encodeString(s);
            case Number number -> encodeNumber(number);
            case List<?> list -> encodeList(list);
            case Map<?, ?> map -> encodeDic(map);
            case byte[] bytes -> encodeByteArray(bytes);
            case null, default -> throw new IllegalArgumentException("Tipo de dado não suportado para codificação.");
        }

    }

    private void encodeList(List<?> toEncode) throws IOException {
        output.write('l');
        for (Object item : toEncode) {
            encode(item);
        }
        output.write('e');
    }

    void encodeDic(Map<?, ?> toEncode) throws IOException {
        // Ordena o dicionário
        Map<String, Object> sortedDict = new TreeMap<>();
        for (Map.Entry<?, ?> entry : toEncode.entrySet()) {
            sortedDict.put(entry.getKey().toString(), entry.getValue());
        }

        output.write('d');
        for (Map.Entry<String, Object> entry : sortedDict.entrySet()) {
            encodeString(entry.getKey());
            encode(entry.getValue());
        }
        output.write('e');
    }

    void encodeString(String bencodeDecoded) throws IOException {
        byte[] bytes = bencodeDecoded.getBytes(StandardCharsets.ISO_8859_1);
        output.write((bytes.length + ":").getBytes(StandardCharsets.ISO_8859_1));
        output.write(bytes);
    }

    private void encodeByteArray(byte[] bytes) throws IOException {
        output.write((bytes.length + ":").getBytes(StandardCharsets.ISO_8859_1));
        output.write(bytes);
    }

    void encodeNumber(Number bencodeDecoded) throws IOException {
        output.write(("i" + bencodeDecoded + "e").getBytes(StandardCharsets.ISO_8859_1));
    }
}
