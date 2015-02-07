#HttpLibrary Summary
As you know as an Android developer, you can't make a network call on the UI thread, and on the other hand you can't touch UI elements on any other thread than this one. So you need to manage threading.So you need to manage threading. In the HttpLibrary, threading is done using asynchronous methods and callback interfaces to implement.<br>
More detailed configuration policies, see the sample code in the repository.<br>
Enjoy it! Any question? You can ask for me: kymjs123(wechat) or kymjs123@gmail.com.

##surprise to you
What is the most troublesome for internet application. It's Http data cache. Now, you don't need to consider this,because KJFrameForAndroid have a caching reverse proxy. Default: first request,it will save the response data. Within five minutes the second request the same interface, it will return the cached data. And, you can configure the cache valid time, it is best treated differently WiFi and cellular networks.<br>

##How to use
// get mode or post mode request JSON data example
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

### download file method

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

            /* onLoading just in download method effective, and a second time */
            @Override
            public void onLoading(long count, long current) {
                super.onLoading(count, current);
                mProgress.setMax((int) count);
                mProgress.setProgress((int) current);
                KJLoger.debug(count + "------" + current);
            }
        });
```