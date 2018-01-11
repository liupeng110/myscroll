package com.andlp.myscroll;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.andlp.myscroll.adapter.MainViewPagerAdapter;
import com.andlp.myscroll.fragment.ListFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    public static  final String TAG="xujun";

    private final String[] mTitles=new String[]{
            "首页","第二","相册","是我的"
    };
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (findFragment(HomeFragment.class) == null) {
//            loadRootFragment(R.id.fl_container, HomeFragment.newInstance());  //load root Fragment
//        }
        initView();
        initdata();

    }

    private void initdata() {
        mFragments = new ArrayList<>();
        for(int i=0;i<mTitles.length;i++){
            ListFragment listFragment = ListFragment.newInstance(mTitles[i]);
            mFragments.add(listFragment);
        }

        MainViewPagerAdapter baseViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(baseViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ((ListFragment)mFragments.get(position)).onSelected();
                Log.i(TAG, "onPageSelected: position=" +position);
            }
        });
    }

    private void initView() {
        mViewPager=(ViewPager)findViewById(R.id.viewPager);
        mTabLayout=(TabLayout)findViewById(R.id.tabs);
    }
}

