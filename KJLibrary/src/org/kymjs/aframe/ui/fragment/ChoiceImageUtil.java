/*
 * Copyright (c) 2014, KJFrameForAndroid 张涛 (kymjs123@gmail.com).
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
package org.kymjs.aframe.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 图片筛选操作工具类
 * 
 * @注 私有工具类，仅本包可用
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-6-12
 */
final class ChoiceImageUtil {
    /**
     * 获取全部图片地址
     */
    public static List<String> listAlldir(Context cxt) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri uri = intent.getData();
        List<String> list = new ArrayList<String>();
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = cxt.getContentResolver().query(uri, proj, null, null,
                null);
        while (cursor.moveToNext()) {
            String path = cursor.getString(0);
            list.add(new File(path).getAbsolutePath());
        }
        return list;
    }

    /**
     * 获取到文件夹数据集合
     * 
     * @注 感觉这种方法效率不高，应该优化
     */
    public static List<FolderBean> LocalImgFileList(Context cxt) {
        // 文件夹数据集合
        List<FolderBean> datas = new ArrayList<FolderBean>();
        // 全部图片的地址
        List<String> allImgPath = listAlldir(cxt);
        // 文件夹名集合（去掉重复的文件夹名，TreeSet不保存重复值）
        Set<String> folders = new TreeSet<String>();
        if (allImgPath != null) {
            // 遍历全部文件，找出文件夹，并自动去重
            for (int i = 0; i < allImgPath.size(); i++) {
                folders.add(getFolderName(allImgPath.get(i)));
            }
            // 将文件夹，插入数据集合
            Iterator<String> iterator = folders.iterator();
            while (iterator.hasNext()) {
                FolderBean folderBean = new FolderBean();
                folderBean.setFolderName(iterator.next());
                datas.add(folderBean);
            }
            // 将全部图片插入数据集合相应文件夹中
            for (FolderBean bean : datas) {
                boolean imgListIsNull = (bean.getFilePath() == null); // 用于减少执行次数
                for (String imgPath : allImgPath) {
                    if (bean.getFolderName().equals(getFolderName(imgPath))) {
                        if (imgListIsNull) {
                            bean.setFilePath(new ArrayList<String>());
                            imgListIsNull = false;
                        }
                        bean.getFilePath().add(imgPath);
                    }
                }

            }
        }
        return datas;
    }

    /**
     * 返回参数文件所在的文件夹名
     */
    public static String getFolderName(String path) {
        String filename[] = path.split("/");
        if (filename != null) {
            return filename[filename.length - 2];
        }
        return null;
    }
}
