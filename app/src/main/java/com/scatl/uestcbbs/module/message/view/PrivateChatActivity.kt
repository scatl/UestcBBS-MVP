package com.scatl.uestcbbs.module.message.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.CountDownTimer
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityPrivateChatBinding
import com.scatl.uestcbbs.entity.PrivateChatBean
import com.scatl.uestcbbs.entity.PrivateChatDraftBean
import com.scatl.uestcbbs.entity.SendPrivateMsgResultBean
import com.scatl.uestcbbs.entity.UploadResultBean
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector
import com.scatl.uestcbbs.module.message.adapter.PrivateChatAdapter
import com.scatl.uestcbbs.module.message.presenter.PrivateChatPresenter
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.ImageUtil
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.ColorUtil
import com.scatl.util.ScreenUtil
import com.scatl.widget.emotion.IEmotionEventListener
import org.litepal.LitePal
import java.io.File

/**
 * Created by sca_tl at 2023/3/29 15:35
 */
class PrivateChatActivity: BaseVBActivity<PrivateChatPresenter, PrivateChatView, ActivityPrivateChatBinding>(), PrivateChatView,
    IEmotionEventListener {

    private lateinit var privateChatAdapter: PrivateChatAdapter
    private var hisName = ""
    private var hisUid = Int.MAX_VALUE
    private var tmpContent = ""

    private val countDownTimer = object : CountDownTimer(15000, 1000) {

        override fun onTick(l: Long) {
            mBinding.edittext.isEnabled = false
            mBinding.edittext.hint = "${l / 1000}秒后可再次发消息"
        }

        override fun onFinish() {
            enableInput()
            if (tmpContent.isNotEmpty()) {
                mBinding.edittext.setText(tmpContent)
            }
        }
    }

    override fun getViewBinding() = ActivityPrivateChatBinding.inflate(layoutInflater)

    override fun initPresenter() = PrivateChatPresenter()

    override fun getContext() = this

    override fun getIntent(intent: Intent?) {
        hisName = intent?.getStringExtra(Constant.IntentKey.USER_NAME) ?: ""
        hisUid = intent?.getIntExtra(Constant.IntentKey.USER_ID, Int.MAX_VALUE) ?: Int.MAX_VALUE
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)
        mBinding.toolbar.title = hisName
        mBinding.edittext.background = GradientDrawable().apply {
            cornerRadius = ScreenUtil.dip2pxF(getContext(), 20f)
            setColor(ColorUtil.getAttrColor(getContext(), R.attr.colorSurfaceVariant))
        }

        privateChatAdapter = PrivateChatAdapter(R.layout.item_private_chat)
        privateChatAdapter.setHasStableIds(true)
        mBinding.recyclerView.apply {
            adapter = privateChatAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_scale_in)
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = ScreenUtil.dip2px(context, 20f)
                    outRect.left = ScreenUtil.dip2px(context, 10f)
                    outRect.right = ScreenUtil.dip2px(context, 10f)
                }
            })
        }

        bindClickEvent(mBinding.addPhotoBtn, mBinding.addEmotionBtn, mBinding.sendMsgBtn, mBinding.edittext)
        mBinding.emotionLayout.eventListener = this

        initDraft()

        mPresenter?.getPrivateMsg(hisUid)
        mPresenter?.getUserSpace(hisUid)
    }

    private fun initDraft() {
        val draft = LitePal
            .where("hostUid = ? and chatUid = ?", SharePrefUtil.getUid(getContext()).toString(), hisUid.toString())
            .find(PrivateChatDraftBean::class.java)
        if (draft.isNotEmpty()) {
            mBinding.edittext.setText(draft[0].content)
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            mBinding.addPhotoBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .isCamera(true)
                    .isGif(true)
                    .showCropFrame(false)
                    .hideBottomControls(false)
                    .theme(com.luck.picture.lib.R.style.picture_WeChat_style)
                    .maxSelectNum(1)
                    .isEnableCrop(false)
                    .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                    .forResult(PictureConfig.CHOOSE_REQUEST)
            }
            mBinding.addEmotionBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                if (mBinding.emotionLayout.visibility == View.GONE) {
                    mBinding.smoothInputLayout.showInputPane(true)
                    mBinding.emotionLayout.visibility = View.VISIBLE
                } else {
                    mBinding.smoothInputLayout.showKeyboard()
                }
            }
            mBinding.edittext -> {
                mBinding.smoothInputLayout.showKeyboard()
            }
            mBinding.sendMsgBtn -> {
                disableInput()
                mPresenter?.sendPrivateMsg(mBinding.edittext.text.toString(), "text", hisUid)
            }
        }
    }

    override fun setOnItemClickListener() {
        privateChatAdapter.setOnItemChildClickListener { adapter, view, position ->
            when(view.id) {
                R.id.his_img_content, R.id.mine_img_content -> {
                    val urls = ArrayList<String>()
                    urls.add(privateChatAdapter.data[position].content)
                    ImageUtil.showImages(this, urls, 0)
                }
                R.id.his_icon, R.id.mine_icon -> {
                    val intent = Intent(getContext(), UserDetailActivity::class.java).apply {
                        putExtra(Constant.IntentKey.USER_ID, privateChatAdapter.data[position].sender)
                    }
                    startActivity(intent)
                }
            }
        }

        privateChatAdapter.setOnItemLongClickListener { adapter, view, position ->
            mPresenter?.showDeletePrivateMsgDialog(privateChatAdapter.data[position].mid, hisUid, position)
            true
        }
    }

    override fun onGetPrivateListSuccess(privateChatBean: PrivateChatBean) {
        mBinding.recyclerView.scheduleLayoutAnimation()
        privateChatAdapter.setNewData(privateChatBean.body.pmList[0].msgList)
    }

    override fun onGetPrivateListError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onSendPrivateChatMsgSuccess(sendPrivateMsgResultBean: SendPrivateMsgResultBean, content: String?, type: String) {
        disableInput()
        tmpContent = if ("image" == type) {
            mBinding.edittext.text.toString()
        } else {
            ""
        }
        mBinding.edittext.setText("")
        countDownTimer.cancel()
        countDownTimer.start()

        privateChatAdapter.insertMsg(this, content, type)
        mBinding.recyclerView.scrollToPosition(privateChatAdapter.data.size - 1)
        showToast(sendPrivateMsgResultBean.head.errInfo, ToastType.TYPE_SUCCESS)
    }

    override fun onSendPrivateChatMsgError(msg: String?) {
        enableInput()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onCompressImageSuccess(compressedFiles: List<File>) {
        disableInput()
        mPresenter?.uploadImages(compressedFiles, "pm", "image")
    }

    override fun onCompressImageFail(msg: String?) {
        enableInput()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onUploadSuccess(uploadResultBean: UploadResultBean) {
        disableInput()
        mPresenter?.sendPrivateMsg(uploadResultBean.body.attachment.getOrNull(0)?.urlName, "image", hisUid)
    }

    override fun onUploadError(msg: String?) {
        enableInput()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onDeleteSinglePmSuccess(msg: String?, position: Int) {
        privateChatAdapter.deleteMsg(position)
        showToast(msg, ToastType.TYPE_SUCCESS)
    }

    override fun onDeleteSinglePmError(msg: String?) {
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onGetUserSpaceSuccess(isOnline: Boolean) {
        mBinding.onlineStatus.backgroundTintList =
            if (isOnline) {
                ColorStateList.valueOf(Color.parseColor("#FF049E3B"))
            } else {
                ColorStateList.valueOf(ColorUtil.getAttrColor(this, R.attr.colorOutline))
            }
    }

    override fun onGetUserSpaceError(msg: String?) {

    }

    private fun disableInput() {
        mBinding.edittext.isEnabled = false
        mBinding.edittext.hint = "消息发送中..."
        mBinding.addEmotionBtn.isEnabled = false
        mBinding.addPhotoBtn.isEnabled = false
        mBinding.sendMsgBtn.isEnabled = false
    }

    private fun enableInput() {
        mBinding.edittext.isEnabled = true
        mBinding.edittext.hint = "请在此输入消息内容~"
        mBinding.addEmotionBtn.isEnabled = true
        mBinding.addPhotoBtn.isEnabled = true
        mBinding.sendMsgBtn.isEnabled = true
    }

    override fun onEmotionClick(path: String?) {
        if (!mBinding.edittext.isEnabled) {
            showToast("请稍候...", ToastType.TYPE_NORMAL)
            return
        }
        path?.let {
            mBinding.edittext.insertEmotion(path)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            val files: MutableList<String> = java.util.ArrayList()
            for (i in selectList.indices) {
                files.add(selectList[i].realPath)
            }
            mPresenter?.checkBeforeSendImage(files)
        }
    }

    override fun onStop() {
        saveDraft()
        super.onStop()
    }

    private fun saveDraft() {
        val draft = PrivateChatDraftBean().apply {
            hostUid = SharePrefUtil.getUid(getContext())
            chatUid = hisUid
            content = mBinding.edittext.text.toString()
        }
        draft.saveOrUpdate("hostUid = ? and chatUid = ?", SharePrefUtil.getUid(getContext()).toString(), hisUid.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}