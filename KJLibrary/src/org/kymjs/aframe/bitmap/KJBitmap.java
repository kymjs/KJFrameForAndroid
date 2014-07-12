package org.kymjs.aframe.bitmap;

import java.util.HashSet;
import java.util.Set;

import org.kymjs.aframe.KJLoger;
import org.kymjs.aframe.bitmap.utils.BitmapCreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

/**
 * The BitmapLibrary's core classes
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-7-11
 */
public class KJBitmap {
    /**
     * 图片加载的配置器：以static修饰的配置器，保证一次设置可一直使用
     */
    public static KJBitmapConfig config;
    /** 记录所有正在下载或等待下载的任务 */
    private Set<BitmapWorkerTask> taskCollection;
    private MemoryCache mMemoryCache;

    public KJBitmap(Context context) {
        if (config == null) {
            config = new KJBitmapConfig();
        }
        mMemoryCache = new MemoryCache(config.memoryCacheSize);
        taskCollection = new HashSet<BitmapWorkerTask>();
    }

    /**
     * 加载网络图片
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    public void display(View imageView, String imageUrl) {
        if (config.openProgress) {
            loadImageWithProgress(imageView, imageUrl);
        } else {
            loadImage(imageView, imageUrl);
        }
    }

    /**
     * 显示加载中的环形等待条
     */
    private void loadImageWithProgress(View imageView, String imageUrl) {
        loadImage(imageView, imageUrl);
    }

    /**
     * 加载图片（核心方法）
     * 
     * @param imageView
     *            要显示图片的控件(ImageView设置src，普通View设置bg)
     * @param imageUrl
     *            图片的URL
     */
    private void loadImage(View imageView, String imageUrl) {
        if (config.callBack != null)
            config.callBack.imgLoading(imageView);
        final Bitmap bitmap = mMemoryCache.get(imageUrl);
        KJLoger.debug("========" + (bitmap == null));
        if (bitmap != null) {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(bitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
        } else {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(config.loadingBitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(
                        config.loadingBitmap));
            }
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            taskCollection.add(task);
            task.execute(imageUrl);
        }
    }

    /********************* 异步获取Bitmap并设置image的任务类 *********************/
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private View imageView;

        public BitmapWorkerTask(View imageview) {
            this.imageView = imageview;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            // 从指定链接调取image
            byte[] res = config.imgLoader.loadImage(params[0]);
            if (res != null) {
                bitmap = BitmapCreate.bitmapFromByteArray(res, 0, res.length,
                        config.width, config.height);
            }
            if (bitmap != null) {
                // 图片载入完成后缓存到LrcCache中
                mMemoryCache.put(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView instanceof ImageView) {
                if (bitmap != null) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                }
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            if (config.callBack != null)
                config.callBack.imgLoadSuccess(imageView);
            taskCollection.remove(this);
        }
    }
}
