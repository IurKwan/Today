package com.krbb.today;

/**
 * Created by rui on 2018/2/5 21:25
 * Email：guanzhirui@outlook.com
 */


public interface OnDragVHListener {
    /**
     * Item被选中时触发
     */
    void onItemSelected();


    /**
     * Item在拖拽结束/滑动结束后触发
     */
    void onItemFinish();
}
