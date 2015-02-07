#BitmapLibrary Summary
Can whichever View set image(for ImageView set src;other view set background).
Used BitmapLibrary, loading bitmap from internet or local SD card, it definitely not out of memory.<br>
It will be widget the size of the bitmap as to decode it. And joined the multi-level cache for bitmap.<br>
defualt make use of MemoryLruCache + DiskLruCache image.<br>
More detailed configuration policies, see the sample code in the repository.<br>
Enjoy it! Any question? You can ask for me: kymjs123(wechat) or kymjs123@gmail.com.

##surprise to you
You can choose according to their needs, the process of configuring the bitmap loaded. For example: to define their own rules download; animation definition picture shows; the definition of the size of the bitmap to be displayed; custom bitmap is displayed when downloading.<br>
You probably used [ImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader), BitmapLibrary not so complicated, but at the same time not like ImageLoader use flash inside ListView.<br>

##How to use
```java

	KJBitmap kjb = KJBitmap.create();
	/**
	 * url can be local sdcard path or internet url;
	 * view can whichever View set image(for ImageView set src;for View set background).
	 */
	// local sdcard image
	kjb.display(imageView, "/storage/sdcard0/1.jpg"); 
	// internet url
	kjb.display(textView, http://www.xxx.com/xxx.jpg); 
	//configuration after loading the size
	kjb.display(view, http://www.xxx.com/xxx.jpg, 100, 80); //width=100,height=80
	//configuration bitmap loading ImageView display
	kjb.display(view, http://www.xxx.com/xxx.jpg, R.drawable.xxx);
	kjb.display(view, http://www.xxx.com/xxx.jpg, bitmap);
	kjb.display(view, http://www.xxx.com/xxx.jpg, drawable);
```