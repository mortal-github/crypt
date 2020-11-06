package mortal.learn.gdut.crypt.des;

import mortal.learn.gdut.crypt.MyApp;

import java.util.Arrays;
import java.util.Random;

public class DES {
    private static byte[][] MATRIX_IP;   //初始置换矩阵
    private static byte[][] MATRIX_IP_;  //逆初始置换矩阵
    private static byte[][] MATRIX_PS1;  //置换选择1
    private static byte[][] MATRIX_PS2;  //置换选择2
    private static byte[][] MATRIX_E;    //扩展选择运算E
    private static byte[][] MATRIX_P;    //置换运算P
    private static byte[][] SBox_1;      //S盒1
    private static byte[][] SBox_2;      //S盒2
    private static byte[][] SBox_3;      //S盒3
    private static byte[][] SBox_4;      //S盒4
    private static byte[][] SBox_5;      //S盒5
    private static byte[][] SBox_6;      //S盒6
    private static byte[][] SBox_7;      //S盒7
    private static byte[][] SBox_8;      //S盒8
    private static byte[][][] SBox;      //所有的8个S盒

    static{
        DES.MATRIX_IP = new byte[8][8];
        DES.MATRIX_IP[0] = new byte[] {58,50,42,34,26,18,10,2};
        DES.MATRIX_IP[1] = new byte[] {60,52,44,36,28,20,12,4};
        DES.MATRIX_IP[2] = new byte[] {62,54,46,38,30,22,14,6};
        DES.MATRIX_IP[3] = new byte[] {64,56,48,40,32,24,16,8};
        DES.MATRIX_IP[4] = new byte[] {57,49,41,33,25,17,9, 1};
        DES.MATRIX_IP[5] = new byte[] {59,51,43,35,27,19,11,3};
        DES.MATRIX_IP[6] = new byte[] {61,53,45,37,29,21,13,5};
        DES.MATRIX_IP[7] = new byte[] {63,55,47,39,31,23,15,7};
    }
    static{
        DES.MATRIX_IP_ = new byte[8][8];
        DES.MATRIX_IP_[0] = new byte[] {40, 8,48,16,56,24,64,32};
        DES.MATRIX_IP_[1] = new byte[] {39, 7,47,15,55,23,63,31};
        DES.MATRIX_IP_[2] = new byte[] {28, 6,46,14,54,22,62,30};
        DES.MATRIX_IP_[3] = new byte[] {37, 5,45,13,53,21,61,29};
        DES.MATRIX_IP_[4] = new byte[] {36, 4,44,12,52,20,60,28};
        DES.MATRIX_IP_[5] = new byte[] {35, 3,43,11,51,19,59,27};
        DES.MATRIX_IP_[6] = new byte[] {34, 2,42,10,50,18,58,26};
        DES.MATRIX_IP_[7] = new byte[] {33, 1,41, 9,49,17,57,25};
    }
    static{
        DES.MATRIX_PS1 = new byte[8][];
        //C0
        DES.MATRIX_PS1[0] = new byte[]{57,49,41,33,25,17, 9, 1};
        DES.MATRIX_PS1[1] = new byte[]{58,50,42,34,26,18,10, 2};
        DES.MATRIX_PS1[2] = new byte[]{59,51,43,35,27,19,11, 3};
        DES.MATRIX_PS1[3] = new byte[]{60,52,44,36};
        //D0
        DES.MATRIX_PS1[4] = new byte[]{63,55,47,39,31,23,15,7};
        DES.MATRIX_PS1[5] = new byte[]{62,54,46,38,30,22,14,6};
        DES.MATRIX_PS1[6] = new byte[]{61,53,45,37,29,21,13,5};
        DES.MATRIX_PS1[7] = new byte[]{28,20,12,4};
    }
    static{
        DES.MATRIX_PS2 = new byte[8][6];
        DES.MATRIX_PS2[0] = new byte[]{14,17,11,24, 1, 5};
        DES.MATRIX_PS2[1] = new byte[]{ 3,28,15, 6,21,10};
        DES.MATRIX_PS2[2] = new byte[]{23,19,12, 4,26, 8};
        DES.MATRIX_PS2[3] = new byte[]{16, 7,27,20,13, 2};
        DES.MATRIX_PS2[4] = new byte[]{41,52,31,37,47,55};
        DES.MATRIX_PS2[5] = new byte[]{30,40,51,45,33,48};
        DES.MATRIX_PS2[6] = new byte[]{44,49,39,56,34,53};
        DES.MATRIX_PS2[7] = new byte[]{46,42,50,36,29,32};
    }
    static{
        DES.MATRIX_E = new byte[8][6];
        DES.MATRIX_E[0] = new byte[]{32, 1, 2, 3, 4, 5};
        DES.MATRIX_E[1] = new byte[]{ 4, 5, 6, 7, 8, 9};
        DES.MATRIX_E[2] = new byte[]{ 8, 9,10,11,12,13};
        DES.MATRIX_E[3] = new byte[]{12,13,14,15,16,17};
        DES.MATRIX_E[4] = new byte[]{16,17,18,19,20,21};
        DES.MATRIX_E[5] = new byte[]{20,21,22,23,24,25};
        DES.MATRIX_E[6] = new byte[]{24,25,26,27,28,29};
        DES.MATRIX_E[7] = new byte[]{28,29,30,31,32, 1};
    }
    static{
        DES.MATRIX_P = new byte[8][4];
        DES.MATRIX_P[0] = new byte[]{16, 7,20,21};
        DES.MATRIX_P[1] = new byte[]{29,12,28,17};
        DES.MATRIX_P[2] = new byte[]{ 1,15,23,26};
        DES.MATRIX_P[3] = new byte[]{ 5,18,31,10};
        DES.MATRIX_P[4] = new byte[]{ 2, 8,24,14};
        DES.MATRIX_P[5] = new byte[]{32,27, 3, 9};
        DES.MATRIX_P[6] = new byte[]{19,13,30, 6};
        DES.MATRIX_P[7] = new byte[]{22,11, 4,25};
    }
    static{
        DES.SBox_1 = new byte[4][16];
        DES.SBox_2 = new byte[4][16];
        DES.SBox_3 = new byte[4][16];
        DES.SBox_4 = new byte[4][16];
        DES.SBox_5 = new byte[4][16];
        DES.SBox_6 = new byte[4][16];
        DES.SBox_7 = new byte[4][16];
        DES.SBox_8 = new byte[4][16];

        DES.SBox_1[0] = new byte[]{14, 4,13, 1, 2,15,11, 8, 3,10, 6,12, 5, 9, 0, 7};
        DES.SBox_1[1] = new byte[]{ 0,15, 7, 4,14, 2,13, 1,10, 6,12,11, 9, 5, 3, 8};
        DES.SBox_1[2] = new byte[]{ 4, 1,14, 8,13, 6, 2,11,15,12, 9, 7, 3,10, 5, 0};
        DES.SBox_1[3] = new byte[]{15,12, 8, 2, 4, 9, 1, 7, 5,11, 3,14,10, 0, 6,13};

        DES.SBox_2[0] = new byte[]{15, 1, 8,14, 6,11, 3, 4, 9, 7, 2,13,12, 0, 5,10};
        DES.SBox_2[1] = new byte[]{ 3,13, 4, 7,15, 2, 8,14,12, 0, 1,10, 6, 9,11, 5};
        DES.SBox_2[2] = new byte[]{ 0,14, 7,11,10, 4,13, 1, 5, 8,12, 6, 9, 3, 2,15};
        DES.SBox_2[3] = new byte[]{13, 8,10, 1, 3,15, 4, 2,11, 6, 7,12, 0, 5,14, 9};

        DES.SBox_3[0] = new byte[]{10, 0, 9,14, 6, 3,15, 5, 1,13,12, 7,11, 4, 2, 8};
        DES.SBox_3[1] = new byte[]{13, 7, 0, 9, 3, 4, 6,10, 2, 8, 5,14,12,11,15, 1};
        DES.SBox_3[2] = new byte[]{13, 6, 4, 9, 8,15, 3, 0,11, 1, 2,12, 5,10,14, 7};
        DES.SBox_3[3] = new byte[]{ 1,10,13, 0, 6, 9, 8, 7, 4,15,14, 3,11, 5, 2,12};

        DES.SBox_4[0] = new byte[]{ 7,13,14, 3, 0, 6, 9,10, 1, 2, 8, 5,11,12, 4,15};
        DES.SBox_4[1] = new byte[]{13, 8,11, 5, 6,15, 0, 3, 4, 7, 2,12, 1,10,14, 9};
        DES.SBox_4[2] = new byte[]{10, 6, 9, 0,12,11, 7,13,15, 1, 3,14, 5, 2, 8, 4};
        DES.SBox_4[3] = new byte[]{ 3,15, 0, 6,10, 1,13, 8, 9, 4, 5,11,12, 7, 2,14};

        DES.SBox_5[0] = new byte[]{ 2,12, 4, 1, 7,10,11, 6, 8, 5, 3,15,13, 0,14, 9};
        DES.SBox_5[1] = new byte[]{14,11, 2,12, 4, 7,13, 1, 5, 0,15,10, 3, 9, 8, 6};
        DES.SBox_5[2] = new byte[]{ 4, 2, 1,11,10,13, 7, 8,15, 9,12, 5, 6, 3, 0,14};
        DES.SBox_5[3] = new byte[]{11, 8,12, 7, 1,14, 2,13, 6,15, 0, 9,10, 4, 5, 3};

        DES.SBox_6[0] = new byte[]{12, 1,10,15, 9, 2, 6, 8, 0,13, 3, 4,14, 7, 5,11};
        DES.SBox_6[1] = new byte[]{10,15, 4, 2, 7,12, 9, 5, 6, 1,13,14, 0,11, 3, 8};
        DES.SBox_6[2] = new byte[]{ 9,14,15, 5, 2, 8,12, 3, 7, 0, 4,10, 1,13,11, 6};
        DES.SBox_6[3] = new byte[]{ 4, 3, 2,12, 9, 5,15,10,11,14, 1, 7, 6, 0, 8,13};

        DES.SBox_7[0] = new byte[]{ 4,11, 2,14,15, 0, 8,13, 3,12, 9, 7, 5,10, 6, 1};
        DES.SBox_7[1] = new byte[]{13, 0,11, 7, 4, 9, 1,10,14, 3, 5,12, 2,15, 8, 6};
        DES.SBox_7[2] = new byte[]{ 1, 4,11,13,12, 3, 7,14,10,15, 6, 8, 0, 5, 9, 2};
        DES.SBox_7[3] = new byte[]{ 6,11,13, 8, 1, 4,10, 7, 9, 5, 0,15,14, 2, 3,12};

        DES.SBox_8[0] = new byte[]{13, 2, 8, 4, 6,15,11, 1,10, 9, 3,14, 5, 0,12, 7};
        DES.SBox_8[1] = new byte[]{ 1,15,13, 8,10, 3, 7, 4,12, 5, 6,11, 0,14, 9, 2};
        DES.SBox_8[2] = new byte[]{ 7,11, 4, 1, 9,12,14, 2, 0, 6,10,13,15, 3, 5, 8};
        DES.SBox_8[3] = new byte[]{ 2, 1,14, 7, 4,10, 8,13,15,12, 9, 0, 3, 5, 6,11};
    }
    static{
        DES.SBox = new byte[8][][];
        DES.SBox[0] = DES.SBox_1;
        DES.SBox[1] = DES.SBox_2;
        DES.SBox[2] = DES.SBox_3;
        DES.SBox[3] = DES.SBox_4;
        DES.SBox[4] = DES.SBox_5;
        DES.SBox[5] = DES.SBox_6;
        DES.SBox[6] = DES.SBox_7;
        DES.SBox[7] = DES.SBox_8;
    }

