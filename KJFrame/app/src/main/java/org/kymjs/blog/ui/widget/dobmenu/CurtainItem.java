/*
 * Copyright (c) 2015, 张涛.
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
package org.kymjs.blog.ui.widget.dobmenu;

import android.view.View;

public class CurtainItem {

    public enum SlidingType {
        SIZE, MOVE
    }

    private boolean enabled = true;
    private View curtainContentView;
    private SlidingType slidingType = SlidingType.MOVE;
    private int maxDuration = CurtainViewController.DEFAULT_INT;
    private float jumpLinePercentage = CurtainViewController.DEFAULT_JUMP_LINE_PERCENTAGE;

    private OnSwitchListener onSwitchListener;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public View getSlidingView() {
        return curtainContentView;
    }

    public void setSlidingView(View slidingView) {
        this.curtainContentView = slidingView;
    }

    public SlidingType getSlidingType() {
        return slidingType;
    }

    public void setSlidingType(SlidingType slidingType) {
        this.slidingType = slidingType;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public float getJumpLinePercentage() {
        return jumpLinePercentage;
    }

    public void setJumpLinePercentage(float jumpLinePercentage) {
        this.jumpLinePercentage = jumpLinePercentage;
    }

    public OnSwitchListener getOnSwitchListener() {
        return onSwitchListener;
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    /**
     * 窗帘开关监听器
     */
    public interface OnSwitchListener {
        /**
         * 卷起时回调
         */
        public void onCollapsed();

        /**
         * 掉下时回调
         */
        public void onExpanded();
    }
}
