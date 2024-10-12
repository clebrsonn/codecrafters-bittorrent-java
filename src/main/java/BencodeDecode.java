import java.math.BigDecimal;
import java.util.*;

public class BencodeDecode {
    private Integer current=0;
    public Object decode(byte[] bencodedString){
        if(Character.isDigit((char) bencodedString[current])){
            return decodeString(bencodedString);
        }else if((char) bencodedString[current] == 'i'){
            return decodeNumber(bencodedString);
        }else if((char) bencodedString[current] =='l'){
            return decodeList(bencodedString);
        }else if((char) bencodedString[current] =='d'){
            return decodeMap(bencodedString);
        }else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

    private List<Object> decodeList(byte[] toDecode){
        List<Object> decodeds= new ArrayList<>();
        current++;
        while ((char) toDecode[current] != 'e'){
            decodeds.add(decode(toDecode));
        }
        current++;
        return decodeds;
    }

    private Map<Object, Object> decodeMap(byte[] toDecode){
        Map<Object, Object> decodeds= new TreeMap<>();
        current++;
        while ((char) toDecode[current] != 'e'){
            decodeds.put(decode(toDecode), decode(toDecode));
        }
        current++;
        return decodeds;
    }

    private Long decodeNumber(byte[] encodedValue) {
        int start = current + 1, end = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = start; i < encodedValue.length; i++) {
            if ((char) encodedValue[i] != 'e') {
                buffer.append((char) encodedValue[i]);

            }else{
                end =i;
                break;
            }
        }
        current = end + 1;

        return new BigDecimal(buffer.toString()).longValue();
    }

    private String decodeString(byte[] encodedValue) {
        int delimeterIndex = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = current; i < encodedValue.length; i++) {
            if ((char)encodedValue[i] != ':') {

                buffer.append((char)encodedValue[i]);
            }else{
                delimeterIndex = i;
                break;
            }
        }
        int length =
                new BigDecimal(buffer.toString()).intValue();
        int start = delimeterIndex + 1, end = start + length;
        current = end;

        StringBuilder buffer2 = new StringBuilder();
        for (int i = start; i < end; i++) {
            buffer2.append((char)encodedValue[i]);
        }

        return buffer2.toString();
    }
}
