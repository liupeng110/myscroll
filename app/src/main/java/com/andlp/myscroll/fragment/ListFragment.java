package com.andlp.myscroll.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andlp.myscroll.R;
import com.andlp.myscroll.adapter.FragmentsAdapter;
import com.andlp.myscroll.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * @ explain:
 * @ author：xujun on 2016/10/25 23:27
 * @ email：gdutxiaoxu@163.com
 */
public class ListFragment extends BaseFragment {

    static final String key = "key";
    public static final String TAG = "xujun";

    private String mTitle = "";

    ViewPager mViewPager;
    TextView mTextView;

    ListView mListview;
    private List<Fragment> mFragments;
    private FragmentsAdapter mFragmentsAdapter;
    private ArrayList<String> mList;
    private ListViewAdapter mListViewAdapter;

    ScrollView mNoHorizontalScrollView;

    private int mSize = 4;

    private int mScrollY;
    private int mScrollX;

    public static ListFragment newInstance(String title) {

        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(key, title);
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    protected void initView(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mTextView = (TextView) view.findViewById(R.id.tv_page);
        mListview = (ListView) view.findViewById(R.id.listview);//lisetview
        mNoHorizontalScrollView = (ScrollView) view.findViewById(R.id.NoHorizontalScrollView);
    }

    @Override protected int getLayoutId() { return R.layout.fragment_list;  }

    @Override public void fetchData() {
        Log.i(TAG, "fetchData: mTitle =" + mTitle);
        int scrollY = mNoHorizontalScrollView.getScrollY();
    }

    //界面切换时调用 true可见 ,false 不可见
    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Logger.i( "setUserVisibleHint: mTitle=" + mTitle + "  " + " isVisibleToUser=" +  isVisibleToUser);
        if (isVisibleToUser) {//表示界面可见
            if (mNoHorizontalScrollView != null) {// 之所以判断是否为空，
                Log.i(TAG, "setUserVisibleHint: mTitle=" + mTitle + "  " + " isVisibleToUser=" +
                        isVisibleToUser + "mScrollY=" + mScrollY);
                //   mNoHorizontalScrollView.setDescendantFocusability(ViewGroup .FOCUS_BLOCK_DESCENDANTS);
            }

        } else {// 表示界面不可见
                     if (mNoHorizontalScrollView != null) {
                        mScrollX = mNoHorizontalScrollView.getScrollX();
                        mScrollY = mNoHorizontalScrollView.getScrollY();
                       Log.i(TAG, "setUserVisibleHint: mTitle=" + mTitle + "  " + " isVisibleToUser=" +
                        isVisibleToUser + "mScrollY=" + mScrollY);
                      }
                }

    }

    @Override protected void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTextView.setText(String.format("%d/" + mSize, position + 1));
            }
        });
    }
    @SuppressLint("DefaultLocale")
    @Override protected void initData() {
        mTextView.setText(String.format("%d/" + mSize, 1));
        Bundle arguments = getArguments();
        String title = "";
        if (arguments != null) {  title = arguments.getString(key);  mTitle = title; }

        //listview的数据
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String s = String.format("我是第%d个测试Item" + title, i);
            mList.add(s);
        }

        // listview的适配器
        mListViewAdapter = new ListViewAdapter(getActivity(), mList);
        mListview.setAdapter(mListViewAdapter);
        mListview.setDividerHeight(5);                      //设置间隔
        mListViewAdapter.notifyDataSetChanged();


        //创建viewpager的数据
        mFragments = new ArrayList<>();
        for (int i = 0; i < mSize; i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(R.drawable.huoying);
            mFragments.add(imageFragment);
        }
        //创建viewpager的适配器
        mFragmentsAdapter = new  FragmentsAdapter(getChildFragmentManager() , mFragments);
        mViewPager.setAdapter(mFragmentsAdapter);
        mNoHorizontalScrollView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//确保mNoHorizontalScrollView他的子孙不能获得焦点

//        mViewPager.setFocusable(true);
//        mViewPager.setFocusableInTouchMode(true);
//        mViewPager.requestFocus(); //viewpager调试用参数

    }

    public void onSelected() {  }


}
