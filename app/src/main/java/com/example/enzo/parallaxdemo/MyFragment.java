package com.example.enzo.parallaxdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by enzo on 9/6/2017.
 */

public class MyFragment extends Fragment{
    private static final String ARGUMENT_ID="package com.example.enzo.parallaxdemo.MyFragment";//key for fragment argument
    private int mId;//unique id representing the tab number
    private ArrayList<String> mList ;//list for the recyclerviwe
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private Handler mResponseHandler;//to perform ui update on main thread
    public float mRatio=0.0f;//holds the opacity of appbar for this particular fragment
    private RecyclerViewListener mScrollListener;//listener that tracks the behaviour of recyclerview scrolls
    LinearLayoutManager mLinearLayout;
    public MyFragment(){
        //Log.d("SCROLL","FRAGMENT constructor");
    }

    //factory method to return an instance of MyFragment with argument set to i
    public static MyFragment newInstance(int i){
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_ID,i);
        MyFragment f =new MyFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        Bundle args= getArguments();
        if(args!=null){
            mId=args.getInt(ARGUMENT_ID);
            //Log.d("SCROLL"," OnCreateView ID = "+mId);
        }
        //Log.d("SCROLL","For id "+mId+"savedInstance state is "+savedInstanceState);
        View v = inflater.inflate(R.layout.list_fragment,container,false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        mLinearLayout = new LinearLayoutManager(getActivity());
        mLinearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayout);

        //init the array list for recycler view
        mList = new ArrayList<>();
        for(int i=0;i<100;i++){
            Log.d("SCROLL","i was executed");
            mList.add( "Text View "+i );
        }

        mAdapter = new MyAdapter(mList);
        //setting the handler
        mResponseHandler = new Handler(Looper.getMainLooper());
        mScrollListener = new RecyclerViewListener(mLinearLayout) {
            @Override
            public void onAlphaChanged(final float alpha) {

                MainActivity a = (MainActivity)getActivity();
                int index = a.getViewPager().getCurrentItem();
                if(index != mId){
                    //check if the current tab visible on screen is euqla to the mId
                    //because the listener is called for next cached tab in viewpager as well .
                    return;
                }
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRatio=alpha;
                        Log.d("SCROLL","Called for fragment "+mId+"\n alpha = "+alpha);
                        Toolbar appbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                        Drawable c = appbar.getBackground();
                        c.setAlpha(Math.round(mRatio * 255));
                        appbar.setBackground(c);
                        }
                    });
                }
            };
            mRecyclerView.addOnScrollListener(mScrollListener);
        return v;
    }

    //Rest of the code
    @Override
    public void onResume(){
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
        //Log.d("SCROLL","++++++++++++++++++++onResume was called++++++++++++++++++++++"+getArguments().getInt(ARGUMENT_ID));
    }
    private class MyViewHolder extends RecyclerView.ViewHolder {
        private String text;
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);

        }
        public void bindString(String s) {
            text = s;
            textView.setText(text);
        }
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        private ArrayList<String> mList;
        MyAdapter(ArrayList<String> list){
            mList = list;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.row,parent,false);
            MyViewHolder holder= new MyViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.bindString(mList.get(position));
        }
        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
