package mortal.learn.gdut.crypt;

import mortal.learn.gdut.crypt.xor.XOR;

import javax.swing.*;
import java.awt.*;

public class App {

    public static void main(String[] args){
        EventQueue.invokeLater(()->{
            JFrame frame = new JFrame();
            frame.setTitle("密码学作业——3118005434钟景文，信息安全18(2)班");
            frame.setVisible(true);
            Dimension screen = frame.getToolkit().getScreenSize();
            frame.setBounds(screen.width/4, screen.height/4,screen.width/2,screen.height/2);
            JPanel panel = new JPanel();
            frame.add(panel);

            JFrame xor = new mortal.learn.gdut.crypt.xor.App();



            JButton xor_b = new JButton("异或加密");
            xor_b.addActionListener(event->{
                xor.setVisible(true);
            });
            panel.add(xor_b);

        });
    }

}
