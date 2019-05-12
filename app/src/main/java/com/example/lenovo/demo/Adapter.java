package com.example.lenovo.demo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final Object MainActivity = com.example.lenovo.demo.MainActivity.class;
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    private List<Data> dataList;
    private Context context;
    private OnClickListener mlistener;
    private Loader mloader;

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public Adapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        mloader = new Loader(context);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout1;
        ImageView imageView;
        TextView textView1;
        TextView textView2;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout1 = itemView.findViewById(R.id.line1);
            imageView = itemView.findViewById(R.id.image);
            textView1 = itemView.findViewById(R.id.text1);
            textView2 = itemView.findViewById(R.id.text2);
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = itemView.findViewById(R.id.progress);
            tvLoading = itemView.findViewById(R.id.text3);
            llEnd = itemView.findViewById(R.id.linear);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyleitemlayout, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        } else if (i == TYPE_FOOTER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footlayout, viewGroup, false);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;
            if (mlistener != null) {
                viewHolder1.linearLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mlistener.onClick(viewHolder.getAdapterPosition());
                    }
                });
                viewHolder1.linearLayout1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mlistener.onLongClick(viewHolder.getAdapterPosition());
                        return false;
                    }
                });
            }
            Data data = dataList.get(viewHolder.getAdapterPosition());
            String picURL = data.getCover();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mloader.bindBitmap(picURL, viewHolder1.imageView);
            }
            viewHolder1.textView1.setText(data.getSite().getCat_cn());
            viewHolder1.textView2.setText(data.getSite().getDesc());

        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }


        }


    }

//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
//
//    }


    @Override
    public int getItemCount() {

        return dataList.size()+1;
    }

    void SetOnItemCickListener(OnClickListener listener) {
        this.mlistener = listener;

    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
    }
}