    /**
     * 初始置换矩阵，置换后数据第i*8+j位是i行j列元素所指的源数据的位。
     * 若将源数据从低位到高位，将每位数据依次按从左到右，从上到下，排列成8*8矩阵。
     * 显然，置换矩阵就是
     * 1.从小到大遍历偶数列，在每列中，从大到小遍历行。
     * 2.从小到大遍历奇数列，在每列中，从大到小遍历行。
     *  58,50,42,34,26,18,10,2
     *  60,52,44,36,28,20,12,4
     *  62,54,46,38,30,22,14,6
     *  64,56,48,40,32,24,16,8
     *
     *  57,49,41,33,25,17,9,1
     *  59,51,43,35,27,19,11,3
     *  61,53,45,37,29,21,13,5
     *  63,55,47,39,31,23,15,7
     * @param src 源数据，将按照初始置换矩阵，初始置换各个数据位。
     * @return out 置换结果。
     */
    public static long IP(long src){
        long out = 0L;
        int index = 0;
        for(int i=1; i<8; i+=2) {
            for(int j=7; j>-1; j--) {
                out |= ((src & (1L << (j*8+i))) >>> (j*8+i))<<index;
                index++;
            }
        }
        for(int i=0; i<8; i+=2) {
            for(int j=7; j>-1; j--) {
                out |= ((src & (1L << (j*8+i))) >>> (j*8+i))<<index;
                index++;
            }
        }
        return out;
    }
    /**
     *逆向初始置换。
     * @see DES#IP(long)
     * @param src 源数据，将按照初始置换矩阵，逆向初始置换各个数据位。
     * @return out 置换结果。
     */
    public static long IP_(long src){
        long out = 0L;
        int index = 0;
        for(int i=1; i<8; i+=2) {
            for(int j=7; j>-1; j--) {
                out |= ((src & (1L<<index))>>>index)<<(j*8+i);
                index++;
            }
        }
        for(int i=0; i<8; i+=2) {
            for(int j=7; j>-1; j--) {
                out |= ((src & (1L<<index))>>>index)<<(j*8+i);
                index++;
            }
        }
        return out;
    }

