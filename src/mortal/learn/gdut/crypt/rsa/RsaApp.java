package mortal.learn.gdut.crypt.rsa;

import javax.swing.*;
import java.awt.*;

public class RsaApp extends JFrame {

    public RsaApp(){
        JPanel panel = new JPanel();
        this.add(panel);

        JLabel p_label = new JLabel("p:");


        //设置jframe框架。
        this.setTitle("RSA公钥算法");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        this.setVisible(false);
    }
}
