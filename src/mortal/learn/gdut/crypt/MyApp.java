package mortal.learn.gdut.crypt;

import mortal.learn.gdut.crypt.des.DesApp;
import mortal.learn.gdut.crypt.rsa.RsaApp;
import mortal.learn.gdut.crypt.xor.XorApp;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.util.*;

public class MyApp {

    public static void show(String[] args){
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
            JFrame rsa = new RsaApp();

            JButton xor_b = new JButton("异或加密");
            xor_b.addActionListener(event->xor.setVisible(true));
            panel.add(xor_b);

            JButton des_b = new JButton("DES加密");
            des_b.addActionListener(event->des.setVisible(true));
            panel.add(des_b);

            JButton rsa_b = new JButton("RSA加密");
            rsa_b.addActionListener(event->rsa.setVisible(true));
            panel.add(rsa_b);
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

    /**
     * 模冥运算，m^e mod n
     * @param m
     * @param e
     * @param n
     * @return 模冥结果。
     */
    public static BigInteger ExpMod(BigInteger m, BigInteger e, BigInteger n){
        BigInteger c = BigInteger.valueOf(1L);
        byte[] e_b = e.toByteArray();//大端
        for(int i=0; i<e_b.length; i++){
            for(int j=7; j>-1; j--){
                c = c.multiply(c);
                c = c.mod(n);
                if(0 != (e_b[i] & (1<<j))){
                    c = c.multiply(m);
                    c = c.mod(n);
                }
            }
        }
        return c;
    }

    /**
     * 冥运算a^b
     * @param a
     * @param b
     * @return 冥运算结果
     */
    public static BigInteger Exp(BigInteger a, BigInteger b){
        BigInteger r = BigInteger.valueOf(1L);
        byte[] e_b = b.toByteArray();//大端
        for(int i=0; i<e_b.length; i++){
            for(int j=7; j>-1; j--){
                r = r.multiply(r);
                if(0 != (e_b[i] & (1<<j))){
                    r = r.multiply(a);
                }
            }
        }
        return r;
    }

    /**
     * 素数产生的概率性算法。
     * @param l l>0,生成数介于10^l到10^(l+1)之间。
     * @param k 检验次数。
     * @return 返回数字或者null。该数字不是素数的概率<=2^(-2k)。
     */
    public static BigInteger isPrime(int l, int k, Random random){
        assert l>0;
        Objects.requireNonNull(random);
        //1 pass = false
        boolean pass = false;
        //2. 随机地从10^l到10^(l+1)范围内任取一个奇整数n=2^t*m+1;m是n-1最大奇因子
        BigInteger n = MyApp.random(l, random);
        BigInteger[] mt = MyApp.getMT(n);
        //3. 随机地从2到n-2之间取k个互不相同地整数：a1,a2,...ak;
        if(-1 == n.compareTo(BigInteger.valueOf(k+3))){
            k = n.intValue()-3;//n比k+3小，所以最多能取n-2-(2-1)=n-3个数字。
        }
        BigInteger[] a = MyApp.getA(k, n, random);
        //4 for i=1 to k loop
        for(int i=0; i<k; i++){
            //5. 调用子过程Miller(n,ai),a^m(mod n)
            pass = MyApp.Miller(a[i],mt[0],mt[1],n);
            if(false == pass){
                //6. pass=false, n肯定为合数。
                //System.out.println("fail: "+ n);
                return null;
            }
        }
        //8 pass=true，则认为n可能为素数。
        return n;
    }

    private static boolean Miller(BigInteger a, BigInteger m, BigInteger t ,BigInteger n){
        //1. b= a^m (mode n)
        BigInteger b = ExpMod(a,m,n);
        //2 if b=+-1 then pass=1 and goto 8;
        if(b.equals(BigInteger.ONE) || b.equals(BigInteger.valueOf(-1))){
            return true;
        }
        //4 fro j=1 to t loop
        while(1 == t.compareTo(BigInteger.ZERO)){
            t = t.subtract(BigInteger.ONE);
            //5 b=b^2 mod n
            b = b.multiply(b).mod(n);
            //6 if b=-1 then pass = true and goto 8
            if(b.equals(BigInteger.ONE) || b.equals(BigInteger.valueOf(-1))){
                return true;
            }
        }
        //8
        return false;
    }

    private static BigInteger random(int l, Random random){
        BigInteger min = Exp(BigInteger.valueOf(10),BigInteger.valueOf(l));
        BigInteger m = min.multiply(BigInteger.valueOf(9));

        byte[] bytes = new byte[m.bitLength()/8 + 1];
        random.nextBytes(bytes);

        BigInteger result = new BigInteger(1,bytes);
        if(result.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            result = result.add(BigInteger.ONE);
        }
        result = result.mod(m);
        result = result.add(min);

        return result;
    }

    private static BigInteger[] getMT(BigInteger n){
        BigInteger two = BigInteger.valueOf(2);

        BigInteger m = n.subtract(BigInteger.ONE);
        BigInteger t = BigInteger.valueOf(0);

        while(m.mod(two).equals(BigInteger.ZERO)){
            m = m.divide(two);
            t = t.add(BigInteger.ONE);
        }
        return new BigInteger[]{m,t};
    }

    private static BigInteger[] getA(int k, BigInteger n,Random random){
        BigInteger n_1 = n.subtract(BigInteger.valueOf(1));

        BigInteger[] a = new BigInteger[k];
        byte[] temp = new byte[n.bitLength()/8 + 1];

        outer:
        for(int i=0; i<a.length; ){
            random.nextBytes(temp);
            a[i] = new BigInteger(1,temp).mod(n_1);
            if(a[i].equals(BigInteger.ONE) || a[i].equals(BigInteger.ZERO)){
                continue outer;
            }
            for(int j=0; j<i; j++){
               if(a[i].equals(a[j])){
                   continue outer;
               }
            }
            i++;
        }
        return a;
    }

    /**
     * 模逆运算，(a,m)=1,求a模m逆
     * 即ax+my=1，x为a模m的逆。
     * 故可以通过扩展欧几里得运算求x。
     * 1. 带余除法
     * a=q0b+r0
     * b=q1r0+r1
     * r0=q2r1+r2
     * ...
     * r_(i-2)=q_i * r_(i-1) + ri
     * ...
     * r_(n-2)=q_(n) * r_(n-1) + rn, rn=0;
     * 最大公因子为r_(n-1)
     * 2. 递推公式
     * ri=xi * a + yi * b;
     * xi=x_(i-2)-qi * x_(i-1)
     * yi=y_(i-2)-qi * y_(i-2)
     * x_(-2)=1,y_(-2)=0;
     * x_(-1)=0,y_(-1)=1;
     * @param a
     * @param m
     */
    public static BigInteger NegMod(BigInteger a, BigInteger m){
        assert 1 == a.compareTo(BigInteger.ZERO);
        assert 1 == m.compareTo(BigInteger.ZERO);
        ArrayList<BigInteger> q = new ArrayList<>();
        boolean trun = false;
        //辗转相除法
        BigInteger r_i_2 ;
        BigInteger r_i_1 ;
        BigInteger r_i ;
        if(1 == a.compareTo(m)){
            r_i_2 = a;
            r_i_1 = m;
        }else{
            r_i_2 = m;
            r_i_1 = a;
            trun = true;
        }
        do{
            //带余除法
            r_i = r_i_2.mod(r_i_1);
            q.add(r_i_2.divide(r_i_1));
            //更新
            r_i_2 = r_i_1;
            r_i_1 = r_i;
        }while(false == r_i.equals(BigInteger.ZERO));
       //递推公式
        BigInteger xi_2;
        BigInteger xi_1;
        BigInteger xi;
        if(trun){
            xi_1 = BigInteger.ZERO;
            xi  = BigInteger.ONE;
        }else{
            xi_1 = BigInteger.ONE;
            xi  = BigInteger.ZERO;
        }
        //求1=ax+by,即r[q.size-2]=1
        for(int i=0; i<q.size()-1; i++){
            //进位
            xi_2 = xi_1;
            xi_1 = xi;
            //递推计算
            xi = xi_2.subtract(xi_1.multiply(q.get(i)));
        }
        //返回模逆结果
        while(-1 == xi.compareTo(BigInteger.ZERO)){
            xi = xi.add(m);
        }
        return xi;
    }

    public static BigInteger gcd(BigInteger a, BigInteger b){
        BigInteger r_i_2;
        BigInteger r_i_1;
        BigInteger r_i;
        if(1 == a.compareTo(b)){
            r_i_2 = a;
            r_i_1 = b;
        }else{
            r_i_2 = b;
            r_i_1 = a;
        }
        //辗转相除
        do{
            r_i = r_i_2.mod(r_i_1);
            r_i_2 = r_i_1;
            r_i_1 = r_i;
        }while(! r_i.equals(BigInteger.ZERO));
        assert a.mod(r_i_2).equals(BigInteger.ZERO);
        assert b.mod(r_i_2).equals(BigInteger.ZERO);
        return r_i_2;
    }
    public static void main(String[] args){
        //MyApp.show(args);
        Random random = new Random(System.currentTimeMillis());
        int count = 0;
        for(int i=0; i<100*100*100; i++){
            BigInteger a;
            do {
                a = MyApp.isPrime(4, 50, random);
            } while (null == a);
            BigInteger m;
            m = MyApp.random(99, random);
            if(1 != m.compareTo(BigInteger.ONE)){
                m = BigInteger.valueOf(2);
            }

            BigInteger b = NegMod(a, m);
            BigInteger check = a.multiply(b).mod(m);
            if(!check.equals(BigInteger.ONE)){
                System.out.println("check=" + check + ", gcd=" + MyApp.gcd(a, m) + ", a=" + a + ", b=" + b + ", m=" + m);
            }

        }
    }
}
