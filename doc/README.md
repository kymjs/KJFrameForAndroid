## ![logo](https://github.com/kymjs/KJFrameForAndroid/blob/master/KJLibraryExample/res/drawable-hdpi/ic_launcher.png) CJFrameForAndroid简介
**CJFrameForAndroid** 是一个实现android插件化开发的框架。使用CJFrameForAndroid，apk动态加载不再是难题，更重要的是可以轻松实现插件与APP项目之间的解耦。<br>

## CJFrameForAndroid 相关链接
* blog：http://my.oschina.net/kymjs/blog<br>
* QQ群：[257053751](http://jq.qq.com/?_wv=1027&k=WoM2Aa)(开发者群1)，[201055521](http://jq.qq.com/?_wv=1027&k=MBVdpK)(开发者群2)
* 项目地址：[https://github.com/kymjs/CJFrameForAndroid](https://github.com/kymjs/CJFrameForAndroid)

# 原理描述
CJFrameForAndroid的实现原理是通过类加载器，动态加载存在于SD卡上的apk包中的Activity。通过使用一个托管所，插件Activity全部事务(包括声明周期与交互事件)将交由托管所来处理，间接实现插件的运行。更多介绍:[CJFrameForAndroid原理介绍](http://my.oschina.net/kymjs/blog/331997)<br>
一句话概括：CJFrameForAndroid中的托管所，复制了插件中的Activity，来替代插件中的Activity与用户交互。<br>

# 框架使用
●使用 CJFrameForAndroid 插件开发框架需要在你项目的AndroidManifest.xml文件中加入托管所的声明。<br>
```xml
<activity android:name="org.kymjs.aframe.plugin.CJProxyActivity" />  <!-- 如果使用了插件Activity，需要添加 -->
<service android:name="org.kymjs.aframe.plugin.service.CJProxyService"/>  <!-- 如果使用了插件Service，需要添加 -->
```
●让插件应用中的Activity继承CJActivity，并且一切使用this调用的方法都使用that替代。例如this.setContentView();需要改为that.setContentView();<br>
●插件中涉及到的Android权限，须在APP项目清单中具有声明。<br>
●插件Activity跳转时，推荐使用CJActivityUtils类来辅助跳转。若一定要startActivity或startActivityForResult，在跳转过程中的Intent不能自己new，必须使用CJActivityUtils.getPluginIntent();<br>
●在插件和APP两个工程中不能引用相同的jar包。解决办法是：在插件工程的项目中添加一个/cjlibs的文件夹，将需要调用的jar包放到这个文件夹中，并在插件项目目录下的.classpath中加入如下语句，系统会自动处理相关细节
```xml
<classpathentry kind="lib" path="cjlibs"/>
```

# 示例工程运行
下载[KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid)项目,并运行demo；下载[插件化演示Demo](https://github.com/kymjs/CJFrameForAndroid/tree/master/binrary/DemoResources)，点击KJFrameForAndroid的Demo中Plugin模块根据提示操作

----
## 注意事项
●APP项目和插件项目中，都需要使用到CJFrameForAndroid的jar包。<br>
●在项目中必须加入托管所声明。<br>
●在开发插件的时候，必须继承CJ框架相应基类;<br>
●在插件的Activity中，一切使用this的部分必须使用that来替代;<br>
●在插件Activity跳转时，推荐使用CJActivityUtils类来辅助跳转；<br>
●在插件和APP两个工程中不能引用相同的jar包；<br>

## 名词解释
**APP项目**：指要调用插件apk的那个已经安装到用户手机上的应用。<br>
**插件项目**：指没有被安装且希望借助已经安装到手机上的项目运行的apk。<br>
**插件化**：Activity继承自CJActivity，且与APP项目jar包冲突已经解决的插件项目称为已经被插件化。<br>
**Activity事务**：在CJFrameForAndroid中，一个Activity的生命周期以及交互事件统称为Activity的事务。<br>
**托管所**：指插件中的一个委派/代理Activity，通过这个Activity去处理插件中Activity的全部事务，从而表现为就像插件中的Activity在运行一样。<br>

## 许可
  Copyright (c) 2014, Zhang Tao.
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
	   
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  
## 帮助我
我是张涛，中国深圳，Android高级工程师<br>
如果CJFrameForAndroid项目帮到了你，可否在你有能力的基础捐助本项目的开发与维护，以让我更有信心和能力回馈网友。<br>
[点这里参与我的众筹](https://shenghuo.alipay.com/send/payment/fill.htm) 我的支付宝账号[kymjs@foxmail.com](https://shenghuo.alipay.com/send/payment/fill.htm)<br>
