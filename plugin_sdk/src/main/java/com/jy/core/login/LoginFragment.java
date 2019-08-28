package com.jy.core.login;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import androidx.core.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jy.core.R;
import com.jy.core.bean.AccountInfo;
import com.jy.core.utils.UIUtils;

import java.util.List;

/**
 * 登录界面
 * 游客登录，普通账号注册登录
 */
public class LoginFragment extends Fragment implements LoginContract.LoginView, View.OnClickListener, PopupWindow.OnDismissListener, DialogInterface.OnDismissListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    //需要使用的参宿
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //默认当前业务是登录
    private int currentBusiness = INDEX;
    private View mRootView; //根布局
    private LinearLayout mIndexLayout;
    private LinearLayout mInputLayout;
    private TextView mTtileText;
    private Button mBackBtn;
    private RelativeLayout mBackLayout;

    private EditText mAccountInput;
    private EditText mPasswordInput;

    private ImageView mEyeImageView; //注意区分
    private RelativeLayout mEyeReLayout;
    private RelativeLayout mTriangleReLayout;

    private LinearLayout loginOrRegisterBtn;
    private ProgressBar mProgressBar;
    private TextView mLoginText;

    private RelativeLayout mAccountInputLayout; //用户抛点
    private LinearLayout mRuleLineLayout;
    private ImageView mAgreeCheck;

    private LinearLayout mBottomLineLayout;
    private TextView mRuleText;
    private List<AccountInfo> mAccountInfos;

    private boolean isAgress = true; //是否同意了用户协议，默认同意

    private LoginContract.LoginPresenter mPresenter;
    private boolean isShowPassword;

    public LoginPresenter getPresenter() {
        return (LoginPresenter) mPresenter;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public Context getContext() {
        return getActivity();
    }

    public static final String[] requestPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mPresenter = createPresenter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.csdk_login_fragment, null);
        initView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.attachView(this);
    }

    private <T extends View> T findView(int id) {
        return (T) mRootView.findViewById(id);
    }

    private void addClick(View view) {
        view.setOnClickListener(this);
    }

    private void addClick(int id) {
        mRootView.findViewById(id).setOnClickListener(this);
    }

    private void initView() {
        mIndexLayout = findView(R.id.csdk_login_llyt_index);
        mInputLayout = findView(R.id.csdk_login_llyt_input);
        mTtileText = findView(R.id.csdk_login_tv_title);
        mBackBtn = findView(R.id.csdk_login_btn_back);
        mBackLayout = findView(R.id.csdk_login_rtyt_back);
        mAccountInput = findView(R.id.csdk_login_et_account);
        mPasswordInput = findView(R.id.csdk_login_et_passwd);
        mProgressBar = findView(R.id.csdk_login_pb_tologin);
        mLoginText = findView(R.id.csdk_login_tv_tologin);
        mEyeImageView = findView(R.id.csdk_login_iv_eye);
        mAccountInputLayout = findView(R.id.csdk_login_rtyt_account_input);
        mEyeReLayout = findView(R.id.csdk_login_rtyt_passwd_hint_show);
        mTriangleReLayout = findView(R.id.csdk_login_rtyt_account_list);
        loginOrRegisterBtn = findView(R.id.csdk_login_llyt_tologin);
        mRuleLineLayout = findView(R.id.csdk_login_llyt_user_rule);
        mAgreeCheck = findView(R.id.csdk_login_iv_agree_user_rule);
        mBottomLineLayout = findView(R.id.csdk_login_llyt_bottom);
        mRuleText = findView(R.id.csdk_login_tv_user_rule);
        addClick(mEyeReLayout);
        addClick(mTriangleReLayout);
        addClick(loginOrRegisterBtn);
        addClick(mAgreeCheck);
        addClick(mBackBtn);
        addClick(mBackLayout);
        addClick(R.id.csdk_login_close_btn);
        addClick(R.id.csdk_login_btn_register);
        addClick(R.id.csdk_login_btn_guest_login);
        addClick(R.id.csdk_login_btn_account_login);
        loginOrRegisterBtn.setEnabled(false);
        mBackLayout.setVisibility(View.GONE);
        mLoginText.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
        mAccountInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    mPasswordInput.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        mAccountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String account = s.toString();
                String password = mPasswordInput.getText().toString();
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                    if (currentBusiness == REGISTER && !isAgress)
                        return;
                    loginOrRegisterBtn.setEnabled(true);
                    mLoginText.setTextColor(getResources().getColor(R.color.csdk_normal_text_color));
                } else {
                    loginOrRegisterBtn.setEnabled(false);
                    mLoginText.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
                }
            }
        });


        mPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                String account = mAccountInput.getText().toString();
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
                    if (currentBusiness == REGISTER && !isAgress)
                        return;

                    loginOrRegisterBtn.setEnabled(true);
                    mLoginText.setTextColor(getResources().getColor(R.color.csdk_normal_text_color));
                } else {
                    loginOrRegisterBtn.setEnabled(false);
                    mLoginText.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
                }

            }
        });
    }

    private void initSpannable() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        String text = getResources().getString(R.string.csdk_agree_register_rule);
        String spanText = getResources().getString(R.string.csdk_register_rule_span_text);

        SpannableString spannableInfo = new SpannableString(text);
        spannableInfo.setSpan(new Clickable(clickListener), text.length() - spanText.length() - 1, text.length() - 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        mRuleText.setText(spannableInfo);
        mRuleText.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void openBrowse(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.d("openBrowse: ", "地址为空");
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public LoginContract.LoginPresenter createPresenter() {
        return new LoginPresenter(getActivity());
    }


    Dialog loadingDialog;

    private boolean isInLoading;

    @Override
    public void showLoading(String message) {
        isInLoading = true;
        mProgressBar.setVisibility(View.VISIBLE);
        mLoginText.setText(message);
        loadingDialog = new Dialog(getActivity(), R.style.csdk_game_style_loading_t);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.csdk_dialog_loading, null);
        TextView tipText = view.findViewById(R.id.csdk_dialog_loading_tip_tv);
        tipText.setText(message);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setOnDismissListener(this);
        loadingDialog.show();
    }


    //监听取消请求
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (isInLoading == true) {
            hideLoading("");
            mPresenter.cancelLogin();
        }
    }

    @Override
    public void hideLoading(String message) {
        isInLoading = false;
        mProgressBar.setVisibility(View.GONE);
        if (currentBusiness == LOGIN) {
            mLoginText.setText(R.string.csdk_login_text);
        } else if (currentBusiness == REGISTER) {
            mLoginText.setText(R.string.csdk_register_and_login_text);
        }
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }

    @Override
    public void showSuccessMsg(String message) {
        isInLoading = false;
        mProgressBar.setVisibility(View.GONE);
        mLoginText.setText(message);
        if (loadingDialog != null)
            loadingDialog.dismiss();
        getActivity().finish();
    }

    @Override
    public void showFailedMsg(String message) {
        mProgressBar.setVisibility(View.GONE);
        if (currentBusiness == LOGIN) {
            mLoginText.setText(R.string.csdk_login_text);
        } else if (currentBusiness == REGISTER) {
            mLoginText.setText(R.string.csdk_register_and_login_text);
        }
        if (loadingDialog != null)
            loadingDialog.dismiss();
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void acceptAccountInfoList(List<AccountInfo> ls) {
        mAccountInfos = ls;
    }

    @Override
    public void changeBusiness(int business) {

        mAccountInput.setText("");
        mPasswordInput.setText("");
        if (business == INDEX) {
            mTtileText.setText(R.string.csdk_index_title);
            mIndexLayout.setVisibility(View.VISIBLE);
            mInputLayout.setVisibility(View.GONE);
            mBackLayout.setVisibility(View.GONE);

        } else if (business == LOGIN) {
            isShowPassword = false;
            mIndexLayout.setVisibility(View.GONE);
            mInputLayout.setVisibility(View.VISIBLE);
            mBackLayout.setVisibility(View.VISIBLE);
            mTtileText.setText(R.string.csdk_account_login);
            mLoginText.setText(R.string.csdk_login_text);
            mTriangleReLayout.setVisibility(View.VISIBLE);
            mRuleLineLayout.setVisibility(View.GONE);
            mBottomLineLayout.setVisibility(View.VISIBLE);
            mPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mEyeImageView.setBackgroundResource(R.drawable.csdk_btn_eye_close);

            if (mAccountInfos != null && mAccountInfos.size() > 0) {
                mAccountInput.setText(mAccountInfos.get(0).getAccount());
                mPasswordInput.setText(mAccountInfos.get(0).getPassword());
            }
            if (!TextUtils.isEmpty(mAccountInput.getText().toString()) && !TextUtils.isEmpty(mPasswordInput.getText().toString())) {
                loginOrRegisterBtn.setEnabled(true);
                mLoginText.setTextColor(getResources().getColor(R.color.csdk_normal_text_color));
            } else {
                loginOrRegisterBtn.setEnabled(false);
                mLoginText.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
            }
        } else if (business == REGISTER) {
            isShowPassword = false;
            mIndexLayout.setVisibility(View.GONE);
            mInputLayout.setVisibility(View.VISIBLE);
            mBackLayout.setVisibility(View.VISIBLE);
            mTtileText.setText(R.string.csdk_account_register);
            mLoginText.setText(R.string.csdk_register_and_login_text);
            mAgreeCheck.setBackgroundResource(R.drawable.csdk_checkbox_checked);
            isAgress = true;
            mTriangleReLayout.setVisibility(View.GONE);
            mRuleLineLayout.setVisibility(View.VISIBLE);
            mBottomLineLayout.setVisibility(View.GONE);
            mPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mEyeImageView.setBackgroundResource(R.drawable.csdk_btn_eye_close);
        }
        currentBusiness = business;

    }

    @Override
    public int getCurrentBusiness() {
        return currentBusiness;
    }

    @Override
    public String getAccount() {
        return mAccountInput.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return mPasswordInput.getText().toString().trim();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            case R.id.csdk_login_llyt_tologin:
                if (currentBusiness == LOGIN) {
                    mPresenter.login();
                } else {
                    mPresenter.register();
                }
                break;
            case R.id.csdk_login_rtyt_passwd_hint_show:
                if (isShowPassword) {
                    mPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEyeImageView.setBackgroundResource(R.drawable.csdk_btn_eye_close);
                } else {
                    mPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mEyeImageView.setBackgroundResource(R.drawable.csdk_btn_eye_open);
                }
                isShowPassword = !isShowPassword;
                break;
            case R.id.csdk_login_rtyt_account_list:
                showPopwindow();
                break;
            case R.id.csdk_login_iv_agree_user_rule:
                if (isAgress) {
                    mAgreeCheck.setBackgroundResource(R.drawable.csdk_checkbox_unchecked);
                    loginOrRegisterBtn.setEnabled(false);
                    mLoginText.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
                } else {
                    mAgreeCheck.setBackgroundResource(R.drawable.csdk_checkbox_checked);
                    if (!TextUtils.isEmpty(mAccountInput.getText().toString()) && !TextUtils.isEmpty(mPasswordInput.getText().toString())) {
                        mLoginText.setTextColor(getResources().getColor(R.color.csdk_normal_text_color));
                        loginOrRegisterBtn.setEnabled(true);
                    }
                }
                isAgress = !isAgress;
                break;
            case R.id.csdk_login_btn_register:
                changeBusiness(REGISTER);
                break;
            case R.id.csdk_login_rtyt_back:
                if (currentBusiness == LOGIN) {
                    //TODO 回退到index页
                    changeBusiness(INDEX);
                } else if (currentBusiness == REGISTER) {
                    changeBusiness(LOGIN);
                }
                break;
            case R.id.csdk_login_btn_back:
                if (currentBusiness == LOGIN) {
                    //TODO 回退到index页
                    changeBusiness(INDEX);
                } else if (currentBusiness == REGISTER) {
                    changeBusiness(LOGIN);
                }
                break;
            case R.id.csdk_login_btn_account_login:
                changeBusiness(LOGIN);
                break;
            case R.id.csdk_login_btn_guest_login:
                mPresenter.guestLogin();
                break;
            case R.id.csdk_login_close_btn:
                getActivity().finish();
                break;
        }
    }

    PopupWindow popupWindow;

    private void showPopwindow() {
        if (mAccountInfos == null || mAccountInfos.size() == 0) {
            return;
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.csdk_popwin_account_list, null);
            ListView listView = (ListView) view.findViewById(R.id.csdk_popwin_lv_account_list);
            listView.setVerticalScrollBarEnabled(false);
            listView.setAdapter(new AccountListAdapter(getActivity(),
                    R.layout.csdk_popwin_account_item, mAccountInfos));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAccountInput.setText(mAccountInfos.get(position).getAccount());
                    mPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEyeImageView.setBackgroundResource(R.drawable.csdk_btn_eye_close);
                    isShowPassword = false;
                    mPasswordInput.setText(mAccountInfos.get(position).getPassword());
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(view,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setWidth(mAccountInputLayout.getWidth());
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(true);
            popupWindow.setContentView(view);
            popupWindow.showAsDropDown(mAccountInputLayout, 0, UIUtils.dip2px(getActivity(), 15.0f));
            popupWindow.setOnDismissListener(this);
            mTriangleReLayout.setBackgroundResource(R.drawable.csdk_btn_arrow_up);
        }
    }

    @Override
    public void onDismiss() {
        mTriangleReLayout.setBackgroundResource(R.drawable.csdk_btn_arrow);
    }


    static class AccountListAdapter extends ArrayAdapter<AccountInfo> {

        private List<AccountInfo> data;
        private int itemId;
        private Context context;

        public AccountListAdapter(Context context, int resource, List<AccountInfo> objects) {
            super(context, resource, objects);
            data = objects;
            itemId = resource;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(itemId, parent, false);
                holder.accountText = (TextView) convertView.findViewById(R.id.account_item_tv);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.accountText.setText(data.get(position).getAccount());
            return convertView;
        }

        static class ViewHolder {
            TextView accountText;
        }
    }


    class Clickable extends ClickableSpan {

        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(LoginFragment.this.getResources().getColor(R.color.csdk_user_rule_text_span_color));
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
