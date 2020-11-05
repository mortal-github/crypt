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

    public static void main(String[] args){
        long value = ~(1L<<(6+8+8+8+8+8+8+8));
        long ip = DES.IP(value);
        long ip_ = DES.IP_(ip);
        System.out.println("value = " + MyApp.bytes2string(MyApp.getBytes(value)));
        System.out.println("ip    = " + MyApp.bytes2string(MyApp.getBytes(ip)));
        System.out.println("ip_   = " + MyApp.bytes2string(MyApp.getBytes(ip_)));

    }
}
