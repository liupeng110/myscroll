package com.andlp.myscroll;

/**
 * 717219917@qq.com      2018/1/11  14:46
 */

public class info {
    //整个架构布局
    //1.最外层为原生ViewPager
    //2.为viewpager的单个ListFragment
    //Listfragment最外层为自定义的ScrollView  不拦截水平滑动事件
    //ListFragment 内容部分:
    //上半部分为  自定义ViewPager (重写touch事件  设置 页面在最后一页和第0页的时候 ,由上层 拦截触摸事件)
    //下半部分为   原生listview (重写onmeasue函数,解决listview显示不完全)
    //3.上半部分viewpager的单个fragment为ImageFragment
    //imagefragment只有一个imageview

    //原理
    //





}
