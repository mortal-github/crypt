package mortal.learn.gdut.crypt.blockcipher;

import mortal.learn.gdut.crypt.des.DES;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class BlockCipher {

    /**
     * 电码本模式
     */
    public static final int ECB = 0;
    /**
     * 密文链接模式。
     * 加密错误传播无界，解密错误传播有界。
     */
    public static final int CBC = 1;
    /**
     * 明文链接模式。
     * 加密错误传播有界，解密错误传播无界。
     */
    public static final int PBC = 2;
    /**
     * 明密文链接模式。
     * 加密错误传播无界，解密错误传播无界。
     */
    public static final int PCBC = 3;
    /**
     * 输出反馈模式。
     * 无错误传播，适合加密冗余度大的数据，入语音和图像数据。
     */
    public static final int OFB = 4;
    /**
     * 密文反馈模式。
     * 加解密都错误传播无界，适合数据完整性认证访方面的应用。
     */
    public static final int CFB = 5;

    /**
     * 短块加密，填充法。
     */
    public static final int FILL = -1;
    /**
     * 短块加密，序列密码加密。
     */
    public static final int SCE = -2;
    /**
     * 短块加密，密文挪用技术
     */
    public static final int CET = -3;

    private int work_mode;
    private int short_block_mode;

    private static long[] DES_getSubKeysForEncrypt(long key){
        //获取加密子密钥
        long[] keys = DES.getSubKeys(key);
        return keys;
    }

    private static long[] DES_getSubKeysForDecrypt(long key){
        //获取加密子密钥
        long[] keys = DES.getSubKeys(key);
        //获取解密子密钥
        for(int i=0; i<keys.length/2; i++){
            long temp = keys[i];
            keys[i] = keys[keys.length-1-i];
            keys[keys.length-1-i] = temp;
        }
        return keys;
    }

    /**
     * 加密64位数据。
     * @param keys 加密子密钥。
     * @param src 数据源，8个字节。
     * @return out 加密结果。
     */
    private static byte[] DES_encrypt64(long[] keys, byte[] src){
        Objects.requireNonNull(keys);
        Objects.requireNonNull(src);
        assert src.length == 8;
        assert keys.length == 16;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];

        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        out_long = DES.R(src_long, keys);

        for(int i=0; i<8; i++){
            out[i] |= (out_long & 0xff);
            out_long>>>=8;
        }

        return out;
    }

    /**
     * 解密64位数据。
     * @param keys 解密子密钥
     * @param src 数据源，8个字节。
     * @return out 解密结果。
     */
    private static byte[] DES_decrypt64(long[] keys, byte[] src){
        Objects.requireNonNull(keys);
        Objects.requireNonNull(src);
        assert src.length == 8;
        assert keys.length == 16;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];

        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        out_long = DES.R(src_long, keys);

        for(int i=0; i<8; i++){
            out[i] |= (out_long & 0xff);
            out_long>>>=8;
        }

        return out;
    }

    public static void main(String[] args){
        Random random = new Random(System.currentTimeMillis());
        for(int i=0; i<10000000; i++){
            long key = random.nextLong();
            long[] encrypt_keys = DES_getSubKeysForEncrypt(key);
            long[] decrypt_keys = DES_getSubKeysForDecrypt(key);

            byte[] message = new byte[8];
            random.nextBytes(message);

            byte[] cipher = DES_encrypt64(encrypt_keys, message);
            byte[] plain = DES_decrypt64(decrypt_keys, cipher);

            if(! Arrays.equals(message,plain)){
                System.out.println("i = " + i + ", ERROR!");
                System.out.println(Arrays.toString(message));
                System.out.println(Arrays.toString(plain));
            }
        }

    }
}
