package com.xuan.dropmenu;

import android.view.View;
import android.view.ViewGroup;

/**
 * com.xuan.dropmenu
 *
 * @author by xuan on 2018/6/2
 * @version [版本号, 2018/6/2]
 * @update by xuan on 2018/6/2
 * @descript
 */
public abstract class BaseDropMenuAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public boolean hasStableIds() {
        return false;
    }

    public void registerDataSetObserver(DropMenuObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DropMenuObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void closeMenu(){
        mDataSetObservable.onCloseMenu();
    }

    //获取有几个tab
    public abstract int getCount();

    //获取当前点击的tab
    public abstract View getTabView(int position, ViewGroup parent);

    //获取点击tab展开的菜单内容的布局
    public abstract View getContentMenuView(int position, ViewGroup parent);

    public abstract void defaultTabView(ViewGroup parent,View tabView);

    public abstract void selectTabView(ViewGroup parent, View tabView);

}
