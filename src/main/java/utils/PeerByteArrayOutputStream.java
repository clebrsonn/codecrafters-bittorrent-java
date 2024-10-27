package utils;

import java.io.ByteArrayOutputStream;

public class PeerByteArrayOutputStream extends ByteArrayOutputStream {

    public byte[] getBuffer(){
        return buf;
    }
}
