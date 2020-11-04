package mortal.learn.gdut.crypt.xor;

import javax.swing.*;
import java.util.Objects;

public class XOR extends JFrame {
    private byte[] key = null;

    public XOR(String key){
        Objects.requireNonNull(key);
        this.key = key.getBytes();
    }

    public byte[] encrypt(byte[] plaintext){
        byte[] ciphertext = new byte[plaintext.length];
        for(int i=0, j=0; i<plaintext.length; i++){
            ciphertext[i] = (byte) (this.key[j] ^ plaintext[i]);
            j++;
            if(key.length == j)
                j = 0;
        }
        return ciphertext;
    }
}
