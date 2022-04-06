[![OSL](https://kymjs.com/qiniu/image/logo3.png)](https://www.kymjs.com/works/)


=================

网络请求与图片加载模块请使用：[RxVolley：https://github.com/kymjs/RxVolley](https://github.com/kymjs/RxVolley)   
重写的```KJFrame```, API 设计更合理，文档更完善。   
支持断点续传、大文件上传进度、https、cookie持久化、Rxjava。

---
*KJFrameForAndroid* 又叫KJLibrary，是一个android的orm 和 ioc 框架。同时封装了android中的Bitmap与Http操作的框架，使其更加简单易用；<br>
KJFrameForAndroid的设计思想是通过封装Android原生SDK中复杂的复杂操作而达到简化Android应用级开发，最终实现快速而又安全的开发APP。我们提倡用最少的代码，完成最多的操作，用最高的效率，完成最复杂的功能。<br>

## 快速入门
#### AndroidStudio

build.gradle中添加：  

```groovy 
implementation 'org.kymjs.kjframe:kjframe:2.6'
```

#### eclipse

复制jar包 [KJFrameForAndroid_v2.x](https://github.com/kymjs/KJFrameForAndroid/tree/master/binrary) 到你工程的/libs目录中.   
eclipes版本源码请查看[相关分支](https://github.com/kymjs/KJFrameForAndroid/tree/eclipse_end)   

## 使用帮助
1、很遗憾，没有帮助文档，但这几篇博客也许能帮到你  
    [MVC模块](https://github.com/kymjs/KJFrameForAndroid/wiki/MVCLibrary_cn)   
    [KJBitmap使用方法](https://www.kymjs.com/code/2015/03/25/01/)   
    [KJHttp请求的使用](https://www.kymjs.com/code/2015/05/12/01/)   
    [数据库模块使用方法](https://github.com/kymjs/KJFrameForAndroid/wiki/DBLibrary)   
    [KJBitmap与KJHttp的深度用法](https://www.kymjs.com/code/2015/09/24/01/)   
2、更多在实际项目中使用的Demo: [音乐播放器](https://github.com/KJFrame/KJMusic) [爱看博客客户端](https://github.com/KJFrame/KJBlog)    
3、框架API文档：[http://kjframe.github.io](https://kjframe.github.io/)    

*注，`KJFrameForAndroid`需要在 `AndroidManifest.xml` 中声明如下权限*

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## 关于

[开源实验室开放项目](https://kymjs.com/)  


## 开源协议
```
 Copyright (C) 2014-2016, 张涛
 
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
