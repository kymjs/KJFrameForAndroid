=================
#[![OSL](http://www.kymjs.com/image/logo_s.png)](http://www.kymjs.com/works/)

## KJFrameForAndroid简介
*KJFrameForAndroid* 又叫KJLibrary，是一个android的orm 和 ioc 框架。同时封装了android中的Bitmap与Http操作的框架，使其更加简单易用；<br>
KJFrameForAndroid的设计思想是通过封装Android原生SDK中复杂的复杂操作而达到简化Android应用级开发，最终实现快速而又安全的开发APP。我们提倡用最少的代码，完成最多的操作，用最高的效率，完成最复杂的功能。<br>

## 使用说明
####AndroidStudio
build.gradle中添加：  
``` 
compile 'org.kymjs.kjframe:kjframe:2.+'
```
####eclipse
复制jar包 [KJFrameForAndroid_v2.x](https://github.com/kymjs/KJFrameForAndroid/tree/master/binrary) 到你工程的/libs目录中.<br>
eclipes版本源码请查看[相关分支](https://github.com/kymjs/KJFrameForAndroid/tree/eclipse_end)<br>

## 快速入门
1、查看各个模块的使用帮助<br>
    [MVC模块](https://github.com/kymjs/KJFrameForAndroid/wiki/MVCLibrary_cn)<br>
    [Bitmap加载](https://github.com/kymjs/KJFrameForAndroid/wiki/BitmapLibrary_cn)<br>
    [Http请求](https://github.com/kymjs/KJFrameForAndroid/wiki/HttpLibrary_cn)<br>
    [数据库模块](https://github.com/kymjs/KJFrameForAndroid/wiki/DBLibrary)<br>
2、更多在实际项目中使用的Demo: [音乐播放器](https://github.com/KJFrame/KJMusic) [爱看博客客户端](https://github.com/KJFrame/KJBlog) <br>
3、框架API文档：[http://kjframe.github.io](http://kjframe.github.io/)
*注，KJFrameForAndroid需要在AndroidManifest.xml 中声明如下权限*
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 关于
* GitHub项目地址：[https://github.com/kymjs/KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid)  
* QQ 群[257053751](http://jq.qq.com/?_wv=1027&k=WoM2Aa)(开发者群1)，[201055521](http://jq.qq.com/?_wv=1027&k=MBVdpK)(开发者群2)<br>
* 更多我的开源项目：[开源实验室](http://www.kymjs.com/)
* blog：http://blog.kymjs.com/

自我介绍：[张涛就是我](http://blog.kymjs.com/about)<br>
如果我的项目帮到了你，可否在你有能力的基础捐助我买书学习，以让我更有信心和能力回馈网友。<br>
[点这里参与捐助](https://shenghuo.alipay.com/send/payment/fill.htm) 我的支付宝账号[kymjs@foxmail.com](https://shenghuo.alipay.com/send/payment/fill.htm)<br>

我们会将捐助者信息公布在[开源实验室·捐赠](http://www.kymjs.com/donate)捐赠页，如果你有什么想说的话也可以留言给我。


##开源协议
```
 Copyright (C) 2014, 张涛
 
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
