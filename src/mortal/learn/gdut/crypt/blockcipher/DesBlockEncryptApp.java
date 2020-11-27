package mortal.learn.gdut.crypt.blockcipher;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public class DesBlockEncryptApp extends JFrame{

    private final JFileChooser chooser = new JFileChooser();
    private final int length = 50;
    private DESBlockEncrypt des;
    private boolean encrypt = false;
    private int work_mode = 0;
    private int short_mode = 0;
    private Random random = new Random(System.currentTimeMillis());

    public DesBlockEncryptApp(){
        JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new GridBagLayout());

        chooser.setCurrentDirectory(new File("."));

        //组件
        JLabel k_label = new JLabel("密钥：");
        JTextField k_text = new JTextField("",length);
        panel.add(k_label, MyApp.get(1,0));
        panel.add(k_text, MyApp.get(0,100));
        k_text.setText(MyApp.bytes2string(MyApp.getBytes(random.nextLong())));

        JLabel IV_label = new JLabel("初始向量：");
        JTextField IV_text = new JTextField("",length);
        panel.add(IV_label, MyApp.get(1,0));
        panel.add(IV_text, MyApp.get(0,100));
        IV_text.setText(MyApp.bytes2string(MyApp.getBytes(random.nextLong())));

        JLabel WM_label = new JLabel("工作模式");
        JComboBox<String> WM_select = new JComboBox<>();
        WM_select.addItem("ECB");
        WM_select.addItem("CBC");
        WM_select.setSelectedItem("CBC");
        WM_select.setEditable(false);
        panel.add(WM_label, MyApp.get(1,0));
        panel.add(WM_select, MyApp.get(0,100));

        JLabel SBE_label = new JLabel("短块加密方法");
        JComboBox<String> SBE_select = new JComboBox<>();
        SBE_select.addItem("填充法");
        SBE_select.addItem("序列密码加密法");
        SBE_select.addItem("密文挪用技术");
        SBE_select.setSelectedItem("密文挪用技术");
        SBE_select.setEditable(false);
        panel.add(SBE_label, MyApp.get(1,0));
        panel.add(SBE_select, MyApp.get(0,100));

        JButton encrypt = new JButton("加密");
        JTextField e_text = new JTextField("",length);
        e_text.setEditable(false);
        panel.add(encrypt, MyApp.get(1,0));
        panel.add(e_text, MyApp.get(0,100));

        JButton decrypt = new JButton("解密");
        JTextField d_text = new JTextField("",length);
        d_text.setEditable(false);
        panel.add(decrypt, MyApp.get(1,0));
        panel.add(d_text, MyApp.get(0,100));

        JLabel i_label = new JLabel("信息");
        JTextField i_text = new JTextField("",length);
        i_text.setEditable(false);
        panel.add(i_label, MyApp.get(1,0));
        panel.add(i_text, MyApp.get(0,100));

        JLabel encrypt_label = new JLabel("加密内容");
        JLabel decrypt_label = new JLabel("解密结果");
        JTextArea encrypt_text = new JTextArea(length/4, length);
        JTextArea decrypt_text = new JTextArea(length/4, length);
        encrypt_text.setLineWrap(true);
        decrypt_text.setLineWrap(true);
        encrypt_text.setEditable(false);
        decrypt_text.setEditable(false);
        panel.add(new JScrollPane(encrypt_label), MyApp.get(1,0));
        panel.add(new JScrollPane(encrypt_text), MyApp.get(0,100));
        panel.add(new JScrollPane(decrypt_label), MyApp.get(1,0));
        panel.add(new JScrollPane(decrypt_text), MyApp.get(0,100));

        Runnable r = ()->{
            //获取密钥和初始向量
            byte[] key = MyApp.string2bytes(k_text.getText());
            byte[] IV = MyApp.string2bytes(IV_text.getText());

            if(null == key || null == IV){
                i_text.setText("请输入二进制");
                return ;
            }

            key = Arrays.copyOf(key, 8);
            IV = Arrays.copyOf(IV,8);

            this.des = new DESBlockEncrypt(MyApp.getLong(key), MyApp.getLong(key));

            String k_str = MyApp.bytes2string(key);
            String iv_str = MyApp.bytes2string(IV);

            k_text.setText(k_str);
            IV_text.setText(iv_str);

            //获取工作模式和短块加密方式
            String work_mode_str = WM_select.getItemAt(WM_select.getSelectedIndex());
            String short_mode_str = SBE_select.getItemAt(SBE_select.getSelectedIndex());
            switch(work_mode_str){
                case "ECB":
                    this.work_mode = DESBlockEncrypt.ECB;
                    break;
                case "CBC":
                    this.work_mode = DESBlockEncrypt.CBC;
                    break;
                default:
                    i_text.setText("工作模式复选框出错");
                    return;
            }
            switch (short_mode_str){
                case "填充法":
                    this.short_mode = DESBlockEncrypt.FILL;
                    break;
                case "序列密码加密法":
                    this.short_mode = DESBlockEncrypt.SCE;
                    break;
                case "密文挪用技术":
                    this.short_mode = DESBlockEncrypt.CET;
                    break;
                default:
                    i_text.setText("短块加密复选框出错");
                    return;
            }

            byte[] src;
            byte[] out;
            String message = "";
            //获取加解密文件，并加解密。
            int result = this.chooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                Path path = this.chooser.getSelectedFile().toPath();
                try {
                     src = Files.readAllBytes(path);
                     if(this.encrypt){
                         message += "保存加密结果？";
                         e_text.setText(path.toString());

                         out = this.des.encrypt(this.work_mode, this.short_mode, src);
                         encrypt_text.setText(new String(src, StandardCharsets.UTF_8));
                     }else{
                         message += "保存解密结果？";
                         d_text.setText(path.toString());
                         out = this.des.decrypt(this.work_mode, this.short_mode, src);
                         decrypt_text.setText(new String(out, StandardCharsets.UTF_8));
                     }
                } catch (IOException e) {
                    e.printStackTrace();
                    i_text.setText("读取文件出错");
                    return;
                }
            }else{
                i_text.setText("没有选中文件");
                return ;
            }

            result = JOptionPane.showConfirmDialog(this,message,"3118005434", JOptionPane.OK_CANCEL_OPTION);
            if(result == JOptionPane.OK_OPTION){
                result = this.chooser.showSaveDialog(this);
                if(result == JFileChooser.APPROVE_OPTION){
                    Path path = this.chooser.getSelectedFile().toPath();
                    try{
                        FileOutputStream fo = new FileOutputStream(path.toString());
                        fo.write(out);
                        fo.close();
                        i_text.setText("保存到：" + path.toString());
                    }catch (IOException e) {
                        e.printStackTrace();
                        i_text.setText("写入文件出错");
                    }
                }else{
                    i_text.setText("没有保存文件");
                }
            }
        };

        encrypt.addActionListener(event->{
            this.encrypt = true;
            r.run();
        });
        decrypt.addActionListener(event->{
            this.encrypt = false;
            r.run();
        });

        this.setTitle("短块加密与工作模式(DES)");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        this.setVisible(false);
    }
}
