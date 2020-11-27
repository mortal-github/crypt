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
    private long[] encrypt_keys;
    private long[] decrypt_keys;
    private Random random ;

    public BlockCipher(long key, int work_mode, int short_block_mode){
        if(work_mode<0 || 5<work_mode){
            throw new IllegalArgumentException("work_mode must between 0 to 5");
        }
        if(short_block_mode<-3 || -1<short_block_mode){
            throw new IllegalArgumentException("work_mode must between -1 to -3");
        }

        this.work_mode = work_mode;
        this.short_block_mode = short_block_mode;
        this.encrypt_keys = DES_getSubKeysForEncrypt(key);
        this.decrypt_keys = DES_getSubKeysForDecrypt(key);
        this.random = new Random(System.currentTimeMillis());
    }

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
     * @param src 数据源，8个字节。
     * @return out 加密结果。
     */
    private byte[] DES_encrypt64(byte[] src){
        Objects.requireNonNull(src);
        assert src.length == 8;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];

        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        out_long = DES.R(src_long, this.encrypt_keys);

        for(int i=0; i<8; i++){
            out[i] |= (out_long & 0xff);
            out_long>>>=8;
        }

        return out;
    }

    /**
     * 解密64位数据。
     * @param src 数据源，8个字节。
     * @return out 解密结果。
     */
    private byte[] DES_decrypt64( byte[] src){
        Objects.requireNonNull(src);
        assert src.length == 8;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];

        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        out_long = DES.R(src_long, this.decrypt_keys);

        for(int i=0; i<8; i++){
            out[i] |= (out_long & 0xff);
            out_long>>>=8;
        }

        return out;
    }

    public static void main(String[] args){
        Random random = new Random(System.currentTimeMillis());
        for(int i=0; i<1000000000; i++){
            long key = random.nextLong();
            BlockCipher block_cipher = new BlockCipher(key, 0, -1);

            int length ;
            do{
                length = random.nextInt();
            }while(length == 0);
            if(length < 0){
                length = -length;
            }
            length = length % 8;

            byte[] message = new byte[length];
            random.nextBytes(message);

            byte[] cipher = block_cipher.fillEncrypt(message);
            byte[] plain = block_cipher.fillDecrypt(cipher);

            if(! Arrays.equals(message,plain)){
                System.out.println("i = " + i + ", ERROR!");
                System.out.println(Arrays.toString(message));
                System.out.println(Arrays.toString(plain));
            }
        }
    }

    /**
     * 短块加密，填充加密法。
     * @param src 短块，当加密数据没有短块时，传入一个长度为0的数组。
     * @return out 加密数据
     */
    private byte[] fillEncrypt(byte[] src){
        Objects.requireNonNull(src);
        assert src.length < 9;

        int length = src.length;
        byte[] out = new byte[8];
        random.nextBytes(out);
        for(int i=0; i<length; i++){
            out[i] = src[i];
        }
        out[7] = (byte)length;

        return this.DES_encrypt64(out);
    }

    /**
     * 解密短块，填充加密的数据。
     * @param src 填充加密短块的结果。
     * @return out 解密出短块。
     */
    private byte[] fillDecrypt(byte[] src){
        Objects.requireNonNull(src);
        assert src.length == 8;

        byte[] out = this.DES_decrypt64(src);
        int length = out[7];

        return Arrays.copyOf(out, length);
    }
}
