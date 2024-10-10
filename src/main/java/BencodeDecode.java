import java.util.ArrayList;
import java.util.List;

public class BencodeDecode {
    private Integer currenIdx = 0;

    public Object decode(String bencodedString){
        if(Character.isDigit(bencodedString.charAt(0))){
            return decodeToString(bencodedString);
        }else if(bencodedString.startsWith("i")){
            return decodeToNumber(bencodedString);
        }else if(bencodedString.startsWith("l")){
            return decodeToList(bencodedString);
        }else {
            throw new RuntimeException("Only strings are supported at the moment");
        }
    }

    private List<Object> decodeToList(String toDecode){
        List<Object> decodeds= new ArrayList<>();
        while (toDecode.charAt(currenIdx) != 'e'){
            currenIdx+=1;
            decodeds.add(decode(toDecode.substring(currenIdx)));
        }
        return decodeds;
    }

    private Long decodeToNumber(String toDecode){
        currenIdx= toDecode.indexOf("e")+1;
        return Long.parseLong(toDecode.substring(1,currenIdx-1));
    }


    private String decodeToString(String toDecode){
        int firstColonIndex = toDecode.indexOf(":");
        int length = Integer.parseInt(toDecode.substring(0, firstColonIndex));
        currenIdx=firstColonIndex+1+length;
        return toDecode.substring(firstColonIndex+1, currenIdx);
    }
}
