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
package org.kymjs.blog.utils;

import android.content.Context;
import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.blog.domain.Blog;
import org.kymjs.blog.domain.BlogAuthor;
import org.kymjs.blog.domain.EverydayMessage;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.SystemTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析工具类
 *
 * @author kymjs
 */
public class Parser {

    public static <T> T xmlToBean(Class<T> type, String xml) {
        T data = null;
        try {
            XStream xStream = new XStream(new DomDriver("UTF-8"));
            xStream.processAnnotations(type);
            data = (T) xStream.fromXML(xml);
        } catch (Exception e) {
            try {
                data = type.newInstance();
            } catch (Exception ee) {
            } finally {
                Log.e("kymjs", "xml解析异常");
            }
        }
        return data;
    }

    /**
     * 大神博客列表
     *
     * @param json
     * @return
     */
    public static List<BlogAuthor> getBlogAuthor(String json) {
        List<BlogAuthor> datas = new ArrayList<BlogAuthor>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                BlogAuthor data = new BlogAuthor();
                JSONObject obj = jsonArray.getJSONObject(i);
                data.setHead(obj.optString("image", ""));
                data.setId(obj.optInt("id", 863548));
                data.setName(obj.optString("name", "张涛"));
                data.setDescription(obj.optString("description", "暂无简介"));
                datas.add(data);
            }
        } catch (JSONException e) {
            Log.e("kymjs", "getBlogAuthor()解析异常");
        }

        return datas;
    }

    /**
     * 每日资讯数据
     *
     * @param json
     * @return
     */
    public static List<EverydayMessage> getEveryDayMsg(String json) {
        List<EverydayMessage> datas = new ArrayList<EverydayMessage>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                EverydayMessage data = new EverydayMessage();
                JSONObject obj = jsonArray.getJSONObject(i);
                data.setId(obj.optString("id", "-1"));
                data.setDescription(obj.optString("description", "暂无简介"));
                data.setTitle(obj.optString("title", "欢迎访问我的博客"));
                data.setImgUrl(obj.optString("imgurl",
                        "http://www.kymjs.com/assets/img/372102.jpg"));
                data.setUrl(obj.optString("url", "http://blog.kymjs.com/"));
                data.setHasItem(obj.optBoolean("hasitem", false));

                List<String> imgUrls = new ArrayList<String>(5);
                JSONArray imgArray = obj.optJSONArray("imageurllist");
                for (int j = 0; j < imgArray.length(); j++) {
                    imgUrls.add(imgArray.getString(j));
                }
                data.setImageUrlList(imgUrls);

                List<String> urlList = new ArrayList<String>(5);
                JSONArray urlArray = obj.optJSONArray("urllist");
                for (int j = 0; j < urlArray.length(); j++) {
                    urlList.add(urlArray.getString(j));
                }
                data.setUrlList(urlList);

                List<String> titleList = new ArrayList<String>(5);
                JSONArray titleArray = obj.optJSONArray("titlelist");
                for (int j = 0; j < titleArray.length(); j++) {
                    titleList.add(titleArray.getString(j));
                }
                data.setTitleList(titleList);

                data.setTime(obj.optString("time", "未知时间"));
                datas.add(data);
            }
        } catch (JSONException e) {
            Log.e("kymjs", "getEveryDayMsg()解析异常");
        }
        return datas;
    }

    /**
     * 首页博客列表
     *
     * @param json
     * @return
     */
    public static List<Blog> getBlogList(String json) {
        List<Blog> datas = new ArrayList<Blog>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                Blog data = new Blog();
                JSONObject obj = jsonArray.getJSONObject(i);
                data.setId(obj.optString("id", "-1"));
                data.setTitle(obj.optString("title", "无题"));
                data.setUrl(obj.optString("url", "http://blog.kymjs.com/"));
                data.setImageUrl(obj.optString("imageUrl", ""));
                data.setDate(obj.optString("date", "未知时间"));
                data.setIsRecommend(obj.optInt("isRecommend", 0));
                data.setAuthor(obj.optString("author", "佚名"));
                data.setIsAuthor(obj.optInt("isAuthor", 0));
                data.setDescription(obj.optString("description", "暂无简介"));
                datas.add(data);
            }
        } catch (JSONException e) {
            Log.e("kymjs", "getBlogList()解析异常");
        }
        return datas;
    }

    /**
     * 检测更新
     *
     * @param json
     */
    public static String checkVersion(Context cxt, String json) {
        String url = "";
        try {
            JSONObject obj = new JSONObject(json);
            int serverVersion = obj.optInt("version", 0);
            int currentVersion = SystemTool.getAppVersionCode(cxt);
            KJLoger.debug("当前版本：" + currentVersion + "最新版本：" + serverVersion);
            if (serverVersion > currentVersion) {
                url = obj.optString("url");
            }
        } catch (JSONException e) {
            Log.e("kymjs", "getBlogList()解析异常");
        }
        return url;
    }
}
