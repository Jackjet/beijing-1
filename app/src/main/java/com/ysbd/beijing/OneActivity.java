package com.ysbd.beijing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ysbd.beijing.base.BaseLoginActivity;
import com.ysbd.beijing.bean.CaCert;
import com.ysbd.beijing.ui.activity.LoginActivity;
import com.ysbd.beijing.ui.activity.MainActivity;
import com.ysbd.beijing.utils.Constants;
import com.ysbd.beijing.utils.ToastUtil;
import com.ysbd.beijing.utils.WebServiceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.org.bjca.signet.component.core.activity.SignetCoreApi;
import cn.org.bjca.signet.component.core.activity.SignetToolApi;
import cn.org.bjca.signet.component.core.bean.results.FindBackUserResult;
import cn.org.bjca.signet.component.core.bean.results.GetUserListResult;
import cn.org.bjca.signet.component.core.bean.results.LoginResult;
import cn.org.bjca.signet.component.core.callback.FindBackUserCallBack;
import cn.org.bjca.signet.component.core.callback.LoginCallBack;
import cn.org.bjca.signet.component.core.enums.FindBackType;


/**
 * 此活动界面为ca证书验证界面,只有通过验证才可以进入正式登录界面
 *
 */

public class OneActivity extends BaseLoginActivity {

    @BindView(R.id.button)
    Button button;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.select_cert)
    TextView selectCert;


    List<String> certUserList;
    List<String> certMsspIdList;
    int key;    //key代表userList对应的MsspList中的msspID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ButterKnife.bind(this);
        certMsspIdList=new ArrayList<>();
        certUserList=new ArrayList<>();

        if (sp.getBoolean(Constants.IS_LOGIN, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }



    }






    @OnClick({R.id.button, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                SignetCoreApi.useCoreFunc(new FindBackUserCallBack(this, FindBackType.FINDBACK_USER) {
                    @Override
                    public void onFindBackResult(FindBackUserResult findBackUserResult) {
                    }
                });
                break;
            case R.id.login:
                getData();

                break;
        }

    }

    /**
     * 获取当前设备已下载证书列表
     * 返回值: 判断本地是否有用户证书     有:返回true   没有:返回false
     */
    private Boolean getCertList() {
        GetUserListResult getUserListResult = SignetToolApi.getUserList(this);//获取当前设备证书
        Map<String, String> map = getUserListResult.getUserListMap();//map集合存储 key:msspID ,value:用户名
        if(map==null||map.isEmpty()){
            return false;
        }
        for (String string : map.keySet()) {
            Log.d("=====", "key+"+string);
            certMsspIdList.add(string);        ///将map数组中的key和value保存下来,
            certUserList.add(map.get(string));  ///之后要用到通过userList下标找到对应的msspId进行验证
        }

        return true;
    }


    /**
     *如果参数为true,将本地用户证书添加到列表,否则提示获取证书
     * @param havaCert
     */
    private void displayList(Boolean havaCert){

        /**
         * if语句将判断本地是否有证书,如果有就将listPopupWindow空间展开
         * 否则提示设备无证书
         */

        if (havaCert){

            selectCert.setText("点击选择用户证书");
            final ListPopupWindow listPopupWindow=new ListPopupWindow(this);
            listPopupWindow.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,certUserList));
            listPopupWindow.setHeight(LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setWidth(LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setAnchorView(selectCert);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectCert.setText(certUserList.get(i));
                    listPopupWindow.dismiss();
                    key=i;   ///////////key代表当前用户对应的msspID下标
                }
            });

            /**
             * 监听点击事件,将列表展开
             */
            selectCert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listPopupWindow.show();
                }
            });

        }
        else{
            selectCert.setText("设备无用户证书");
        }
    }



    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String jobId;
                int count=0;
//                do {
                    jobId=WebServiceUtils.getInstance().getSignJobId();
//                }while (TextUtils.isEmpty(jobId)&&count++<10);

                Message msg = new Message();
                msg.what=0;
                msg.obj = jobId;
                handler.sendMessage(msg);

            }
        }).start();

    }

    private void seedVerifyCert(final String CertData, final String CertBook, final String CertValue){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String verifyCert;
//                int count=0;
//                do {
                    verifyCert=WebServiceUtils.getInstance().verifyCert(CertData, CertBook,CertValue);
//                }while (TextUtils.isEmpty(verifyCert)&&count++<10);

                Message msg = new Message();
                msg.what=1;
                msg.obj = verifyCert;
                handler.sendMessage(msg);

            }
        }).start();
    }



    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String signJobId;

                    signJobId  = msg.obj.toString();
                    String strings[] = signJobId.split("\"");
                    signJobId= strings[6].substring(0,strings[6].length()-1);
                    Log.d("=============", "handleMessage: "+signJobId);
                    if (selectCert.getText().equals("设备无用户证书")||selectCert.getText().equals("点击选择用户证书"))
                    {
                        ToastUtil.show("请选择用户证书",OneActivity.this);
                    }else if(!TextUtils.isEmpty(signJobId)){
                        SignetCoreApi.useCoreFunc(new LoginCallBack(OneActivity.this,certMsspIdList.get(key),signJobId ) {
                            @Override
                            public void onLoginResult(LoginResult loginResult) {

                                if (loginResult.getErrCode().equals("0x12200000")){
                                    Log.d("密码错误提示+++++++++", "onLoginResult: "+loginResult.getErrMsg());
                                    ToastUtil.show("密码输入错误",OneActivity.this);
                                    Intent intent=new Intent(OneActivity.this,OneActivity.class);
                                    startActivity(intent);
                                }else if (loginResult.getErrCode().equals("0x11000001")){
                                    ToastUtil.show("取消操作",OneActivity.this);
                                    Intent intent=new Intent(OneActivity.this,OneActivity.class);
                                    startActivity(intent);

                                }else {
                                    String certData=loginResult.getSignDataJobId();
                                    String certBook=loginResult.getCert();
                                    String certValue=loginResult.getSignDataInfos().get(0).getSignature();

                                    seedVerifyCert(certData,certBook,certValue);
                                }



                            }
                        });
                    }
                    else {
                        ToastUtil.show("本次验证失败",OneActivity.this);
                    }
                    break;
                case 1:
                    ArrayList<String> names=new ArrayList<>();
                    String result = msg.obj.toString();
                    CaCert caCerts = JSONToObject(result);
                    List<CaCert.UserinfoBean> cacerts= caCerts.getUserinfo();
                    if (caCerts.getSuccess().equals("true")){
                        Intent intent = new Intent(OneActivity.this,LoginActivity.class);
                        for (int j = 0; j <cacerts.size(); j++) {
                            names.add(cacerts.get(j).getEMPLOYEE_LOGINNAME());
                        }

                        intent.putStringArrayListExtra("names",names);
                        startActivity(intent);
                        finish();
                    }else {
                        ToastUtil.show("证书验证失败",OneActivity.this);
                    }

                    break;
            }
        }
    };


    public static CaCert JSONToObject(String json) {
        CaCert caCerts= new Gson().fromJson(json, CaCert.class);

        return caCerts;
    }

    @Override
    protected void onResume() {
        super.onResume();
        certUserList.clear();
        certMsspIdList.clear();
        Boolean haveCert=getCertList();
        displayList(haveCert);
    }
}

