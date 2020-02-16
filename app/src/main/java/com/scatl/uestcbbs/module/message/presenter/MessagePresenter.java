package com.scatl.uestcbbs.module.message.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.entity.PrivateMsgBean;
import com.scatl.uestcbbs.helper.ExceptionHelper;
import com.scatl.uestcbbs.helper.rxhelper.Observer;
import com.scatl.uestcbbs.helper.rxhelper.SubscriptionManager;
import com.scatl.uestcbbs.module.message.model.MessageModel;
import com.scatl.uestcbbs.module.message.view.MessageView;
import com.scatl.uestcbbs.util.SharePrefUtil;

import io.reactivex.disposables.Disposable;

public class MessagePresenter extends BasePresenter<MessageView> {

}
