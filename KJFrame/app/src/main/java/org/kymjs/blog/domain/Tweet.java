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
package org.kymjs.blog.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 动弹实体类
 * 
 * @author kymjs(http://www.kymjs.com/)
 * @version 1.1 添加语音动弹功能
 */
@SuppressWarnings("serial")
@XStreamAlias("tweet")
public class Tweet implements Serializable, Comparable<Tweet> {

    @XStreamAlias("id")
    private int id;
    @XStreamAlias("portrait")
    private String portrait;
    @XStreamAlias("author")
    private String author;
    @XStreamAlias("authorid")
    private int authorid;
    @XStreamAlias("body")
    private String body;
    @XStreamAlias("appclient")
    private int appclient;
    @XStreamAlias("commentCount")
    private String commentCount;
    @XStreamAlias("pubDate")
    private String pubDate;
    @XStreamAlias("imgSmall")
    private String imgSmall;
    @XStreamAlias("imgBig")
    private String imgBig;
    @XStreamAlias("attach")
    private String attach;
    @XStreamAlias("likeCount")
    private String likeCount;
    @XStreamAlias("isLike")
    private String isLike;
    @XStreamAlias("likeList")
    private List<User> likeList = new ArrayList<Tweet.User>();

    private String imageFilePath;
    private String audioPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }

    public List<User> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<User> likeList) {
        this.likeList = likeList;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorid() {
        return authorid;
    }

    public void setAuthorid(int authorid) {
        this.authorid = authorid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getAppclient() {
        return appclient;
    }

    public void setAppclient(int appclient) {
        this.appclient = appclient;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getImgSmall() {
        return imgSmall;
    }

    public void setImgSmall(String imgSmall) {
        this.imgSmall = imgSmall;
    }

    public String getImgBig() {
        return imgBig;
    }

    public void setImgBig(String imgBig) {
        this.imgBig = imgBig;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    @Override
    public int compareTo(Tweet another) {
        return another.id - this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tweet) {
            return ((Tweet) o).id == this.id;
        } else {
            return false;
        }
    }

    @XStreamAlias("user")
    public class User {
        @XStreamAlias("name")
        String name;
        @XStreamAlias("uid")
        String uid;
        @XStreamAlias("portrait")
        String portrait;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

    }
}
