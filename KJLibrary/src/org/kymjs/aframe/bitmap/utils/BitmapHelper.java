package org.kymjs.aframe.bitmap.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapHelper {
    /**
     * 图片压缩方法
     * 
     * @param bitmap
     */
    public static void imageZoom(Bitmap bitmap) {
        // 图片允许最大空间 单位：KB
        double maxSize = 40.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = scale(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
    }

    /***
     * 图片的缩放方法
     * 
     * @param src
     *            ：源图片资源
     * @param newWidth
     *            ：缩放后宽度
     * @param newHeight
     *            ：缩放后高度
     */
    public static Bitmap scale(Bitmap src, double newWidth, double newHeight) {
        float width = src.getWidth();
        float height = src.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, (int) width,
                (int) height, matrix, true);
        src = null;
        return bitmap;
    }

    /**
     * 图片的缩放方法
     * 
     * @param src
     *            ：源图片资源
     * @param scaleMatrix
     *            ：缩放规则
     */
    public static Bitmap scale(Bitmap src, Matrix scaleMatrix) {
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), scaleMatrix, true);
        src = null;
        return bitmap;
    }

    /**
     * 图片的缩放方法
     * 
     * @param src
     *            ：源图片资源
     * @param scaleX
     *            ：横向缩放比例
     * @param scaleY
     *            ：纵向缩放比例
     */
    public static Bitmap scale(Bitmap src, float scaleX, float scaleY) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        Bitmap bitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
        src = null;
        return bitmap;
    }

    /**
     * 图片的缩放方法
     * 
     * @param src
     *            ：源图片资源
     * @param scale
     *            ：缩放比例
     */
    public static Bitmap scale(Bitmap src, float scale) {
        return scale(src, scale, scale);
    }
}
