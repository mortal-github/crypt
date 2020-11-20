package mortal.learn.gdut.crypt.rsa;

import mortal.learn.gdut.crypt.MyApp;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class RSA {
    private final BigInteger p;
    private final BigInteger q;
    public  final BigInteger n;
    private final BigInteger euler_n;
    public  final BigInteger e;
    private final BigInteger d;

    /**
     * 生成一个的RSA。
     * @param p
     * @param q
     * @param e
     */
    public RSA(BigInteger p, BigInteger q, BigInteger e){
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
        this.euler_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        this.e = e;
        this.d = MyApp.NegMod(e,euler_n);
    }

    /**
     * 生成一个RSA。满足以下参数条件。
     * @param size 加解密时分组的二进制长度。
     * @param e 公钥，建议使用素数，且使用65537。
     * @param random 随机数生成器
     */
    public RSA(int size, BigInteger e, Random random) {
        size = (int)(Math.log10(Math.pow(2,size))/2);
        int round = 0;
        int max_round = 1000;
        int round_count = 0;
        //获取随机pq
        BigInteger p,q,n,euler_n;
        BigInteger gcd;
        do{
            if(round > max_round) {
                size++;
                round=0;
                round_count++;
            }
            do{
                p = MyApp.isPrime(size,50,random);
            }while(null == p);
            do{
                q = MyApp.isPrime(size, 50, random);
            }while(null == q);
            n = p.multiply(q);
            euler_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            gcd = MyApp.gcd(e,euler_n);
        }while( ((size >= n.bitLength()) || (!gcd.equals(BigInteger.ONE))) && round_count<2) ;

        this.p = p;
        this.q = q;
        this.n = n;
        this.euler_n = euler_n;
        this.e = e;
        this.d = MyApp.NegMod(this.e,this.euler_n);
    }

    /**
     * 颠倒数组位序，主要用来将转换大小端排序。
     * @param bytes
     * @return
     */
    public static byte[] reverse(byte[] bytes){
        byte[] result = Arrays.copyOf(bytes, bytes.length);
        for(int i=0; i<result.length/2; i++){
            byte temp = result[i];
            result[i] = result[result.length-1-i];
            result[result.length-1-i] = temp;
        }
        return result;
    }

    /**
     * 加密任意二进制数据。
     * @param message
     * @param e
     * @param n
     * @return
     */
    public static byte[] encrypt(byte[] message, BigInteger e, BigInteger n){
        //明文分组尺寸，要求分组数值小于模数n。
        int group_size_p = (n.bitLength()-1)/8;
        //密文分组尺寸，要求分组能容纳模数n。
        int group_size_c = n.bitLength()/8;
        if(0 != (n.bitLength()%8))
            group_size_c++;
        //分组数量
        int group_count = message.length / group_size_p;
        if( 0 != message.length % group_size_p)
            group_count++;

        byte[] cipher = new byte[group_count * group_size_c];
        byte[] temp;
        BigInteger src;
        BigInteger out;

        for(int i=0; i<group_count;i++){
            //获取明文数据
            temp = Arrays.copyOfRange(message,i*group_size_p,i*group_size_p + group_size_p);
            temp = RSA.reverse(temp);
            //加密
            src = new BigInteger(1, temp);
            out = MyApp.ExpMod(src,e,n);
            //获取密文数据
            temp = out.toByteArray();
            temp = RSA.reverse(temp);
            assert temp.length <= group_size_c+1: temp.length + ":" + group_size_c + ":" + n.bitLength() + ":" + out;
            //复制密文数据到输出,补码可能侵占1位标志位。
            for(int j=0; j<temp.length && j<group_size_c; j++){
                cipher[i*group_size_c + j] = temp[j];
            }
        }

        return cipher;
    }

    /**
     * 解密任意二进制数据。
     * @param cipher
     * @return
     */
    public byte[] decrypt(byte[] cipher){
        //明文分组尺寸，要求分组数值小于模数n。
        int group_size_p = (n.bitLength()-1)/8;
        //密文分组尺寸，要求分组能容纳模数n。
        int group_size_c = n.bitLength()/8;
        if(0 != (n.bitLength()%8))
            group_size_c++;
        //分组数量
        int group_count = cipher.length / group_size_c;
        assert 0 == cipher.length % group_size_c;//密文分组一般都是整齐的

        byte[] plain = new byte[group_count * group_size_p];
        byte[] temp;
        BigInteger src;
        BigInteger out;

        for(int i=0; i<group_count;i++){
            //获取密文数据
            temp = Arrays.copyOfRange(cipher,i*group_size_c,i*group_size_c + group_size_c);
            temp = RSA.reverse(temp);
            //解密
            src = new BigInteger(1, temp);
            out = MyApp.ExpMod(src,this.d,this.n);
            //获取明文数据
            temp = out.toByteArray();
            temp = RSA.reverse(temp);
            assert temp.length <= group_size_p+1 :  temp.length + ":" + group_size_p+ ":" + n.bitLength() + ":" + out;
            //复制明文数据到输出,补码可能侵占1位标志位。
            for(int j=0; j<temp.length & j<group_size_p; j++){
                plain[i*group_size_p + j] = temp[j];
            }
        }

        return plain;
    }

    public BigInteger getP(){
        return this.p;
    }

    public BigInteger getQ(){
        return this.q;
    }

    public BigInteger getEuler_n(){
        return this.euler_n;
    }
    
    public BigInteger getD(){
        return this.d;
    }

    public static void main(String[] args){
        Random random = new Random(System.currentTimeMillis());

        for(int i=0; i<1000; i++) {
            //RSA
            RSA rsa = new RSA(738, BigInteger.valueOf(65537), random);
            //Check RSA
            for(int j=0; j<1000; j++) {
                BigInteger message;
                do {
                    message = MyApp.isPrime(99, 50, random);
                } while (null == message);
                for(int k=0; k<11; k++){
                    message = message.multiply(message);
                }

                byte[] m = message.toByteArray();
                //m = RSA.reverse(m);//使其小端排序
                byte[] c = RSA.encrypt(m,rsa.e,rsa.n);
                byte[] p = rsa.decrypt(c);
                //p = RSA.reverse(p);//使其大端排序
                p = Arrays.copyOf(p,m.length);

                BigInteger result = new BigInteger(1, p);
                if (!message.equals(result)) {
                    System.out.println(message);
                    System.out.println(new BigInteger(c));
                    System.out.println(new BigInteger(p));
                }
            }
        }
        System.out.println("-----------------------");
    }
}
