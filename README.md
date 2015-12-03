=================
#[![OSL](http://www.kymjs.com/image/logo_s.png)](http://www.kymjs.com/works/)   
*KJFrameForAndroid* 又叫KJLibrary，是一个android的orm 和 ioc 框架。同时封装了android中的Bitmap与Http操作的框架，使其更加简单易用；<br>
KJFrameForAndroid的设计思想是通过封装Android原生SDK中复杂的复杂操作而达到简化Android应用级开发，最终实现快速而又安全的开发APP。我们提倡用最少的代码，完成最多的操作，用最高的效率，完成最复杂的功能。<br>

## 快速入门
####AndroidStudio
build.gradle中添加：  
``` 
compile 'org.kymjs.kjframe:kjframe:2.6'
```
####eclipse
复制jar包 [KJFrameForAndroid_v2.x](https://github.com/kymjs/KJFrameForAndroid/tree/master/binrary) 到你工程的/libs目录中.   
eclipes版本源码请查看[相关分支](https://github.com/kymjs/KJFrameForAndroid/tree/eclipse_end)   

##使用帮助
1、很遗憾，没有帮助文档，但这几篇博客也许能帮到你  
    [MVC模块](https://github.com/kymjs/KJFrameForAndroid/wiki/MVCLibrary_cn)   
    [KJBitmap使用方法](http://www.kymjs.com/code/2015/03/25/01/)   
    [KJHttp请求的使用](http://www.kymjs.com/code/2015/05/12/01/)   
    [数据库模块使用方法](https://github.com/kymjs/KJFrameForAndroid/wiki/DBLibrary)   
    [KJBitmap与KJHttp的深度用法](http://www.kymjs.com/code/2015/09/24/01/)   
2、更多在实际项目中使用的Demo: [音乐播放器](https://github.com/KJFrame/KJMusic) [爱看博客客户端](https://github.com/KJFrame/KJBlog)    
3、框架API文档：[http://kjframe.github.io](http://kjframe.github.io/)    
*注，KJFrameForAndroid需要在AndroidManifest.xml 中声明如下权限*
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 关于
自我介绍：[张涛就是我](http://blog.kymjs.com/about)<br>
* GitHub项目地址：[https://github.com/kymjs/KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid)  
* QQ 群[257053751](http://jq.qq.com/?_wv=1027&k=WoM2Aa)(开发者群1)，[201055521](http://jq.qq.com/?_wv=1027&k=MBVdpK)(开发者群2)<br>
* 更多我的开源项目：[开源实验室](http://www.kymjs.com/works)
* blog：http://blog.kymjs.com/

## 回馈
如果你认为KJFrameForAndroid帮你节省了大量的开发时间，可否在你有能力的基础捐助我的网站域名与服务器开销？<br>
我们会将捐助者信息公布在[开源实验室·捐赠](http://www.kymjs.com/donate)捐赠页，如果你有什么想说的话也可以[留言](http://www.kymjs.com/tweet)给我。


##开源协议
```
 Copyright (C) 2014-1015, 张涛
 
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
