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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
@XStreamAlias("oschina")
public class LoginData implements Serializable {
    private static final long serialVersionUID = 1L;

    @XStreamAlias("result")
    private Result result;
    @XStreamAlias("user")
    private User user;
    @XStreamAlias("notice")
    private Notice notice;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @XStreamAlias("result")
    public class Result {

        @XStreamAlias("errorCode")
        private int errorCode;
        @XStreamAlias("errorMessage")
        private String errorMessage;

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @XStreamAlias("notice")
    public class Notice {
        @XStreamAlias("atmeCount")
        private int atmeCount;
        @XStreamAlias("msgCount")
        private int msgCount;
        @XStreamAlias("reviewCount")
        private int reviewCount;
        @XStreamAlias("newFansCount")
        private int newFansCount;
        @XStreamAlias("newLikeCount")
        private int newLikeCount;

        public int getAtmeCount() {
            return atmeCount;
        }

        public void setAtmeCount(int atmeCount) {
            this.atmeCount = atmeCount;
        }

        public int getMsgCount() {
            return msgCount;
        }

        public void setMsgCount(int msgCount) {
            this.msgCount = msgCount;
        }

        public int getReviewCount() {
            return reviewCount;
        }

        public void setReviewCount(int reviewCount) {
            this.reviewCount = reviewCount;
        }

        public int getNewFansCount() {
            return newFansCount;
        }

        public void setNewFansCount(int newFansCount) {
            this.newFansCount = newFansCount;
        }

        public int getNewLikeCount() {
            return newLikeCount;
        }

        public void setNewLikeCount(int newLikeCount) {
            this.newLikeCount = newLikeCount;
        }

    }

}
