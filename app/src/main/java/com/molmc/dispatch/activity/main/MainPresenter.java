package com.molmc.dispatch.activity.main;

import android.app.PendingIntent;
import android.content.Context;

import com.molmc.ginkgo.basic.biz.BasicBiz;
import com.molmc.ginkgo.basic.data.NetDataSource;
import com.molmc.ginkgo.basic.entity.MQMessage;
import com.molmc.ginkgo.basic.mqtt.MQTTClient;
import com.molmc.ginkgo.basic.utils.LogUtils;

/**
 * Created by wyl on 2017/12/8
 */
public class MainPresenter implements MainContract.Presenter, MQTTClient.Observer {
    private final MainContract.View mView;
    private final Context mContext;

    MainPresenter(MainContract.View view) {
        this.mView = view;
        this.mContext = view.getContext();
    }

    @Override
    public void start() {
        // 检查更新
        BasicBiz.checkVersionUpdate(mContext, this, false);
        // 监听MQ消息
        MQTTClient.addObserver(this);
    }



    @Override
    public void onMessageArrive(MQMessage msg) {
        LogUtils.i(msg.getData());
        switch (msg.getMessageType()) {

        }
    }

    /**
     * 发送通知
     */
    private void sendNotification(String title, String content, boolean tts, PendingIntent pendingIntent) {
        /*NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        Integer notificationId = 0;
        switch (dto.getType()) {
            case NotificationType.TYPE_MESSAGE:
                ChatUserDto chatUserDto = (ChatUserDto) intent.getSerializableExtra("ChatUserDto");
                String chatId = chatUserDto.getId();
                notificationId = map.get(chatId);
                if (notificationId == null) {
                    notificationId = ++mRequestCode;
                    map.put(chatId, notificationId);
                }
                break;
            case NotificationType.TYPE_TASK:
                notificationId = map.get(msg);
                if (notificationId == null) {
                    notificationId = ++mRequestCode;
                    map.put(msg, notificationId);
                }
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Ticker是状态栏显示的提示
        builder.setTicker(msg)
                //第一行内容  通常作为通知栏标题
                .setContentTitle(dto.getContentTitle())
                //第二行内容 通常是通知正文
                .setContentText(msg)
                //可以点击通知栏的删除按钮删除
                .setAutoCancel(true)
                //点击跳转的intent
                .setContentIntent(pIntent)
                //通知默认的声音 震动 呼吸灯
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //系统状态栏显示的小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //下拉显示的大图标
                .setLargeIcon(bitmap);*/
    }

    @Override
    public void exit() {
        NetDataSource.unSubscribe(this);
        NetDataSource.unRegister(this);
        MQTTClient.removeObserver(this);
    }
}
