package org.kymjs.kjframe.demo;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;

import android.graphics.Bitmap;
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
        mTv4.setText("高级设置");
        mBtn.setText("保存网络图片到本地");
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
            ViewInject.toast("请查看代码中的更多方法");
            break;
        case R.id.button:
            save();
            ViewInject.toast("图片将会出现在SD卡根目录OSL.png");
            break;
        }
    }

    private void save() {
        KJBitmap kjb = new KJBitmap();
        kjb.saveImage(this, "http://www.kymjs.com/image/logo.png",
                FileUtils.getSDCardPath() + "/OSL.png");
    }

    private void removeCache() {
        KJBitmap kjb = new KJBitmap();
        kjb.removeCache("http://static.oschina.net/uploads/space/2015/0420/133006_NnLQ_12.jpg");
    }

    private void display1() {
        KJBitmap kjb = new KJBitmap();
        kjb.display(mImg1,
                "http://static.oschina.net/uploads/space/2015/0420/133006_NnLQ_12.jpg");
    }

    private void display2() {
        KJBitmap kjb = new KJBitmap();
        kjb.display(
                mImg2,
                "http://static.oschina.net/uploads/space/2015/0420/133006_NnLQ_12.jpg",
                0, 0);
    }

    private void display3() {
        KJBitmap kjb = new KJBitmap();
        kjb.display(
                mImg3,
                "http://static.oschina.net/uploads/space/2015/0420/133006_NnLQ_12.jpg",
                new BitmapCallBack() {
                    @Override
                    public void onPreLoad() {
                        super.onPreLoad();
                        ViewInject.toast("即将开始下载");
                    }

                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        super.onSuccess(bitmap);
                        ViewInject.toast("加载成功");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        super.onFailure(e);
                        ViewInject.toast("加载失败");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ViewInject.toast("加载完成");
                    }
                });
    }
}
