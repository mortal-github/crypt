package mortal.learn.gdut.crypt.rsa;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;

public class RsaApp extends JFrame {
    private RSA rsa ;//rsa RSA(int size, BigInteger e, Random random)
    private static final int length = 50;
    private static Random random = new Random(System.currentTimeMillis());

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public RsaApp(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        this.add(panel);

        //Swing 组件
        JLabel p_label = new JLabel("大素数 p:");
        JTextField p_text = new JTextField("",length);
        p_text.setEditable(false);
        panel.add(p_label, MyApp.get(2,0));
        panel.add(p_text, MyApp.get(0,100));

        JLabel q_label = new JLabel("大素数 q:");
        JTextField q_text = new JTextField("",length);
        q_text.setEditable(false);
        panel.add(q_label, MyApp.get(2,0));
        panel.add(q_text, MyApp.get(0,100));

        JLabel n_label = new JLabel("RSA模数 n:");
        JTextField n_text = new JTextField("",length);
        n_text.setEditable(false);
        panel.add(n_label, MyApp.get(2,0));
        panel.add(n_text, MyApp.get(0,100));

        JLabel euler_n_label = new JLabel("RSA欧拉函数 euler_n:");
        JTextField euler_n_text = new JTextField("",length);
        euler_n_text.setEditable(false);
        panel.add(euler_n_label, MyApp.get(2,0));
        panel.add(euler_n_text, MyApp.get(0,100));

        JLabel e_label = new JLabel("RSA加密密钥 e:");
        JTextField e_text = new JTextField("", length);
        e_text.setEditable(false);
        panel.add(e_label, MyApp.get(2,0));
        panel.add(e_text, MyApp.get(0,100));

        JLabel d_label = new JLabel("RSA解密密钥 d:");
        JTextField d_text = new JTextField("", length);
        d_text.setEditable(false);
        panel.add(d_label, MyApp.get(2,0));
        panel.add(d_text, MyApp.get(0,100));

        JLabel c_label = new JLabel("用分组位数构建RSA：");
        JComboBox<Integer> size_select = new JComboBox<>();
        for(int i=0; i<1000; i++)
            size_select.addItem(i+1);
        size_select.setSelectedIndex(130);
        size_select.setEditable(false);
        JTextField c_text = new JTextField("",length);
        c_text.setEditable(false);

        panel.add(c_label, MyApp.get(1,0));
        panel.add(size_select, MyApp.get(1,0));
        panel.add(c_text, MyApp.get(0,100));

        JLabel src_label = new JLabel("输入(加密输入字符串，解密输入Base64编码)：");
        JTextArea src_text = new JTextArea(length/2,length);
        src_text.setLineWrap(true);
        JScrollPane src_scroll = new JScrollPane(src_text);
        panel.add(src_label, MyApp.get(2,0));
        panel.add(src_scroll, MyApp.get(0,100));

        JButton e_b = new JButton("加密");
        JButton d_b = new JButton("解密");
        JTextField b_text = new JTextField("",length);
        b_text.setEditable(false);
        panel.add(e_b, MyApp.get(1,0));
        panel.add(d_b, MyApp.get(1,0));
        panel.add(b_text, MyApp.get(0,100));

        JLabel out_label = new JLabel("输出(加密输出Base64编码，解密输出字符串：");
        JTextArea out_text = new JTextArea(length/2,length);
        out_text.setEditable(false);
        out_text.setLineWrap(true);
        JScrollPane out_scroll = new JScrollPane(out_text);
        panel.add(out_label, MyApp.get(3,0));
        panel.add(out_scroll, MyApp.get(0,100));

        //事件处理
        size_select.addActionListener(event->{
            int size = size_select.getItemAt(size_select.getSelectedIndex());
            this.rsa = new RSA(size, BigInteger.valueOf(65537), this.random);
            p_text.setText(rsa.getP().toString());
            q_text.setText(rsa.getQ().toString());
            n_text.setText(rsa.n.toString());
            euler_n_text.setText(rsa.getEuler_n().toString());
            e_text.setText(rsa.e.toString());
            d_text.setText(rsa.getD().toString());
            c_text.setText("binary group size = " + size);
        });
        e_b.addActionListener(event->{
            String string = src_text.getText();
            byte[] bytes = string.getBytes();
            byte[] result = RSA.encrypt(bytes,rsa.e,rsa.n);
            String base64_result = encoder.encodeToString(result);
            out_text.setText(base64_result);
            b_text.setText("成功加密");
        });
        d_b.addActionListener(event->{
            String base64_string = src_text.getText();
            byte[] bytes;
            try{
                bytes = decoder.decode(base64_string);
            }catch (Throwable e){
                b_text.setText("输入的数据不是正确的base64编码");
                return;
            }

            byte[] result = rsa.decrypt(bytes);
            String string ;
            try{
                string = new String(result);
            }catch(Throwable e){
                b_text.setText("base64编码表示的二进制不是默认编码的字符串的二进制数据");
                return ;
            }

            out_text.setText(string);
            b_text.setText("解密成功");
        });
        //上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点上的拉法基萨芬了开发叫我雷锋去v阿斯利康带你离开丘吉尔法国v八二2123和即可还看见符号撒旦发射点
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
