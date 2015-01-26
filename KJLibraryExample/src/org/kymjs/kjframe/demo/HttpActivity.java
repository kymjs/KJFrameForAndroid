package org.kymjs.kjframe.demo;

import java.io.File;
import java.io.FileNotFoundException;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import org.kymjs.kjframe.http.HttpConfig;
import org.kymjs.kjframe.http.HttpParams;
import org.kymjs.kjframe.ui.BindView;
import org.kymjs.kjframe.ui.ViewInject;
import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.KJLoger;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class HttpActivity extends KJActivity {

    @BindView(id = R.id.button1, click = true)
    private Button mBtn1;
    @BindView(id = R.id.button2, click = true)
    private Button mBtn2;
    @BindView(id = R.id.button3, click = true)
    private Button mBtn3;
    @BindView(id = R.id.button4, click = true)
    private Button mBtn4;
    @BindView(id = R.id.button5, click = true)
    private Button mBtn5;
    @BindView(id = R.id.button6, click = true)
    private Button mBtn6;
    @BindView(id = R.id.edittext)
    private EditText mEtDownloadPath;
    @BindView(id = R.id.progress)
    private ProgressBar mProgress;

    private final KJHttp kjh = new KJHttp();

    @Override
    public void setRootView() {
        setContentView(R.layout.http);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtn1.setText("GET请求");
        mBtn2.setText("POST请求");
        mBtn3.setText("文件上传");
        mBtn4.setText("自定义(高级设置)");
        mBtn5.setText("下载");
        mBtn6.setText("暂停下载");
        mEtDownloadPath
                .setText("http://www.orchidshell.com/materials/OrchidShell/Instructions.rar");
        // mEtDownloadPath.setText("http://192.168.1.145/kymjs/upload/1.pdf");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.button1:
            get();
            break;
        case R.id.button2:
            post();
            break;
        case R.id.button3:
            upload();
            break;
        case R.id.button4:
            highRequest();
            break;
        case R.id.button5:
            mBtn6.setVisibility(View.VISIBLE);
            download();
            break;
        case R.id.button6:
            // 有断点下载功能，直接重新加载即可
            if (kjh.isStopDownload()) {
                mBtn6.setText("暂停下载");
                download();
            } else {
                mBtn6.setText("继续");
                kjh.stopDownload();
            }
            break;
        }
    }

    private void get() {
        HttpConfig config = new HttpConfig();// 每个KJHttp对象对应一个config
        config.cachePath = "KJLibrary/cache"; // 数据缓存到SD卡根目录KJLibrary文件夹中cache文件夹内
        config.cacheTime = 0;// 强制不使用缓存
        // （你可以自己设置缓存时间，建议区分WiFi模式和3G网模式设置不同缓存时间并动态切换）
        config.httpHeader.put("cache", "kjlibrary");// 设置http请求头信息
        config.maxRetries = 10;// 出错重连次数
        KJHttp kjhttp = new KJHttp(config);
        kjhttp.get("http://www.oschina.net/", new HttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
                KJLoger.debug("即将开始http请求");
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                ViewInject.longToast("GET请求成功");
                KJLoger.debug("请求成功:" + t.toString());
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                KJLoger.debug("出现异常:" + strMsg);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                KJLoger.debug("请求完成，不管成功还是失败");
            }
        });
    }

    private void post() {
        HttpParams params = new HttpParams();
        params.put("id", "1");
        params.put("name", "张涛");
        kjh.post("http://192.168.1.149/post.php", params, new HttpCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                ViewInject.toast("POST成功：" + t.toString());
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                ViewInject.toast("失败：" + strMsg);
            }
        });
    }

    // 文件上传的PHP后台实现示例
    // <?php
    // if ($_FILES["file"]["error"] > 0)
    // {
    // echo "Return Code: " . $_FILES["file"]["error"] . "<br />";
    // }
    // else
    // {
    // echo "Upload: " . $_FILES["file"]["name"] . "<br />";
    // echo "Type: " . $_FILES["file"]["type"] . "<br />";
    // echo "Size: " . ($_FILES["file"]["size"] / 1024) . " Kb<br />";
    // echo "Temp file: " . $_FILES["file"]["tmp_name"] . "<br />";
    //
    // if (file_exists("upload/" . $_FILES["file"]["name"]))
    // {
    // echo $_FILES["file"]["name"] . " already exists. ";
    // }
    // else
    // {
    // move_uploaded_file($_FILES["file"]["tmp_name"],
    // "upload/" . $_FILES["file"]["name"]);
    // echo "Stored in: " . "upload/" . $_FILES["file"]["name"];
    // }
    // }
    // ?>
    private void upload() {
        HttpParams params = new HttpParams();
        params.put("uid", "12");
        try {
            params.put("avatar", FileUtils.getSaveFile("KJLibrary", "logo.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // kjh.post("http://192.168.1.125/kymjs/hello.php", params,
        kjh.post("http://182.92.220.212/golf/index.php/Appuser/uplaodAvatar/",
                params, new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        ViewInject.toast("文件上传完成");
                        KJLoger.debug("服务器返回：" + t);
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ViewInject.toast("文件上传失败" + strMsg);
                    }
                });
    }

    private void highRequest() {
        HttpConfig config = new HttpConfig();// 每个KJHttp对象对应一个config
        config.cachePath = "hello/world"; // 数据缓存到SD卡根目录hello文件夹中world文件夹内
        config.cacheTime = 0;// 强制不使用缓存
                             // （你可以自己设置缓存时间，建议区分WiFi模式和3G网模式设置不同缓存时间并动态切换）
        config.httpHeader.put("cache", "kjlibrary");// 设置http请求头信息
        config.maxRetries = 10;// 出错重连次数
        KJHttp kjhttp = new KJHttp(config);
        // //剩下的都是一样的了
        ViewInject.toast("请查看代码中注释");
    }

    private void download() {
        kjh.download(mEtDownloadPath.getText().toString(),
                FileUtils.getSaveFile("KJLibrary", "l.pdf"),
                new HttpCallBack() {
                    @Override
                    public void onSuccess(File f) {
                        super.onSuccess(f);
                        KJLoger.debug("success");
                        ViewInject.toast("下载成功");
                        mProgress.setProgress(mProgress.getMax());
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        KJLoger.debug("onFailure");
                    }

                    @Override
                    public void onLoading(long count, long current) {
                        super.onLoading(count, current);
                        mProgress.setMax((int) count);
                        mProgress.setProgress((int) current);
                        KJLoger.debug(count + "------" + current);
                    }
                });
    }
}
