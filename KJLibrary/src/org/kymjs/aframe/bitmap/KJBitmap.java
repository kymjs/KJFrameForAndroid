package org.kymjs.aframe.bitmap;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.kymjs.aframe.bitmap.utils.BitmapCreate;
import org.kymjs.aframe.utils.FileUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class KJBitmap {

    private static KJBitmapConfig config; // 将配置文件设置为全局保证一次设置可一直使用
    private MemoryCache mMemoryCache;
    /** 记录所有正在下载或等待下载的任务 */
    private Set<BitmapWorkerTask> taskCollection;

    public KJBitmap(Context context) {
        if (config == null) {
            config = new KJBitmapConfig();
        }
        mMemoryCache = new MemoryCache(config.memoryCacheSize);
        taskCollection = new HashSet<BitmapWorkerTask>();
    }

    /**
     * 设置图片加载的配置器
     * 
     * @param config
     *            将以static修饰的配置器，保证一次设置可一直使用
     */
    public static void setConfig(KJBitmapConfig config) {
        KJBitmap.config = config;
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
        final String imageKey = String.valueOf(imageView.getId());
        final Bitmap bitmap = mMemoryCache.get(imageKey);
        if (bitmap != null) {
            if (imageView instanceof ImageView) {
                ((ImageView) imageView).setImageBitmap(bitmap);
            } else {
                imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
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

    /************************ 异步下载图片的任务类 *******************************/
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private View imageview;

        public BitmapWorkerTask(View imageview) {
            this.imageview = imageview;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitmap(params[0]);
            if (bitmap != null) {
                // 图片下载完成后缓存到LrcCache中
                mMemoryCache.put(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageview instanceof ImageView) {
                if (bitmap != null) {
                    ((ImageView) imageview).setImageBitmap(bitmap);
                }
            } else {
                imageview.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            taskCollection.remove(this);
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         * 
         * @param imageUrl
         *            图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(config.timeOut);
                con.setReadTimeout(config.timeOut * 2);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();
                byte[] data = FileUtils.input2byte(con.getInputStream());
                bitmap = BitmapCreate.bitmapFromByteArray(data, 0, data.length,
                        config.width, config.height);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return bitmap;
        }
    }
}
