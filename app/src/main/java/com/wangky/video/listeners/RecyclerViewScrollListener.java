package com.wangky.video.listeners;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //是否向上滑动
    private boolean isSildingUpward = false;


    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if(newState == RecyclerView.SCROLL_STATE_IDLE){

            int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

            int itemCount = layoutManager.getItemCount();

            if(lastVisible == itemCount -1 && isSildingUpward){ //最后一项可见，且往上滑动

                loadMore();//加载更多

            }

        }

    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dy > 0 向上 < 0 向下滑动
        isSildingUpward = dy > 0;
    }





    public abstract void  loadMore();


}
