package com.ysbd.beijing;

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

public class OneActivity extends BaseActivity {

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
        Boolean haveCert=getCertList();
        displayList(haveCert);
        getData();

    }






    @OnClick({R.id.button, R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                SignetCoreApi.useCoreFunc(new FindBackUserCallBack(this, FindBackType.FINDBACK_USER) {
                    @Override
                    public void onFindBackResult(FindBackUserResult findBackUserResult) {
                        Log.d("=====", findBackUserResult.getMsspID());
                    }
                });
                break;
            case R.id.login:

                if (selectCert.getText().equals("设备无用户证书")||selectCert.getText().equals("点击选择用户证书"))
                {
                    ToastUtil.show("请选择用户证书",this);
                }else if(!TextUtils.isEmpty(signJobId)){
                    SignetCoreApi.useCoreFunc(new LoginCallBack(this,certMsspIdList.get(key),signJobId ) {
                        @Override
                        public void onLoginResult(LoginResult loginResult) {
                            Log.d("====="," ErrCode+"+loginResult.getErrCode());
                            Log.d("=====", loginResult.getCert());
                        }
                    });
                }
                else {
                    ToastUtil.show("本次验证失败",this);
                }
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

    String wsdl_url = "http://192.168.0.110:9998/risenetoabjcz/services/mobileUserInfo?wsdl";
    String name_sapce = "http://www.freshpower.com.cn";

    String signJobId;
    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {


//                signJobId=WebServiceManager.questToWebService(wsdl_url,name_sapce,"login");

               String sss=WebServiceUtils.getInstance().getSignJobId();
                Message msg = new Message();
                msg.obj = sss;
                handler.sendMessage(msg);

            }
        }).start();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        certUserList.clear();
        certMsspIdList.clear();
        displayList(getCertList());

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            signJobId  = msg.obj.toString();
        }
    };

}

