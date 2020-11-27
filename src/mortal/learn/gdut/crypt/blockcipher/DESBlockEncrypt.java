package mortal.learn.gdut.crypt.blockcipher;

import mortal.learn.gdut.crypt.des.DES;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class DESBlockEncrypt {
    /**
     * 短块加密，填充法。
     */
    public static final int FILL = 0;
    /**
     * 短块加密，序列密码加密。
     */
    public static final int SCE = 1;
    /**
     * 短块加密，密文挪用技术
     */
    public static final int CET = 2;

    /**
     * 电码本模式
     */
    public static final int ECB = 3;
    /**
     * 密文链接模式。
     * 加密错误传播无界，解密错误传播有界。
     */
    public static final int CBC = 4;


    private byte[] IV;
    private long[] encrypt_keys;
    private long[] decrypt_keys;
    public static final Random random = new Random(System.currentTimeMillis());

    public DESBlockEncrypt(long key, long IV){
        this.IV = new byte[8];
        for(int i=0; i<8; i++){
            this.IV[i] |= (IV>>>(i*8));
        }

        long[] keys = DES.getSubKeys(key);
        this.encrypt_keys = Arrays.copyOf(keys, keys.length);

        int length = keys.length;
        for(int i=0; i<length/2; i++){
            long temp = keys[i];
            keys[i] = keys[length-1-i];
            keys[length-1-i] = temp;
        }
        this.decrypt_keys = keys;
    }

    /**
     * 加密64位数据。
     * @param src 数据源，8个字节。
     * @return out 加密结果。
     */
    private byte[] encrypt64(byte[] src){
        Objects.requireNonNull(src);
        assert src.length == 8;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];

        //字节数组转long
        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        //加密
        out_long = DES.R(src_long, this.encrypt_keys);
        //long转字节数组。
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
    private byte[] decrypt64(byte[] src){
        Objects.requireNonNull(src);
        assert src.length == 8;

        long src_long = 0;
        long out_long ;
        byte[] out = new byte[8];
        //字节数组转long
        for(int i=0; i<8; i++){
            src_long |=  (((long)src[i])&0xff) << (i*8);
        }
        //解密
        out_long = DES.R(src_long, this.decrypt_keys);
        //long转字节数组。
        for(int i=0; i<8; i++){
            out[i] |= (out_long & 0xff);
            out_long>>>=8;
        }

        return out;
    }


    private byte[] fillEncrypt(int mode, byte[] src){
        assert null != src;
        assert 0 != src.length;

        //填充数据。
        byte[] blocks = Arrays.copyOf(src, (src.length/8)*8+8);
        for(int i=src.length; i<blocks.length-1; i++){
            blocks[i] = (byte)random.nextInt();
        }
        blocks[blocks.length-1] = (byte)(src.length % 8);
        //加密
        return this.encryptWithMode(mode, blocks);
    }

    private byte[] fillDecrypt(int mode, byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        //解密
        byte[] blocks = this.decryptWithMode(mode, src);
        //去除填充数据。
        int fill_length = 8 - blocks[blocks.length-1];
        return Arrays.copyOf(blocks, blocks.length - fill_length);
    }

    private byte[] sceEncrypt(int mode, byte[] src){
        assert null != null;
        assert 0 != src.length;

        if( 0 == src.length % 8){
            return this.encryptWithMode(mode, src);
        }else if(src.length < 8){
            //Cn = Mn XOR MSBu(E(IV,K))
            byte[] MSB = this.encrypt64(this.IV);
            MSB = Arrays.copyOf(MSB, src.length);
            for(int i=0; i<src.length; i++){
                MSB[i] ^= src[i];
            }
            return MSB;
        }else{
            assert 0 != src.length % 8;
            //加密连续分组
            byte[] blocks = Arrays.copyOfRange(src, 0,src.length/8*8);
            blocks = this.encryptWithMode(mode, blocks);
            //加密短块
            byte[] shorts = Arrays.copyOfRange(src, blocks.length, src.length);
            //Cn = Mn XOR MSBu(E(Cn-1, K))
            byte[] MSB = Arrays.copyOfRange(blocks, blocks.length-8, blocks.length);
            MSB = this.encrypt64(MSB);
            MSB = Arrays.copyOf(MSB, shorts.length);
            for(int i=0; i<shorts.length; i++){
                shorts[i] ^= MSB[i];
            }
            //保存加密结果
            byte[] out = new byte[src.length];
            for(int i=0; i<blocks.length; i++){
                out[i] = blocks[i];
            }
            for(int i=0; i<shorts.length; i++){
                out[blocks.length + i] = shorts[i];
            }
            //返回结果
            return out;
        }
    }

    private byte[] sceDecrypt(int mode, byte[] src){
        assert null != null;
        assert 0 != src.length;

        if( 0 == src.length % 8){
            return this.decryptWithMode(mode, src);
        }else if(src.length < 8){
            //Cn = Mn XOR MSBu(E(IV,K))
            //Mn = Cn XOR MSBu(E(IV,K))
            byte[] MSB = this.encrypt64(this.IV);
            MSB = Arrays.copyOf(MSB, src.length);
            for(int i=0; i<src.length; i++){
                MSB[i] ^= src[i];
            }
            return MSB;
        }else{
            assert 0 != src.length % 8;
            //解密连续分组
            byte[] blocks = Arrays.copyOfRange(src, 0,src.length/8*8);
            blocks = this.decryptWithMode(mode, blocks);
            //解密短块
            byte[] shorts = Arrays.copyOfRange(src, blocks.length, src.length);
            //Cn = Mn XOR MSBu(E(Cn-1,K))
            //Mn = Cn XOR MSBu(E(Cn-1,K))
            byte[] MSB = Arrays.copyOfRange(src, blocks.length-8, blocks.length);
            MSB = this.encrypt64(MSB);
            MSB = Arrays.copyOf(MSB, shorts.length);
            for(int i=0; i<shorts.length; i++){
                shorts[i] ^= MSB[i];
            }
            //保存解密结果
            byte[] out = new byte[src.length];
            for(int i=0; i<blocks.length; i++){
                out[i] = blocks[i];
            }
            for(int i=0; i<shorts.length; i++){
                out[blocks.length + i] = shorts[i];
            }
            //返回结果
            return out;
        }
    }

    private byte[] cetEncrypt(int mode, byte[] src){
        assert null != src;
        assert 0 != mode;

        if(0 == src.length % 8){
            return this.encryptWithMode(mode, src);
        }else if(src.length < 8){
            byte[] D = new byte[8];
            //密文挪用
            for(int i=0; i<8-src.length; i++){
                D[i] = this.IV[src.length + i];
            }
            for(int i=0; i<src.length; i++){
                D[8-src.length + i] = src[i];
            }
            //加密
            byte[] C = this.encrypt64(D);
            //保存加密结果
            for(int i=0; i<8-src.length; i++){
                this.IV[src.length + i] = C[i];
            }
            //返回结果
            return Arrays.copyOfRange(C, 8-src.length, 8);
        }else{
            assert 0 != src.length % 8;
            //加密连续分组
            byte[] blocks = Arrays.copyOfRange(src, 0, src.length/8*8);
            blocks = this.encryptWithMode(mode, blocks);
            //加密短块
            byte[] shorts = Arrays.copyOfRange(src, blocks.length, src.length);
            //密文挪用
            byte[] D = new byte[8];
            for(int i=0; i<8-shorts.length; i++){
                D[i] = blocks[blocks.length-8+shorts.length + i];
            }
            for(int i=0; i<shorts.length; i++){
                D[8-shorts.length + i] = shorts[i];
            }
            //加密挪用拼合块
            byte[] C = this.encrypt64(D);
            //保存加密结果
            byte[] out = new byte[src.length];
            for(int i=0; i<blocks.length-8+shorts.length; i++){
                out[i] = blocks[i];
            }
            for(int i=0; i<8; i++){
                out[blocks.length-8+shorts.length + i] = C[i];
            }
            //返回结果
            return out;
        }
    }

    private byte[] cetDecrypt(int mode, byte[] src){
        assert null != src;
        assert 0 != mode;

        if(0 == src.length % 8){
            return this.decryptWithMode(mode, src);
        }else if(src.length < 8){
            byte[] C = new byte[8];
            //获取挪用加密结果
            for(int i=0; i<8-src.length; i++){
                C[i] = this.IV[src.length + i];
            }
            for(int i=0; i<src.length; i++){
                C[8-src.length + i] = src[i];
            }
            //解密挪用加密结果
            byte[] D = this.decrypt64(C);
            //保存解密结果
            for(int i=0; i<8-src.length; i++){
                this.IV[src.length + i] = D[i];
            }
            //返回结果
            return Arrays.copyOfRange(D, 8-src.length, 8);
        }else{
            assert 0 != src.length % 8;

            //解密挪用加密块
            byte[] C = new byte[8];
            for(int i=0; i<8; i++){
                C[i] = src[src.length-8 + i];
            }
            byte[] D = this.decrypt64(C);
            //获取解密结果
            byte[] shorts = Arrays.copyOfRange(D, 8-(src.length%8), 8);
            //恢复分组密文
            byte[] blocks = Arrays.copyOf(src, src.length/8*8);
            for(int i=0; i<8-shorts.length; i++){
                blocks[blocks.length-8+shorts.length + i] = D[i];
            }
            //解密分组密文
            blocks = this.decryptWithMode(mode, blocks);
            //保存解密结果
            byte[] out = new byte[src.length];
            for(int i=0; i<blocks.length; i++){
                out[i] = blocks[i];
            }
            for(int i=0; i<shorts.length; i++){
                out[blocks.length + i] = shorts[i];
            }
            //返回结果
            return out;
        }
    }

    private byte[] encryptWithMode(int mode, byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        byte[] out ;
        switch(mode){
            case DESBlockEncrypt.CBC:
                out = this.CBCEncrypt(src);
                break;
            case DESBlockEncrypt.ECB:
                out = this.ECBEncrypt(src);
                break;
            default:throw new IllegalArgumentException("illegal mode : " + mode);
        }
        return out;
    }

    private byte[] decryptWithMode(int mode, byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        byte[] out ;
        switch(mode){
            case DESBlockEncrypt.CBC:
                out = this.CBCDecrypt(src);
                break;
            case DESBlockEncrypt.ECB:
                out = this.ECBDecrypt(src);
                break;
            default:throw new IllegalArgumentException("illegal mode : " + mode);
        }
        return out;
    }

    private byte[] ECBEncrypt(byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        byte[] src_8 = new byte[8];
        byte[] out_8 ;
        byte[] out = new byte[src.length];

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i+j];
            }
            //加密分组
            out_8 = this.encrypt64(src_8);
            //保存加密结果
            for(int j=0; j<8; j++){
                out[i+j] = out_8[j];
            }
        }
        //返回结果
        return out;
    }

    private byte[] ECBDecrypt(byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        byte[] src_8 = new byte[8];
        byte[] out_8 ;
        byte[] out = new byte[src.length];

        for(int i=0; i<src.length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i+j];
            }
            //解密分组
            out_8 = this.decrypt64(src_8);
            //保存解密结果
            for(int j=0; j<8; j++){
                out[i+j] = out_8[j];
            }
        }
        //返回结果
        return out;
    }

    private byte[] CBCEncrypt(byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        int length = src.length;
        byte[] out = new byte[src.length];
        byte[] out_l = new byte[8];
        byte[] src_8 = new byte[8];
        byte[] out_8 ;

        for(int i=0; i<8; i++){
            out_l[i] = this.IV[i];
        }
        for(int i=0; i<length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i+j];
            }
            //密文链接
            for(int j=0; j<8; j++){
                src_8[j] ^= out_l[j];
            }
            //加密分组
            out_8 = this.encrypt64(src_8);
            //保存密文分组，链接密文
            for(int j=0; j<8; j++){
                out_l[j] = out_8[j];
                out[i+j] = out_8[j];
            }
        }
        return out;
    }

    private byte[] CBCDecrypt(byte[] src){
        assert null != src;
        assert 0 != src.length;
        assert 0 == src.length % 8;

        int length = src.length;
        byte[] out = new byte[src.length];
        byte[] out_l = new byte[8];
        byte[] src_8 = new byte[8];
        byte[] out_8 ;

        for(int i=0; i<8; i++){
            out_l[i] = this.IV[i];
        }
        for(int i=0; i<length; i+=8){
            //get src_8
            for(int j=0; j<8; j++){
                src_8[j] = src[i+j];
            }
            //解密分组
            out_8 = this.decrypt64(src_8);
            //解密密文链接。
            for(int j=0; j<8; j++){
                out_8[j] ^= out_l[j];
            }
            //保存明文分组，链接密文
            for(int j=0; j<8; j++){
                out_l[j] = src_8[j];
                out[i+j] = out_8[j];
            }
        }
        return out;
    }

    public static void main(String[] args){
        Random random = new Random(System.currentTimeMillis());
        for(int i=0; i<1000000000; i++){
            int length;
            do{
                length = random.nextInt();
                if(length<0)
                    length = -length;
                length %= 8*8;
            }while( 0==length );

            DESBlockEncrypt des = new DESBlockEncrypt(random.nextLong(),random.nextLong());

            byte[] message = new byte[length];
            random.nextBytes(message);
            byte[] cipher = des.fillEncrypt(DESBlockEncrypt.ECB, message);
            byte[] plain = des.fillDecrypt(DESBlockEncrypt.ECB, cipher);

            if(! Arrays.equals(message,plain)){
                System.out.println(Arrays.toString(message));
                System.out.println(Arrays.toString(plain));
            }
        }
    }

    public byte[] encrypt(int work_mode, int short_mode, byte[] src){
        Objects.requireNonNull(src);
        switch(short_mode){
            case DESBlockEncrypt.FILL:
                return this.fillEncrypt(work_mode, src);
            case DESBlockEncrypt.SCE:
                return this.sceEncrypt(work_mode, src);
            case DESBlockEncrypt.CET:
                return this.cetEncrypt(work_mode, src);
            default: throw new IllegalArgumentException("illegal short_mode :" + short_mode);
        }
    }

    public byte[] decrypt(int work_mode, int short_mode, byte[] src){
        Objects.requireNonNull(src);
        switch(short_mode){
            case DESBlockEncrypt.FILL:
                return this.fillDecrypt(work_mode, src);
            case DESBlockEncrypt.SCE:
                return this.sceDecrypt(work_mode, src);
            case DESBlockEncrypt.CET:
                return this.cetDecrypt(work_mode, src);
            default: throw new IllegalArgumentException("illegal short_mode :" + short_mode);
        }
    }
}

