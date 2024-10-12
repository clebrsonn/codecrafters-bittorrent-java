import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
        StringBuilder builder = new StringBuilder();
        builder.append("l");
        toEncode.stream().sorted().forEach(o -> builder.append(encode(o, BencodeType.from(o.getClass().getName()))));
        builder.append("e");
        return  builder.toString().getBytes();
    }
    byte[] encodeDic(Map<String, Object> toEncode){
        StringBuilder builder= new StringBuilder();
        return ("d" + new TreeMap<>(toEncode).entrySet().stream().map(o -> {
                    builder.append(encode(o.getKey(), BencodeType.STRING));
                    builder.append(encode(o.getValue(), BencodeType.from(o.getValue().getClass().getName())));
                    return builder;
                }).collect(Collectors.joining())+"e").getBytes();
    }

    byte[] encodeString(Object bencodeDecoded){
     return (((String) bencodeDecoded).length() + ":" + bencodeDecoded).getBytes();
    }

    byte[] encodeNumber(Object bencodeDecoded){
        return ("i" + bencodeDecoded + "e").getBytes();
    }
}
