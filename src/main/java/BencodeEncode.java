import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BencodeEncode {

    public byte[] encode(Object bencodeDecoded, BencodeType type){

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

    byte[] encodeList(List<Object> toEncode){
        return "l" +toEncode.stream().sorted().map(o -> (encode(o, BencodeType.from(o.getClass().getName())))).collect(Collectors.joining())+"e";
    }
    byte[] encodeDic(Map<String, Object> toEncode){
        StringBuilder builder= new StringBuilder();
        return ("d" + toEncode.entrySet().stream().map(o -> {
                    builder.append(encode(o.getKey(), BencodeType.STRING));
                    builder.append(encode(o.getValue(), BencodeType.from(o.getValue().getClass().getName())));
                    return builder;
                }).sorted().collect(Collectors.joining())+"e").getBytes();
    }

    byte[] encodeString(Object bencodeDecoded){
     return (((String) bencodeDecoded).length() + ":" + bencodeDecoded).getBytes();
    }

    byte[] encodeNumber(Object bencodeDecoded){
        return ("i" + bencodeDecoded + "e").getBytes();
    }
}
