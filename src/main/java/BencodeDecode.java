import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BencodeDecode {
    private Integer current=0;
    public Object decode(String bencodedString){
        if(Character.isDigit(bencodedString.charAt(current))){
            return decodeString(bencodedString);
        }else if(bencodedString.charAt(current) == 'i'){
            return decodeNumber(bencodedString);
        }else if(bencodedString.charAt(current) =='l'){
            return decodeList(bencodedString);
        }else if(bencodedString.charAt(current) =='d'){
            return decodeMap(bencodedString);
        }else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

    private List<Object> decodeList(String toDecode){
        List<Object> decodeds= new ArrayList<>();
        current++;
        while (toDecode.charAt(current) != 'e'){
            decodeds.add(decode(toDecode));
        }
        current++;
        return decodeds;
    }

    private Map<Object, Object> decodeMap(String toDecode){
        Map<Object, Object> decodeds= new HashMap<>();
        current++;
        while (toDecode.charAt(current) != 'e'){
            decodeds.put(decode(toDecode), decode(toDecode));
        }
        current++;
        return decodeds;
    }

    private Long decodeNumber(String encodedValue) {
        int start = current + 1, end = 0;
        for (int i = start; i < encodedValue.length(); i++) {
            if (encodedValue.charAt(i) == 'e') {
                end = i;
                break;
            }
        }
        current = end + 1;
        return Long.parseLong(encodedValue.substring(start, end));
    }

    private String decodeString(String encodedValue) {
        int delimeterIndex = 0;
        for (int i = current; i < encodedValue.length(); i++) {
            if (encodedValue.charAt(i) == ':') {
                delimeterIndex = i;
                break;
            }
        }
        int length =
                Integer.parseInt(encodedValue.substring(current, delimeterIndex));
        int start = delimeterIndex + 1, end = start + length;
        current = end;
        return encodedValue.substring(start, end);
    }
}
