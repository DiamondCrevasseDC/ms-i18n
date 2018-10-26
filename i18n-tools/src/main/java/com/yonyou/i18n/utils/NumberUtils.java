/**
 *
 */
package com.yonyou.i18n.utils;

/**
 * 主要用于流水号的生成
 *
 * @author wenfan
 */
public class NumberUtils {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        System.out.println(getSeqNumByLong(6L, 5));
    }

    /**
     * 获取流水号
     *
     * @param l        数字值
     * @param bitCount 最少流水号
     * @return
     */
    public static String getSeqNumByLong(Long l, int bitCount) {
        String seqNum = String.format("%0" + bitCount + "d", l);
        return seqNum;
    }

}
