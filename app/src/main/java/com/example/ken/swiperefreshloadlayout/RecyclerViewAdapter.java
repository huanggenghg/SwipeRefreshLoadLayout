package com.example.ken.swiperefreshloadlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ken on 17-3-11.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private int mSwitch = 0;

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<Integer> mData;

    public RecyclerViewAdapter(Context context, ArrayList<Integer> data) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mData = data;
    }

    public void setFooterOn() {
        mSwitch = 1;
        notifyDataSetChanged();
    }

    public void setFooterOff() {
        mSwitch = 0;
        notifyDataSetChanged();
    }

    public void loadMore(int addNum) {
        int fromIndex = mData.size();
        for (int i = fromIndex; i < fromIndex + addNum; i++)
            mData.add(i);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && mSwitch == 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (viewType == TYPE_ITEM) {
            itemView = mLayoutInflater.inflate(R.layout.item_ls, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(itemView);
            return viewHolder;
        } else if (viewType == TYPE_FOOTER) {
            itemView = mLayoutInflater.inflate(R.layout.view_footer, parent, false);
            FooterViewHolder viewHolder = new FooterViewHolder(itemView);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            String text = String.format("我是%s号！！！", mData.get(position).toString());
            ((ItemViewHolder) holder).itemText.setText(text);
        } else if (holder instanceof FooterViewHolder) {
            // you can do somethings here in your footer
        }
    }

    @Override
    public int getItemCount() {
        // 若footer显示则应为item count 增加一项
        return mData.size() + mSwitch;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView itemText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.text);

        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public TextView loadingText;

        public FooterViewHolder(View itemView) {
            super(itemView);
            loadingText = (TextView) itemView.findViewById(R.id.loading_text);
        }
    }
}
