package mortal.learn.gdut.crypt.xor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends JFrame{
    private JFileChooser filechooser = new JFileChooser();
    private byte[] key = new byte[]{1,2,3,4,5,6,7,8,9};
    private byte[] src = new byte[]{1,2,3,4,5,6,7,8,9};
    private byte[] out = new byte[]{1,2,3,4,5,6,7,8,9};

    private static int length = 64;

    private GridBagConstraints get(int gridheight, int gridwidth, int weighty, int weightx) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridheight = gridheight;
        constraints.gridwidth = gridwidth;
        constraints.weighty = weighty;
        constraints.weightx = weightx;
        return constraints;
    }
    public App(){
        filechooser.setCurrentDirectory(new File("."));
        JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new GridBagLayout());

        JLabel pw_lb = new JLabel("密钥：");
        JRadioButton pw_str = new JRadioButton("字符串", true);
        JRadioButton pw_bin = new JRadioButton("二进制串", false);
        JRadioButton pw_fle = new JRadioButton("数据文件",false);
        JTextField pw_tf = new JTextField("123456789",length);
        panel.add(pw_lb, get(1,1,0,0));
        panel.add(pw_str, get(1,1,0,0));
        panel.add(pw_bin, get(1,1,0,0));
        panel.add(pw_fle, get(1,1,0,0));
        panel.add(pw_tf, get(1,0,0,100));

        ButtonGroup pw_bg = new ButtonGroup();
        pw_bg.add(pw_str);
        pw_bg.add(pw_bin);
        pw_bg.add(pw_fle);

        JLabel da_lb = new JLabel("输入：");
        JRadioButton da_str = new JRadioButton("字符串", true);
        JRadioButton da_bin = new JRadioButton("二进制串", false);
        JRadioButton da_fle = new JRadioButton("数据文件",false);
        JTextField da_tf = new JTextField("123456789", length);
        panel.add(da_lb, get(1,1,0,0));
        panel.add(da_str, get(1,1,0,0));
        panel.add(da_bin, get(1,1,0,0));
        panel.add(da_fle, get(1,1,0,0));
        panel.add(da_tf, get(1,0,0,100));

        ButtonGroup da_bg = new ButtonGroup();
        da_bg.add(da_bin);
        da_bg.add(da_str);
        da_bg.add(da_fle);

        JLabel out_lb = new JLabel("输出：");
        JButton crypt = new JButton("加密/解密");
        JTextField out_tf = new JTextField("123456789",length);
        panel.add(out_lb,get(1,1,0,0));
        panel.add(crypt,get(1,3,0,0));
        panel.add(out_tf,get(1,0,0,100));

        pw_fle.addActionListener(event->{
            int result = this.filechooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                Path path = this.filechooser.getSelectedFile().toPath();
                try {
                    this.key = Files.readAllBytes(path);
                    pw_tf.setText("file://" + path.toString());
                    pw_tf.setEditable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        pw_str.addActionListener(event->{
            pw_tf.setEditable(true);
        });
        pw_bin.addActionListener(event->{
            pw_tf.setEditable(true);
        });
        da_fle.addActionListener(event->{
            int result = this.filechooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                Path path = this.filechooser.getSelectedFile().toPath();
                try {
                    this.src = Files.readAllBytes(path);
                    da_tf.setText("file://" + path.toString());
                    da_tf.setEditable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        da_str.addActionListener(event->{
            da_tf.setEditable(true);
        });
        da_bin.addActionListener(event->{
            da_tf.setEditable(true);
        });

        crypt.addActionListener(event->{
            if(pw_str.isSelected())
                this.key = pw_tf.getText().getBytes();
            else if(pw_bin.isSelected())
                this.key = string2bytes(pw_tf.getText());

            if(da_str.isSelected())
                this.src = da_tf.getText().getBytes();
            else if(da_bin.isSelected())
                this.src = string2bytes(da_tf.getText());

            if(this.key == null || this.src == null){
                out_tf.setText("没有可用数据用来加密，机密。");
                return;
            }
           
            this.out = XOR.xor(this.key, this.src);
            String result = null;

            if(da_str.isSelected())
                result = new String(this.out);
            else
                result = new String(bytes2string(this.out));


            out_tf.setText(result);
        });

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setVisible(false);
    }
    private byte[] string2bytes(String binary){
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
    private String bytes2string(byte[] bytes)
    {
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
}
