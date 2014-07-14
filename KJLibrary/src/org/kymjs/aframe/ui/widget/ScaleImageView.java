/*
 * Copyright (c) 2014-2015, kymjs 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.ui.widget;

import org.kymjs.aframe.utils.DensityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 图片缩放控件（支持缩放、拖动、双击）
 * 
 * @update 重构代码，优化执行效率，优化阅读
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-6-12
 */
public class ScaleImageView extends ImageView {
    public enum ImageState {
        NONE, // 初始状态
        DRAG, // 拖动
        ZOOM // 缩放
    }

    // constant
    private float MIN_SCALE = 0F; // 缩放比例
    private float MAX_SCALE = 0F; // 缩放比例
    ImageState mode = ImageState.NONE; // 当前模式

    // data
    private Bitmap imageRes = null;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Activity aty;

    public ScaleImageView(Activity aty, Bitmap bitmap) {
        super(aty);
        this.aty = aty;
        this.imageRes = bitmap; // 将图片设置成适合屏幕展示的大小
        setupView(false); // 设置view属性
        initScale(bitmap); // 初始化缩放比例
    }

    /**
     * 初始化最大最小缩放比例
     */
    private void initScale(Bitmap src) {
        int screenWidth = DensityUtils.getDialogW(aty);
        int screenHeight = DensityUtils.getScreenH(aty);
        float minX = screenWidth * 0.666f / (float) src.getWidth();
        float minY = screenHeight * 0.666f / (float) src.getHeight();
        MIN_SCALE = minX < minY ? minX : minY;
        float maxX = screenWidth / (float) src.getWidth();
        float maxY = screenHeight / (float) src.getHeight();
        MAX_SCALE = maxX > maxY ? maxX : maxY;
    }

    /**
     * 更新最大最小缩放比例
     */
    private void refreshScale() {
        int screenH = DensityUtils.getScreenH(aty);
        int screenW = DensityUtils.getScreenW(aty);
        float p[] = new float[9];
        matrix.getValues(p);
        // 当前图片宽高（p[0]、p[4]表示图片在XY方向上的缩放比）
        float height = imageRes.getHeight() * p[4];
        float width = imageRes.getWidth() * p[0];

        float minX = screenW * 0.666f / width;
        float minY = screenH * 0.666f / height;
        MIN_SCALE = minX < minY ? minX : minY;
        float maxX = screenW / (float) width;
        float maxY = screenH / (float) height;
        MAX_SCALE = maxX < maxY ? maxX : maxY;
    }

    /**
     * 设置控件属性
     * 
     * @param newLoad
     *            ：是否首次加载
     */
    private void setupView(boolean newLoad) {
        setScaleType(ScaleType.MATRIX);
        setBackgroundColor(0xff000000);
        setImageBitmap(imageRes);
        bitmapCenter(matrix, imageRes, newLoad);
        setImageMatrix(matrix);
        setOnTouchListener(new ImageOnTouchListener());
    }

    /**
     * 刷新图片
     */
    public void refresh(Bitmap bitmap) {
        this.refresh(bitmap, false);
    }

    /**
     * 刷新图片
     */
    public void refresh(Bitmap bitmap, boolean newLoad) {
        this.imageRes = bitmap;
        setupView(newLoad);
    }

    /**
     * 将图片在屏幕居中显示
     */
    public void bitmapCenter(Matrix matrix, Bitmap bitmap) {
        this.bitmapCenter(matrix, bitmap, false);
    }

    /**
     * 将图片在屏幕居中显示
     */
    private void bitmapCenter(Matrix matrix, Bitmap bitmap, boolean newLoad) {
        if (bitmap == null || matrix == null) {
            return;
        }
        Matrix m = new Matrix();
        m.set(matrix);
        // 图片显示区域
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        // 将Matrix应用到Rect中并获取显示区域的宽高
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        int screenHeight = DensityUtils.getScreenH(aty);
        int screenWidth = DensityUtils.getScreenW(aty);
        if (newLoad) { // 将图片设置成适合屏幕显示的大小
            float X = screenWidth * 0.666f / (float) bitmap.getWidth();
            float Y = screenHeight * 0.666f / (float) bitmap.getHeight();
            float scale = Math.min(X, Y);
            matrix.setScale(scale, scale);
            bitmapCenter(matrix, bitmap, false);
        }
        // 纵坐标位移
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top; // 减去原始高度
        } else if (rect.top > 0) {
            deltaY = -rect.top; // 减掉上端空白的高度
        } else if (rect.bottom < screenHeight) {
            deltaY = this.getHeight() - rect.bottom;
        }
        // 横坐标位移
        if (width < screenWidth) {
            deltaX = (screenWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < screenWidth) {
            deltaX = screenWidth - rect.right;
        }
        // 从当前点移动
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 控件的触摸事件监听器类、以及与触摸事件相关的方法
     * 
     * kymjs(kymjs123@gmail.com)
     */
    class ImageOnTouchListener implements OnTouchListener {
        /** 存储float类型的x，y值，就是你点下的坐标的X和Y */
        private PointF prev = new PointF(); // 上一次
        private PointF mid = new PointF(); // 当前
        private float dist = 1F; // 两次点击的间距
        private boolean normal = true; // 正常状态还是双击放大状态
        // 判断双击需要
        private final int DOUBLE_TAP_TIMEOUT = 200;
        private MotionEvent mCurrentDownEvent;
        private MotionEvent mPreviousUpEvent;

        /**
         * 两点的距离
         */
        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }

        /**
         * 两点的中点
         */
        private void midPoint(PointF point, MotionEvent event) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }

        /**
         * 判断两次按下时间间隔
         */
        private boolean isConsideredDoubleTap(MotionEvent firstDown,
                MotionEvent firstUp, MotionEvent secondDown) {
            if (firstDown == null || firstUp == null) {
                return false;
            }
            if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
                return false;
            }
            int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
            int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
            firstUp.recycle();
            firstDown.recycle();
            return deltaX * deltaX + deltaY * deltaY < 10000;
        }

        /**
         * 双击图片的事件：放大到宽或高至少一个达到全屏
         */
        public void doubleClick() {
            refreshScale();
            if (normal) {
                matrix.postScale(MAX_SCALE, MAX_SCALE, prev.x, prev.y);
            } else {
                matrix.postScale(MIN_SCALE, MIN_SCALE, prev.x, prev.y);
            }
            bitmapCenter(matrix, imageRes);
            setImageMatrix(matrix);
            normal = !normal;
        }

        /**
         * 图片位置或大小改变后恢复之前的状态(双指缩放后或拖动后调用)
         */
        private void resetView() {
            refreshScale();
            float p[] = new float[9];
            matrix.getValues(p);
            if (mode == ImageState.ZOOM) {
                if (p[0] < MIN_SCALE) {
                    matrix.setScale(MIN_SCALE, MIN_SCALE);
                }
                if (p[0] > MAX_SCALE + 1) { // 双指缩放适当多放大一点
                    matrix.set(savedMatrix);
                }
            }
            bitmapCenter(matrix, imageRes);
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                prev.set(event.getX(), event.getY());// 保存主点坐标
                mode = ImageState.DRAG;
                mCurrentDownEvent = MotionEvent.obtain(event);
                if (isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent,
                        event)) {
                    doubleClick();
                }
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                // 如果连续两点距离大于10，则判定为多点模式
                if (spacing(event) > 10F) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ImageState.ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
                mPreviousUpEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = ImageState.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ImageState.DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                } else if (mode == ImageState.ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10) {
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
            }
            setImageMatrix(matrix);
            resetView();
            return true;
        }
    }
}