    /**
     * 置换选择1。
     * 64为密钥分为8个字节，每个字节前7位是真正的密钥，第8位是奇偶校验位。
     * 置换选择1有两个作用：
     * 1. 从64位密钥中去掉8个奇偶校验位。
     * 2. 其余56位密钥打乱重排，前28位C0,后28为D0。
     * 置换矩阵C0
     * 57,49,41,33,25,17, 9,1
     * 58,50,42,34,26,18,10,2
     * 59,51,43,35,27,19,11,3
     * 60,52,44,36
     * 置换矩阵D0
     * 63,55,47,39,31,23,15,7
     * 62,54,46,38,30,22,14,6
     * 61,53,45,37,29,21,13,5
     * 28,20,12,4
     * 若将密钥从低位到高位，依次将数据位排列成8*8矩阵。
     * 则置换矩阵C0即是
     * 1. 从第1行开始，从小到大遍历每一列，在每一列中，从第8行开始，从大到小遍历每一行。
     * 2. 直到遍历了3列
     * 3. 将第4列,从第8行开始，从大到小遍历4个元素。
     * 则置换矩阵D0即是
     * 1. 从第7列开始，从大到小遍历每一列，在每一列中，从第8行开始，从大到小遍历每一行。
     * 2. 直到遍历了3列。
     * 3. 将第4列，从第4行开始，从大到小遍历4个元素。
     * @param src 源数据，将被置换选择1。
     * @return out 置换选择1的结果。C0:1~28位，D0:29~56位，57~64位恒为0。
     */
    public static long PS1(long src){
        long out = 0L;
        int index = 0;
        //C0
        for(int i=0; i<3; i++){
            for(int j=7; j>-1; j--){
                out |=  ((src & (1L<<(j*8+i)))>>>(j*8+i))<<index;
                index++;
            }
        }
        for(int j=7; j>3; j--){
            out |=  ((src & (1L<<(j*8+3)))>>>(j*8+3))<<index;
            index++;
        }
        //D0
        for(int i=6; i>3; i--){
            for(int j=7; j>-1; j--){
                out |=  ((src & (1L<<(j*8+i)))>>>(j*8+i))<<index;
                index++;
            }
        }
        for(int j=3; j>-1; j--){
            out |=  ((src & (1L<<(j*8+3)))>>>(j*8+3))<<index;
            index++;
        }
        return out;
    }
    /**
     * 置换选择2。
     * 将Ci和Pi合并成一个56位的中间数据，置换选择2从中选择出一个48位的子密钥。
     * 置换选择2矩阵。
     * 14,17,11,24, 1, 5
     * 3,28,15, 6,21, 10
     * 23,19,12, 4,26, 8
     * 16, 7,27,20,13, 2
     * 41,52,31,37,47,55
     * 30,40,51,45,33,48
     * 44,49,39,56,34,53
     * 46,42,50,36,29,32
     * 暂时没有找到规律，所以暴力置换。
     * @see DES#PS1(long)
     * @param src 源数据，Ci:1~28位，Di:29~56位，将被置换选择2。
     * @return out 置换选择2的结果，子密钥，1~48位有效，49~64恒为0。
     */
    public static long PS2(long src){
        long out = 0L;
        int index = 0;
        for(int i=0; i<8; i++){
            for(int j=0; j<6; j++){
                out |= ((src & (1L<<(DES.MATRIX_PS2[i][j]-1)))>>>(DES.MATRIX_PS2[i][j]-1))<<index;
                index++;
            }
        }
        return out;
    }
    /**
     * 子密钥产生算法。
     * 64位密钥，其中每一个字节的最高位为奇偶校验位，不是密钥位。
     * 有效密钥为每一个字节前7位，共56位。
     * 产生16个子密钥，每个子密钥48位。
     * 1. 置换选择2
     * 2. 循环左移与置换选择2产生一个48位子密钥
     * @see DES#PS1(long)
     * @see DES#PS2(long)
     * @param key 密钥。其中8,16,24,32,40,48,56,64位为奇偶校验位。剩余位才是有效密钥位。
     * @return keys 子密钥数组，keys[i]是第i个密钥，1~48位有效，49~64位恒为0。
     */
    public static long[] getSubKeys(long key){
        long[] keys = new long[16];
        key = DES.PS1(key);
        for(int i=0; i<16; i++){
            //循环左移
            key<<=1;
            key = ((key & (1L<<28))>>>28) | (key & (~1L));
            key = ((key & (1L<<56))>>>28) | (key & (~(1L<<28)));
            //置换选择2
            keys[i] = DES.PS2(key);
        }
        return keys;
    }

