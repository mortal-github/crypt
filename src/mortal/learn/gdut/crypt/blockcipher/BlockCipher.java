package mortal.learn.gdut.crypt.blockcipher;

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
}
