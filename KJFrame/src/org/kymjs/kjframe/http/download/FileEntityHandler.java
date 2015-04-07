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
package org.kymjs.kjframe.http.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.kymjs.kjframe.http.download.SimpleDownloader.DownloadProgress;
import org.kymjs.kjframe.utils.FileUtils;

/**
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class FileEntityHandler {

    private boolean mStop = false;

    public boolean isStop() {
        return mStop;
    }

    public void setStop(boolean stop) {
        this.mStop = stop;
    }

    public File handleEntity(HttpEntity entity, DownloadProgress callback,
            File save, boolean isResume) throws IOException {
        long current = 0;
        RandomAccessFile file = new RandomAccessFile(save, "rw");
        if (isResume) {
            current = file.length();
        }
        InputStream input = entity.getContent();
        long count = entity.getContentLength() + current;
        if (mStop) {
            FileUtils.closeIO(file);
            return save;
        }
        // 在这里其实这样写是不对的，之所以如此是为了用户体验，谁都不想自己下载时进度条都走了一大半了，就因为一个暂停一下子少了一大串
        /**
         * 这里实际的写法应该是： <br>
         * current = input.skip(current); <br>
         * file.seek(current); <br>
         * 根据JDK文档中的解释：Inputstream.skip(long i)方法跳过i个字节，并返回实际跳过的字节数。<br>
         * 导致这种情况的原因很多，跳过 n 个字节之前已到达文件末尾只是其中一种可能。这里我猜测可能是碎片文件的损害造成的。
         */
        file.seek(input.skip(current));

        int readLen = 0;
        byte[] buffer = new byte[1024];

        while ((readLen = input.read(buffer, 0, 1024)) != -1) {
            if (mStop) {
                break;
            } else {
                file.write(buffer, 0, readLen);
                current += readLen;
                callback.onProgress(count, current);
            }
        }
        callback.onProgress(count, current);

        if (mStop && current < count) { // 用户主动停止
            FileUtils.closeIO(file);
            throw new IOException("user stop download thread");
        }
        FileUtils.closeIO(file);
        return save;
    }
}
