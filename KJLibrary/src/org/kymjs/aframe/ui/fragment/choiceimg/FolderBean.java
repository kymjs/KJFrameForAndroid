package org.kymjs.aframe.ui.fragment.choiceimg;

import java.util.List;

/**
 * 一个包含了图片的文件夹
 * 
 * @注 私有数据集，仅本包可用
 * @author kymjs(kymjs123@gmail.com)
 * @version 1.0
 * @created 2014-6-12
 */
class FolderBean {
    private String folderName;
    private List<String> filePath;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<String> getFilePath() {
        return filePath;
    }

    public void setFilePath(List<String> filePath) {
        this.filePath = filePath;
    }

}
