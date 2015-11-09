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

import android.text.TextUtils;

import org.kymjs.kjframe.utils.KJLoger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 请求文件方法类
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class FileRequest extends Request<byte[]> {
    private final File mStoreFile;
    private final File mTemporaryFile; // 临时文件

    private Map<String, String> mHeaders = new HashMap<>();

    public FileRequest(String storeFilePath, String url, HttpCallBack callback) {
        super(HttpMethod.GET, url, callback);
        mStoreFile = new File(storeFilePath);
        mHeaders.put("cookie", HttpConfig.sCookie);
        File folder = mStoreFile.getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        if (!mStoreFile.exists()) {
            try {
                mStoreFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mTemporaryFile = new File(storeFilePath + ".tmp");
    }

    public File getStoreFile() {
        return mStoreFile;
    }

    public File getTemporaryFile() {
        return mTemporaryFile;
    }

    @Override
    public String getCacheKey() {
        return "";
    }

    @Override
    public boolean shouldCache() {
        return false;
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        String errorMessage = null;
        if (!isCanceled()) {
            if (mTemporaryFile.canRead() && mTemporaryFile.length() > 0) {
                if (mTemporaryFile.renameTo(mStoreFile)) {
                    return Response.success(response.data, response.headers,
                            HttpHeaderParser.parseCacheHeaders(mConfig,
                                    response));
                } else {
                    errorMessage = "Can't rename the download temporary file!";
                }
            } else {
                errorMessage = "Download temporary file was invalid!";
            }
        }
        if (errorMessage == null) {
            errorMessage = "Request was Canceled!";
        }
        return Response.error(new KJHttpException(errorMessage));
    }

    @Override
    public Map<String, String> getHeaders() {
        mHeaders.put("Range", "bytes=" + mTemporaryFile.length() + "-");
        mHeaders.put("Accept-Encoding", "identity");
        return mHeaders;
    }

    public void setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public byte[] handleResponse(KJHttpResponse response) throws IOException,
            KJHttpException {
        long fileSize = response.getContentLength();
        if (fileSize <= 0) {
            KJLoger.debug("Response doesn't present Content-Length!");
        }

        long downloadedSize = mTemporaryFile.length();
        boolean isSupportRange = HttpUtils.isSupportRange(response);
        if (isSupportRange) {
            fileSize += downloadedSize;

            String realRangeValue = response.getHeaders().get("Content-Range");
            if (!TextUtils.isEmpty(realRangeValue)) {
                String assumeRangeValue = "bytes " + downloadedSize + "-"
                        + (fileSize - 1);
                if (TextUtils.indexOf(realRangeValue, assumeRangeValue) == -1) {
                    throw new IllegalStateException(
                            "The Content-Range Header is invalid Assume["
                                    + assumeRangeValue + "] vs Real["
                                    + realRangeValue + "], "
                                    + "please remove the temporary file ["
                                    + mTemporaryFile + "].");
                }
            }
        }

        if (fileSize > 0 && mStoreFile.length() == fileSize) {
            mStoreFile.renameTo(mTemporaryFile);
            mRequestQueue.getConfig().mDelivery.postDownloadProgress(this,
                    fileSize, fileSize);
            return null;
        }

        RandomAccessFile tmpFileRaf = new RandomAccessFile(mTemporaryFile, "rw");
        if (isSupportRange) {
            tmpFileRaf.seek(downloadedSize);
        } else {
            tmpFileRaf.setLength(0);
            downloadedSize = 0;
        }

        InputStream in = response.getContentStream();
        try {
            if (HttpUtils.isGzipContent(response)
                    && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }
            byte[] buffer = new byte[6 * 1024]; // 6K buffer
            int offset;

            while ((offset = in.read(buffer)) != -1) {
                tmpFileRaf.write(buffer, 0, offset);

                downloadedSize += offset;
                mRequestQueue.getConfig().mDelivery.postDownloadProgress(this,
                        fileSize, downloadedSize);

                if (isCanceled()) {
                    break;
                }
            }
        } finally {
            in.close();
            try {
                response.getContentStream().close();
            } catch (Exception e) {
                KJLoger.debug("Error occured when calling consumingContent");
            }
            tmpFileRaf.close();
        }
        return null;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected void deliverResponse(Map<String, String> headers, byte[] response) {
        if (mCallback != null) {
            mCallback.onSuccess(headers, response);
        }
    }
}
