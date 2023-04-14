package com.scatl.uestcbbs.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.scatl.uestcbbs.base.BaseEvent;
//import com.scatl.uestcbbs.module.message.view.AtMeMsgActivity;
import com.scatl.uestcbbs.module.message.view.DianPingMsgFragment;
//import com.scatl.uestcbbs.module.message.view.ReplyMeMsgActivity;
import com.scatl.uestcbbs.module.message.view.SystemMsgFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * author: sca_tl
 * description:处理通知栏点击事件
 * 如果在清单文件里静态注册，那么该对象的实例在onReceive被调用之后就会在任意时间内被销毁。
 * 也就是说，我们并不需要担心静态注册的BroadcastReceiver的销毁问题
 * date: 2019/8/6 10:40
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null)
            switch (intent.getAction()) {
//                case BaseEvent.EventCode.NEW_AT_MSG:
//                    Intent intent1 = new Intent(context, AtMeMsgActivity.class);
//                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent1);
//                    break;

//                case BaseEvent.EventCode.NEW_REPLY_MSG:
//                    Intent intent2 = new Intent(context, ReplyMeMsgActivity.class);
//                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent2);
//                    break;

//                case BaseEvent.EventCode.NEW_PRIVATE_MSG:
//                    EventBus.getDefault().post(new BaseEvent<>(BaseEvent.EventCode.SWITCH_TO_MESSAGE));
//                    break;

//                case BaseEvent.EventCode.NEW_SYSTEM_MSG:
//                    Intent intent3 = new Intent(context, SystemMsgFragment.class);
//                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent3);
//                    break;
//
//                case BaseEvent.EventCode.NEW_DAINPING_MSG:
//                    Intent intent4 = new Intent(context, DianPingMsgFragment.class);
//                    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent4);
//                    break;

                default:
                    break;
            }
    }
}
