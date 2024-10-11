public enum BencodeType {
    DICTIONARY,
    STRING,
    NUMBER,
    LIST;

    static BencodeType from(String type){
        if(type.contains("String")){
            return STRING;
        }
        if(type.contains("Map")){
            return DICTIONARY;
        }if(type.contains("List")){
            return LIST;
        }if(type.contains("Long")){
            return NUMBER;
        }
        throw new RuntimeException("Not Type");
    }
}