    /**
     * 扩展选择运算E。
     * 选择运算E是一种扩展运算，它将32位数据扩展为48数据，以便与48位子密钥模2相加并满足替代函数组S对数据长度的要求。
     * 选择运算矩阵。
     * 32, 1, 2, 3, 4, 5
     *  4, 5, 6, 7, 8, 9
     *  8, 9,10,11,12,13
     * 12,13,14,15,16,17
     * 16,17,18,19,20,21
     * 20,21,22,23,24,25
     * 24,25,26,27,28,29
     * 28,29,30,31,32, 1
     * @param src 源数据，1~32位有效，将被扩展运算。
     * @return out 扩展运算的结果，1~48位有效，49~64位恒为0。
     */
    public static long E(long src){
        long out = 0L;
        int index = 0;

        out |= ((src & (1L<<(31)))>>>(31))<<index;
        index++;
        for(int j=0; j<5; j++){
            out |= ((src & (1L<<(j)))>>>(j))<<index;
            index++;
        }

        for(int i=1; i<7; i++){
            for(int j=-1; j<5; j++){
                out |= ((src & (1L<<(i*4+j)))>>>(i*4+j))<<index;
                index++;
            }
        }

        for(int j=-1; j<4; j++){
            out |= ((src & (1L<<(28L+j)))>>>(28+j))<<index;
            index++;
        }
        out |= ((src & (1L<<(0)))>>>(0))<<index;

        return out;
    }
    /**
     * 置换运算P。
     * 置换运算P把S盒输出的32位数据打乱重排，得到32位加密函数输出。
     * 用P置换来提供扩散，把S盒的混淆作用扩散开来。
     * 置换矩阵P
     * 16, 7,20,21
     * 29,12,28,17
     *  1,15,23,26
     *  5,18,31,10
     *  2, 8,24,14
     * 32,27, 3, 9
     * 19,13,30, 6
     * 22,11, 4,25
     * @param src 源数据，1~32位有效，将被置换运算P。
     * @return out 置换运算P结果，1~32位有效，33~64位恒为0。
     */
    public static long P(long src){
        long out = 0L;
        int index = 0;
        for(int i=0; i<8; i++){
            for(int j=0; j<4; j++){
                out |= ((src & (1L<<(DES.MATRIX_P[i][j]-1)))>>>(DES.MATRIX_P[i][j]-1))<<index;
                index++;
            }
        }
        return out;
    }
    /**
     * 加密函数。
     * 32位数据，与48位子密钥加密，获取加密数据。
     * @see DES#E(long)
     * @see DES#P(long)
     * @see DES#SBox
     * @param src   源数据，1~32位有效。
     * @param key   子密钥，1~48位有效。
     * @return out  加密结果，1~32位有效，33~64位恒为0。
     */
    public static long F(long src, long key){
        //扩展运算
        long E_src = DES.E(src);
        //中间结果
        long middle = E_src^key;
        //S盒输出
        long SBox_out = 0L;
        for(int i=0; i<8; i++){
            //行号b6b1,列号，b5b4b3b2
            int row = (int)((middle & 0b1L) | ((middle & 0b100000L)>>>4));
            int col = (int)((middle & 0b11110L)>>>1);
            SBox_out |= (( ((long)DES.SBox[i][row][col]) & 0b1111L)<<(i*4));
            middle>>>=6;
        }
        //置换运算P。
        return DES.P(SBox_out);
    }

