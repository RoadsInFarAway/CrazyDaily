package com.crazysunj.crazydaily.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crazysunj.crazydaily.R;
import com.crazysunj.crazydaily.base.BaseActivity;
import com.crazysunj.crazydaily.constant.ActivityConstant;
import com.crazysunj.crazydaily.presenter.ZhihuNewsDetailPresenter;
import com.crazysunj.crazydaily.presenter.contract.ZhihuNewsDetailContract;
import com.crazysunj.crazydaily.util.HtmlUtil;
import com.crazysunj.crazydaily.util.SnackbarUtil;
import com.crazysunj.domain.entity.ZhihuNewsDetailEntity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;

public class ZhihuNewsDetailActivity extends BaseActivity<ZhihuNewsDetailPresenter> implements ZhihuNewsDetailContract.View {

    @BindView(R.id.zhihu_news_detail_icon)
    ImageView mIcon;
    @BindView(R.id.zhihu_news_detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.zhihu_news_detail_web)
    WebView mWeb;
    @BindView(R.id.zhihu_news_detail_fab)
    FloatingActionButton mFab;
    @BindView(R.id.zhihu_news_detail_appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.zhihu_news_detail_ctl)
    CollapsingToolbarLayout mBar;

    private long mId;
    private String mIconUrl;

    boolean isImageShow = false;
    boolean isTransitionEnd = false;
    boolean isNotTransition = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTranslucent(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        mId = getIntent().getLongExtra(ActivityConstant.ID, 0L);
        setSupportActionBar(mToolbar);

        final WebSettings settings = mWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        handleTranstion();
    }

    @Override
    protected void initListener() {

        mFab.setOnClickListener(v -> SnackbarUtil.show(this, "喜欢就点个star吧！"));

        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.getZhihuNewsDetail(mId);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_zhihu_news_detail;
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public void showContent(ZhihuNewsDetailEntity zhihuNewsDetailEntity) {
        mIconUrl = zhihuNewsDetailEntity.getImage();
        if (isNotTransition) {
            Glide.with(this).load(mIconUrl).centerCrop().into(mIcon);
        } else {
            if (!isImageShow && isTransitionEnd) {
                Glide.with(this).load(mIconUrl).centerCrop().into(mIcon);
            }
        }
        mBar.setTitle(zhihuNewsDetailEntity.getTitle());
        String htmlData = HtmlUtil.createHtmlData(zhihuNewsDetailEntity);
        mWeb.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWeb.canGoBack()) {
            mWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleTranstion() {
        (getWindow().getSharedElementEnterTransition()).addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                isTransitionEnd = true;
                if (!TextUtils.isEmpty(mIconUrl)) {
                    isImageShow = true;
                    Glide.with(ZhihuNewsDetailActivity.this).load(mIconUrl).centerCrop().into(mIcon);
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    public static void start(Activity activity, long id, View shareView) {
        Intent intent = new Intent(activity, ZhihuNewsDetailActivity.class);
        intent.putExtra(ActivityConstant.ID, id);
        if (shareView != null) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, shareView, "shareView").toBundle());
        } else {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        }
    }
}
