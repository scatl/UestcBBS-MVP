package com.scatl.uestcbbs.module.post.view

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jaeger.library.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ToastType
import com.scatl.uestcbbs.base.BaseEvent
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityCreateCommentBinding
import com.scatl.uestcbbs.entity.AttachmentBean
import com.scatl.uestcbbs.entity.ReplyDraftBean
import com.scatl.uestcbbs.entity.SendCommentSuccessEntity
import com.scatl.uestcbbs.entity.SendPostBean
import com.scatl.uestcbbs.entity.UploadResultBean
import com.scatl.uestcbbs.helper.glidehelper.GlideEngineForPictureSelector
import com.scatl.uestcbbs.module.account.view.SwitchAccountView
import com.scatl.uestcbbs.module.post.adapter.AttachmentAdapter
import com.scatl.uestcbbs.module.post.adapter.CreateCommentImageAdapter
import com.scatl.uestcbbs.module.post.presenter.CreateCommentPresenter
import com.scatl.uestcbbs.module.user.view.AtUserListActivity
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.load
import com.scatl.uestcbbs.util.showToast
import com.scatl.util.SystemUtil
import com.scatl.widget.dialog.BlurAlertDialogBuilder
import com.scatl.widget.emotion.IEmotionEventListener
import org.greenrobot.eventbus.EventBus
import org.litepal.LitePal
import java.io.File

/**
 * Created by sca_tl at 2023/4/17 9:36
 */
class CreateCommentActivity: BaseVBActivity<CreateCommentPresenter, CreateCommentView, ActivityCreateCommentBinding>(), CreateCommentView, IEmotionEventListener {

    private var boardId = Int.MAX_VALUE
    private var topicId = Int.MAX_VALUE
    private var quoteId = Int.MAX_VALUE
    private var quoteUserName: String? = null
    private var isQuote = false
    private var replyPosition = 0
    private var sendSuccess = false

    private lateinit var imageAdapter: CreateCommentImageAdapter
    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var progressDialog: ProgressDialog

    /**
     *当前选中用来回复的用户帐户ID（用于多帐户情形下）
     */
    private var currentReplyUid = 0

    /**
     * 选择的附件 uri和上传后获取的附件id
     */
    private val attachments: MutableMap<Uri, Int> = LinkedHashMap()

    companion object {
        const val ACTION_ADD_PHOTO = 12
    }

    override fun getViewBinding() = ActivityCreateCommentBinding.inflate(layoutInflater)

    override fun initPresenter()= CreateCommentPresenter()

    override fun getContext() = this

    override fun getIntent(intent: Intent?) {
        intent?.let {
            boardId = it.getIntExtra(Constant.IntentKey.BOARD_ID, Integer.MAX_VALUE)
            topicId = it.getIntExtra(Constant.IntentKey.TOPIC_ID, Integer.MAX_VALUE)
            quoteId = it.getIntExtra(Constant.IntentKey.QUOTE_ID, Integer.MAX_VALUE)
            isQuote = it.getBooleanExtra(Constant.IntentKey.IS_QUOTE, false)
            quoteUserName = it.getStringExtra(Constant.IntentKey.USER_NAME)
            replyPosition = it.getIntExtra(Constant.IntentKey.POSITION, -1)
        }
    }

