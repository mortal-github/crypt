package mortal.learn.gdut.crypt.des;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public class DesApp extends JFrame {
    private static final int length = 50;
    public boolean decrypt = false;
    public DesApp(){
        JPanel panel = new JPanel();
        this.add(panel);
        //组件
        JLabel key_label = new JLabel("二进制密钥：");
        JTextField key_input = new JTextField("101010101010",length);

        JLabel src_label = new JLabel("输入");
        JTextField src_input = new JTextField("1010101010101",length);

        JLabel IP_label = new JLabel("初始置换：");
        JTextField IP_output = new JTextField(length);
        IP_output.setEditable(false);

        JLabel R_label = new JLabel("计算轮次：");
        JComboBox<Integer> R_select = new JComboBox<>();
        for(int i=0; i<16; i++)
            R_select.addItem(i+1);
        R_select.setSelectedIndex(15);
        R_select.setEditable(false);
        JTextField R_output = new JTextField(length);
        R_output.setEditable(false);

        JLabel IP__label = new JLabel("逆初始置换：");
        JTextField IP__output = new JTextField(length);
        IP__output.setEditable(false);

        JButton encrypt = new JButton("加密");
        JButton decrypt = new JButton("解密");
        JTextField result = new JTextField(length);
        result.setEditable(false);
        //布局
        panel.setLayout(new GridBagLayout());
        panel.add(key_label, MyApp.get(2,0));
        panel.add(key_input, MyApp.get(0,100));
        panel.add(src_label, MyApp.get(2,0));
        panel.add(src_input, MyApp.get(0,100));
        panel.add(IP_label, MyApp.get(2,0));
        panel.add(IP_output, MyApp.get(0,100));
        panel.add(R_label, MyApp.get(1,0));
        panel.add(R_select,MyApp.get(1,100));
        panel.add(R_output, MyApp.get(0,100));
        panel.add(IP__label, MyApp.get(2,0));
        panel.add(IP__output, MyApp.get(0,100));
        panel.add(encrypt, MyApp.get(1,0));
        panel.add(decrypt, MyApp.get(1,0));
        panel.add(result, MyApp.get(0,100));

        //事件处理
        Consumer<ActionEvent> run = event->{
            byte[] keys = MyApp.string2bytes(key_input.getText());
            if(null == keys) {
                result.setText("密码应该输入二进制。");
                return;
            }
            if(keys.length > 8){
                result.setText("密钥不应该超过64位。");
                return;
            }
            byte[] src_s = MyApp.string2bytes(src_input.getText());
            if(null == src_s){
                result.setText("数据应该输入二进制。");
                return;
            }
            if(src_s.length > 8){
                result.setText("数据不应该超过64位。");
                return;
            }
            //获取完整密钥
            long key = MyApp.getLong(keys);
            key_input.setText(MyApp.bytes2string(MyApp.getBytes(key)));
            //获取完整输入
            long src = MyApp.getLong(src_s);
            src_input.setText(MyApp.bytes2string(MyApp.getBytes(src)));
            //获取初始置换结果
            long IP = DES.IP(src);
            IP_output.setText(MyApp.bytes2string(MyApp.getBytes(IP)));
            //获取n轮加密
            long[] subKeys = DES.getSubKeys(key);
            int r = R_select.getItemAt(R_select.getSelectedIndex());
            subKeys = Arrays.copyOf(subKeys,r);
            if(this.decrypt){
                for(int i=0; i<subKeys.length/2; i++){
                    long temp = subKeys[i];
                    subKeys[i] = subKeys[subKeys.length-1-i];
                    subKeys[subKeys.length-1-i] = temp;
                }
            }
            long n = DES.R(src,subKeys);
            IP__output.setText(MyApp.bytes2string(MyApp.getBytes(n)));
            n = DES.IP(n);
            R_output.setText(MyApp.bytes2string(MyApp.getBytes(n)));

            //加解密。
            long[] crypt;
            if(this.decrypt){
                crypt = DES.decrypt(key, src);
            }else{
                crypt = DES.encrypt(key, src);
            }
            result.setText(MyApp.bytes2string(MyApp.getBytes(crypt[0])));
        };
        encrypt.addActionListener(event->{
            this.decrypt =false;
            run.accept(event);
        });
        decrypt.addActionListener(event->{
            this.decrypt = true;
            run.accept(event);
        });
        R_select.addActionListener(run::accept);

        this.setTitle("DES加密分析");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        this.setVisible(false);
    }
}
