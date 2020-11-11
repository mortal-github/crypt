package mortal.learn.gdut.crypt;

import mortal.learn.gdut.crypt.des.DesApp;
import mortal.learn.gdut.crypt.xor.XorApp;

import javax.swing.*;
import java.awt.*;

public class MyApp {

    public static void main(String[] args){
        EventQueue.invokeLater(()->{
            JFrame frame = new JFrame();
            frame.setTitle("密码学作业——3118005434钟景文，信息安全18(2)班");
            frame.setVisible(true);
            Dimension screen = frame.getToolkit().getScreenSize();
            frame.setBounds(screen.width/4, screen.height/4,screen.width/2,screen.height/2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel panel = new JPanel();
            frame.add(panel);

            JFrame xor = new XorApp();
            JFrame des = new DesApp();

            JButton xor_b = new JButton("异或加密");
            xor_b.addActionListener(event->xor.setVisible(true));
            panel.add(xor_b);

            JButton des_b = new JButton("DES加密");
            des_b.addActionListener(event->des.setVisible(true));
            panel.add(des_b);
        });
    }

    public static byte[] string2bytes(String binary){
        int length = binary.length();
        int temp = length % 8;
        length = length/8;
        if(0 != temp)
            length++;

        byte[] result = new byte[length];
        for(int i=0; i<binary.length(); i++){
            if('1' == binary.charAt(i)) {
                result[i/8] |= 1<<(i%8);
            }else if('0' != binary.charAt(i))
                return null;
        }
        return result;
    }
    public static String bytes2string(byte[] bytes) {
        char[] str = new char[bytes.length*8];
        for(int i=0; i<str.length; i++)
        {
            if( 0 != (bytes[i/8] & (1<<i%8))){
                str[i] = '1';
            }else{
                str[i] = '0';
            }
        }
        return new String(str);
    }

    public static byte[] getBytes(long value){
        byte[] result = new byte[8];
        for(int i=0; i<8; i++){
            result[i] = (byte)(value >> (i*8));
        }
        return result;
    }
    public static long getLong(byte[] value){
        assert value.length <=8;
        long result = 0;
        for(int i=0; i<value.length; i++){
            result |= (((long)value[i])&0xffL)<<(i*8);
        }
        return result;
    }

    public static GridBagConstraints get(int grid_width, int weight_x) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridheight = 1;
        constraints.gridwidth = grid_width;
        constraints.weighty = 0;
        constraints.weightx = weight_x;
        return constraints;
    }
}
