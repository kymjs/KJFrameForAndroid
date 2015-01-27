=================
#KJFrameForAndroid Summary
[![Android Arsenal](http://img.shields.io/badge/Android%20Arsenal-KJFrameForAndroid-blue.svg?style=flat)](http://android-arsenal.com/details/1/836)
[![BlackDuck OpenHUB](https://www.openhub.net/p/KJFrameForAndroid/widgets/project_thin_badge.gif)](https://www.openhub.net/p/KJFrameForAndroid)
[![OSChina](https://www.oschina.net/img/logo_s2.gif)](http://www.oschina.net/p/kjframeforandroid)<br>

## What is KJFrameForAndroid
KJFrameForAndroid is also called KJLibrary. It's an Android ORM and IOC framework and includes UILibrary, PluginLibrary, HttpLibrary, BitmapLibrary, DBLibrary. KJFrameForAndroid is designed to wrap complexity of the Android native SDK and keep things simple.
However,KJFrameForAndroid is free open source object. Thanks for you follow this KJFrameForAndroid.

## KJFrameForAndroid links
* QQ group：[257053751](http://jq.qq.com/?_wv=1027&k=WoM2Aa)(开发者群1)，[201055521](http://jq.qq.com/?_wv=1027&k=MBVdpK)(开发者群2)
* 国内用户请访问git.osc：[http://git.oschina.net/kymjs/KJFrameForAndroid](http://git.oschina.net/kymjs/KJFrameForAndroid)
* dynamic-load-apk：[https://github.com/singwhatiwanna/dynamic-load-apk](https://github.com/singwhatiwanna/dynamic-load-apk)
* 
* issues [https://github.com/kymjs/KJFrameForAndroid/issues](https://github.com/kymjs/KJFrameForAndroid/issues)
* version log [https://github.com/kymjs/KJFrameForAndroid/blob/master/debug_log.txt](https://github.com/kymjs/KJFrameForAndroid/blob/master/debug_log.txt)

## Integrating KJFrameForAndroid to your project
Used in real project example: [KJMusic player](https://github.com/KJMusicPlayer/KJMusic)
>Create /binrary/kjlibrary.jar and include as jar dependency to your project.<br>
>Include the KJFrameForAndroid project as Library Dependency in your project.<br>
*make use of KJFrameForAndroid works on Android 3.0 or higher and need permission in your AndroidManifest.xml*
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## PluginLibrary
Did you really try? execute not installed apk file, PluginLibrary can I help you.<br>
you can download [pluginDemo](https://github.com/kymjs/KJFrameForAndroid/blob/master/doc/plugin_demo.zip) project integrating example project learn KJFrameForAndroid.[Detail](https://github.com/kymjs/KJFrameForAndroid/blob/master/PluginLibraryExplain.md)

## UILibrary
**UILibrary -> Widget function**
import in common use widget,for example, can pull ListView/ScrollView; can double click zoom's ImageView.<br>
**UILibrary -> Topology function**
import a Activity inheritance link.Get topology all function, you can extends org.kymjs.kjframe.KJActivity(KJFragment) for your Activity(Fragment).

```java
		public class TabExample extends KJActivity {
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
				mRbtn1.setText("widget clicked listener");
			}

			@Override
			public void widgetClick(View v) {
				super.widgetClick(v);
				switch (v.getId()) {
				case R.id.bottombar_content1:
				ViewInject.toast("clicked mRbtn1");
					break;
				case R.id.bottombar_content2:
				ViewInject.toast("clicked mRbtn2");
					break;
				}
			}
		}
```
in topology method called queue：<br>
setRootView(); <br>
@BindView <br>
initDataFromThread();（asynchronous,can do time consuming） <br>
threadDataInited();（initDataFromThread() executed just call back） <br>
initData(); <br>
initWidget(); <br>
registerBroadcast(); <br>

## BitmapLibrary
Can whichever View set image(for ImageView set src;other view set background).<br>
Loading bitmap,never out of memory exception.<br>
defualt make use of memory least recently used + disk least recently used cache image.<br>
**before in android 2.3, we used to SoftReference or WeakReference to cache image. However,on the basis of Google
represent, System.gc() more likely recovery to SoftReference or WeakReference.And into android 3.0,image data for cache
in memory,will difficult recovery, have possible crash. BitmapLibrary make use of lru algorithm manager cache called**

```java

	KJBitmap kjb = KJBitmap.create();
	/**
	 * url can be local sdcard path or internet url;
	 * view can whichever View set image(for ImageView set src;for View set background).
	 */
	// local sdcard image
	kjb.display(imageView, "file:///storage/sdcard0/1.jpg"); 
	// internet url
	kjb.display(textView, http://www.xxx.com/xxx.jpg); 
	//自定义图片显示大小
	kjb.display(view, http://www.xxx.com/xxx.jpg, 80, 80); //width=80,height=80

```

## HttpLibrary
As you know as an Android developer, you can't make a network call on the UI thread, and on the other hand you can't touch UI elements on any other thread than this one. So you need to manage threading.So you need to manage threading. In the HttpLibrary, threading is done using asynchronous methods and callback interfaces to implement.
### get method request JSON example
```java
		// get
		kjh.get("http://www.oschina.net/", new HttpCallBack();//like post, so just one example
		
		// post
        KJHttp kjh = new KJHttp();
        HttpParams params = new HttpParams();
        params.put("id", "1");
        params.put("name", "kymjs");
        kjh.post("http://192.168.1.149/post.php", params, new HttpCallBack() {
            @Override
            public void onPreStart() {
                super.onPreStart();
                KJLoger.debug("before start");
            }
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                ViewInject.longToast("request success");
                KJLoger.debug("log:" + t.toString());
            }
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                KJLoger.debug("exception:" + strMsg);
            }
            @Override
            public void onFinish() {
                super.onFinish();
                KJLoger.debug("request finish. Regardless of success or failure.");
            }
        });

```

### post upload file parameter
```php
	// on server example
	<?php
		if ($_FILES["file"]["error"] > 0)
		{
			echo "Return Code: " . $_FILES["file"]["error"] . "<br />";
		}
		else
		{
			echo "Upload: " . $_FILES["file"]["name"] . "<br />";
			echo "Type: " . $_FILES["file"]["type"] . "<br />";
			echo "Size: " . ($_FILES["file"]["size"] / 1024) . " Kb<br />";
			echo "Temp file: " . $_FILES["file"]["tmp_name"] . "<br />";

			if (file_exists("upload/" . $_FILES["file"]["name"]))
			{
				echo $_FILES["file"]["name"] . " already exists. ";
			}
			else
			{
				move_uploaded_file($_FILES["file"]["tmp_name"], "upload/" . $_FILES["file"]["name"]);
				echo "Stored in: " . "upload/" . $_FILES["file"]["name"];
			}
		}
	?>
```

```java
	private void upload() {
        HttpParams params = new HttpParams();
        //support more file
        params.put("file", FileUtils.getSaveFile("KJLibrary", "logo.jpg"));
		params.put("file1", new File("/path/xxx/xxx")); // support
		params.put("file2", new FileInputStream(file)); // support
        kjh.post("http://192.168.1.149/kymjs/hello.php", params,
                new HttpCallBack() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        ViewInject.toast("success");
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                            String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        ViewInject.toast("error" + strMsg);
                    }
					/** more method... **/
                });
    }
```
```java
		kjh.download(mEtDownloadPath.getText().toString(), FileUtils.getSaveFile("KJLibrary", "l.pdf"),new HttpCallBack() {
            @Override
            public void onSuccess(File f) {
                super.onSuccess(f);
                KJLoger.debug("success");
                ViewInject.toast("toast");
                mProgress.setProgress(mProgress.getMax());
            }

            @Override
            public void onFailure(Throwable t, int errorNo,String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                KJLoger.debug("onFailure");
            }

            /* onLoading just in download method effective，1 second 1 call */
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                mProgress.setMax((int) count);
                mProgress.setProgress((int) current);
                KJLoger.debug(count + "------" + current);
            }
        });
```

## DBLibrary
in android orm framework. Make use of sqlite handle. one line just to add/delete/update/query. holder one-more,more-one entity<br>
About DataBase function，comes frome to finaldb object. Thinks.[finaldb](https://github.com/kymjs/afinal)<br>
```java
	// data file
	KJDB db = KJDB.create(this);
	User ugc = new User(); //warn: The ugc must have id field or @ID annotate
	ugc.setEmail("kymjs123@gmail.com");
	ugc.setName("kymjs");
	db.save(ugc);
```

```java
	//one - many
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
					Toast.makeText(this,item.getText() + item.getChildren().getList().get(0).getText(),Toast.LENGTH_LONG).show();
			}

```

##License
```
 Copyright 2014,The KJFrameForAndroid Open Source Project.
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ```
## About Me
I am kymjs, From ShenZhen China.<br>
blog：http://my.oschina.net/kymjs/blog<br>
email：kymjs123@gmail.com<br>
If my project help you, Can you buy a bottle of beer for me? <br>
my account is: kymjs@foxmail.com [click there one's voluntary contribution](https://shenghuo.alipay.com/send/payment/fill.htm)<br>
如果我的项目帮助了你，希望你有能力的基础上能捐助我买书学习，以让我更有信心和能力回馈网友。
我的支付宝账号：kymjs@foxmail.com[点这里参与我的众筹](https://shenghuo.alipay.com/send/payment/fill.htm)
##感谢 沙加(￥99) 扯淡兄(￥88) 的捐赠