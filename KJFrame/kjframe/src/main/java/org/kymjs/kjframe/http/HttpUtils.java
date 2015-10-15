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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Http请求工具类
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class HttpUtils {

    public static byte[] responseToBytes(KJHttpResponse response)
            throws IOException, KJHttpException {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(
                ByteArrayPool.get(), (int) response.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = response.getContentStream();
            if (isGzipContent(response) && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }

            if (in == null) {
                throw new KJHttpException("服务器连接异常");
            }

            buffer = ByteArrayPool.get().getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by
                // "consuming the content".
//                entity.consumeContent();
                response.getContentStream().close();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the
                // entity in
                // an invalid state.
                KJLoger.debug("Error occured when calling consumingContent");
            }
            ByteArrayPool.get().returnBuf(buffer);
            bytes.close();
        }
    }

    /**
     * Returns the charset specified in the Content-Type of this header.
     */
    public static String getCharset(KJHttpResponse response) {
        Map<String, String> header = response.getHeaders();
        if (header != null) {
            String contentType = header.get("Content-Type");
            if (!TextUtils.isEmpty(contentType)) {
                String[] params = contentType.split(";");
                for (int i = 1; i < params.length; i++) {
                    String[] pair = params[i].trim().split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("charset")) {
                            return pair[1];
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getHeader(KJHttpResponse response, String key) {
        return response.getHeaders().get(key);
    }

    public static boolean isSupportRange(KJHttpResponse response) {
        if (TextUtils.equals(getHeader(response, "Accept-Ranges"), "bytes")) {
            return true;
        }
        String value = getHeader(response, "Content-Range");
        return value != null && value.startsWith("bytes");
    }

    public static boolean isGzipContent(KJHttpResponse response) {
        return TextUtils
                .equals(getHeader(response, "Content-Encoding"), "gzip");
    }

}
