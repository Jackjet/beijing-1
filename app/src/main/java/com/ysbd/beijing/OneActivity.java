package com.ysbd.beijing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.org.bjca.signet.component.core.activity.SignetCoreApi;
import cn.org.bjca.signet.component.core.bean.results.FindBackUserResult;
import cn.org.bjca.signet.component.core.callback.FindBackUserCallBack;
import cn.org.bjca.signet.component.core.enums.FindBackType;

public class OneActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.button)
    public void onViewClicked() {

        SignetCoreApi.useCoreFunc(new FindBackUserCallBack(this,FindBackType.FINDBACK_USER) {
            @Override
            public void onFindBackResult(FindBackUserResult findBackUserResult) {
                Log.d("--------", findBackUserResult.getMsspID());
            }
        });
    }
}
