package mortal.learn.gdut.crypt.xor;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

public class XOR {
    public static byte[] xor(byte[]key, byte[] src){
        byte[] out = new byte[src.length];
        for(int i=0, j=0; i<src.length; i++){
            out[i] = (byte) (key[j] ^ src[i]);
            j++;
            if(key.length == j)
                j = 0;
        }
        return out;
    }
}
