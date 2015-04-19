package org.kymjs.kjframe.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import android.util.Log;

/**
 * Form表单形式的Http请求
 * 
 * @author kymjs
 * 
 */
public class FormRequest extends Request<byte[]> {

    private final HttpParams mParams;

    public FormRequest(String url, HttpCallBack callback) {
        this(HttpMethod.GET, url, null, callback);
    }

    public FormRequest(int httpMethod, String url, HttpParams params,
            HttpCallBack callback) {
        super(httpMethod, url, callback);
        if (params == null) {
            params = new HttpParams();
        }
        this.mParams = params;
    }

    @Override
    public String getBodyContentType() {
        return mParams.getContentType().getValue();
    }

    @Override
    public Map<String, String> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mParams.writeTo(bos);
        } catch (IOException e) {
            Log.e("kymjs", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        if (mCallback != null) {
            mCallback.onSuccess(response);
        }
    }
}