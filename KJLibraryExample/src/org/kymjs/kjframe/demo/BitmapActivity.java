package org.kymjs.kjframe.demo;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.bitmap.BitmapDownloader;
import org.kymjs.kjframe.bitmap.helper.BitmapCreate;
import org.kymjs.kjframe.demo.bean.ImageData;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BitmapActivity extends KJActivity {

    @BindView(id = R.id.imageView1, click = true)
    private ImageView mImg1;
    @BindView(id = R.id.imageView2, click = true)
    private ImageView mImg2;
    @BindView(id = R.id.imageView3, click = true)
    private ImageView mImg3;
    @BindView(id = R.id.imageView4, click = true)
    private ImageView mImg4;

    @BindView(id = R.id.textView1)
    private TextView mTv1;
    @BindView(id = R.id.textView2)
    private TextView mTv2;
    @BindView(id = R.id.textView3)
    private TextView mTv3;
    @BindView(id = R.id.textView4)
    private TextView mTv4;
    @BindView(id = R.id.button, click = true)
    private Button mBtn;
    @BindView(id = R.id.oomcheck, click = true)
    private Button mOOMBtn;

    @Override
    public void setRootView() {
        setContentView(R.layout.bitmap);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mTv1.setText("使用控件宽高显示图片(默认)");
        mTv2.setText("强制显示原图(可能OOM)");
        mTv3.setText("加载过程中自定义显示过程(基础设置)");
        mTv4.setText("使用回调自定义显示过程(高级设置)");
        mBtn.setText("保存网络图片到本地");
        mOOMBtn.setText("OOM测试");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.imageView1:
            display1();
            break;
        case R.id.imageView2:
            display2();
            break;
        case R.id.imageView3:
            display3();
            break;
        case R.id.imageView4:
            display4();
            break;
        case R.id.button:
            save();
            break;
        case R.id.oomcheck:
            showActivity(aty, OOMCheckActivity.class);
            break;
        }
    }

    private void display1() {
        KJBitmap kjb = KJBitmap.create();
        // kjb.display(mImg1, FileUtils.getSDCardPath() + "1.jpg"); // 加载本地图片的方法
        kjb.display(
                mImg1,
                "http://static.oschina.net/uploads/space/2014/1202/142217_a864_12.jpg",
                R.drawable.ic_launcher);
    }

    private void display2() {
        KJBitmap kjb = KJBitmap.create();
        kjb.display(
                mImg2,
                "http://static.oschina.net/uploads/space/2014/1202/141911_y1Zl_993989.jpg",
                0, 0);
    }

    private void display3() {
        KJBitmap kjb = KJBitmap.create();
        kjb.display(
                mImg3,
                "http://static.oschina.net/uploads/space/2014/1202/142217_a864_12.jpg",
                BitmapCreate.bitmapFromResource(getResources(),
                        R.drawable.ic_launcher, 0, 0));
    }

    private void display4() {
        KJBitmap kjb = KJBitmap.create();
        kjb.setCallback(new BitmapCallBack() {
            @Override
            public void onSuccess(View view) {
                ViewInject.toast("加载成功");
            }

            @Override
            public void onPreLoad(View view) {
                super.onPreLoad(view);
                ViewInject.toast("开始加载图片");
            }

            @Override
            public void onFailure(Exception e) {
                ViewInject.toast("加载失败");
            }
        });
        kjb.display(mImg4,
                "http://static.oschina.net/uploads/space/2014/1127/160305_YsAg_12.jpg");
    }

    /**
     * 自定义缓存路径
     */
    private void display5() {
        BitmapConfig bitmapConfig = new BitmapConfig();
        bitmapConfig.cachePath = "hello/world"; // 设置图片缓存路径为SD卡根目录hello文件夹下world文件夹内
        bitmapConfig.downloader = new BitmapDownloader(bitmapConfig, 0, 0); // 使用自定义的图片加载器（默认使用框架中的）
        KJBitmap kjb = KJBitmap.create(bitmapConfig);
        /** 然后剩下的都一样了 */
    }

    /**
     * 保存一个网络图片到本地
     */
    private void save() {
        // 简洁版，保存当前图片到SD卡KJLibrary目录下并命名为KJLibraryImage.jpg
        // KJBitmap.create().saveImage(ImageData.imgs[2],
        // "KJLibrary/KJLibraryImage.jpg");

        // 最完善的一种方法，提供整个图片下载过程中的回调
        KJBitmap.create().saveImage(ImageData.imgs[2], "KJLibraryImage.jpg",
                new BitmapCallBack() {
                    @Override
                    public void onPreLoad(View view) {
                        super.onPreLoad(view);
                        ViewInject.toast("开始");
                    }

                    @Override
                    public void onSuccess(View view) {
                        super.onSuccess(view);
                        ViewInject.toast("成功");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        super.onFailure(e);
                        ViewInject.toast("失败");
                    }

                    @Override
                    public void onFinish(View view) {
                        super.onFinish(view);
                        ViewInject.toast("完成");
                    }
                });
    }
}
