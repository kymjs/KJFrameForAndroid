[![OSL](https://kymjs.com/qiniu/image/logo3.png)](https://www.kymjs.com/works/)


=================

网络请求与图片加载模块请使用：[RxVolley：https://github.com/kymjs/RxVolley](https://github.com/kymjs/RxVolley)   
重写的```KJFrame```, API 设计更合理，文档更完善。   
支持断点续传、大文件上传进度、https、cookie持久化、Rxjava。

---

`KJFrameForAndroid` 是一个 `Android` 快速开发框架。同时封装了`Bitmap`、`Http`、`数据库`使原本复杂操作最简化，实现快速而又安全的开发APP。  

这个框架是我从 `2014`年开始开发的，这么多年断断续续一直在维护，期间也被很多大厂使用，比如曾经的无盒子不开撸的`YY多玩盒子`、`乐视TV`、`中国联通`、当然还有很多我不知道的APP，希望也能帮到你。


目前已经兼容  

*   Android S 开发
*   支持 androidx
*   targetSdkVersion=30
*   符合国家要求的隐私权限调用


## 快速入门
#### AndroidStudio
  
最新版本：[![](https://jitpack.io/v/kymjs/KJFrameForAndroid.svg)](https://jitpack.io/#kymjs/KJFrameForAndroid)  

``` 
// root build.gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

// module build.gradle
dependencies {
    implementation 'com.github.kymjs:KJFrameForAndroid:3.0.0'
}
```

#### eclipse（大清都亡了，还在用？）

复制jar包 [KJFrameForAndroid_v2.x](https://github.com/kymjs/KJFrameForAndroid/tree/master/binrary) 到你工程的/libs目录中.   
eclipes版本源码请查看[相关分支](https://github.com/kymjs/KJFrameForAndroid/tree/eclipse_end)   

## 使用帮助
1、这几篇博客也许能帮到你  
    [MVC模块](https://github.com/kymjs/KJFrameForAndroid/wiki/MVCLibrary_cn)   
    [KJBitmap使用方法](https://www.kymjs.com/code/2015/03/25/01/)   
    [KJHttp请求的使用](https://www.kymjs.com/code/2015/05/12/01/)   
    [数据库模块使用方法](https://github.com/kymjs/KJFrameForAndroid/wiki/DBLibrary)   
    [KJBitmap与KJHttp的深度用法](https://www.kymjs.com/code/2015/09/24/01/)   
2、更多在实际项目中使用的Demo: [音乐播放器](https://github.com/KJFrame/KJMusic) [爱看博客客户端](https://github.com/KJFrame/KJBlog)    
3、框架API文档：[http://kjframe.github.io](https://kjframe.github.io/)     


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
