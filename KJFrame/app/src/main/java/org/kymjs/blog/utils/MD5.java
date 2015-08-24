/**
 * 
 */
package org.kymjs.blog.utils;

import java.security.MessageDigest;

/**
 * 
 * @author coder
 * 
 *         2014-6-9 下午3:37:12
 */
public class MD5 {

    private static final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55,
            56, 57, 97, 98, 99, 100, 101, 102 };

    public static String hexdigest(String paramString) {
        try {
            String str = hexdigest(paramString.getBytes());
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String hexdigest(byte[] paramArrayOfByte) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            int i = 0;
            int j = 0;
            while (true) {
                if (i >= 16)
                    return new String(arrayOfChar);
                int k = arrayOfByte[i];
                int m = j + 1;
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                j = m + 1;
                arrayOfChar[m] = hexDigits[(k & 0xF)];
                i++;
            }
        } catch (Exception localException) {
        }
        return null;
    }
}
