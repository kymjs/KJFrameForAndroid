/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.kjframe.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class RoundImageView extends ImageView {
    private int mBorderThickness = 2; // 厚度
    private int mBorderOutsideColor = 0; // 外边框
    private int mBorderInsideColor = 0xffffffff; // 内边框
    private Bitmap currentBitmap; // 当前显示的bitmap

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initCurrentBitmap() {
        Bitmap temp = null;
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            temp = ((BitmapDrawable) drawable).getBitmap();
        }
        if (temp != null) {
            currentBitmap = temp;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initCurrentBitmap();
        if (currentBitmap == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        int width = getWidth();
        int height = getHeight();
        int radius = 0; // 半径
        if (mBorderInsideColor != 0 && mBorderOutsideColor != 0) { // 画两个边框
            radius = (width < height ? width : height) / 2 - 2
                    * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2, width,
                    height, mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness
                    + mBorderThickness / 2, width, height, mBorderOutsideColor);
        } else if (mBorderInsideColor != 0 && mBorderOutsideColor == 0) { // 画一个边框
            radius = (width < height ? width : height) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, width,
                    height, mBorderInsideColor);
        } else if (mBorderInsideColor == 0 && mBorderOutsideColor != 0) {// 画一个边框
            radius = (width < height ? width : height) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, width,
                    height, mBorderOutsideColor);
        } else { // 没有边框
            radius = (width < height ? width : height) / 2;
        }
        Bitmap roundBitmap = getCroppedRoundBitmap(currentBitmap, radius);
        canvas.drawBitmap(roundBitmap, width / 2 - radius, height / 2 - radius,
                null);
    }

    /**
     * 获取裁剪后的圆形图片
     * 
     * @param radius
     *            半径
     * @param bmp
     *            要被裁减的图片
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // 回收资源
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int w, int h,
            int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(w / 2, h / 2, radius, paint);
    }

    /************************ 供外部调用的方法 ******************************/

    /**
     * 必须直接或间接被调用至少一次，也就是如果currentBitmap不赋初值，则不会显示图片
     */
    public void setBitmapRes(Bitmap bitmap) {
        currentBitmap = bitmap;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmapRes(bm);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setBitmapRes(BitmapFactory.decodeResource(getResources(), resId));
    }

    /**
     * 边框粗细
     */
    public int getBorderThickness() {
        return mBorderThickness;
    }

    /**
     * 设置边框粗细
     */
    public void setBorderThickness(int borderThickness) {
        this.mBorderThickness = borderThickness;
    }

    /**
     * 外圈颜色
     */
    public int getBorderOutsideColor() {
        return mBorderOutsideColor;
    }

    /**
     * 设置外圈颜色
     */
    public void setBorderOutsideColor(int borderOutsideColor) {
        this.mBorderOutsideColor = borderOutsideColor;
    }

    /**
     * 内圈颜色
     */
    public int getBorderInsideColor() {
        return mBorderInsideColor;
    }

    /**
     * 设置内圈颜色
     */
    public void setBorderInsideColor(int borderInsideColor) {
        this.mBorderInsideColor = borderInsideColor;
    }
}
