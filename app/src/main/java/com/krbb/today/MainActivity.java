package com.krbb.today;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnChannelListener{

    private Button mButton;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private BottomSheetBehavior behavior;

    private List<Channel> mDatas = new ArrayList<>();
    List<Channel> mSelectedDatas;
    List<Channel> mUnSelectedDatas;
    private boolean isUpdate = false;
    private String firstAddChannelName = "";

    NewAdapter adapter;
    private OnChannelListener onChannelListener;

    /**
     * 是否正在编辑
     */
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.button);
        mImageView = findViewById(R.id.close);
        mRecyclerView = findViewById(R.id.recy);

        View bottomSheet = findViewById(R.id.bottomsheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setSkipCollapsed(true);
        //默认设置为隐藏
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        initData();

        //添加已选栏目
        Channel channel = new Channel();
        channel.setItemtype(Channel.TYPE_MY);
        channel.setChannelName("已选栏目");
        mDatas.add(channel);

        //获取当前频道顺序
        setDataType(mSelectedDatas, Channel.TYPE_MY_CHANNEL);
        setDataType(mUnSelectedDatas, Channel.TYPE_OTHER_CHANNEL);
        mDatas.addAll(mSelectedDatas);

        //添加未选栏目
        Channel morechannel = new Channel();
        morechannel.setItemtype(Channel.TYPE_OTHER);
        morechannel.setChannelName("选择栏目");
        mDatas.add(morechannel);
        mDatas.addAll(mUnSelectedDatas);

        ItemDragHelperCall callback = new ItemDragHelperCall(this);
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);

        adapter = new NewAdapter(mDatas, helper);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = adapter.getItemViewType(position);
                return itemViewType == Channel.TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL ? 1 : 4;
            }
        });
        adapter.OnChannelListener(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
    }

    private void initData(){
        mSelectedDatas = new ArrayList<>();
        Channel top = new Channel();
        top.setChannelName("推荐");
        top.setChannelType(1);
        mSelectedDatas.add(top);
        for (int i = 0; i < 5; i++) {
            Channel entity = new Channel();
            entity.setChannelName("已选" + i);
            entity.setChannelType(0);
            mSelectedDatas.add(entity);
        }
        mUnSelectedDatas = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Channel entity = new Channel();
            entity.setChannelName("未选" + i);
            entity.setChannelType(0);
            mUnSelectedDatas.add(entity);
        }
    }

    private void setDataType(List<Channel> datas, int type) {
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setItemtype(type);
        }
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        if (starPos < 0 || endPos < 0){
            return;
        }
        if (mDatas.get(endPos).getChannelName().equals("推荐")){
            return;
        }
        //我的频道之间移动
        if (onChannelListener != null){
            //去除标题所占的一个index
            onChannelListener.onItemMove(starPos - 1, endPos - 1);
        }
        onMove(starPos, endPos, false);
    }

    private void onMove(int starPos, int endPos, boolean isAdd) {
        isUpdate = true;
        Channel startChannel = mDatas.get(starPos);
        //先删除之前的位置
        mDatas.remove(starPos);
        //添加到现在的位置
        mDatas.add(endPos, startChannel);
        adapter.notifyItemMoved(starPos, endPos);
        if (isAdd) {
            if (TextUtils.isEmpty(firstAddChannelName)) {
                firstAddChannelName = startChannel.getChannelName();
            }
        } else {
            if (startChannel.getChannelName().equals(firstAddChannelName)) {
                firstAddChannelName = "";
            }
        }
    }

    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        onMove(starPos, endPos, true);
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        onMove(starPos, endPos, false);
    }

    @Override
    public void onFinish(String selectedChannelName) {

    }

    @Override
    public void onEditing(boolean isEdit) {
        if (isEdit){
            if (behavior.getState()  == BottomSheetBehavior.STATE_EXPANDED){
                if (behavior instanceof LockableBottomSheetBehavior) {
                    ((LockableBottomSheetBehavior) behavior).setLocked(true);
                }
            }
        }else {
            ((LockableBottomSheetBehavior) behavior).setLocked(false);
        }
    }
}
