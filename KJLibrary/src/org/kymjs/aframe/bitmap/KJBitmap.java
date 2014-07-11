package org.kymjs.aframe.bitmap;

import org.kymjs.aframe.bitmap.utils.BitmapCreate;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class KJBitmap {

    private static KJBitmap instance;

    public static synchronized KJBitmap create(Context ctx) {
        if (instance == null) {
            instance = new KJBitmap(ctx.getApplicationContext());
        }
        return instance;
    }

    private KJBitmap(Context context) {
        cxt = context;
        config = new KJBitmapConfig();
        mMemoryCache = new MemoryCache(config.memoryCacheSize);
    }

    /************************************************************************/
    private Context cxt;
    private KJBitmapConfig config;
    private MemoryCache mMemoryCache;

    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);
        final Bitmap bitmap = mMemoryCache.get(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(config.loadingBitmap);
            BitmapWorkerTask task = new BitmapWorkerTask();
            task.execute(resId);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        // 在后台加载图片。
        @Override
        protected Bitmap doInBackground(Integer... params) {
            final Bitmap bitmap = BitmapCreate.bitmapFromResource(
                    cxt.getResources(), params[0], 100, 100);
            mMemoryCache.put(String.valueOf(params[0]), bitmap);
            return bitmap;
        }
    }
}
