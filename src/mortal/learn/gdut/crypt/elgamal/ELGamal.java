package mortal.learn.gdut.crypt.elgamal;

import mortal.learn.gdut.crypt.MyApp;
import mortal.learn.gdut.crypt.rsa.RSA;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class ELGamal {
    public BigInteger p;
    public BigInteger g;
    private BigInteger d = BigInteger.ZERO;
    public BigInteger y = BigInteger.ONE;

    /**
     * p是大素数，g是p的生成元。
     * @param p 大素数。
     * @param g  p的本原元。
     */
    public ELGamal(BigInteger p, BigInteger g){
        Objects.requireNonNull(p);
        Objects.requireNonNull(g);

        if(!MyApp.isPrime(p,50, new Random(System.currentTimeMillis())))
            throw new IllegalArgumentException("p must be prime number");

        BigInteger p_1 = p.subtract(BigInteger.ONE);
        BigInteger[] q = MyApp.getAllPrimeFactor(p_1);
        if( !MyApp.isPrimitiveRoot(g,q,p_1,p))
            throw new IllegalArgumentException("g must be a primitive root of p");

        this.p = p;
        this.g = g;
    }

    /**
     * 生成一个ELGamal。
     * @param l 素数p的数量级，即生成介于10^l到10^(l+1)之间的素数。
     * @param k 检验一个素数是否为素数时候的检验次数，p不是素数的概率为2^(-2k).
     * @param random 用来生成p的随机数生成器。
     * @return dh 一个dh对象，储存了素数p以及p的本原元g.
     */
    public static ELGamal getELGamal(int l, int k, Random random){
        BigInteger p;
        do{
            p = MyApp.getPrime(l,k,random);
        }while(null == p);

        BigInteger g = MyApp.minPrimitiveRoot(p);

        return new ELGamal(p,g);
    }

    /**
     * 使用一个随机数d作为解密密钥，并计算返回公钥y。
     * @param d 1<d<p-1
     * @return y 公钥。
     */
    public BigInteger getY(BigInteger d){
        Objects.requireNonNull(d);
        if(1 != d.compareTo(BigInteger.ZERO))
            throw new IllegalArgumentException("d must be > 1");
        if(1 != this.p.compareTo(d))
            throw new IllegalArgumentException("x must be < " + this.p);

        BigInteger y = MyApp.ExpMod(this.g,d,this.p);

        this.d = d;
        this.y = y;

        return y;
    }

    /**
     * 使用加密随机数e和对方公钥加密数据。
     * @param data 待加密数据。
     * @param e 加密用随机数。
     * @param y 对方公钥。
     * @param p 对方公钥的模数。
     * @param g 对方公钥的本原元。
     * @return cipher （C1,C2), C1=g^e mod p, C2=UM mod p, U=y^e mod p
     */
    public static byte[][] encrypt(byte[] data, BigInteger e, BigInteger y, BigInteger p, BigInteger g){
        Objects.requireNonNull(data);
        Objects.requireNonNull(e);
        Objects.requireNonNull(y);
        Objects.requireNonNull(p);
        Objects.requireNonNull(g);
        assert 1 == e.compareTo(BigInteger.ONE);
        assert -1 == e.compareTo(p.subtract(BigInteger.ONE));
        //...公钥不做验证。

        //明文分组尺寸，要求分组数值小于模数p。
        int group_size_p = (p.bitLength()-1)/8;
        //密文分组尺寸，要求分组能容纳模数n。
        int group_size_c = p.bitLength()/8;
        if(0 != (p.bitLength()%8))
            group_size_c++;
        //分组数量
        int group_count = data.length / group_size_p;
        if( 0 != data.length % group_size_p)
            group_count++;

        byte[] cipher = new byte[group_count * group_size_c];
        byte[] temp;
        BigInteger src;
        BigInteger out;

        //分组加密
        BigInteger C1 = MyApp.ExpMod(g,e,p);
        BigInteger u  = MyApp.ExpMod(y,e,p);

        for(int i=0; i<group_count; i++){
            //获取明文数据
            temp = Arrays.copyOfRange(data,i*group_size_p,i*group_size_p + group_size_p);
            temp = RSA.reverse(temp);
            //模乘加密
            src = new BigInteger(1, temp);
            out = u.multiply(src).mod(p);
            //获取密文数据
            temp = out.toByteArray();
            temp = RSA.reverse(temp);
            assert temp.length <= group_size_c+1: temp.length + ":" + group_size_c + ":" + p.bitLength() + ":" + out;
            //复制密文数据到输出,补码可能侵占1位标志位。
            for(int j=0; j<temp.length && j<group_size_c; j++){
                cipher[i*group_size_c + j] = temp[j];
            }
        }

        byte[][] result = new byte[2][];
        result[0] = C1.toByteArray();
        result[1] = cipher;
        return result;
    }

    /**
     * 解密数据。
     * @param data data[1]=C1, data[2]=C2, C1=g^e mod p ,C2 = UM mod p, U=y^e mod p, e为加密方的加密随机数。
     * @return plain 明文
     */
    public byte[] decrypt(byte[][] data){
        Objects.requireNonNull(data);
        Objects.requireNonNull(data[0]);
        Objects.requireNonNull(data[1]);

        //明文分组尺寸，要求分组数值小于模数n。
        int group_size_p = (this.p.bitLength()-1)/8;
        //密文分组尺寸，要求分组能容纳模数n。
        int group_size_c = this.p.bitLength()/8;
        if(0 != (this.p.bitLength()%8))
            group_size_c++;
        //分组数量
        int group_count = data[1].length / group_size_c;
        assert 0 == data[1].length % group_size_c;//密文分组一般都是整齐的

        //分组解密
        byte[] plain = new byte[group_count * group_size_p];
        byte[] temp;
        BigInteger src;
        BigInteger out;

        BigInteger c1 = new BigInteger(data[0]);
        BigInteger v = MyApp.ExpMod(c1,this.d, this.p);
        BigInteger v_ = MyApp.NegMod(v,p);

        for(int i=0; i<group_count;i++){
            //获取密文数据
            temp = Arrays.copyOfRange(data[1],i*group_size_c,i*group_size_c + group_size_c);
            temp = RSA.reverse(temp);
            //模乘解密
            src = new BigInteger(1, temp);
            out = v_.multiply(src).mod(this.p);
            //获取明文数据
            temp = out.toByteArray();
            temp = RSA.reverse(temp);
            assert temp.length <= group_size_p+1 :  temp.length + ":" + group_size_p+ ":" + this.p.bitLength() + ":" + out;
            //复制明文数据到输出,补码可能侵占1位标志位。
            for(int j=0; j<temp.length & j<group_size_p; j++){
                plain[i*group_size_p + j] = temp[j];
            }
        }

        return plain;
    }

    public static void main(String[] args){
        Random random = new Random(System.currentTimeMillis());
        int mod_size = 10;
        int data_length = 99;
        for(int i=0; i<10000; i++){
            ELGamal elgmal = getELGamal(mod_size,50,random);
            BigInteger d;
            //获取公钥
            do{
                do{
                    d = MyApp.getPrime(mod_size,50,random);
                }while(null == d);
                d = d.mod(elgmal.p.subtract(BigInteger.ONE));
            }while(1 != d.compareTo(BigInteger.ONE));
            elgmal.getY(d);

            //随机数据
            BigInteger data ;
            do{
                data = MyApp.getPrime(data_length,50,random);
            }while(null == data);

            //获取加密随机数
            BigInteger e ;
            do{
                do{
                    e = MyApp.getPrime(mod_size,50,random);
                }while (null == e);
                e = e.mod(elgmal.p.subtract(BigInteger.ONE));
            }while(1 != e.compareTo(BigInteger.ONE));

            byte[] message = data.toByteArray();
            message = RSA.reverse(message);
            //加解密。
            byte[][] cipher = ELGamal.encrypt(message, e,elgmal.y,elgmal.p,elgmal.g);
            byte[] plain = elgmal.decrypt(cipher);
            //获取解密数据。
            plain = RSA.reverse(plain);
            BigInteger data2 = new BigInteger(1, plain);

            if(!data.equals(data2)){
                System.out.println(i);
                System.out.println("before = " + data);
                System.out.println("after  = " + data2);
                System.out.println();
            }else {
                System.out.println("OK " + i);
            }
        }
    }
}
