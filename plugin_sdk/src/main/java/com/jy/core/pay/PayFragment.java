package com.jy.core.pay;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.cwsdk.plugin.event.EventPublisher;
import com.cwsdk.sdkplugin.GlobalVariable;
import com.cwsdk.sdkplugin.R;
import com.cwsdk.sdkplugin.bean.GameOrderInfo;
import com.cwsdk.sdkplugin.bean.GameRoleInfo;
import com.cwsdk.sdkplugin.bean.GameUserInfo;
import com.cwsdk.sdkplugin.bean.OrderResponse;
import com.cwsdk.sdkplugin.bean.PayBean;
import com.cwsdk.sdkplugin.callback.RequestCommonCallBack;
import com.cwsdk.sdkplugin.utils.CwSdkLog;
import com.cwsdk.sdkplugin.utils.RequestUtils;
import com.jy.core.R;

/**
 * Created by yuan on 2017/6/23.
 */

public class PayFragment extends Fragment implements View.OnClickListener {

    private View mRootView;

    private WebView mWebView;

    private static final String ARG_ORDER_INFO = "GameOrderInfo";

    private static final String ARG_ROLE_INFO = "GameRoleInfo";

    private static final String ARG_USER_INFO = "GameUserInfo";

    public static PayFragment newInstance(GameOrderInfo orderInfo, GameRoleInfo roleInfo, GameUserInfo userInfo) {
        PayFragment contentFragment = new PayFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER_INFO, orderInfo);
        args.putSerializable(ARG_ROLE_INFO, roleInfo);
        args.putSerializable(ARG_USER_INFO, userInfo);
        contentFragment.setArguments(args);
        return contentFragment;
    }

    public static PayFragment newInstance(String orderInfo, String roleInfo, String userInfo) {
        GameOrderInfo gameOrderInfo = RequestUtils.gson.fromJson(orderInfo, GameOrderInfo.class);
        GameRoleInfo gameRoleInfo = RequestUtils.gson.fromJson(roleInfo, GameRoleInfo.class);
        GameUserInfo gameUserInfo = RequestUtils.gson.fromJson(userInfo, GameUserInfo.class);
        return newInstance(gameOrderInfo, gameRoleInfo, gameUserInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderInfo = (GameOrderInfo) getArguments().getSerializable(ARG_ORDER_INFO);
            mRoleInfo = (GameRoleInfo) getArguments().getSerializable(ARG_ROLE_INFO);
            mUserInfo = (GameUserInfo) getArguments().getSerializable(ARG_USER_INFO);
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

        String payInfo = mOrderInfo.getGoodsName() + "x" + mOrderInfo.getCount();

        mPayBean = new PayBean(mUserInfo.getUID(), mRoleInfo.getServerID(), String.valueOf(mOrderInfo.getAmount()), payInfo, mOrderInfo.getCpDate());

        TextView goodName = mRootView.findViewById(R.id.cw_text_good_name);

        goodName.setText(payInfo);
        TextView priceText = mRootView.findViewById(R.id.cw_text_good_price);

        double i = ((mOrderInfo.getAmount() + 0.0) / 100);

        priceText.setText("￥" + String.valueOf(i));
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
//        CwSdkLog.d("toWxPay");
//        String url = GlobalVariable.sdkPluginParams.getBaseUrl() + "/pay/shenfutongOrder";
//        mPayBean.setPayID(String.valueOf(21));
        if (loadingDialog != null && !loadingDialog.isShowing())
            loadingDialog.show();

//        RequestUtils.postFormData(url, mPayBean, OrderResponse.class, new RequestCommonCallBack<OrderResponse>() {
//            @Override
//            public void successCallBack(OrderResponse orderResponse) {
//                if ("success".equalsIgnoreCase(orderResponse.getResult())) {
//                    openUri(orderResponse.getPayUrl());
//                }
//            }
//
//            @Override
//            public void failedCallBack(String message) {
//
//            }
//        });
    }

    void toAliPay() {
//        String url = GlobalVariable.sdkPluginParams.getBaseUrl() + "/pay/shenfutongOrder";
//        mPayBean.setPayID(String.valueOf(22));
        if (loadingDialog != null && !loadingDialog.isShowing())
            loadingDialog.show();
//        RequestUtils.postFormData(url, mPayBean, OrderResponse.class, new RequestCommonCallBack<OrderResponse>() {
//            @Override
//            public void successCallBack(OrderResponse orderResponse) {
//                if ("success".equalsIgnoreCase(orderResponse.getResult())) {
//                    mWebView.loadUrl(orderResponse.getPayUrl());
//                }
//            }
//
//            @Override
//            public void failedCallBack(String message) {
//
//            }
//        });
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
//            CwSdkLog.d("webview 移除");
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
