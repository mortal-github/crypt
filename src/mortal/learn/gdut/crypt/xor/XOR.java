package mortal.learn.gdut.crypt.xor;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;

public class XOR {
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

    public byte[] decrpyt(byte[] ciphertext){
        byte[] plaintext = new byte[ciphertext.length];
        for(int i=0, j=0; i<ciphertext.length; i++){
            plaintext[i] = (byte)(this.key[j] ^ ciphertext[i]);
            j++;
            if(key.length == j)
                j = 0;
        }
        return plaintext;
    }

    public byte[] getKey(){
        return Arrays.copyOf(this.key,this.key.length);
    }
    public void setKey(String key){
        Objects.requireNonNull(key);
        this.key = key.getBytes();
    }
}
