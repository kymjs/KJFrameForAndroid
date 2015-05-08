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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.kymjs.kjframe.utils.FileUtils;

import android.text.TextUtils;

/**
 * Http请求的参数集合
 */
public class HttpParams implements HttpEntity {

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
    private String mBoundary = null;
    private final String NEW_LINE_STR = "\r\n";
    private final String CONTENT_TYPE = "Content-Type: ";
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";

    /** 文本参数和字符集 */
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";

    /** 字节流参数 */
    private final String TYPE_OCTET_STREAM = "application/octet-stream";
    /**
     * 二进制参数
     */
    private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n"
            .getBytes();
    /**
     * 文本参数
     */
    private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n"
            .getBytes();

    private final Map<String, String> urlParams = new ConcurrentHashMap<String, String>(
            8);
    private final Map<String, String> mHeaders = new HashMap<String, String>();
    private final ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
    private boolean hasFile;
    private String contentType = null;

    private String jsonParams;

    public HttpParams() {
        this.mBoundary = generateBoundary();
        mHeaders.put("cookie", HttpConfig.sCookie);
    }

    /**
     * 生成分隔符
     * 
     * @return
     */
    private final String generateBoundary() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }

    public void putHeaders(final String key, final int value) {
        this.putHeaders(key, value + "");
    }

    public void putHeaders(final String key, final String value) {
        mHeaders.put(key, value);
    }

    public void put(final String key, final int value) {
        this.put(key, value + "");
    }

    public void putJsonParams(String json) {
        this.jsonParams = json;
    }

    /**
     * 添加文本参数
     * 
     * @param key
     * @param value
     */
    public void put(final String key, final String value) {
        urlParams.put(key, value);
        writeToOutputStream(key, value.getBytes(), TYPE_TEXT_CHARSET,
                BIT_ENCODING, "");
    }

    /**
     * 添加二进制参数, 例如Bitmap的字节流参数
     * 
     * @param paramName
     * @param rawData
     */
    public void put(String paramName, final byte[] rawData) {
        hasFile = true;
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM,
                BINARY_ENCODING, "KJFrameFile");
    }

    /**
     * 添加文件参数,可以实现文件上传功能
     * 
     * @param key
     * @param file
     */
    public void put(final String key, final File file) {
        try {
            hasFile = true;
            writeToOutputStream(key,
                    FileUtils.input2byte(new FileInputStream(file)),
                    TYPE_OCTET_STREAM, BINARY_ENCODING, file.getName());
        } catch (FileNotFoundException e) {
        }
    }

    /**
     * 将数据写入到输出流中
     * 
     * @param key
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeToOutputStream(String paramName, byte[] rawData,
            String type, byte[] encodingBytes, String fileName) {
        try {
            writeFirstBoundary();
            mOutputStream
                    .write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream
                    .write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数开头的分隔符
     * 
     * @throws IOException
     */
    private void writeFirstBoundary() throws IOException {
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());
    }

    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(mBoundary).append("\r\n");
        stringBuilder.append(CONTENT_DISPOSITION + "form-data; name=\""
                + paramName + "\"");
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\"" + fileName + "\"");
        }
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }

    @Override
    public long getContentLength() {
        return mOutputStream.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        if (contentType != null) {
            return new BasicHeader("Content-Type", contentType);
        }
        if (hasFile) {
            return new BasicHeader("Content-Type",
                    "multipart/form-data; boundary=" + mBoundary);
        }
        return null;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (hasFile) {
            // 参数最末尾的结束符
            final String endString = "--" + mBoundary + "--\r\n";
            // 写入结束符
            mOutputStream.write(endString.getBytes());
            //
            outstream.write(mOutputStream.toByteArray());
        } else {
            outstream.write(getUrlParams().substring(1).getBytes());
        }
    }

    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(mOutputStream.toByteArray());
    }

    public StringBuilder getUrlParams() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            if (!isFirst) {
                result.append("&");
            } else {
                result.append("?");
                isFirst = false;
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result;
    }

    public String getJsonParams() {
        return jsonParams;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }
}
