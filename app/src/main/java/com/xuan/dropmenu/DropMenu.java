package com.xuan.dropmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * com.xuan.dropmenu
 *
 * @author by xuan on 2018/6/2
 * @version [版本号, 2018/6/2]
 * @update by xuan on 2018/6/2
 * @descript
 */
public class DropMenu extends LinearLayout {
    private int match= LinearLayout.LayoutParams.MATCH_PARENT;
    private int wrap= LinearLayout.LayoutParams.WRAP_CONTENT;

    private LinearLayout tabLayout;
    private int tabLayoutHeight=120;
    private int tabLayoutColor= ContextCompat.getColor(getContext(),R.color.gray);
    private int currentPosition=-1;

    private FrameLayout contentLayout;

    private View shadowView;
    private int shadowColor= ContextCompat.getColor(getContext(),R.color.shadowColor);
    private float shadowViewAlpha=0.7f;

    private FrameLayout menuLayout;
    private int menuColor= ContextCompat.getColor(getContext(),R.color.white);
    private int menuLayoutHeight;

    private BaseDropMenuAdapter mAdapter;
    private int duration=500;

    private boolean isPerformAnim=false;

    public DropMenu(Context context) {
        this(context,null);
    }

    public DropMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DropMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("TAG","onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 拿到整个高度
        int height=MeasureSpec.getSize(heightMeasureSpec);
        // 修改tab布局高度
        tabLayoutHeight= (int) (height*8f/100);
        tabLayout.getLayoutParams().height=tabLayoutHeight;
        // 修改menu布局高度
        menuLayoutHeight= (int) (height*75f/100);
        menuLayout.getLayoutParams().height=menuLayoutHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i("TAG","onLayout changed"+changed);
        super.onLayout(changed, l, t, r, b);


        //默认 使整个 menuLayout 内容+阴影布局上滑隐藏
        //只有当 changed=true才进行平移隐藏 也就是初始化摆放位置的时候
        //当 changed=false 也就是设置visible or gone的时候 不做平移
        if(changed)
        menuLayout.setTranslationY(-menuLayoutHeight);

    }

    private void initLayout() {
        this.setOrientation(VERTICAL);

        //创建头部tab
        tabLayout=new LinearLayout(getContext());
        LinearLayout.LayoutParams tabParams=new LinearLayout.LayoutParams(
                match,
                wrap);
        tabLayout.setLayoutParams(tabParams);
        tabLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabLayout.setBackgroundColor(tabLayoutColor);

        addView(tabLayout);

        //创建 内容布局+阴影布局
        contentLayout=new FrameLayout(getContext());
        LinearLayout.LayoutParams contentParams=new LinearLayout.LayoutParams(
                match,0);
        contentParams.weight=1;
        contentLayout.setLayoutParams(contentParams);
        addView(contentLayout);

        //阴影
        shadowView=new View(getContext());
        shadowView.setBackgroundColor(shadowColor);
        shadowView.setAlpha(0f);
        shadowView.setVisibility(GONE);
        shadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        contentLayout.addView(shadowView);

        //实际下拉菜单内容布局
        menuLayout=new FrameLayout(getContext());
        menuLayout.setLayoutParams(
                new ViewGroup.LayoutParams(match,
                        wrap));
        menuLayout.setBackgroundColor(menuColor);

        contentLayout.addView(menuLayout);


    }


    public void setMenuLayoutHeight(int menuLayoutHeight){
        this.menuLayoutHeight=menuLayoutHeight;
    }

    private AdapterDataSetObserver observer;

    /**
     * 具体的观察者类对象
     */
    private class AdapterDataSetObserver extends DropMenuObserver{
        @Override
        public void onCloseMenu() {
            //如果有注册观察者 就收到通知
            DropMenu.this.closeMenu();
        }
    }

