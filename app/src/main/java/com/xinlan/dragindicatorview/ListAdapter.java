package com.xinlan.dragindicatorview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xinlan.dragindicator.DragIndicatorView;

import java.util.List;

/**
 * Created by panyi on 2016/4/6.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<Bean> mDataList;
    private Context mContext;

    public ListAdapter(Context context, List<Bean> data) {
        this.mContext = context;
        this.mDataList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataList == null)
            return;
        final Bean bean = mDataList.get(position);
        holder.mText.setText(bean.name);
        Picasso.with(mContext).load(bean.url).transform(new CircleTransform()).
                placeholder(R.drawable.ic_android_black_24dp).into(holder.mImage);
        if (bean.isIndicatorShow) {
            holder.mDragView.setText(String.valueOf(bean.num));
            holder.mDragView.setVisibility(View.VISIBLE);
        } else {
            holder.mDragView.setVisibility(View.GONE);
        }//end if
        holder.mDragView.setOnDismissAction(new DragIndicatorView.OnIndicatorDismiss() {
            @Override
            public void OnDismiss(DragIndicatorView view) {
                bean.isIndicatorShow = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public TextView mText;
        public DragIndicatorView mDragView;

        public ViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.image);
            mText = (TextView) v.findViewById(R.id.text);
            mDragView = (DragIndicatorView) v.findViewById(R.id.indicator);
        }
    }//end inner class
}//end class
