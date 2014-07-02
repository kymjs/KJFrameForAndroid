package org.kymjs.aframe.ui.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 图片缩放控件(支持双指缩放、双击放大)
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-6-12
 */
public class ScaleImageView extends ImageView {

    public enum ImageState {
        NONE, // 初始状态
        DRAG, // 拖动
        ZOOM // 缩放
    }

    // 判断双击需要
    private final int DOUBLE_TAP_TIMEOUT = 200;
    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    /** 图片资源 */
    private Bitmap imageRes = null;
    /** 最小缩放比例 */
    final float minScaleR = 1F;
    /** 最大缩放比例 */
    final float MAX_SCALE = 8.5F;
    // 图片大小
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    ImageState mode = ImageState.NONE; // 当前模式
    Activity aty;

    /** 存储float类型的x，y值，就是你点下的坐标的X和Y */
    PointF prev = new PointF(); // 上一次
    PointF mid = new PointF(); // 当前
    float dist = 1F;

    public ScaleImageView(Activity aty, Bitmap bitmap) {
        super(aty);
        this.setBackgroundColor(0xff000000);// 改背景为黑色
        this.aty = aty;
        this.imageRes = bitmap; // 保存图片资源
        setupView();
    }

    private void setupView() {
        setScaleType(ScaleType.MATRIX);
        setImageBitmap(imageRes);
        bitmapCenter(matrix, imageRes);
        setImageMatrix(matrix);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 主点按下
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    prev.set(event.getX(), event.getY());// 保存主点坐标
                    mode = ImageState.DRAG;
                    if (isConsideredDoubleTap(mCurrentDownEvent,
                            mPreviousUpEvent, event)) {
                        scaleImg(); // 放大
                    }
                    mCurrentDownEvent = MotionEvent.obtain(event);
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
                        matrix.postTranslate(event.getX() - prev.x,
                                event.getY() - prev.y);
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
                CheckView();
                return true;
            }
        });
    }

    /**
     * 按照1.5倍放大图片
     */
    public void scaleImg() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (p[0] > MAX_SCALE) { // 限制最大比例
            matrix.postScale(1, 1, prev.x, prev.y);
        } else {
            matrix.postScale(1.5F, 1.5F, prev.x, prev.y);
        }
        this.setImageMatrix(matrix);
    }

    /**
     * 刷新图片
     */
    public void refresh(Bitmap bitmap) {
        this.imageRes = bitmap;
        setupView();
    }

    /**
     * 横向、纵向居中
     */
    private void bitmapCenter(Matrix matrix, Bitmap bitmap) {
        if (bitmap == null || matrix == null) {
            return;
        }
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        Matrix m = new Matrix();
        m.set(matrix); // 图片显示区域
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect); // 将Matrix写入到Rect中
        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;
        // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
        int screenHeight = dm.heightPixels;
        if (height < screenHeight) {
            deltaY = (screenHeight - height) / 2 - rect.top; // 减去原始高度
        } else if (rect.top > 0) {
            deltaY = -rect.top; // 减掉上端空白的高度
        } else if (rect.bottom < screenHeight) {
            deltaY = this.getHeight() - rect.bottom;
        }

        int screenWidth = dm.widthPixels;
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
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ImageState.ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        bitmapCenter(matrix, imageRes);
    }

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
}
