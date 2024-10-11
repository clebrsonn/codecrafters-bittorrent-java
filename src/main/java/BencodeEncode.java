import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BencodeEncode {

    public String encode(Object bencodeDecoded, BencodeType type){

        if(BencodeType.LIST.equals(type)){
            return encodeList((List<Object>)bencodeDecoded);
        }else if(BencodeType.DICTIONARY.equals(type)){
            return encodeDic((Map<String, Object>)bencodeDecoded);
        }if(BencodeType.STRING.equals(type)){
            return encodeString(bencodeDecoded);
        }else if(BencodeType.NUMBER.equals(type)){
            return encodeNumber(bencodeDecoded);

        }else{
            throw new RuntimeException("Unknown type");
        }

    }

    String encodeList(List<Object> toEncode){
        return toEncode.stream().map(o -> ("l" +encode(o, BencodeType.valueOf(o.getClass().getName()))+"e")).collect(Collectors.joining());
    }
    String encodeDic(Map<String, Object> toEncode){
        return toEncode.entrySet().stream().map(o -> ("d" +encode(o.getKey(), BencodeType.STRING) +encode(o.getValue(), BencodeType.valueOf(o.getValue().getClass().getName()))+"e")).collect(Collectors.joining());
    }

    String encodeString(Object bencodeDecoded){
     return ((String) bencodeDecoded).length() + ":" + bencodeDecoded + "e";
    }

    String encodeNumber(Object bencodeDecoded){
        return "i" + bencodeDecoded + "e";
    }
}
