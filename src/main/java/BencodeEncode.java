import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BencodeEncode {
    private final OutputStream output;

    public static final byte INTEGER_BYTE = 'i';
    public static final byte COLON_BYTE = ':';
    public static final byte END_BYTE = 'e';
    public static final byte LIST_BYTE = 'l';
    public static final byte MAP_BYTE = 'd';

    public BencodeEncode(OutputStream output) {
        this.output = output;
    }

    public void encode(Object root) {
        try {

            switch (root) {
                case String string -> encodeString(string);
                case Long number -> encodeNumber(number);
                case Integer number -> encodeNumber(Integer.toUnsignedLong(number));
                case Short number -> encodeNumber(Short.toUnsignedInt(number));
                case Byte number -> encodeNumber(Byte.toUnsignedInt(number));
                case List<?> list -> encodeList(list);
                case Map<?, ?> map -> encodeDic(map);
                case null, default ->
                        throw new IllegalArgumentException("Tipo de dado não suportado para codificação.");
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void encodeList(List<?> toEncode) throws IOException {
        output.write(LIST_BYTE);

        for (final var element : toEncode) {
            encode(element);
        }

        output.write(END_BYTE);
    }

    void encodeDic(Map<?, ?> toEncode) throws IOException {
        // Ordena o dicionário
        output.write(MAP_BYTE);

        for (final var entry : toEncode.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue();

            encodeString((String) key);
            encode(value);
        }

        output.write(END_BYTE);
    }

    void encodeString(String bencodeDecoded) throws IOException {
        output.write(String.valueOf(bencodeDecoded.length()).getBytes());
        output.write(COLON_BYTE);
        output.write(bencodeDecoded.getBytes(StandardCharsets.ISO_8859_1));
    }

    private void encodeByteArray(byte[] bytes) throws IOException {
        output.write(bytes.length);
        output.write(COLON_BYTE);

        output.write(bytes);
    }

    void encodeNumber(Number bencodeDecoded) throws IOException {
        output.write(INTEGER_BYTE);
        output.write(String.valueOf(bencodeDecoded).getBytes());
        output.write(END_BYTE);
    }
}
