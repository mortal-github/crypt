package mortal.learn.gdut.crypt.xor;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XorApp extends JFrame{
    private final JFileChooser chooser = new JFileChooser();
    private byte[] key = new byte[]{1,2,3,4,5,6,7,8,9};
    private byte[] src = new byte[]{1,2,3,4,5,6,7,8,9};
    private byte[] out = new byte[]{1,2,3,4,5,6,7,8,9};

    public XorApp(){
        chooser.setCurrentDirectory(new File("."));
        JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new GridBagLayout());

        JLabel pw_lb = new JLabel("密钥：");
        JRadioButton pw_str = new JRadioButton("字符串", true);
        JRadioButton pw_bin = new JRadioButton("二进制串", false);
        JRadioButton pw_fle = new JRadioButton("数据文件",false);
        JTextField pw_tf = new JTextField("123456789",50);
        panel.add(pw_lb, MyApp.get(1,0));
        panel.add(pw_str, MyApp.get(1,0));
        panel.add(pw_bin, MyApp.get(1,0));
        panel.add(pw_fle, MyApp.get(1,0));
        panel.add(pw_tf, MyApp.get(0,100));

        ButtonGroup pw_bg = new ButtonGroup();
        pw_bg.add(pw_str);
        pw_bg.add(pw_bin);
        pw_bg.add(pw_fle);

        JLabel da_lb = new JLabel("输入：");
        JRadioButton da_str = new JRadioButton("字符串", true);
        JRadioButton da_bin = new JRadioButton("二进制串", false);
        JRadioButton da_fle = new JRadioButton("数据文件",false);
        JTextField da_tf = new JTextField("123456789", 50);
        panel.add(da_lb, MyApp.get(1,0));
        panel.add(da_str, MyApp.get(1,0));
        panel.add(da_bin, MyApp.get(1,0));
        panel.add(da_fle, MyApp.get(1,0));
        panel.add(da_tf, MyApp.get(0,100));

        ButtonGroup da_bg = new ButtonGroup();
        da_bg.add(da_bin);
        da_bg.add(da_str);
        da_bg.add(da_fle);

        JLabel out_lb = new JLabel("输出：");
        JButton crypt = new JButton("加密/解密");
        JTextField out_tf = new JTextField("123456789",50);
        panel.add(out_lb,MyApp.get(1,0));
        panel.add(crypt,MyApp.get(3,0));
        panel.add(out_tf,MyApp.get(0,100));

        pw_fle.addActionListener(event->{
            int result = this.chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                Path path = this.chooser.getSelectedFile().toPath();
                try {
                    this.key = Files.readAllBytes(path);
                    pw_tf.setText("file://" + path.toString());
                    pw_tf.setEditable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        pw_str.addActionListener(event->pw_tf.setEditable(true));
        pw_bin.addActionListener(event->pw_tf.setEditable(true));
        da_fle.addActionListener(event->{
            int result = this.chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                Path path = this.chooser.getSelectedFile().toPath();
                try {
                    this.src = Files.readAllBytes(path);
                    da_tf.setText("file://" + path.toString());
                    da_tf.setEditable(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        da_str.addActionListener(event->da_tf.setEditable(true));
        da_bin.addActionListener(event->da_tf.setEditable(true));

        crypt.addActionListener(event->{
            if(pw_str.isSelected())
                this.key = pw_tf.getText().getBytes();
            else if(pw_bin.isSelected())
                this.key = MyApp.string2bytes(pw_tf.getText());

            if(da_str.isSelected())
                this.src = da_tf.getText().getBytes();
            else if(da_bin.isSelected())
                this.src = MyApp.string2bytes(da_tf.getText());

            if(this.key == null || this.src == null){
                out_tf.setText("没有可用数据用来加密，机密。");
                return;
            }
           
            this.out = XOR.xor(this.key, this.src);
            String result;

            if(da_str.isSelected())
                result = new String(this.out);
            else
                result = MyApp.bytes2string(this.out);

            out_tf.setText(result);
        });

        this.setTitle("异或加密字符串或文件。");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        this.setVisible(false);
    }
}
