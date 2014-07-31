KJFrameForAndroid
=================

# KJFrameForAndroid 交流平台
* QQ群：[257053751](http://shang.qq.com/wpa/qunwpa?idkey=00d92c040e81d87ccd21f8d0fffb10640baaa66da45254c3bd329b6ff7d46fef)(开发者群1)
* 第三方开发分支：[https://github.com/kuangsunny/KJFrameForAndroid](https://github.com/kuangsunny/KJFrameForAndroid)
* 项目地址：[https://github.com/kymjs/KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid)
* 项目备用地址（可能不是最新代码）：[http://git.oschina.net/kymjs/KJFrameForAndroid](http://git.oschina.net/kymjs/KJFrameForAndroid)

## ![logo](https://github.com/kymjs/KJFrameForAndroid/blob/master/KJFrameExample/logo.jpg) 简介
**KJFrameForAndroid** 又叫KJLibrary，是一个android的orm 和 ioc 框架。同时封装了android中的Bitmap与Http操作的框架，使其更加简单易用；<br>
KJFrameForAndroid的设计思想是通过封装Android原生SDK中复杂的复杂操作而达到简化Android应用级开发，最终实现快速而又安全的开发APP。我们提倡用最少的代码，完成最多的操作，用最高的效率，完成最复杂的功能。<br>
同时，KJFrameForAndroid是免费的、开源的、简易的、遵循Apache Licence 2.0开源协议发布的android应用开发框架，总共分为五大模块：UILibrary，UtilsLibrary，HttpLibrary，BitmapLibrary，DBLibrary。<br>

---
# 框架使用
clone下KJFrameForAndroid最新源码后，导入eclipse中，若只想使用框架而不考虑源码查看与学习，可直接复制KJLibrary工程中bin目录下的kjlibrary.jar文件至自己项目的libs文件夹中。<br>
若需要对源码改动或学习，可打开KJLibrary工程查看源码，同时结合KJFrameExample演示项目更好的学习，另外也可以自己新建工程，并右键工程->preference->Android->library->Add,选择KJLibrary工程加入后apply应用。

*注：使用 KJFrameForAndroid 应用开发框架需要在你项目的AndroidManifest.xml文件中加入以下权限：*
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
* 第一个是访问网络
* 第二个是访问sdcard
* 访问网络是请求网络图片的时候需要或者是http数据请求时候需要，访问sdcard是图片缓存的需要。

----


## =======各模块使用介绍=======

## UILibrary模块

UILibrary包含两个部分Widget、Topology  [详细介绍...](http://my.oschina.net/kymjs/blog/284897)<br>

**UILibrary -> Widget控件部分**
主要封装了常用的UI控件，为了不让项目jar包过大，我们只引入了开发中一定会用到的控件，例如：可上下拉的KJListView、可上下拉的KJScrollView、可以双指缩放双击缩放双指旋转的ScaleImageView、等等......更多内容请自行查看项目文件中org.kymjs.aframe.widget包下的内容<br>

**UILibrary -> Topology拓扑部分**
包含一个使用IOC设计思想的控件初始化方式：可通过注解的方式进行UI绑定，与设置监听，在Activity和Fragment中均可以通过一行代码绑定控件并实现点击监听；还包含了在目前应用开发中常见的布局界面，如侧滑效果，高效的底部TAB导航，3D效果的切换。同时UILibrary为开发者定义了完善的BaseActivity和BaseFragment，开发者只需手动继承就可以获得Topology部分的全部功能。<br>
```java

public class TabExample extends BaseActivity {

    @BindView(id = R.id.bottombar_content1, click = true)
    public RadioButton mRbtn1;
    @BindView(id = R.id.bottombar_content2, click = true)
    private RadioButton mRbtn2;

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_tab_example);
    }
    
    @Override
    protected void initWidget() {
        super.initWidget();
        mRbtn1.setText("控件已经初始化绑定并设置了监听");
    }

    @Override
    public void widgetClick(View v) {
        super.widgetClick(v);
        switch (v.getId()) {
        case R.id.bottombar_content1:
        ViewInject.toast("点击了mRbtn1");
            break;
        case R.id.bottombar_content2:
        ViewInject.toast("点击了mRbtn2");
            break;
        }
    }
}
```

## UtilsLibrary模块
包含了应用开发中的常用工具类，例如系统级别的Log管理、网络状态监测、Bitmap压缩工具类、获取屏幕宽高以及单位转换的工具类、错误信息处理与文件处理工具类、preference工具类、字符串操作与常用正则判断等。详细内容请自行查看项目文件中org.kymjs.aframe.utils包下的内容[更多介绍...](http://my.oschina.net/kymjs/blog)<br><br>

## HttpLibrary模块
使用HttpClient与HttpUrlConnection两种实现方式实现网络通信、数据上传、多线程断点下载。根据Google建议：在2.3系统之前由于HttpUrlConnection不稳定且有一定的BUG，应该尽量使用HttpClient；在2.3以后的系统，若只是简单的数据交互，应该使用更加轻量级、易扩展的HttpUrlConnection。对于实现的方式，KJLibrary将交由开发者来选择。<br>

###普通get方法示例：
```java
KJHttp kjh = new KJHttp();
kjh.urlget("http://my.oschina.net/kymjs/blog", new StringRespond(){

	@Override
	public void success(String t) {
			ViewInject.toast("显示JSON信息：" + t);
	}

	@Override
	public void failure(Throwable t, int errorNo, String strMsg) {
		ViewInject.toast("网络加载失败，请检查您的网络");
	}
});

```

## BitmapLibrary模块
任何View(ImageView设置src，普通View设置bg)加载图片的时候都无需考虑图片加载过程中出现的oom和android容器快速滑动时候出现的图片错位等现象，同时无需考虑图片加载过程中出现的OOM。默认使用内存lru算法+磁盘lru算法缓存图片 [详细介绍](http://my.oschina.net/kymjs/blog/295001)<br>
**注：**在Android2.3之前，我们常常使用软引用或弱引用的形式去做缓存图片，然而根据Google的描述：垃圾回收器会更倾向于回收持有软引用或弱引用的对象，这让软引用和弱引用变得不再可靠。另外，Android 3.0 (API Level 11)中，图片的数据会存储在本地的内存当中，因而无法用一种可预见的方式将其释放，这就有潜在的风险造成应用程序的内存溢出并崩溃。BitmapLibrary使用lru算法去管理缓存，同时内存缓存配合磁盘缓存能更有效的管理缓存调用。
```java

KJBitmap kjb = KJBitmap.create();
/**
 * url不仅支持网络图片显示，同时支持本地SD卡上的图片显示；
 * view不仅可以是imageview，同时普通view也可以传入，框架会自动识别对imageview设置src对普通view设置bg
 */
// 载入本地图片
kjb.display(imageView, "/storage/sdcard0/1.jpg");
// 载入网络图片
kjb.display(textView, "http://www.eoeandroid.com/data/attachment/forum/201107/18/142935bbi8d3zpf3d0dd7z.jpg");

```

## 许可
********本项目采用 Apache Licence 2.0 授权协议:<br>
Apache Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）<br>
[更多...](http://www.oschina.net/question/12_2828)<br>
********欢迎大家在这个基础上进行改进，并与大家分享。<br>

## 关于作者kymjs
blog：http://my.oschina.net/kymjs/blog<br>
email：kymjs123@gmail.com<br>
KJLibrary交流QQ群：[257053751](http://shang.qq.com/wpa/qunwpa?idkey=00d92c040e81d87ccd21f8d0fffb10640baaa66da45254c3bd329b6ff7d46fef)
