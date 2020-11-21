package mortal.learn.gdut.crypt.dh;

import mortal.learn.gdut.crypt.MyApp;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

public class DiffieHellman {
    public final BigInteger p;
    public final BigInteger g;
    private BigInteger x = BigInteger.ZERO;
    public BigInteger y = BigInteger.ONE;
    private BigInteger k;

    public DiffieHellman(BigInteger p, BigInteger g){
        Objects.requireNonNull(p);
        Objects.requireNonNull(g);

        if(!MyApp.isPrime(p,50, new Random(System.currentTimeMillis())))
            throw new IllegalArgumentException("p must be prime number");

        BigInteger p_1 = p.subtract(BigInteger.ONE);
        BigInteger[] q = MyApp.getAllPrimeFactor(p_1);
        if( !MyApp.isPrimitiveRoot(g,q,p_1,p))
            throw new IllegalArgumentException("g must be a primitiveroot of p");

        this.p = p;
        this.g = g;
    }

    /**
     * 生成一个DiffieHellman
     * @param l 素数p的数量级，即生成介于10^l到10^(l+1)之间的素数。
     * @param k 检验一个素数是否为素数时候的检验次数，p不是素数的概率为2^(-2k).
     * @param random 用来生成p的随机数生成器。
     * @return dh 一个dh对象，储存了素数p以及p的本原元g.
     */
    public static DiffieHellman getDiffieHellman(int l, int k, Random random){
        BigInteger p;
        do{
            p = MyApp.getPrime(l,k,random);
        }while(null == p);

        BigInteger g = MyApp.minPrimitiveRoot(p);

        return new DiffieHellman(p,g);
    }

    /**
     * 用随机数x生成一个公钥。
     * @param x 随机数x,1<x<p
     * @return y 公钥，y=g^x mod p; x是随机数。
     */
    public BigInteger getY(BigInteger x){
        Objects.requireNonNull(x);
        if(1 != x.compareTo(BigInteger.ONE))
            throw new IllegalArgumentException("x must be > 1");
        if(1 != this.p.compareTo(x))
            throw new IllegalArgumentException("x must be < " + this.p);
        BigInteger y = MyApp.ExpMod(this.g,x,this.p);

        this.x = x;
        this.y = y;

        return y;
    }

    /**
     * 用对方的公钥，生成双方共享的密钥。
     * @param y 对方的公钥。
     * @return k 双发共享的密钥。
     */
    public BigInteger getK( BigInteger y){
        Objects.requireNonNull(y);
        assert 1 == y.compareTo(BigInteger.ZERO);
        assert 1 == this.p.compareTo(y);

        BigInteger k = MyApp.ExpMod(y,this.x,this.p);
        this.k = k;

        return k;
    }

}
