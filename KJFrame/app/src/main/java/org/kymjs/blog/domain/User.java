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

import org.kymjs.kjframe.database.annotate.Id;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("user")
public class User {

    private String account;
    private String pwd;
    private String cookie;

    @Id
    private int id; // 数据库id
    @XStreamAlias("uid")
    private int uid; // OSC用户ID
    @XStreamAlias("name")
    private String name;
    @XStreamAlias("portrait")
    private String portrait;

    @XStreamAlias("location")
    private String location;
    @XStreamAlias("followers")
    private String followers;
    @XStreamAlias("fans")
    private String fans;
    @XStreamAlias("score")
    private String score;
    @XStreamAlias("favoritecount")
    private String favoritecount;
    @XStreamAlias("gender")
    private String gender;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getFavoritecount() {
        return favoritecount;
    }

    public void setFavoritecount(String favoritecount) {
        this.favoritecount = favoritecount;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
