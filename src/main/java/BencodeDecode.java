import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class BencodeDecode {
    private final PushbackInputStream input;

    public BencodeDecode(InputStream inputStream) {
        this.input = new PushbackInputStream(inputStream, 1); // Buffer de 1 byte para pushback
    }

    public Object decode() throws IOException {
        int prefix = this.input.read();
        if(Character.isDigit(prefix)){
            return decodeString(prefix);
        }else if(prefix == 'i'){
            return decodeNumber();
        }else if(prefix =='l'){
            return decodeList();
        }else if(prefix =='d'){
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

    private Map<Object, Object> decodeMap() throws IOException{
        Map<Object, Object> decodeds= new TreeMap<>();


        while (true) {
            int next = this.input.read();
            if (next == 'e') {
                break; // Fim do dicionário
            } else {
                this.input.unread(next);
                // Decodificar chave (que sempre será uma string)
                String key = new String(decodeString(this.input.read()), "UTF-8");
                // Decodificar valor (que pode ser qualquer tipo)
                Object value = decode();
                decodeds.put(key, value);
            }
        }
        return decodeds;
    }

    private Long decodeNumber() throws IOException{
        StringBuilder number = new StringBuilder();
        int b;
        while ((b = this.input.read()) != 'e') {
            number.append((char) b);
        }
        return Long.parseLong(number.toString());
    }

    private byte[] decodeString(int firstDigit) throws IOException {
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
}
