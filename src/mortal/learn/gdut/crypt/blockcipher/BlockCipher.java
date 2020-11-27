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

    private long[] encrypt_keys;
    private long[] decrypt_keys;
    private static Random random = new Random(System.currentTimeMillis());

    public BlockCipher(long key){
        this.encrypt_keys = DES.getSubKeys(key);
        this.decrypt_keys = DES.getSubKeys(key);
        for(int i=0; i<this.decrypt_keys.length/2; i++){
            long temp = this.decrypt_keys[i];
            this.decrypt_keys[i] = this.decrypt_keys[this.decrypt_keys.length-1-i];
            this.decrypt_keys[this.decrypt_keys.length-1-i] = temp;
        }
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
            BlockCipher block_cipher = new BlockCipher(key);

            int length ;
            do{
                length = random.nextInt();
                if(length < 0){
                    length = -length;
                }
                length %= 8*8*8;
            }while((0 == length) || (0 != length % 8));

            byte[] message = new byte[length];
            random.nextBytes(message);

            long z = random.nextLong();

            byte[] cipher = block_cipher.CBCEncrypt(z,message);
            byte[] plain = block_cipher.CBCDecrypt(z,cipher);

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
        assert src.length < 8;

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

    /**
     * 加密短块，序列密码加密法。
     * @param cipher 上一个分组的密文。
     * @param src 短块数据，不足8个字节。
     * @return out 短块加密结果。
     */
    private byte[] sceEncrypt(byte[] cipher, byte[] src){
        Objects.requireNonNull(cipher);
        Objects.requireNonNull(src);
        assert cipher.length == 8;
        assert src.length < 8;

        //Cn = Mn XOR MSBu(E(Cn-1, K))
        int length = src.length;
        byte[] out = new byte[length];
        cipher = this.DES_encrypt64(cipher);
        for(int i=0; i<length; i++){
            out[i] |= src[i] ^ cipher[ 8-length+i];
        }
        return out;
    }

    /**
     * 解密短块，序列密码加密法。
     * @param cipher 上一个分组密文。
     * @param src 短块密文数据。
     * @return out 短块解密结果。
     */
    private byte[] sceDecrypt(byte[] cipher, byte[] src){
        Objects.requireNonNull(cipher);
        Objects.requireNonNull(src);
        assert cipher.length == 8;
        assert src.length < 8;

        //Cn = Mn XOR MSBu(E(Cn-1, K))
        //Mn = Cn XOR MSBu(E(Cn-1, K))
        int length = src.length;
        byte[] out = new byte[length];
        cipher = this.DES_encrypt64(cipher);
        for(int i=0; i<length; i++){
            out[i] |= src[i] ^ cipher[ 8-length+i];
        }

        return out;
    }

    /**
     * 加密短块，密文挪用技术。
     * Cn-1  = E_M(Mn-1, k) = a||b.
     * Cn = E(b||Mn, K) = b'||d.
     * @param cipher 上一个分组密文, a||b。
     * @param src 短块，不足8个字节，Mn。
     * @return out 短块加密, b'||d。
     */
    private byte[] cetEncrypt(byte[] cipher, byte[] src){
        Objects.requireNonNull(cipher);
        Objects.requireNonNull(src);
        assert cipher.length == 8;
        assert src.length < 8;

        //get b||Mn
        byte[] c_s = new byte[8];
        int length = src.length;
        for(int i=0; i<8-length; i++){
            c_s[i] = cipher[length + i];
        }
        for(int i=8-length; i<8; i++){
            c_s[i] = src[i-(8-length)];
        }

        return DES_encrypt64(c_s);
    }

    /**
     * 解密短块，密文挪用激素。
     * Cn-1  = E_M(Mn-1, k) = a||b
     * Cn = E(b||Mn, K) = b' ||d
     * cipher = a||b'
     * src == d
     * @param cipher 上一个分组密文，a||b'。
     * @param src 短块，d。
     * @return out 短块解密，b||Mn。
     */
    private byte[] cetDecrypt(byte[] cipher, byte[] src){
        Objects.requireNonNull(cipher);
        Objects.requireNonNull(src);
        assert cipher.length == 8;
        assert src.length < 8;

        //get b'||d
        byte[] c_s = new byte[8];
        int length = src.length;
        for(int i=0; i<8-length; i++){
            c_s[i] = cipher[length + i];
        }
        for(int i=8-length; i<8; i++){
            c_s[i] = src[i-(8-length)];
        }

        return DES_decrypt64(c_s);
    }


    /**
     * 电码本工作加密。
     * 必须是完整分组，无短块。
     * @param src 数据源。
     * @return out 加密结果
     */
    private byte[] ECBEncrypt(byte[] src){
        assert null != src;
        assert 0 == src.length % 8;

        byte[] src_8 = new byte[8];
        byte[] out_8 = new byte[8];
        byte[] out = new byte[src.length];

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i + j];
            }
            //encrypt src_8
            out_8 = DES_encrypt64(src_8);
            //assgin out_8 to out
            for(int j=0; j<8; j++){
                out[i + j] = out_8[j];
            }
        }
        return out;
    }

    /**
     * 电码本工作模式解密。
     * 必须是完整分组，无短块。
     * @param src 数据源。
     * @return out 解密结果。
     */
    private byte[] ECBDecrypt(byte[] src){
        assert null != src;
        assert 0 == src.length % 8;

        byte[] src_8 = new byte[8];
        byte[] out_8 = new byte[8];
        byte[] out = new byte[src.length];

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i + j];
            }
            //encrypt src_8
            out_8 = DES_decrypt64(src_8);
            //assgin out_8 to out
            for(int j=0; j<8; j++){
                out[i + j] = out_8[j];
            }
        }
        return out;
    }

    /**
     * 密文链接工作模式加密。
     * 必须是完整分组，无短块。
     * @param z 初始向量。
     * @param src 源数据。
     * @return out 加密结果。
     */
    private byte[] CBCEncrypt(long z, byte[] src){
        assert null != src;
        assert 0 == src.length % 8;

        byte[] last = new byte[8];
        byte[] src_8 = new byte[8];
        byte[] out_8 = new byte[8];
        byte[] out = new byte[src.length];

        //get初始向量
        for(int i=0; i<8; i++){
            last[i] |= (byte)(z&0xff);
            z>>>=8;
        }

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i+j];
            }
            //密文链接
            for(int j=0; j<8; j++){
                src_8[j] ^= last[j];
            }
            //加密一组
            out_8 = DES_encrypt64(src_8);
            //保存密文
            for(int j=0; j<8; j++){
                out[i+j] = out_8[j];
                last[j] = out_8[j];
            }
        }
        return out;
    }

    /**
     * 密文链接工作模式解密。
     * 必须是完整分组，无短块。
     * @param z 初始向量。
     * @param src 源数据。
     * @return out 解密结果。
     */
    private byte[] CBCDecrypt(long z, byte[] src){
        assert null != src;
        assert 0 == src.length % 8;

        byte[] last = new byte[8];
        byte[] src_8 = new byte[8];
        byte[] out_8 = new byte[8];
        byte[] out = new byte[src.length];

        //get初始向量
        for(int i=0; i<8; i++){
            last[i] |= (byte)(z&0xff);
            z>>>=8;
        }

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i +j];
            }
            //解密一组
            out_8 = DES_decrypt64(src_8);
            //解密密文链接
            for(int j=0; j<8; j++){
                out_8[j] ^= last[j];
            }
            //保存明文，密文分组
            for(int j=0; j<8; j++){
                out[i+j] = out_8[j];
                last[j] = src_8[j];
            }
        }
        return out;
    }
}
