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
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 来自网络：图片缩放控件（支持缩放、拖动、双击、旋转）<br>
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class ScaleImageView extends ImageView {

    public enum ImageState {
        NONE, // 初始状态
        DRAG, // 拖动
        ZOOM, // 缩放
        ROTATE, // 旋转
        ZOOM_OR_ROTATE // 缩放或旋转
    }

    private ImageState mode = ImageState.NONE;

    static final float MAX_SCALE = 2.5f;

    private float imageW; // 图片宽度
    private float imageH; // 图片高度
    private float rotatedImageW;
    private float rotatedImageH;
    private float viewW;
    private float viewH;

    private final Matrix matrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();

    private final PointF pA = new PointF();
    private final PointF pB = new PointF();
    private final PointF mid = new PointF();
    private final PointF lastClickPos = new PointF();

    private long lastClickTime = 0; // 上次点击时间（用于计算双击）
    private double rotation = 0.0;
    private float dist = 1f; // 两点间距

    private boolean canRotate = true; // 是否开启旋转图片功能
    private boolean canDoubleClick = true; // 是否开启双击缩放功能

    public void setCanDoubleClick(boolean canDoubleClick) {
        this.canDoubleClick = canDoubleClick;
    }

    public boolean canDoubleClick() {
        return canDoubleClick;
    }

    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }

    public boolean canRotate() {
        return canRotate;
    }

    public ScaleImageView(Context context) {
        super(context);
        initView();
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setImageWidthHeight();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setImageWidthHeight();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setImageWidthHeight();
    }

    private void setImageWidthHeight() {
        Drawable d = getDrawable();
        if (d == null) {
            return;
        }
        imageW = rotatedImageW = d.getIntrinsicWidth();
        imageH = rotatedImageH = d.getIntrinsicHeight();
        initImage();
    }

    /**
     * 图片大小改变时回调
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewW = w;
        viewH = h;
        if (oldw == 0) { // 如果图片还没有计算大小
            initImage();
        } else {
            fixScale(); // 固定大小
            fixTranslation();
            setImageMatrix(matrix);
        }
    }

    /**
     * 首次加载，初始化图片控件
     */
    private void initImage() {
        if (viewW <= 0 || viewH <= 0 || imageW <= 0 || imageH <= 0) {
            return;
        }
        mode = ImageState.NONE;
        matrix.setScale(0, 0);
        fixScale();
        fixTranslation();
        setImageMatrix(matrix);
    }

    /**
     * 设置matrix，记录当前图片大小
     */
    private void fixScale() {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);
        float minScale = Math.min(viewW / rotatedImageW, viewH / rotatedImageH);
        if (curScale < minScale) {
            if (curScale > 0) {
                double scale = minScale / curScale;
                p[0] = (float) (p[0] * scale);
                p[1] = (float) (p[1] * scale);
                p[3] = (float) (p[3] * scale);
                p[4] = (float) (p[4] * scale);
                matrix.setValues(p);
            } else {
                matrix.setScale(minScale, minScale);
            }
        }
    }

    /**
     * 最大缩放值
     */
    private float maxPostScale() {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);

        float minScale = Math.min(viewW / rotatedImageW, viewH / rotatedImageH);
        float maxScale = Math.max(minScale, MAX_SCALE);
        return maxScale / curScale;
    }

    /**
     * 移动matrix
     */
    private void fixTranslation() {
        RectF rect = new RectF(0, 0, imageW, imageH);
        matrix.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (width < viewW) {
            deltaX = (viewW - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewW) {
            deltaX = viewW - rect.right;
        }

        if (height < viewH) {
            deltaY = (viewH - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewH) {
            deltaY = viewH - rect.bottom;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        // 主点按下 ：记录当前matrix，以及初始化按下坐标、图片状态
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            pA.set(event.getX(), event.getY());
            pB.set(event.getX(), event.getY());
            mode = ImageState.DRAG;
            break;
        // 副点按下 ：如果间距大于10，记录此时matrix，以及两点的坐标、图片状态
        case MotionEvent.ACTION_POINTER_DOWN:
            if (event.getActionIndex() > 1) // 超过两个点返回
                break;
            dist = spacing(event.getX(0), event.getY(0), event.getX(1),
                    event.getY(1));
            if (dist > 10f) { // 如果连续两点距离大于10，则判定为多点模式
                savedMatrix.set(matrix);
                pA.set(event.getX(0), event.getY(0));
                pB.set(event.getX(1), event.getY(1));
                mid.set((event.getX(0) + event.getX(1)) / 2,
                        (event.getY(0) + event.getY(1)) / 2);
                mode = ImageState.ZOOM_OR_ROTATE;
            }
            break;

        // 主点或负点抬起
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            if (mode == ImageState.DRAG) {
                if (spacing(pA.x, pA.y, pB.x, pB.y) < 50) {
                    long now = System.currentTimeMillis();
                    if (now - lastClickTime < 500
                            && spacing(pA.x, pA.y, lastClickPos.x,
                                    lastClickPos.y) < 50) {
                        doubleClick(pA.x, pA.y);
                        now = 0;
                    }
                    lastClickPos.set(pA);
                    lastClickTime = now;
                }
            } else if (mode == ImageState.ROTATE) {
                int level = (int) Math.floor((rotation + Math.PI / 4)
                        / (Math.PI / 2));
                if (level == 4)
                    level = 0;
                matrix.set(savedMatrix);
                matrix.postRotate(90 * level, mid.x, mid.y);
                if (level == 1 || level == 3) {
                    float tmp = rotatedImageW;
                    rotatedImageW = rotatedImageH;
                    rotatedImageH = tmp;
                    fixScale();
                }
                fixTranslation();
                setImageMatrix(matrix);
            }
            mode = ImageState.NONE;
            break;

        // 拖动时
        case MotionEvent.ACTION_MOVE:
            if (mode == ImageState.ZOOM_OR_ROTATE) {
                PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x,
                        event.getY(1) - event.getY(0) + pA.y);
                double a = spacing(pB.x, pB.y, pC.x, pC.y);
                double b = spacing(pA.x, pA.y, pC.x, pC.y);
                double c = spacing(pA.x, pA.y, pB.x, pB.y);
                if (a >= 10) {
                    double cosB = (a * a + c * c - b * b) / (2 * a * c);
                    double angleB = Math.acos(cosB);
                    double PID4 = Math.PI / 4;
                    if (angleB > PID4 && angleB < 3 * PID4) {
                        mode = ImageState.ROTATE;
                        rotation = 0;
                    } else {
                        mode = ImageState.ZOOM;
                    }
                }
            }

            if (mode == ImageState.DRAG) {
                matrix.set(savedMatrix);
                pB.set(event.getX(), event.getY());
                matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
                fixTranslation();
                setImageMatrix(matrix);
            } else if (mode == ImageState.ZOOM) {
                float newDist = spacing(event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1));
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    float tScale = Math.min(newDist / dist, maxPostScale());
                    matrix.postScale(tScale, tScale, mid.x, mid.y);
                    fixScale();
                    fixTranslation();
                    setImageMatrix(matrix);
                }
            } else if (mode == ImageState.ROTATE) {
                if (canRotate) {
                    PointF pC = new PointF(
                            event.getX(1) - event.getX(0) + pA.x, event.getY(1)
                                    - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (b > 10) {
                        double cosA = (b * b + c * c - a * a) / (2 * b * c);
                        double angleA = Math.acos(cosA);
                        double ta = pB.y - pA.y;
                        double tb = pA.x - pB.x;
                        double tc = pB.x * pA.y - pA.x * pB.y;
                        double td = ta * pC.x + tb * pC.y + tc;
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA;
                        }
                        rotation = angleA;
                        matrix.set(savedMatrix);
                        matrix.postRotate((float) (rotation * 180 / Math.PI),
                                mid.x, mid.y);
                        setImageMatrix(matrix);
                    }
                }
            }
            break;
        }
        return true;
    }

    /**
     * 两点的距离
     */
    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 双击时调用
     */
    private void doubleClick(float x, float y) {
        if (canDoubleClick) {
            float p[] = new float[9];
            matrix.getValues(p);
            float curScale = Math.abs(p[0]) + Math.abs(p[1]);

            float minScale = Math.min(viewW / rotatedImageW, viewH
                    / rotatedImageH);
            if (curScale <= minScale + 0.01) { // 放大
                float toScale = Math.max(minScale, MAX_SCALE) / curScale;
                matrix.postScale(toScale, toScale, x, y);
            } else { // 缩小
                float toScale = minScale / curScale;
                matrix.postScale(toScale, toScale, x, y);
                fixTranslation();
            }
            setImageMatrix(matrix);
        }
    }
}