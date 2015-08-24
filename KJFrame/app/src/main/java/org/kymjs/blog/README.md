#Activity继承链调用规则
---

##基类KJActivity中方法调用顺序(SupportFragment具有同样方法)
1. setRootView() // 一般在这里调用setContentView()以及需要在setContentView之前做的操作   
2. @BindView     // setRootView执行后将会执行注解绑定，相当于调用findViewById()    
3. initDataFromThread();    // 执行在异步，用于做耗时操作   
4. threadDataInited();      // initDataFromThread执行完成后将会回调  
5. initData();              //用于初始化数据   
6. initWidget();            //用于设置控件内容。先初始化数据，后初始化控件   
7. registerBroadcast();     //用于注册广播与上下文菜单  

##TitleBarActivity中执行逻辑  
1. 继承自TitleBarActivity类的layout必须调用  
```xml
<include  
        android:id="@+id/titlebar"  
        layout="@layout/main_titlebar" />  
```

2. TitleBarActivity  
本类主要定义了在Activity中对自定义ActionBar的操作  

声明了两方法，相当于在自定义ActionBar的左上角和右上角的两个按钮的点击事件  
```java
    protected void onBackClick() {}  
    protected void onMenuClick() {}
```
    
##TitleBarFragment
1. 本类主要定义了在Fragment中对自定义ActionBar的操作  

2. 封装了自定义ActionBar的三个点的操作左右角的图片与中间的标题，  
    可以通过调用setActionBarRes(actionBarRes)方法，设置  
    actionBarRes.title; actionBarRes.backImageId; actionBarRes.menuImageId    
    三个属性控制ActionBar三个点的显示内容与是否隐藏  

3. TitleBarFragment同样支持TitleBarActivity的两个方法回调  

4. setActionBarRes()以及其相关方法均在onResume()中调用  
 
##其他说明
1. 所有Activity的基类中都有一个Activity的引用，叫aty，用于替换this的使用  

2. 所有Fragment的基类中都有一个外部Activity的引用，叫outsideAty，用于替换getActivity()方法  
