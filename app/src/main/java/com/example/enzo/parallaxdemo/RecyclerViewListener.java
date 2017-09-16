package com.example.enzo.parallaxdemo;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by enzo on 9/6/2017.
 */

public abstract class RecyclerViewListener extends RecyclerView.OnScrollListener{
    private static final int SCROLL_DOWN = 1;//constant to represent that a scroll down occurred
    private static final int SCROLL_UP= 2;//constant to represent that a scroll up occurred

    private double mRatio=1.0;
    private int yDeviation=0;

    private Rect mPreviousRect=null;//original size of imageview of first recyclerview element
    private Rect mCurrentRect = new Rect();//current size of the aformentioned imageview
    private View mView;//temp view to hold imageview
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 3;

    RecyclerView.LayoutManager mLayoutManager;

    public RecyclerViewListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public RecyclerViewListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public RecyclerViewListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    /*
    where magic happens
     */
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        //Log.d("SCROLL","dx = "+dx+" dy = ="+dy);

        int firstVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            firstVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }
        mView = mLayoutManager.findViewByPosition(0);
        if(mView!= null){
            mView=mView.findViewById(R.id.image);
        }
        //mPrevious is null
        if(mPreviousRect == null){
            mPreviousRect = new Rect();
            mView.getGlobalVisibleRect(mPreviousRect);
            //Log.d("SCROLL","**************I was called************");
        }
        //set it 1 by default
        double ratio=1.0;
        if(((LinearLayoutManager)mLayoutManager).findFirstCompletelyVisibleItemPosition() == 0 ){
            //Log.d("SCROLL","**************completely visible************");
            ratio = 0;
            //if completely visible set it to 0
        }
        else if(firstVisibleItemPosition == 0){
            mView.getGlobalVisibleRect(mCurrentRect);
            //Log.d("SCROLL","(float)mCurrentRect.height() ="+(float)mCurrentRect.height());
            //Log.d("SCROLL","(float)mPreviousRect.height() ="+(float)mPreviousRect.height());
            if ( (float)mCurrentRect.height()<(float)mPreviousRect.height() ){
//                Log.d("SCROLL","(float)mCurrentRect.height()<(float)mPreviousRect.height() = "+
//                        ((float)mCurrentRect.height()<(float)mPreviousRect.height()));
                ratio = 1.0 - ((float)mCurrentRect.height()/(float)mPreviousRect.height());
                /*
                if first imageview isnt completely visible ,
                find the ratio of currently visible height and total height and subtract with 1.0 to
                get the opacity of appbar
                 */
            }

        }
        int x = (int) (mRatio*10);
        int y = (int)(ratio*10);
        //converting to int because float comparision is not error free
        if(x != y){
            mRatio = ratio;
            onAlphaChanged((float)mRatio);
        }
        //Log.d("SCROLL","ratio ="+mRatio);

    }



    // Defines the process for actually changing opacity
    public abstract void onAlphaChanged(float alpha);
}
