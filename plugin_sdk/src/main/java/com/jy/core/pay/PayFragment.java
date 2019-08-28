package com.jy.core.pay;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.jy.core.R;

public class PayFragment extends Fragment implements View.OnClickListener {

    private View mRootView;

    private WebView mWebView;

    public static final String ARG_PRODUCE_NAME = "produceName";

    public static final String ARG_AMOUNT = "amount";

    private String produceName;
    private int amount;

    public static PayFragment newInstance(String produceName, int amount) {
        PayFragment contentFragment = new PayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCE_NAME, produceName);
        args.putSerializable(ARG_AMOUNT, amount);
        contentFragment.setArguments(args);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            produceName = (String) getArguments().getSerializable(ARG_PRODUCE_NAME);
            amount = (int) getArguments().getSerializable(ARG_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.csdk_pay_fragment, null);
        initView();
        initWebView();
        return mRootView;
    }

    public Dialog loadingDialog;

    private void initView() {
        mRootView.findViewById(R.id.tc_web_close_btn).setOnClickListener(this);
        mRootView.findViewById(R.id.cw_btn_pay_alipay).setOnClickListener(this);
        mRootView.findViewById(R.id.cw_btn_pay_wx).setOnClickListener(this);
        loadingDialog = new Dialog(getActivity(), R.style.csdk_game_style_loading_t);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.csdk_dialog_loading, null);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);
        TextView goodName = mRootView.findViewById(R.id.cw_text_good_name);
        goodName.setText(produceName);
        TextView priceText = mRootView.findViewById(R.id.cw_text_good_price);
        double i = ((amount + 0.0) / 100);
        priceText.setText("￥" + i);
    }

    private void initWebView() {
        mWebView = new WebView(getActivity());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVisibility(View.GONE);
        mWebView.getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                CwSdkLog.d("inner web url:" + url);
                //打开支付或微信协议地址
                if (url.contains("alipays://") || url.contains("weixin://")) {
                    openUri(url);
                } else {
                    mWebView.loadUrl(url);
                }
                return true;
            }
        });
    }

    public void openUri(String uri) {
        Intent intent;
        try {
            intent = Intent.parseUri(uri,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            getActivity().startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.tc_web_close_btn:
                getActivity().finish();
                break;
            case R.id.cw_btn_pay_alipay:
                toAliPay();
                break;
            case R.id.cw_btn_pay_wx:
                toWxPay();
                break;
        }
    }

    void toWxPay() {
        Toast.makeText(getActivity(),"微信支付",Toast.LENGTH_LONG).show();
    }

    void toAliPay() {
        Toast.makeText(getActivity(),"支付宝支付",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