    /**
     * DES轮运算。
     * DES加密，多轮运算，轮数等于子密钥数量，每轮依次使用一个子密钥进行加密运算。
     * 每个子密钥1~48位有效。
     * 可用用加密，解密(颠倒子密钥顺序即可)。
     * @param src 源数据，将被加密。
     * @param keys 子密钥序列，将被依次用来加密。每个子密钥1~48位有效。
     * @return out 加密结果。
     */
    public static long R(long src, long... keys){
        //初始置换
        long out = DES.IP(src);
        int L = (int)(out & 0xffffffffL);//取前32位。
        int R = (int)(out>>>32); //逻辑右移，取后32位，
        int T ;
        for(long key: keys){
            //加密
            L ^= (int)DES.F(R,key);
            //R = R;
            //变换位置。
            T = L;
            L = R;
            R = T;
        }
        //最后一次加密，不变换位置。
        out = ((long)R) & 0xffffffffL;//负数导致高位填充1。
        out |= ((long)L)<<32;
        //逆初始置换
        out = DES.IP_(out);

        return out;
    }
    /**
     * DES加密。
     * 使用子密钥产生算法产生子密钥，使用DES轮运算实现加密。
     * 每次加密一组字节，每组字节8位，故直接用long数据类型。
     * 密钥key每字节最高位是奇偶检验位。有效密钥位共56位。
     * 使用密钥给明文DES加密。
     * @see DES#getSubKeys(long)
     * @see DES#R(long, long...)
     * @param key  56位DES密钥。
     * @param src  明文8字节组序列。
     * @return out DES加密结果。
     */
    public static long[] encrypt(long key, long...src){
        //获取加密子密钥
        long[] keys = DES.getSubKeys(key);
        //解密
        long[] out = new long[src.length];
        for(int i=0; i<out.length; i++){
            out[i] = DES.R(src[i],keys);
        }
        return out;
    }
    /**
     * DES解密。
     * 使用子密钥产生算法产生子密钥，使用DES轮运算实现解密。
     * 每次解密一组字节，每组字节8位，故直接用long数据类型。
     * 密钥key每字节最高位是奇偶检验位。有效密钥位共56位。
     * 使用密钥给明文DES加密。
     * @see DES#getSubKeys(long)
     * @see DES#R(long, long...)
     * @param key  56位DES密钥。
     * @param src  密文8字节组序列。
     * @return out DES解密结果。
     */
    public static long[] decrypt(long key, long...src){
        //获取加密子密钥
        long[] keys = DES.getSubKeys(key);
        //获取解密子密钥
        for(int i=0; i<keys.length/2; i++){
            long temp = keys[i];
            keys[i] = keys[keys.length-1-i];
            keys[keys.length-1-i] = temp;
        }
        //解密
        long[] out = new long[src.length];
        for(int i=0; i<out.length; i++){
            out[i] = DES.R(src[i],keys);
        }
        return out;
    }

    public static void main(String[] args){
        Random random = new Random();
        long k = random.nextLong();
        int count = random.nextInt();

        for(int i=1; i<count; i++){
            long[] p = new long[] {random.nextLong()};
            long[] e = DES.encrypt(k,p);
            long[] d = DES.decrypt(k,e);

            if(p[0] != d[0])
                System.out.println("false");
        }
    }
}
