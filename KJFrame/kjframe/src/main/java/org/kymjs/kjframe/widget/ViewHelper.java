/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kymjs.kjframe.widget;

import org.kymjs.kjframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.view.View;

/**
 * KJSlidingMenu所需工具类
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public final class ViewHelper {
    private ViewHelper() {}

    public static float getAlpha(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getAlpha()
                : Honeycomb.getAlpha(view);
    }

    public static void setAlpha(View view, float alpha) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setAlpha(alpha);
        } else {
            Honeycomb.setAlpha(view, alpha);
        }
    }

    public static float getPivotX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getPivotX()
                : Honeycomb.getPivotX(view);
    }

    public static void setPivotX(View view, float pivotX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setPivotX(pivotX);
        } else {
            Honeycomb.setPivotX(view, pivotX);
        }
    }

    public static float getPivotY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getPivotY()
                : Honeycomb.getPivotY(view);
    }

    public static void setPivotY(View view, float pivotY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setPivotY(pivotY);
        } else {
            Honeycomb.setPivotY(view, pivotY);
        }
    }

    public static float getRotation(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getRotation() : Honeycomb.getRotation(view);
    }

    public static void setRotation(View view, float rotation) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setRotation(rotation);
        } else {
            Honeycomb.setRotation(view, rotation);
        }
    }

    public static float getRotationX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getRotationX() : Honeycomb.getRotationX(view);
    }

    public static void setRotationX(View view, float rotationX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setRotationX(rotationX);
        } else {
            Honeycomb.setRotationX(view, rotationX);
        }
    }

    public static float getRotationY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getRotationY() : Honeycomb.getRotationY(view);
    }

    public static void setRotationY(View view, float rotationY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setRotationY(rotationY);
        } else {
            Honeycomb.setRotationY(view, rotationY);
        }
    }

    public static float getScaleX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getScaleX()
                : Honeycomb.getScaleX(view);
    }

    public static void setScaleX(View view, float scaleX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setScaleX(scaleX);
        } else {
            Honeycomb.setScaleX(view, scaleX);
        }
    }

    public static float getScaleY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getScaleY()
                : Honeycomb.getScaleY(view);
    }

    public static void setScaleY(View view, float scaleY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setScaleY(scaleY);
        } else {
            Honeycomb.setScaleY(view, scaleY);
        }
    }

    public static float getScrollX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getScrollX() : Honeycomb.getScrollX(view);
    }

    public static void setScrollX(View view, int scrollX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setScrollX(scrollX);
        } else {
            Honeycomb.setScrollX(view, scrollX);
        }
    }

    public static float getScrollY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getScrollY() : Honeycomb.getScrollY(view);
    }

    public static void setScrollY(View view, int scrollY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setScrollY(scrollY);
        } else {
            Honeycomb.setScrollY(view, scrollY);
        }
    }

    public static float getTranslationX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getTranslationX() : Honeycomb.getTranslationX(view);
    }

    public static void setTranslationX(View view, float translationX) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setTranslationX(translationX);
        } else {
            Honeycomb.setTranslationX(view, translationX);
        }
    }

    public static float getTranslationY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view)
                .getTranslationY() : Honeycomb.getTranslationY(view);
    }

    public static void setTranslationY(View view, float translationY) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setTranslationY(translationY);
        } else {
            Honeycomb.setTranslationY(view, translationY);
        }
    }

    public static float getX(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getX()
                : Honeycomb.getX(view);
    }

    public static void setX(View view, float x) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setX(x);
        } else {
            Honeycomb.setX(view, x);
        }
    }

    public static float getY(View view) {
        return AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(view).getY()
                : Honeycomb.getY(view);
    }

    public static void setY(View view, float y) {
        if (AnimatorProxy.NEEDS_PROXY) {
            AnimatorProxy.wrap(view).setY(y);
        } else {
            Honeycomb.setY(view, y);
        }
    }

    @SuppressLint("NewApi")
    private static final class Honeycomb {
        static float getAlpha(View view) {
            return view.getAlpha();
        }

        static void setAlpha(View view, float alpha) {
            view.setAlpha(alpha);
        }

        static float getPivotX(View view) {
            return view.getPivotX();
        }

        static void setPivotX(View view, float pivotX) {
            view.setPivotX(pivotX);
        }

        static float getPivotY(View view) {
            return view.getPivotY();
        }

        static void setPivotY(View view, float pivotY) {
            view.setPivotY(pivotY);
        }

        static float getRotation(View view) {
            return view.getRotation();
        }

        static void setRotation(View view, float rotation) {
            view.setRotation(rotation);
        }

        static float getRotationX(View view) {
            return view.getRotationX();
        }

        static void setRotationX(View view, float rotationX) {
            view.setRotationX(rotationX);
        }

        static float getRotationY(View view) {
            return view.getRotationY();
        }

        static void setRotationY(View view, float rotationY) {
            view.setRotationY(rotationY);
        }

        static float getScaleX(View view) {
            return view.getScaleX();
        }

        static void setScaleX(View view, float scaleX) {
            view.setScaleX(scaleX);
        }

        static float getScaleY(View view) {
            return view.getScaleY();
        }

        static void setScaleY(View view, float scaleY) {
            view.setScaleY(scaleY);
        }

        static float getScrollX(View view) {
            return view.getScrollX();
        }

        static void setScrollX(View view, int scrollX) {
            if (SystemTool.getSDKVersion() > 11) {
                view.setScrollX(scrollX);
            }
        }

        static float getScrollY(View view) {
            return view.getScrollY();
        }

        static void setScrollY(View view, int scrollY) {
            if (SystemTool.getSDKVersion() > 11) {
                view.setScrollY(scrollY);
            }
        }

        static float getTranslationX(View view) {
            return view.getTranslationX();
        }

        static void setTranslationX(View view, float translationX) {
            view.setTranslationX(translationX);
        }

        static float getTranslationY(View view) {
            return view.getTranslationY();
        }

        static void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }

        static float getX(View view) {
            return view.getX();
        }

        static void setX(View view, float x) {
            view.setX(x);
        }

        static float getY(View view) {
            return view.getY();
        }

        static void setY(View view, float y) {
            view.setY(y);
        }
    }
}
