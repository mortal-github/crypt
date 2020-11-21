package mortal.learn.gdut.crypt.elgamal;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;

public class ELGamalApp extends JFrame {
    private ELGamal elgamal;
    private BigInteger e;
    private int length = 15;
    private Random random = new Random(System.currentTimeMillis());

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public ELGamalApp(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        this.add(panel);

        //组件
        JLabel size_label = new JLabel("素数 p 的数量级");
        JComboBox<Integer> size_select = new JComboBox<>();
        for(int i=0; i<2; i++)
            size_select.addItem(i+1);
        size_select.setSelectedIndex(1);
        size_select.setEditable(false);
        panel.add(size_label, MyApp.get(1,0));
        panel.add(size_select, MyApp.get(0,100));

        JLabel p_label = new JLabel("大素数 p:");
        JTextField p_text = new JTextField("",length*2);
        p_text.setEditable(false);
        panel.add(p_label, MyApp.get(1,0));
        panel.add(p_text, MyApp.get(0,100));

        JLabel g_label = new JLabel("本原元 p:");
        JTextField g_text = new JTextField("",length*2);
        g_text.setEditable(false);
        panel.add(g_label, MyApp.get(1,0));
        panel.add(g_text, MyApp.get(0,100));

        JLabel d_label = new JLabel("随机私钥 d:");
        JTextField d_text = new JTextField("",length*2);
        d_text.setEditable(false);
        panel.add(d_label, MyApp.get(1,0));
        panel.add(d_text, MyApp.get(0,100));

        JLabel y_label = new JLabel("y = g^d mod p:");
        JTextField y_text = new JTextField("",length*2);
        y_text.setEditable(false);
        panel.add(y_label, MyApp.get(1,0));
        panel.add(y_text, MyApp.get(0,100));

        JLabel e_label = new JLabel("加密用随机数 e:");
        JTextField e_text = new JTextField("",length*2);
        e_text.setEditable(false);
        panel.add(e_label, MyApp.get(1,0));
        panel.add(e_text, MyApp.get(0,100));

        size_select.addActionListener(event->{
            int size = size_select.getItemAt(size_select.getSelectedIndex());
            this.elgamal = ELGamal.getELGamal(size,50,random);

            BigInteger d;
            do{
                do{
                    d = MyApp.getPrime(size,50,random);
                }while(null == d);
                d = d.mod(this.elgamal.p);
            }while( 1 != d.compareTo(BigInteger.ONE));

            BigInteger e;
            do{
                do{
                    e = MyApp.getPrime(size,50,random);
                }while(null == e);
                e = e.mod(this.elgamal.p);
            }while( 1 != e.compareTo(BigInteger.ONE));
            this.e = e;

            this.elgamal.getY(d);
            p_text.setText(this.elgamal.p.toString());
            g_text.setText(this.elgamal.g.toString());
            d_text.setText(d.toString());
            y_text.setText(this.elgamal.y.toString());
            e_text.setText(e.toString());
        });

        JButton e_b = new JButton("加密");
        JLabel  c_l = new JLabel("密文");
        JLabel  p_l = new JLabel("解密密文");
        panel.add(e_b, MyApp.get(1,0));
        panel.add(c_l, MyApp.get(1,0));
        panel.add(p_l, MyApp.get(0,0));

        JTextArea message = new JTextArea(length, length);
        JTextArea cipher = new JTextArea(length, length);
        JTextArea plain = new JTextArea(length,length);
        message.setLineWrap(true);
        cipher.setLineWrap(true);
        plain.setLineWrap(true);
        cipher.setEditable(false);
        plain.setEditable(false);

        panel.add(new JScrollPane(message), MyApp.get(1,0));
        panel.add(new JScrollPane(cipher), MyApp.get(1,0));
        panel.add(new JScrollPane(plain), MyApp.get(1,0));

        e_b.addActionListener(event->{
            String string = message.getText();
            byte[] bytes = string.getBytes();

            byte[][] c1c2 = ELGamal.encrypt(bytes,ELGamalApp.this.e,
                    ELGamalApp.this.elgamal.y, ELGamalApp.this.elgamal.p,ELGamalApp.this.elgamal.g);
            //密文
            BigInteger C1 = new BigInteger(c1c2[0]);
            String base64_c2 = ELGamalApp.this.encoder.encodeToString(c1c2[1]);
            //解密密文
            bytes = ELGamalApp.this.elgamal.decrypt(c1c2);
            string = new String(bytes);

            cipher.setText("C1 : ");
            cipher.append("\n");
            cipher.append(C1.toString());
            cipher.append("\n");
            cipher.append("C2 : Base64编码");
            cipher.append("\n");
            cipher.append(base64_c2);

            plain.setText(string);
        });

        //设置jframe框架。
        this.setTitle("ELGamal 公钥算法");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        this.setVisible(false);
    }
}
