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
import android.util.Log;

import org.kymjs.kjframe.utils.FileUtils;
import org.kymjs.kjframe.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static android.R.attr.type;

/**
 * Http请求的参数集合
 *
 * @author kymjs (http://www.kymjs.com/) .
 */
public class HttpParams implements Serializable {

    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    .toCharArray();
    private String mBoundary = null;
    private final String NEW_LINE_STR = "\r\n";
    private final String CONTENT_TYPE = "Content-Type: ";
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";

    /**
     * 文本参数和字符集
     */
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";

    /**
     * 字节流参数
     */
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

    private final Map<String, Object> urlParams = new ConcurrentHashMap<String, Object>(
            8);
    private final Map<String, String> mHeaders = new HashMap<String, String>();
//    private final ByteArrayOutputStream outstream = new ByteArrayOutputStream();
    private boolean hasFile;
    private String contentType = null;

    private String jsonParams;

    public HttpParams() {
        this.mBoundary = generateBoundary();
        mHeaders.put("cookie", HttpConfig.sCookie);
    }

    /**
     * 生成分隔符
     */
    private String generateBoundary() {
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

    public void put(final OutputStream outstream,final String key, final int value) {
        this.put(outstream,key, value + "");
    }
    public void put(final String key, final int value) {
        this.put(key, value + "");
    }

    public void putJsonParams(String json) {
        this.jsonParams = json;
    }

    /**
     * 添加文本参数 写入流
     */
    public void put(final OutputStream outstream,final String key, final String value) {
//        urlParams.put(key, value);
        writeToOutputStream(outstream,key, value.getBytes(), TYPE_TEXT_CHARSET,
                BIT_ENCODING, "");
    }


    /**
     * 添加文本参数
     */
    public void put(final String key, final String value) {
        urlParams.put(key, value);

    }

    public void showAll(){
    	for (String key : urlParams.keySet()) {
    		Log.v("HttpMap","key = " + key + "; value = " + urlParams.get(key));
    		}
    }

    /**
     * 添加二进制参数, 例如Bitmap的字节流参数 写入流
     */
    public void put(final OutputStream outstream,String paramName, final byte[] rawData) {
        hasFile = true;
        writeToOutputStream(outstream,paramName, rawData, TYPE_OCTET_STREAM,
                BINARY_ENCODING, "KJFrameFile");
    }

    /**
     * 添加二进制参数, 例如Bitmap的字节流参数
     */
    public void put(String paramName, final byte[] rawData) {
        hasFile = true;
        urlParams.put(paramName, rawData);
    }

    /**
     * 添加文件参数,可以实现文件上传功能 写入流
     */
    public void put(final OutputStream outstream,final String key, final File file) {

        hasFile = true;
        try {
            writeFirstBoundary(outstream);
            outstream
                    .write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            outstream
                    .write(getContentDispositionBytes(key, file.getName()));
            outstream.write(BINARY_ENCODING);
            InputStream inStream=new FileInputStream(file);

            byte[] buffer = null;
            long sum=0;
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inStream.read(b, 0, b.length)) != -1) {
                System.out.println("写入到网络流"+":"+(sum+=len));
                outstream.write(b, 0, len);
            }
            inStream.close();
            outstream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }


//        try {
//
//            writeToOutputStream(key,
//                    FileUtils.input2byte(new FileInputStream(file)),
//                    TYPE_OCTET_STREAM, BINARY_ENCODING, file.getName());
//        } catch (FileNotFoundException e) {
//            Log.e("kjframe", "HttpParams.put()-> file not found");
//        }
    }


    /**
     * 添加文件参数,可以实现文件上传功能
     */
    public void put(final String key, final File file) {

        hasFile = true;
        urlParams.put(key, file);
    }

    /**
     * 将数据写入到输出流中
     */
    private void writeToOutputStream(final OutputStream outstream,String paramName, byte[] rawData,
                                     String type, byte[] encodingBytes, String fileName) {
        try {
            writeFirstBoundary(outstream);
            outstream
                    .write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            outstream
                    .write(getContentDispositionBytes(paramName, fileName));
            outstream.write(encodingBytes);
            outstream.write(rawData);
            outstream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数开头的分隔符
     *
     * @throws IOException
     */
    private void writeFirstBoundary(final OutputStream outstream) throws IOException {
        outstream.write(("--" + mBoundary + "\r\n").getBytes());
    }

    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(mBoundary).append("\r\n").append(CONTENT_DISPOSITION)
                .append("form-data; name=\"").append(paramName).append("\"");
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\"").append(fileName).append("\"");
        }
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }

//    public long getContentLength() {
//        return outstream.toByteArray().length;
//    }

    public String getContentType() {
        //如果contentType没有被自定义，且参数集包含文件，则使用有文件的contentType
        if (hasFile && contentType == null) {
            contentType = "multipart/form-data; boundary=" + mBoundary;
        }
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        if (hasFile) {
            // 参数最末尾的结束符
            final String endString = "--" + mBoundary + "--\r\n";
            // 写入结束符
            for (ConcurrentHashMap.Entry<String, Object> entry : urlParams
                    .entrySet()) {
                if(entry.getValue().getClass().getSimpleName().equals("String")){
                    put(outstream,entry.getKey(),(String)entry.getValue());
                }else if(entry.getValue().getClass().getSimpleName().equals("File")){
                    put(outstream,entry.getKey(),(File)entry.getValue());
                }else{
                    put(outstream,entry.getKey(),(byte[]) entry.getValue());
                }

            }
            
            outstream.write(endString.getBytes());
            //
//            outstream.write(outstream.toByteArray());
        } else if (!StringUtils.isEmpty(getUrlParams())) {
            outstream.write(getUrlParams().substring(1).getBytes());
        }
    }

    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

//    public InputStream getContent() {
//        return new ByteArrayInputStream(outstream.toByteArray());
//    }

    public StringBuilder getUrlParams() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams
                .entrySet()) {
            if(entry.getValue().getClass().getSimpleName().equals("String")){
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

        }
        return result;
    }

    public String getJsonParams() {
        return jsonParams;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getContentEncoding() {
        return null;
    }
}
