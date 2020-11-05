package mortal.learn.gdut.crypt.des;

import mortal.learn.gdut.crypt.MyApp;
public class DES {

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
     * 若将密钥从地位到高位，依次将数据位排列成8*8矩阵。
     * 则置换矩阵C0即是
     * 1. 从第1行开始，从小到大遍历每一列，在每一列中，从第8行开始，从大到小遍历每一行。
     * 2. 直到遍历了3列
     * 3. 将第4列,从第8行开始，从大到小遍历4个元素。
     * 则置换矩阵D0即是
     * 1. 从第7列开始，从大到小遍历每一列，在每一列中，从第8行开始，从大到小遍历每一行。
     * 2. 直到遍历了3列。
     * 3. 将第4列，从第4行开始，从大到小遍历4个元素。
     * @param src 源数据，将被置换选择1。
     * @return out 置换选择1的结果。C0:1~28位，D0:29~56位。
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

    public static void main(String[] args){
        long value = ~(1L<<(3));
        long ps1 = DES.PS1(value);

        System.out.println("value = " + MyApp.bytes2string(MyApp.getBytes(value)));
        System.out.println("ip    = " + MyApp.bytes2string(MyApp.getBytes(ps1)));
    }
}
