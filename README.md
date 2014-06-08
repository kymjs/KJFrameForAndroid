KJFrameForAndroid
=================

*注由于网络问题，本项目已转移至国内代码托管平台请移步：[KJLibrar](http://git.oschina.net/kymjs/KJLibrary#readme)*

## 概述

**KJFrameForAndroid** 又叫KJLibrary，免费的、开源的、简易的、遵循Apache Licence 2.0开源协议发布的android应用开发框架，总共分为五大模块：UILibrary，UtilsLibrary，HttpLibrary，BitmapLibrary，DBLibrary。

**UILibrary模块**分为两部分，widget、topology
> widget部分包含了目前应用开发中常见的自定义控件，例如上下拉ListView、瀑布流、可缩放的ImageView。<br>
> Topology部分包含一个使用IOC设计思想的控件初始化方式：可通过注解的方式进行UI绑定，与设置监听，在Activity和Fragment中均可以通过一行代码绑定控件并实现点击监听；还包含了在目前应用开发中常见的布局界面，如侧滑效果，高效的底部TAB导航，3D效果的切换。<br>
> 同时UILibrary为开发者定义了完善的BaseActivity和BaseFragment，开发者只需手动继承就可以获得Topology部分的全部功能。<br>

**UtilsLibrary模块** 包含了应用开发中的常用工具类，例如系统级别的Log管理、网络状态监测、Bitmap压缩工具类、获取屏幕宽高以及单位转换的工具类、错误信息处理与文件处理工具类、preference工具类、字符串操作与常用正则判断等。<br>

**HttpLibrary模块** 使用HttpClient与HttpUrlConnection两种实现方式实现网络通信、数据上传、多线程断点下载。根据Google建议：在2.3系统之前由于HttpUrlConnection不稳定且有一定的BUG，应该尽量使用HttpClient；在2.3以后的系统，若只是简单的数据交互，应该使用更加轻量级、易扩展的HttpUrlConnection。对于实现的方式，KJLibrary将交由开发者来选择。<br>

**BitmapLibrary模块** 的使用：可以让开发者在使用imageview加载图片的时候无需考虑图片加载过程中出现的OOM问题以及在ListView滑动过程中出现的图片错位问题。<br>

**DBLibrary模块** 目前使用的是开源框架afinal的FinalDB，是Android中的ORM框架，一行代码就可以进行增删改查操作。支持一对多，多对一等查询。<br>

## 许可
********本项目采用 Apache Licence 2.0 授权协议:<br>
Apache Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）<br>
[更多...](http://www.oschina.net/question/12_2828)<br>
********欢迎大家在这个基础上进行改进，并与大家分享。<br>

## 安装与协作
*注：本文假设你已经有Android开发环境*

启动Eclipse，点击菜单并导入Android客户端项目，请确保你当前的Android SDK是最新版。<br>
如果编译出错，请修改项目根目录下的 project.properties 文件。<br>
推荐使用Android 4.0 以上版本的SDK,请使用JDK1.6编译：
> target=android-17

 1. 签出项目

        git clone https://git.oschina.net/kymjs/KJFrameForAndroid.git

 2. 导入eclipse

        file->import

 3. 提交
 
        team->commit
        team->push

## 关于作者kymjs
blog：http://my.oschina.net/kymjs/blog<br>
email：kymjs123@gmail.com<br>
KJLibrary交流QQ群：[257053751](http://shang.qq.com/wpa/qunwpa?idkey=00d92c040e81d87ccd21f8d0fffb10640baaa66da45254c3bd329b6ff7d46fef)