    public void setAdapter(BaseDropMenuAdapter baseDropMenuAdapter){
        if(mAdapter !=null && observer!=null){
            baseDropMenuAdapter.unregisterDataSetObserver(observer);
        }
        mAdapter =baseDropMenuAdapter;

        observer=new AdapterDataSetObserver();
        mAdapter.registerDataSetObserver(observer);



        //创建布局
        int count=baseDropMenuAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View tabView=baseDropMenuAdapter.getTabView(i,tabLayout);
            if(tabView!=null){
                LinearLayout.LayoutParams tabViewParams=
                        (LinearLayout.LayoutParams) tabView.getLayoutParams();

                tabViewParams.weight=1;
                tabViewParams.gravity= Gravity.CENTER;
                tabView.setLayoutParams(tabViewParams);

                setTabViewOnClickListen(i,tabView);

                tabLayout.addView(tabView);
            }

            View menuView=baseDropMenuAdapter.getContentMenuView(i,menuLayout);
            if(menuView!=null){

                menuView.setVisibility(GONE);
                menuLayout.addView(menuView);
            }
        }
        //创建布局

    }

    private void setTabViewOnClickListen(final int position, final View tabView) {
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPosition==-1){
                    //打开
                    openMenu(position,tabView);
                }else{
                    if(currentPosition==position){
                        //点击当前的tab才关闭菜单
                        closeMenu();
                    }else{
                        //点击其他的tab隐藏当前菜单显示点击的菜单
                        //此处会导致onMeasure调用多次
                        //在onMeasure进行menuLayout.setTranslationY(-menuLayoutHeight);
                        //从而导致重新打开的菜单也平移不见
                        menuLayout.getChildAt(currentPosition).setVisibility(GONE);
                        menuLayout.getChildAt(position).setVisibility(VISIBLE);
                        //同时改变tab选中状态
                        mAdapter.defaultTabView(tabLayout,tabLayout.getChildAt(currentPosition));
                        mAdapter.selectTabView(tabLayout,tabView);
                        //重新记录
                        currentPosition=position;
                    }
                }
            }
        });
    }

    private void openMenu(final int position, final View tabView) {
        if(isPerformAnim){
            return;
        }

        //使整个 内容+阴影布局下滑出来
        ObjectAnimator translationYAnim=ObjectAnimator.ofFloat(menuLayout,
                "TranslationY",-menuLayoutHeight,0);

        //使阴影渐显
        shadowView.setVisibility(VISIBLE);
        ObjectAnimator alphaAnim=ObjectAnimator.ofFloat(shadowView,
                "alpha",0,shadowViewAlpha);

        AnimatorSet animSet=new AnimatorSet();
        animSet.playTogether(translationYAnim,alphaAnim);
        animSet.setDuration(duration);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPerformAnim=false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isPerformAnim=true;
                //需要在动画开始之前就修改 防止连续点击导致关闭取到-1
                currentPosition=position;

                //选中的tab样式
                mAdapter.selectTabView(tabLayout,tabView);
            }
        });

        animSet.start();

        //打开当前menuView
        menuLayout.getChildAt(position).setVisibility(VISIBLE);
    }

    private void closeMenu() {
        if(isPerformAnim){
            return;
        }

        //使整个 内容+阴影布局下滑出来
        ObjectAnimator translationYAnim=ObjectAnimator.ofFloat(menuLayout,
                "TranslationY",0,-menuLayoutHeight);

        //使阴影渐显
        shadowView.setVisibility(VISIBLE);
        ObjectAnimator alphaAnim=ObjectAnimator.ofFloat(shadowView,
                "alpha",shadowViewAlpha,0);

        AnimatorSet animSet=new AnimatorSet();
        animSet.playTogether(translationYAnim,alphaAnim);
        animSet.setDuration(duration);

        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPerformAnim=false;

                //默认的tab样式
                mAdapter.defaultTabView(tabLayout,tabLayout.getChildAt(currentPosition));

                //关闭当前menuView
                //关闭动画还没结束 再次快速点击打开关闭 会导致openMenu打开的时候
                //currentPosition=position;还未执行 关闭的时候取到-1
                Log.i("TAG","currentPosition   "+currentPosition);
                menuLayout.getChildAt(currentPosition).setVisibility(GONE);
                //隐藏阴影
                //在关闭动画没有完全执行完成的时候 再次连续点击阴影部分还是会导致取到-1
                //解决办法 动画执行的时候不要再次响应点击事件重复执行动画
                shadowView.setVisibility(GONE);
                currentPosition=-1;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isPerformAnim=true;
            }
        });
        animSet.start();

    }



}