    override fun initView(theftProof: Boolean) {
        super.initView(true)

        bindClickEvent(
            mBinding.atBtn, mBinding.imageBtn, mBinding.emotionBtn,
            mBinding.replyBtn, mBinding.cancelBtn, mBinding.sendBtn,
            mBinding.attachmentBtn, mBinding.edittext, mBinding.accountBtn
        )

        currentReplyUid = SharePrefUtil.getUid(this)

        if (SharePrefUtil.isLogin(this)) {
            mBinding.accountBtn.visibility = View.VISIBLE
            mBinding.accountBtn.load(SharePrefUtil.getAvatar(this))
        } else {
            mBinding.accountBtn.visibility = View.GONE
        }

        mBinding.smoothInputLayout.visibility = View.VISIBLE
        mBinding.smoothInputLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.csu_activity_appear))
        mBinding.emotionLayout.eventListener = this

        mBinding.anonymousCheckbox.visibility = if (boardId == Constant.MIYU_BOARD_ID) View.VISIBLE else View.GONE
        mBinding.edittext.hint = "回复：$quoteUserName"

        imageAdapter = CreateCommentImageAdapter(R.layout.item_post_create_comment_image)
        mBinding.imageRv.adapter = imageAdapter

        attachmentAdapter = AttachmentAdapter(R.layout.item_attachment)
        mBinding.attachmentRv.adapter = attachmentAdapter

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("发送评论")
        progressDialog.setCancelable(false)

        initDraft()
        CommonUtil.showSoftKeyboard(this, mBinding.edittext, 0)
    }

    private fun initDraft() {
        val draft = LitePal
            .where("reply_id = ?", (if (isQuote) quoteId else topicId).toString())
            .find(ReplyDraftBean::class.java)

        if (!draft.isNullOrEmpty()) {
            mBinding.edittext.apply {
                setText(draft[0].content)
                setSelection(length())
            }

            try {
                val images = mutableListOf<String>()
                val imageArray = JSON.parseArray(draft[0].images)
                for (i in 0 until imageArray.size) {
                    images.add(imageArray[i] as String)
                }
                imageAdapter.setNewData(images)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setOnItemClickListener() {
        imageAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.delete_btn) {
                imageAdapter.delete(position)
            }
        }

        attachmentAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.item_attachment_delete_file) {
                attachments.remove(attachmentAdapter.data[position].uri)
                attachmentAdapter.delete(position)
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            mBinding.cancelBtn -> {
                exit()
            }

            mBinding.sendBtn, mBinding.replyBtn -> {
                progressDialog.show()
                onCheckBlack(false)
                //todo 被楼主拉黑后，也无法回复下面的其它帖子，暂时屏蔽黑名单校验
                //mPresenter?.checkBlack(topicId, boardId, quoteId)
            }

            mBinding.imageBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                mPresenter?.requestPermission(this, ACTION_ADD_PHOTO, Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            mBinding.attachmentBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                attachmentLauncher.launch("*/*")
            }

            mBinding.atBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                val intent = Intent(this, AtUserListActivity::class.java)
                atUserLauncher.launch(intent)
            }

            mBinding.emotionBtn -> {
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

            mBinding.accountBtn -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                val switchAccountView = SwitchAccountView(this)
                switchAccountView.setCurrentSelect(currentReplyUid)
                val alertDialog = BlurAlertDialogBuilder(this)
                    .setView(switchAccountView)
                    .show()
                switchAccountView.setOnItemClickListener { selectUid: Int ->
                    alertDialog.dismiss()
                    currentReplyUid = selectUid
                    mBinding.accountBtn.load(Constant.USER_AVATAR_URL + currentReplyUid)
                }
            }
        }
    }

    override fun onCheckBlack(blacked: Boolean) {
        if (blacked) {
            progressDialog.dismiss()
            showToast("对不起，您在该用户的黑名单中，不能进行此操作！", ToastType.TYPE_ERROR)
            return
        }

        if (imageAdapter.data.size == 0) {
            progressDialog.setMessage("正在发表，请稍候...")
            mPresenter?.sendComment(boardId, topicId, quoteId,
                isQuote, mBinding.anonymousCheckbox.isChecked,
                mBinding.edittext.text.toString(), null, null, attachments, currentReplyUid
            )
        } else {
            progressDialog.setMessage("正在压缩图片，请稍候...")
            mPresenter?.compressImage(imageAdapter.data)
        }
    }

    override fun onSendCommentSuccess(sendPostBean: SendPostBean) {
        progressDialog.dismiss()
        sendSuccess = true

        val successEntity = SendCommentSuccessEntity().also {
            it.replyPosition = replyPosition
            it.replyId = currentReplyUid
        }
        EventBus.getDefault().post(BaseEvent(BaseEvent.EventCode.SEND_COMMENT_SUCCESS, successEntity))

        exit()
    }

    override fun onSendCommentError(msg: String?) {
        progressDialog.dismiss()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onUploadSuccess(uploadResultBean: UploadResultBean) {
        progressDialog.setMessage("正在发表，请稍候...")

        val imgIds: MutableList<Int> = ArrayList()
        val imgUrls: MutableList<String> = ArrayList()

        uploadResultBean.body?.attachment?.forEach {
            imgIds.add(it.id)
            imgUrls.add(it.urlName)
        }

        if (imgIds.size != imageAdapter.data.size) {
            onUploadError("接口返回的图片数量与实际上传的数量不一致，可能是河畔不支持该格式的图片")
            return
        }

        mPresenter?.sendComment(
            boardId, topicId, quoteId, isQuote, mBinding.anonymousCheckbox.isChecked,
            mBinding.edittext.text.toString(), imgUrls, imgIds, attachments, currentReplyUid
        )
    }

    override fun onUploadError(msg: String?) {
        progressDialog.dismiss()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onCompressImageSuccess(compressedFiles: List<File>) {
        progressDialog.show()
        progressDialog.setMessage("正在上传图片，请稍候...")

        mPresenter?.uploadImg(compressedFiles, "forum", "image")
    }

    override fun onCompressImageFail(msg: String?) {
        progressDialog.dismiss()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun onPermissionGranted(action: Int) {
        if (action == ACTION_ADD_PHOTO) {
//            Gallery.INSTANCE.with(this).show()
            PictureSelector
                .create(this)
                .openGallery(PictureMimeType.ofImage())
                .isCamera(true)
                .isGif(true)
                .showCropFrame(false)
                .hideBottomControls(false)
                .maxSelectNum(20)
                .isEnableCrop(false)
                .imageEngine(GlideEngineForPictureSelector.createGlideEngine())
                .forResult(action)
        }
    }

    override fun onPermissionRefused() {
        showToast(getString(R.string.permission_request), ToastType.TYPE_WARNING)
    }

    override fun onPermissionRefusedWithNoMoreRequest() {
        showToast(getString(R.string.permission_refuse), ToastType.TYPE_ERROR)
        SystemUtil.goToAppDetailSetting(getContext())
    }

    override fun onStartUploadAttachment() {
        progressDialog.show()
        progressDialog.setMessage("正在上传附件，请稍候...")
    }

    override fun onUploadAttachmentSuccess(attachmentBean: AttachmentBean, msg: String?) {
        progressDialog.dismiss()
        attachments[attachmentBean.uri] = attachmentBean.aid
        attachmentAdapter.addData(attachmentBean)
        mBinding.smoothInputLayout.showKeyboard()
    }

    override fun onUploadAttachmentError(msg: String?) {
        progressDialog.dismiss()
        showToast(msg, ToastType.TYPE_ERROR)
    }

    override fun setStatusBar() {
        StatusBarUtil.setTranslucent(this, 112)
    }

    private fun exit() {
        mBinding.smoothInputLayout.closeKeyboard(true)
        mBinding.smoothInputLayout.visibility = View.INVISIBLE
        val animation = AnimationUtils.loadAnimation(this, R.anim.csu_activity_dismiss).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation) {
                    finish()
                    overridePendingTransition(R.anim.csu_alpha_in, R.anim.csu_alpha_out)
                }

                override fun onAnimationStart(animation: Animation) { }
                override fun onAnimationRepeat(animation: Animation) { }
            })
        }
        mBinding.smoothInputLayout.startAnimation(animation)
    }

    override fun onBackPressed() {
        exit()
    }

    override fun registerEventBus(): Boolean {
        return true
    }

    override fun onEmotionClick(path: String?) {
        path?.let {
            mBinding.edittext.insertEmotion(it)
        }
    }

    override fun receiveEventBusMsg(baseEvent: BaseEvent<Any>) {
        if (baseEvent.eventCode == BaseEvent.EventCode.AT_USER) {
            mBinding.smoothInputLayout.showKeyboard()
            mBinding.edittext.text?.append(baseEvent.eventData as String)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_ADD_PHOTO && resultCode == RESULT_OK && data != null) {
            val selectList = PictureSelector.obtainMultipleResult(data)
            for (i in selectList.indices) {
                imageAdapter.addData(selectList[i].realPath)
            }
            mBinding.smoothInputLayout.showKeyboard()
            mBinding.imageRv.smoothScrollToPosition(imageAdapter.data.size - 1)
        }
    }

    override fun onStop() {
        saveOrDeleteDraft()
        super.onStop()
    }

    private fun saveOrDeleteDraft() {
        if (sendSuccess) {
            if (SharePrefUtil.clearDraftAfterPostSuccess(this)) {
                deleteDraft()
            } else {
                saveDraft()
            }
        } else {
            if (mBinding.edittext.text?.isNotEmpty() == true || imageAdapter.data.size != 0) {
                saveDraft()
            } else {
                deleteDraft()
            }
        }
    }

    private fun saveDraft() {
        if (mBinding.edittext.text?.isNotEmpty() == true || imageAdapter.data.size != 0) {
            val replyDraftBean = ReplyDraftBean().apply {
                reply_id = if (isQuote) quoteId else topicId
                content = mBinding.edittext.text.toString()
            }

            val imageData = JSONArray()
            imageAdapter.data.forEach{ s ->
                imageData.add(s)
            }
            replyDraftBean.images = imageData.toJSONString()
            replyDraftBean.saveOrUpdate("reply_id = ?", replyDraftBean.reply_id.toString())
        }
    }

    private fun deleteDraft() {
        LitePal.deleteAll(ReplyDraftBean::class.java, "reply_id = ?",
            if (isQuote) quoteId.toString() else topicId.toString())
    }

    private val atUserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AtUserListActivity.AT_USER_RESULT && it.data != null) {
            mBinding.edittext.text?.append(it.data!!.getStringExtra(Constant.IntentKey.AT_USER))
            mBinding.smoothInputLayout.showKeyboard()
        }
    }

    private val attachmentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (!attachments.containsKey(it)) {
            mPresenter?.readyUploadAttachment(this, it, boardId)
        } else {
            showToast("已添加该文件，无需重复添加", ToastType.TYPE_NORMAL)
        }
    }
}