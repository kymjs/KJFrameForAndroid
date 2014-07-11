package org.kymjs.aframe.bitmap.utils;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * 不会发生OOM的 BitmapFactory
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class BitmapCreate {
    /**
     * 获取一个指定大小的bitmap
     * 
     * @param res
     *            Resources
     * @param resId
     *            图片ID
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap bitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options = BitmapHelper.calculateInSampleSize(options, reqWidth,
                reqHeight);
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 获取一个指定大小的bitmap
     * 
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap bitmapFromFile(String pathName, int reqWidth,
            int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options = BitmapHelper.calculateInSampleSize(options, reqWidth,
                reqHeight);
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 获取一个指定大小的bitmap
     * 
     * @param data
     *            Bitmap的byte数组
     * @param offset
     *            image从byte数组创建的起始位置
     * @param length
     *            the number of bytes, 从offset处开始的长度
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap bitmapFromByteArray(byte[] data, int offset,
            int length, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, length, options);
        options = BitmapHelper.calculateInSampleSize(options, reqWidth,
                reqHeight);
        return BitmapFactory.decodeByteArray(data, offset, length, options);
    }

    /**
     * 获取一个指定大小的bitmap
     * 
     * @param is
     *            从输入流中读取Bitmap
     * @param outPadding
     *            If not null, return the padding rect for the bitmap if it
     *            exists, otherwise set padding to [-1,-1,-1,-1]. If no bitmap
     *            is returned (null) then padding is unchanged.
     * @param reqWidth
     *            目标宽度
     * @param reqHeight
     *            目标高度
     */
    public static Bitmap bitmapFromByteArray(InputStream is, Rect outPadding,
            int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, outPadding, options);
        options = BitmapHelper.calculateInSampleSize(options, reqWidth,
                reqHeight);
        return BitmapFactory.decodeStream(is, outPadding, options);
    }
}
