## ![logo](https://github.com/kymjs/KJFrameForAndroid/blob/master/KJFrameExample/logo.jpg) KJFrameForAndroid简介
=================
感谢支持KJFrameForAndroid的社区与组织：<br>
[![Android Arsenal](http://img.shields.io/badge/Android%20Arsenal-KJFrameForAndroid-blue.svg?style=flat)](http://android-arsenal.com/details/1/836)
[![BlackDuck OpenHUB](https://www.openhub.net/p/KJFrameForAndroid/widgets/project_thin_badge.gif)](https://www.openhub.net/p/KJFrameForAndroid)
[![OSChina](https://www.oschina.net/img/logo_s2.gif)](https://www.oschina.net/)<br>
**KJFrameForAndroid** 又叫KJLibrary，是一个android的orm 和 ioc 框架。同时封装了android中的Bitmap与Http操作的框架，使其更加简单易用；<br>
KJFrameForAndroid的设计思想是通过封装Android原生SDK中复杂的复杂操作而达到简化Android应用级开发，最终实现快速而又安全的开发APP。我们提倡用最少的代码，完成最多的操作，用最高的效率，完成最复杂的功能。<br>

## KJFrameForAndroid 相关链接
* QQ群：[257053751](http://shang.qq.com/wpa/qunwpa?idkey=00d92c040e81d87ccd21f8d0fffb10640baaa66da45254c3bd329b6ff7d46fef)(开发者群1)，[201055521](http://jq.qq.com/?_wv=1027&k=MBVdpK)(开发者群2)
* 项目地址：[https://github.com/kymjs/KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid)
* 项目备用地址（可能不是最新代码）：[http://git.oschina.net/kymjs/KJFrameForAndroid](http://git.oschina.net/kymjs/KJFrameForAndroid)
* Apk动态加载框架[DL](https://github.com/singwhatiwanna/dynamic-load-apk)
* 
* wiki for English skip to [https://github.com/kymjs/KJFrameForAndroid/wiki](https://github.com/kymjs/KJFrameForAndroid/wiki)
* 留言版 [https://github.com/kymjs/KJFrameForAndroid/issues](https://github.com/kymjs/KJFrameForAndroid/issues)
* 版本日志 [https://github.com/kymjs/KJFrameForAndroid/blob/master/debug_log.txt](https://github.com/kymjs/KJFrameForAndroid/blob/master/debug_log.txt)

---
# 框架使用
**Demo工程运行** ：
①[下载](https://codeload.github.com/kymjs/KJFrameForAndroid/zip/master)框架最新源码。
②选择KJFrameExample工程导入Eclipse。
③将/binrary目录最新的项目jar包复制至demo的libs目录。
④删除[project.properties](https://github.com/kymjs/KJFrameForAndroid/blob/master/KJFrameExample/project.properties)文件的最后一行<br>
**在项目中使用** ：将KJFrameForAndroid的[最新jar包](https://github.com/kymjs/KJFrameForAndroid/tree/master/binrary)添加到你工程/libs目录中并引用。<br>

* KJFrameForAndroid默认已集成了android-support-v4.jar，你无需再次添加。<br>
* 由于使用了SDK最新的API函数，以及3.0版Fragment。KJFrameForAndroid框架最低支持Android3.0版本，本框架可以作代码混淆，混淆时请保留包含注解部分的类的完整性。<br>
*注：使用 KJFrameForAndroid 应用开发框架需要在你项目的AndroidManifest.xml文件中加入以下基本权限：*
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
* 第一个是访问网络
* 第二个是访问sdcard
* 访问网络是请求网络图片的时候需要或者是http数据请求时候需要，访问sdcard是图片缓存的需要(如果你使用utilsLibrary，针对不同的功能可能会需要更多的权限)。

----

## =======各模块使用介绍=======

## Plugin模块
使用Plugin模块可以让你的插件apk不用安装便直接被运行，极大的方便了APP动态更新，且可以轻松实现插件与APP项目之间的解耦。更多介绍请看[Plugin模块详细介绍](https://github.com/kymjs/KJFrameForAndroid/blob/master/PluginLibraryExplain.md)<br>
**现支持以下功能** 
* apk无需安装即可被应用调用<br>
* Activity的动态加载：包括生命周期和交互事件、R文件资源引用、插件与APP之间的数据通信<br>
* Fragment的完美加载使用<br>
* 动态注册的BroadcastReceiver<br>
* 绑定式、启动式Service均可完美使用<br>
* 已成功模拟出launchMode的效果。(launchModer实际上是一个虚拟的，生命周期的调用还是一样的，仅仅模拟出了系统的BackStack)<br>
* 完美集成了KJFrameForAndroid中UiLibrary->Topology的全部功能，支持注解式绑定控件设置监听<br>

## UILibrary模块
UILibrary包含两个部分Widget(控件)、Topology(Android框架结构继承链) [详细介绍...](http://my.oschina.net/kymjs/blog/284897)<br>

**UILibrary -> Widget控件部分**
主要封装了常用的UI控件，为了不让项目jar包过大，我们只引入了开发中一定会用到的控件，例如：可上下拉的KJListView、可上下拉的KJScrollView、可以双指缩放双击缩放双指旋转的ScaleImageView、等等......更多内容请自行查看项目文件中org.kymjs.aframe.widget包下的内容<br>

**UILibrary -> Topology拓扑部分**
规范了Activity中数据及控件的初始化，并包含一个使用IOC设计思想的控件初始化方式：可通过注解的方式进行UI绑定，与设置监听，在Activity和Fragment中均可以通过一行代码绑定控件并实现点击监听；还包含了在目前应用开发中常见的布局界面，如侧滑效果，高效的底部TAB导航，3D效果的切换。同时UILibrary为开发者定义了完善的BaseActivity和BaseFragment，开发者只需手动继承就可以获得Topology部分的全部功能。<br>
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
Topology中各函数调用顺序：
setRootView();<br>
@BindView<br>
initDataFromThread();（异步调用，可做耗时操作）<br>
threadDataInited();（initDataFromThread执行完成后才会回调）<br>
initData();<br>
initWidget();<br>
registerBroadcast();<br>


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
kjb.display(imageView, "/storage/sdcard0/1.jpg",80,80); //可选参数，设置要显示图片的宽高，不设置则默认显示原图
// 载入网络图片
kjb.display(textView, "http://www.eoeandroid.com/data/attachment/forum/201107/18/142935bbi8d3zpf3d0dd7z.jpg");

```

## HttpLibrary模块
KJLibrary默认对所有Http通信的数据做了缓存处理，缓存时间为5分钟。这么做的目的不仅是为了节省用户手机流量，同时是为了减少服务器压力<br>
HttpLibrary模块使用HttpClient与HttpUrlConnection两种实现方式实现网络通信、数据上传、多线程断点下载。根据Google建议：在2.3系统之前由于HttpUrlConnection不稳定且有一定的BUG，应该尽量使用HttpClient；在2.3以后的系统，若只是简单的数据交互，应该使用更加轻量级、易扩展的HttpUrlConnection。对于实现的方式，KJLibrary将交由开发者来选择。<br>

###普通get方法示例：
```java
// 使用HttpClient，与使用HttpUrlConnection的使用方法一样，就只具体写一种了
KJHttp kjh = new KJHttp();
kjh.get(String url,I_HttpParams params, HttpCallback callback);

// 使用HttpUrlConnection
KJHttp kjh = new KJHttp();
kjh.urlGet("http://my.oschina.net/kymjs/blog", new StringCallBack(){

	@Override
	public void onSuccess(String t) {
			ViewInject.toast("显示JSON信息：" + t);
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		ViewInject.toast("网络加载失败，请检查您的网络");
	}
});

```

###普通post请求JSON方法示例：
```java
// 使用HttpClient，与使用HttpUrlConnection的使用方法一样，就只具体写一种了
KJHttp kjh = new KJHttp();
kjh.post(String url,I_HttpParams params, HttpCallback callback);

// 使用HttpUrlConnection
KJHttp kjh = new KJHttp();
KJStringParams params = new KJStringParams();
params.put("user_id", "33");
params.put("birthday", "2008-8-1");
kjh.urlPost("http://my.oschina.net/kymjs/blog", params, new StringCallBack(){
	@Override
	public void onSuccess(String t) {
			ViewInject.toast("显示JSON信息：" + t);
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		ViewInject.toast("网络加载失败，请检查您的网络");
	}
});

```
###post上传文件方法示例：
```java
// 使用HttpClient，与使用HttpUrlConnection的使用方法一样，就只具体写一种了
kjh.post(String url,I_HttpParams params, HttpCallback callback);

// 使用HttpUrlConnection
KJHttp kjh = new KJHttp();
KJFileParams params = new KJFileParams();
params.put("user_id", "33");
params.put(new File("/storage/sdcard0/1.jpg"));//传file对象
params.put(inputstream);//传文件输入流
params.put(byteArray);//传文件byte数组
//以上三种方法任选其一即可
kjh.urlPost("http://my.oschina.net/kymjs/blog", params, new StringCallBack(){

	@Override
	public void onSuccess(String t) {
			ViewInject.toast("显示JSON信息：" + t);
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
		ViewInject.toast("网络加载失败，请检查您的网络");
	}
});

```
###多线程下载方法示例：
```java
KJHttp kjh = new KJHttp();
FileCallBack file = new FileCallBack() {
    @Override
    public void onSuccess(File f) {
        ViewInject.toast("下载成功");
    }
    @Override
    public void onLoading(long count, long current) {
        super.onLoading(count, current);
        if (!maxed) {
            mProgress.setMax((int) count);
            maxed = true;
        }
        mProgress.setProgress((int) current);
    }
    @Override
    public void onFailure(Throwable t, int errorNo, String strMsg) {
        super.onFailure(t, errorNo, strMsg);
        ViewInject.toast("失败原因： " + strMsg);
    }
};
file.setProgress(true); // 若要调用onLoading，必须设置为true
kjh.urlDownload(mEt.getText().toString(), "/storage/sdcard0/3.png",file);

```
## DBLibrary模块
包含了android中的orm框架，一行代码就可以进行增删改查。支持一对多，多对一等查询。<br>
DB模块，很大程度上参考了[finalDB](https://github.com/kymjs/afinal)的设计，并在此基础上完善了几乎全部的API注释，与更多可定制的DB操作<br>
```java
//普通数据存储
KJDB db = KJDB.create(this);
User ugc = new User(); //这里需要注意的是User对象必须有id属性，或者有通过@ID注解的属性
ugc.setEmail("kymjs123@gmail.com");
ugc.setName("kymjs");
db.save(ugc);

```
```java
//一对多数据存储
public class Parent{  //JavaBean
    private int id;
    @OneToMany(manyColumn = "parentId")
    private OneToManyLazyLoader<Parent ,Child> children;
    /*....*/
}
public class Child{ //JavaBean
    private int id;
    private String text;
    @ManyToOne(column = "parentId")
    private  Parent  parent;
    /*....*/
}
List<Parent> all = db.findAll(Parent.class);
    for( Parent  item : all){
        if(item.getChildren ().getList().size()>0)
            ViewInject.toast(item.getText() + item.getChildren().getList().get(0).getText());
    }

```
## UtilsLibrary模块
包含了应用开发中的常用工具类，例如系统级别的Log管理、网络状态监测、Bitmap压缩工具类、获取屏幕宽高以及单位转换的工具类、错误信息处理与文件处理工具类、preference工具类、字符串操作与常用正则判断等。详细内容请自行查看项目文件中org.kymjs.aframe.utils包下的内容[更多介绍...](http://my.oschina.net/kymjs/blog)<br><br>

## 许可
**本项目采用 Apache Licence 2.0 授权协议:<br>
Apache Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）[更多...](http://www.oschina.net/question/12_2828)<br>
  Copyright (c) 2014, KJFrameForAndroid Open Source Project, Zhang Tao.
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
	   
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

## 关于作者kymjs
blog：http://my.oschina.net/kymjs/blog<br>
email：kymjs123@gmail.com<br>
forum/bbs: [http://tieba.baidu.com/f?kw=kym%BD%A9%CA%AC&fr=index](http://tieba.baidu.com/f?kw=kym%BD%A9%CA%AC&fr=index)
