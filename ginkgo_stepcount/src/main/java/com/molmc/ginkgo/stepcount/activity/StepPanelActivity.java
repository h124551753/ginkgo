package com.molmc.ginkgo.stepcount.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.molmc.ginkgo.basic.base.BaseActivity;
import com.molmc.ginkgo.basic.data.SPDataSource;
import com.molmc.ginkgo.basic.views.StepArcView;
import com.molmc.ginkgo.basic.views.TitleView;
import com.molmc.ginkgo.stepcount.R;
import com.molmc.ginkgo.stepcount.listener.UpdateUiCallBack;
import com.molmc.ginkgo.stepcount.service.StepService;

import static com.molmc.ginkgo.stepcount.constant.SPKey.STEP_WALK_QTY;

public class StepPanelActivity extends BaseActivity {

    private TitleView tvTitle;
    private TextView tv_data;
    private StepArcView cc;
    private TextView tv_set;
    private TextView tv_isSupport;

    private boolean isBind = false;
    private int planWalk_QTY = 7000;

    @Override
    protected int setContentView() {
        return R.layout.stepcount_activity_main;
    }

    @Override
    protected void start() {
        initView();
        initListener();
        initData();
    }

    private void initView(){
        tvTitle = findViewById(R.id.tv_title);
        tv_data = findViewById(R.id.tv_data);
        cc = findViewById(R.id.cc);
        tv_set = findViewById(R.id.tv_set);
        tv_isSupport = findViewById(R.id.tv_isSupport);
    }

    private void initData(){
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        planWalk_QTY = (int)SPDataSource.get(this, STEP_WALK_QTY, 7000);
        //设置当前步数为0
        cc.setCurrentCount(planWalk_QTY, 0);
        tv_isSupport.setText("计步中...");
        setupService();
    }

    private void initListener(){
        tvTitle.setOnBackClickListener(v -> finish());
        addOnClickListeners(R.id.tv_set);
        addOnClickListeners(R.id.tv_data);
    }

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            cc.setCurrentCount(planWalk_QTY, stepService.getStepCount());

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    cc.setCurrentCount(planWalk_QTY, stepCount);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int i = view.getId();
        if (i == R.id.tv_set) {
            // startActivity(new Intent(this, SetPlanActivity.class));
        } else if (i == R.id.tv_data) {
            // startActivity(new Intent(this, HistoryActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }
}
