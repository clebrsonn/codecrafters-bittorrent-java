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
        return "l" +toEncode.stream().map(o -> (encode(o, BencodeType.from(o.getClass().getName())))).collect(Collectors.joining())+"e";
    }
    String encodeDic(Map<String, Object> toEncode){
        return "d" + toEncode.entrySet().stream().map(o -> (encode(o.getKey(), BencodeType.STRING) +encode(o.getValue(), BencodeType.from(o.getValue().getClass().getName())))).collect(Collectors.joining())+"e";
    }

    String encodeString(Object bencodeDecoded){
     return ((String) bencodeDecoded).length() + ":" + bencodeDecoded;
    }

    String encodeNumber(Object bencodeDecoded){
        return "i" + bencodeDecoded + "e";
    }
}
