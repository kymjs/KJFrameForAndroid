/*
 * Copyright (c) 2014, 张涛.
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
package org.kymjs.kjframe.http;

import android.util.Log;

/**
 * KJHttp.download()控制器
 * 
 * @author kymjs (http://www.kymjs.com/) .
 */
public class DownloadController {

    private final FileRequest mRequest;
    private final DownloadTaskQueue mQueue;

    private int mStatus;
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_DISCARD = 4;

    DownloadController(DownloadTaskQueue queue, FileRequest request) {
        mRequest = request;
        mQueue = queue;
    }

    /* package */boolean equalsRequest(String storeFilePath, String url) {
        return (storeFilePath.equals(mRequest.getStoreFile().getAbsolutePath()) && url
                .equals(mRequest.getUrl()));
    }

    /* apckage */boolean equalsUrl(String url) {
        return url.equals(mRequest.getUrl());
    }

    /**
     * 如果当前任务是等待态，让他转入运行态
     *
     * @return
     */
    /* package */boolean doLoadOnWait() {
        if (mStatus == STATUS_WAITING) {
            mStatus = STATUS_DOWNLOADING;
            if (mQueue.getRequestQueue() != null) {
                mRequest.resume();
                mQueue.getRequestQueue().add(mRequest);
            } else {
                Log.e("KJLibrary",
                        "must call be DownloadTaskQueue.setRequestQueue()");
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 这个控制器负责的Request
     *
     * @return
     */
    public FileRequest getRequest() {
        return mRequest;
    }

    /**
     * 获取文件的下载状态(待下载，正在下载，已暂停，已完成，已移除)<br>
     * Controller.STATUS_WAITING = 0; <br>
     * Controller.STATUS_DOWNLOADING = 1;<br>
     * Controller.STATUS_PAUSE = 2; <br>
     * Controller.STATUS_SUCCESS = 3;<br>
     * Controller.STATUS_DISCARD = 4;<br>
     */
    public int getStatus() {
        return mStatus;
    }

    public boolean isDownloading() {
        return mStatus == STATUS_DOWNLOADING;
    }

    /**
     * 暂停任务
     *
     * @return
     */
    public boolean pause() {
        if ((mStatus == STATUS_DOWNLOADING || mStatus == STATUS_WAITING) && mRequest != null && 
                mQueue != null) {
            mStatus = STATUS_PAUSE;
            mRequest.cancel();
            mQueue.wake();
            return true;
        }
        return false;
    }

    /**
     * 恢复处于暂停态的任务
     *
     * @return 如果mQueue为null或当前状态不是STATUS_PAUSE，返回false
     * @deprecated 不推荐直接调用本方法，建议直接再次调用{@link DownloadTaskQueue#add(FileRequest)}
     */
    @Deprecated
    public boolean resume() {
        if (mStatus == STATUS_PAUSE && mQueue != null) {
            mStatus = STATUS_WAITING;
            mQueue.wake();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 废弃当前下载任务
     *
     * @return
     */
    public boolean removeTask() {
        if (mStatus == STATUS_DISCARD || mStatus == STATUS_SUCCESS) {
            return false;
        }
        if ((mStatus == STATUS_DOWNLOADING || mStatus == STATUS_WAITING) && mRequest != null) {
            mRequest.cancel();
            mStatus = STATUS_DISCARD;
        }
        if (mRequest != null && mQueue != null) {
            mQueue.remove(mRequest.getUrl());
            return true;
        } else {
            return false;
        }
    }
}