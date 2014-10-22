## ![logo](https://github.com/kymjs/KJFrameForAndroid/blob/master/KJFrameExample/logo.jpg) KJFrameForAndroid---Plugin模块简介
**Plugin模块** 是一个实现android插件化开发的框架。使用KJFrameForAndroid框架的Plugin模块，apk动态加载不再是难题，更重要的是可以轻松实现插件与APP项目之间的解耦。<br>

# 写在前面
**介绍** <br>
  Plugin模块最初是作为一个独立的框架[CJFrameForAndroid](https://github.com/kymjs/CJFrameForAndroid/)存在，后并入[KJFrameForAndroid](https://github.com/kymjs/KJFrameForAndroid/tree/Plugin)框架中。<br>
  如果看完本介绍后对插件化开发还是不太明白，你可以看看这几篇博客：[CJFrameForAndroid原理介绍](http://my.oschina.net/kymjs/blog/331997)。<br>
**原理** <br>
  CJFrameForAndroid的实现原理是通过类加载器，动态加载存在于SD卡上的apk包中的Activity。通过使用一个托管所，插件Activity全部事务(包括声明周期与交互事件)将交由托管所来处理，间接实现插件的运行。<br>
  一句话描述：CJFrameForAndroid中的托管所，复制了插件中的Activity，来替代插件中的Activity与用户交互。<br>

# 名词解释
**APP项目**：指要调用插件apk的那个已经安装到用户手机上的应用。<br>
**插件项目**：指没有被安装且希望借助已经安装到手机上的项目运行的apk。<br>
**插件化**：Activity继承自CJActivity，且与APP项目jar包冲突已经解决的插件项目称为已经被插件化。<br>
**Activity事务**：在CJFrameForAndroid中，一个Activity的生命周期以及交互事件统称为Activity的事务。<br>
**托管所**：指插件中的一个委派/代理Activity，通过这个Activity去处理插件中Activity的全部事务，从而表现为就像插件中的Activity在运行一样。<br>

# 功能支持
* Activity的动态加载：包括生命周期和交互事件、插件与APP之间的数据通信<br>
* Fragment的完美加载使用<br>
* 动态注册的BroadcastReceiver<br>
* 绑定式、启动式Service均可完美使用<br>
* 已成功模拟出launchMode的效果。(launchModer实际上是一个虚拟的，生命周期的调用还是一样的，仅仅模拟出了系统的BackStack)<br>
* 完美集成了KJFrameForAndroid中UILibrary的全部功能，支持注解式绑定控件<br>

# 框架使用
●使用 CJFrameForAndroid 插件开发框架需要在你项目的AndroidManifest.xml文件中加入托管所的声明。<br>
```xml
<activity android:name="org.kymjs.aframe.plugin.activity.CJProxyActivity" />  <!-- 如果使用了插件Activity，需要添加 -->
<service android:name="org.kymjs.aframe.plugin.service.CJProxyService" />  <!-- 如果使用了插件Service，需要添加 -->
```
●让插件应用中的Activity继承CJActivity，并且一切使用this调用的方法都使用that替代。例如this.setContentView();需要改为that.setContentView();<br>
●插件中涉及到的Android权限，须在APP项目清单中具有声明。<br>
●插件Activity跳转时，推荐使用CJActivityUtils类来辅助跳转。若一定要startActivity或startActivityForResult，在跳转过程中的Intent不能自己new，必须使用CJActivityUtils.getPluginIntent();<br>
●在插件和APP两个工程中不能引用相同的jar包。解决办法是：在插件工程的项目中添加一个/cjlibs的文件夹，将需要调用的jar包放到这个文件夹中，并在插件项目目录下的.classpath中加入如下语句，系统会自动处理相关细节
```xml
<classpathentry kind="lib" path="cjlibs"/>
```

# jar包调用
**使用场景一** >>>通常情况：在主APP工程中使用KJFrameForAndroid的完整版jar包（请选择最新版的CJFrameForAndroid）[下载](https://github.com/kymjs/KJFrameForAndroid/tree/Plugin/binrary)；考虑到插件的大小应该越小越好，我们特意制作了精简版的jar包，仅包含插件所比需功能与注解式绑定控件的功能。在插件工程中使用精简版jar包（请选择最新版本的CJPlugin.jar）[下载](https://github.com/kymjs/KJFrameForAndroid/tree/Plugin/binrary)；<br>
**使用场景二** >>>适合插件工程较庞大时使用：在APP工程和插件工程中都使用KJFrameForAndroid的完整版jar包（请选择最新版的CJFrameForAndroid）[下载](https://github.com/kymjs/KJFrameForAndroid/tree/Plugin/binrary)；好处在于开发插件工程时也可以得到KJFrameForAndroid完整版的全部功能支持。<br>
**使用场景三** >>>只使用插件化模块：你可以在在APP工程和插件工程中都使用精简版jar包。

# 注意事项
●APP项目和插件项目中，都需要使用到插件化的jar包。<br>
●在项目中必须加入托管所声明。<br>
●在开发插件的时候，必须继承CJ框架相应基类;<br>
●在插件的Activity中，一切使用this的部分必须使用that来替代;<br>
●在插件Activity跳转时，推荐使用CJActivityUtils类来辅助跳转；<br>
●在Service启动的时候，必须使用CJServiceUtils来启动;<br>
●在插件和APP两个工程中不能引用相同的jar包；<br>

## 关于作者kymjs
blog：http://my.oschina.net/kymjs/blog<br>
email：kymjs123@gmail.com<br>
forum/bbs: [http://tieba.baidu.com/f?kw=kym%BD%A9%CA%AC&fr=index](http://tieba.baidu.com/f?kw=kym%BD%A9%CA%AC&fr=index)<br>