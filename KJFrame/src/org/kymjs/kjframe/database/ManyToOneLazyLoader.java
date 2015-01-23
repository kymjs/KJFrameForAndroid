/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
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
package org.kymjs.kjframe.database;

import org.kymjs.kjframe.KJDB;

/**
 * 多对一延迟加载类<br>
 * <b>创建时间</b> 2014-8-15
 * 
 * @param <O>
 *            宿主实体的class
 * @param <M>
 *            多放实体class
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public class ManyToOneLazyLoader<M, O> {
    M manyEntity;
    Class<M> manyClazz;
    Class<O> oneClazz;
    KJDB db;

    public ManyToOneLazyLoader(M manyEntity, Class<M> manyClazz,
            Class<O> oneClazz, KJDB db) {
        this.manyEntity = manyEntity;
        this.manyClazz = manyClazz;
        this.oneClazz = oneClazz;
        this.db = db;
    }

    O oneEntity;
    boolean hasLoaded = false;

    /**
     * 如果数据未加载，则调用loadManyToOne填充数据
     * 
     * @return
     */
    public O get() {
        if (oneEntity == null && !hasLoaded) {
            this.db.loadManyToOne(this.manyEntity, this.manyClazz,
                    this.oneClazz);
            hasLoaded = true;
        }
        return oneEntity;
    }

    public void set(O value) {
        oneEntity = value;
    }

}
