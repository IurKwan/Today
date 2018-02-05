package com.krbb.today;

/**
 * Created by rui on 2018/2/5 21:20
 * Email：guanzhirui@outlook.com
 */


public interface OnChannelListener {
    void onItemMove(int starPos, int endPos);
    void onMoveToMyChannel(int starPos, int endPos);
    void onMoveToOtherChannel(int starPos, int endPos);
    void onFinish(String selectedChannelName);
    void onEditing(boolean isEdit);
}
