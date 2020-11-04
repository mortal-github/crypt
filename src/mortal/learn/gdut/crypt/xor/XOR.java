package mortal.learn.gdut.crypt.xor;

import javax.swing.*;
import java.util.Objects;

public class XOR extends JFrame {
    private byte[] key = null;

    public XOR(String key){
        Objects.requireNonNull(key);
        this.key = key.getBytes();
    }
}
