package com.ysbd.beijing.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ysbd.beijing.BaseActivity;
import com.ysbd.beijing.R;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.LoginUtils;
import com.ysbd.beijing.utils.MD5;
import com.ysbd.beijing.utils.ToastUtil;
import com.ysbd.beijing.utils.WebServiceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.fl_back)
    FrameLayout flBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_original)
    EditText etOriginal;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.bt_reset)
    Button btReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        tvName.setText(sp.getString(Constants.USER_NAME,""));
    }

    @OnClick({R.id.fl_back, R.id.bt_login, R.id.bt_reset})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.bt_login:
                check();
                break;
            case R.id.bt_reset:
                etCode.setText("");
                etOriginal.setText("");
                etPassword.setText("");
                break;
        }
    }
    String newPass;
    private void check(){
//        etOriginal.getText().toString().equals()
        String original=etOriginal.getText().toString();
        if (!new MD5().getMD5ofStr(original).equals(sp.getString(Constants.USER_PASS,new MD5().getMD5ofStr("123456")))) {
            ToastUtil.show("原密码输入错误",this);
            return;
        }
        boolean containAll = isContainAll(etPassword.getText().toString());

        if (etPassword.getText().toString().equals(etCode.getText().toString())){
            if (etPassword.getText().toString().length()<8){
                ToastUtil.show("密码不能小于8位",SettingActivity.this);
            }else if (etPassword.getText().toString().length()>12){
                ToastUtil.show("密码不能超过12位",SettingActivity.this);
            }else if (containAll){
                newPass=etPassword.getText().toString();
                String rePass=etCode.getText().toString();
                String msg=LoginUtils.validPwd(newPass);
                if (msg.length()>1) {
                    ToastUtil.show(msg,this);
                }else if(rePass.equals(newPass)){
                    reset(newPass);
                }else {
                    ToastUtil.show("两次输入的新密码不一致",this);
                }
            }else {
                ToastUtil.show("密码必须包含大小写字母和数字",SettingActivity.this);
            }
        }else {
            ToastUtil.show("两次输入的新密码不一致，请重新输入",SettingActivity.this);
        }





    }

    public static boolean isContainAll(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLowerCase = false;//定义一个boolean值，用来表示是否包含字母
        boolean isUpperCase = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLowerCase(str.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
                isLowerCase = true;
            } else if (Character.isUpperCase(str.charAt(i))) {
                isUpperCase = true;
            }
        }
        String regex = "^[a-zA-Z0-9]+$";
        boolean isRight = isDigit && isLowerCase && isUpperCase && str.matches(regex);
        return isRight;
    }

    private void reset(String pass){
       final String  pass1=new MD5().getMD5ofStr(pass);
        final String name=sp.getString(Constants.USER_NAME,"");
        new Thread(){
            @Override
            public void run() {
                super.run();
                String res;
                int count=0;
                do{
                    res=WebServiceUtils.getInstance().resetPass(name,pass1);
                }while (TextUtils.isEmpty(res)&&count++<10);

                if (res.contains("成功")) {
                    handler.sendEmptyMessage(1);
                    sp.edit().putString(Constants.USER_PASS,pass1).apply();
                }else {
                    handler.sendEmptyMessage(2);
                }
            }
        }.start();


    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ToastUtil.show("修改成功！",SettingActivity.this);
                    finish();
                    break;
                case 2:
                    ToastUtil.show("修改失败！",SettingActivity.this);
                    break;
            }
        }
    };
}
