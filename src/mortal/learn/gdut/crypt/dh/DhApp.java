package mortal.learn.gdut.crypt.dh;

import mortal.learn.gdut.crypt.MyApp;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.Random;

public class DhApp extends JFrame {
    private int length = 25;
    private DiffieHellman dh_left;
    private DiffieHellman dh_right;
    private Random random = new Random(System.currentTimeMillis());

    public DhApp(){
        JPanel dh_panel = new JPanel();
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        dh_panel.setLayout(new GridBagLayout());
        left.setLayout(new GridBagLayout());
        right.setLayout(new GridBagLayout());
        this.setLayout(new GridBagLayout());
        this.add(dh_panel,MyApp.get(0,100));
        this.add(left, MyApp.get(1,100));
        this.add(right, MyApp.get(0,100));

        //Swing组件
        JLabel p_label = new JLabel("大素数 p:");
        JComboBox<Integer> size_select = new JComboBox<>();
        for(int i=0; i<2; i++)
            size_select.addItem(i+1);
        size_select.setSelectedIndex(1);
        size_select.setEditable(false);
        JTextField p_text = new JTextField("",length);
        p_text.setEditable(false);
        dh_panel.add(p_label,MyApp.get(1,0));
        dh_panel.add(size_select, MyApp.get(1,0));
        dh_panel.add(p_text, MyApp.get(0,100));

        JLabel g_label = new JLabel("(最小)本原元 g:");
        JTextField g_text = new JTextField("",length);
        g_text.setEditable(false);
        dh_panel.add(g_label, MyApp.get(2,0));
        dh_panel.add(g_text, MyApp.get(0,100));


        JButton lx_b = new JButton("随机数 x:");
        JTextField lx_text = new JTextField("",length);
        lx_text.setEditable(false);
        left.add(lx_b,MyApp.get(1,0));
        left.add(lx_text,MyApp.get(0,100));

        JLabel ly_label = new JLabel("公钥 y:");
        JTextField ly_text = new JTextField("",length);
        ly_text.setEditable(false);
        left.add(ly_label,MyApp.get(1,0));
        left.add(ly_text,MyApp.get(0,100));

        JLabel lk_label = new JLabel("共享密钥 K:");
        JTextField lk_text = new JTextField("", length);
        lk_text.setEditable(false);
        left.add(lk_label,MyApp.get(1,0));
        left.add(lk_text,MyApp.get(0,100));


        JButton rx_b = new JButton("随机数 x:");
        JTextField rx_text = new JTextField("",length);
        rx_text.setEditable(false);
        right.add(rx_b,MyApp.get(1,0));
        right.add(rx_text,MyApp.get(0,100));

        JLabel ry_label = new JLabel("公钥 y:");
        JTextField ry_text = new JTextField("",length);
        ry_text.setEditable(false);
        right.add(ry_label,MyApp.get(1,0));
        right.add(ry_text,MyApp.get(0,100));

        JLabel rk_label = new JLabel("共享密钥 K:");
        JTextField rk_text = new JTextField("", length);
        rk_text.setEditable(false);
        right.add(rk_label,MyApp.get(1,0));
        right.add(rk_text,MyApp.get(0,100));


        size_select.addActionListener(event->{
            int size = size_select.getItemAt(size_select.getSelectedIndex());
            this.dh_left = DiffieHellman.getDiffieHellman(size,50,random);
            this.dh_right = new DiffieHellman(this.dh_left.p,this.dh_left.g);
            p_text.setText(this.dh_left.p.toString());
            g_text.setText(this.dh_left.g.toString());
        });
        lx_b.addActionListener(event->{
            BigInteger x ;
            do{
                do{
                    x = BigInteger.valueOf(random.nextInt());
                }while(1 != x.compareTo(BigInteger.ONE));
                x = x.mod(this.dh_left.p);
            }while(1 != x.compareTo(BigInteger.ONE));

            BigInteger y = this.dh_left.getY(x);
            BigInteger lk = this.dh_left.getK(this.dh_right.y);
            BigInteger rk = this.dh_right.getK(y);

            lx_text.setText(x.toString());
            ly_text.setText(y.toString());

            lk_text.setText(lk.toString());
            rk_text.setText(rk.toString());
        });
        rx_b.addActionListener(event->{
            BigInteger x ;
            do{
                do{
                    x = BigInteger.valueOf(random.nextInt());
                }while(1 != x.compareTo(BigInteger.ONE));
                x = x.mod(this.dh_left.p);
            }while(1 != x.compareTo(BigInteger.ONE));

            BigInteger y = this.dh_right.getY(x);
            BigInteger rk = this.dh_right.getK(this.dh_left.y);
            BigInteger lk = this.dh_left.getK(y);

            rx_text.setText(x.toString());
            ry_text.setText(y.toString());
            rk_text.setText(rk.toString());

            lk_text.setText(lk.toString());
        });

        //设置jframe框架。
        this.setTitle("Diffie-Hellman 密钥交换协议");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getWidth())/2,(screen.height-this.getHeight())/2);
        //this.setSize(100,100);
        this.setVisible(false);
    }
}
