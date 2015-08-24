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
 * @author kymjs (http://www.kymjs.com/)
 * 
 * @date 2014年9月28日
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class OSCBlogList implements Serializable {
    @XStreamAlias("id")
    private int id;

    @XStreamAlias("pagesize")
    private int pagesize;

    @XStreamAlias("blogs")
    private List<OSCBlog> bloglist = new ArrayList<OSCBlog>();

    @XStreamAlias("blogsCount")
    private int blogsCount;

    public int getPageSize() {
        return pagesize;
    }

    public void setPageSize(int pageSize) {
        this.pagesize = pageSize;
    }

    public List<OSCBlog> getBloglist() {
        return bloglist;
    }

    public void setBloglist(List<OSCBlog> bloglist) {
        this.bloglist = bloglist;
    }

    public List<OSCBlog> getList() {
        return bloglist;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getBlogsCount() {
        return blogsCount;
    }

    public void setBlogsCount(int blogsCount) {
        this.blogsCount = blogsCount;
    }
}
