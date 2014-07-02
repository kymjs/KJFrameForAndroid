package org.kymjs.aframe;

import java.util.Stack;

import org.kymjs.aframe.ui.activity.KJFrameActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.1
 * @created 2014-2-28
 */
public class KJActivityManager {
    private static Stack<KJFrameActivity> activityStack;

    private KJActivityManager() {
    }

    private static class ManagerHolder {
        private static final KJActivityManager instance = new KJActivityManager();
    }

    public static KJActivityManager create() {
        return ManagerHolder.instance;
    }

    public int getCount() {
        return activityStack.size();
    }

    /**
     * 添加Activity到栈
     */
    public void addActivity(KJFrameActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<KJFrameActivity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public KJFrameActivity topActivity() {
        if (activityStack == null || activityStack.isEmpty()) {
            return null;
        }
        KJFrameActivity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    public KJFrameActivity findActivity(Class<?> cls) {
        KJFrameActivity activity = null;
        for (KJFrameActivity aty : activityStack) {
            if (aty.getClass().equals(cls)) {
                activity = aty;
                break;
            }
        }
        return activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finishActivity() {
        KJFrameActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Class<?> cls) {
        for (KJFrameActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     * 
     * @param cls
     */
    public void finishOthersActivity(Class<?> cls) {
        for (KJFrameActivity activity : activityStack) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 应用程序退出
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            System.exit(-1);
        }
    }
}