package com.scatl.uestcbbs.widget.span

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.annotation.ResetPswType
import com.scatl.uestcbbs.module.account.view.ResetPasswordFragment
import com.scatl.uestcbbs.module.board.view.SingleBoardActivity
import com.scatl.uestcbbs.module.collection.view.CollectionActivity
import com.scatl.uestcbbs.module.credit.view.CreditHistoryActivity
import com.scatl.uestcbbs.module.magic.view.MagicShopActivity
import com.scatl.uestcbbs.module.post.view.NewPostDetailActivity
import com.scatl.uestcbbs.module.post.view.ViewVoterFragment
import com.scatl.uestcbbs.module.task.view.TaskActivity
import com.scatl.uestcbbs.module.user.view.UserDetailActivity
import com.scatl.uestcbbs.module.webview.view.WebViewActivity
import com.scatl.uestcbbs.util.ColorUtil.getAttrColor
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.TimeUtil
import com.scatl.uestcbbs.util.BBSLinkUtil

/**
 * Created by sca_tl at 2023/2/13 14:14
 */
class CustomClickableSpan(): ClickableSpan() {

    private var mUrl: String? = null
    private var mContext: Context? = null
    private var mUnderLine = true
    private var mColor = 0

    constructor(mContext: Context?, url: String) : this(mContext, url, true)

    constructor(mContext: Context?, url: String, color: Int) : this(mContext, url, true) {
        this.mColor = color
    }

    constructor(mContext: Context?, url: String, underLine: Boolean) : this() {
        this.mContext = mContext
        this.mUnderLine = underLine
        this.mUrl = url.replace(" ".toRegex(), "").replace("\n".toRegex(), "")
        this.mColor = getAttrColor(mContext, R.attr.colorPrimary)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = mUnderLine
        ds.color = mColor
    }

    override fun onClick(widget: View) {
        val linkInfo = BBSLinkUtil.getLinkInfo(mUrl)

        when(linkInfo.type) {
            BBSLinkUtil.LinkInfo.LinkType.TASK -> {
                mContext?.startActivity(Intent(mContext, TaskActivity::class.java))
            }
            BBSLinkUtil.LinkInfo.LinkType.MAGIC -> {
                mContext?.startActivity(Intent(mContext, MagicShopActivity::class.java))
            }
            BBSLinkUtil.LinkInfo.LinkType.SPACE_CP -> {
                mContext?.startActivity(Intent(mContext, CreditHistoryActivity::class.java))
            }
            BBSLinkUtil.LinkInfo.LinkType.VIEW_VOTER -> {
                if (mContext is FragmentActivity) {
                    ViewVoterFragment
                        .getInstance(Bundle().apply {
                            putInt(Constant.IntentKey.TOPIC_ID, linkInfo.id)
                        })
                        .show((mContext as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                }
            }
            BBSLinkUtil.LinkInfo.LinkType.OTHER -> {
                if (SharePrefUtil.isOpenLinkByInternalBrowser(mContext) || mUrl?.contains("bbs.uestc.edu.cn") == true) {
                    val intent = Intent(mContext, WebViewActivity::class.java).apply {
                        putExtra(Constant.IntentKey.URL, mUrl)
                    }
                    mContext?.startActivity(intent)
                } else {
                    CommonUtil.openBrowser(mContext, mUrl)
                }
            }
            BBSLinkUtil.LinkInfo.LinkType.POST -> {
                val intent = Intent(mContext, WebViewActivity::class.java).apply {
                    putExtra(Constant.IntentKey.URL, mUrl)
                }
                mContext?.startActivity(intent)
            }
            BBSLinkUtil.LinkInfo.LinkType.TOPIC -> {
                val intent = Intent(mContext, NewPostDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.TOPIC_ID, linkInfo.id)
                }
                mContext?.startActivity(intent)
            }
            BBSLinkUtil.LinkInfo.LinkType.FORUM -> {
                val intent = Intent(mContext, SingleBoardActivity::class.java).apply {
                    putExtra(Constant.IntentKey.BOARD_ID, linkInfo.id)
                }
                mContext?.startActivity(intent)
            }
            BBSLinkUtil.LinkInfo.LinkType.USER_SPACE -> {
                val intent = Intent(mContext, UserDetailActivity::class.java).apply {
                    putExtra(Constant.IntentKey.USER_ID, linkInfo.id)
                }
                mContext?.startActivity(intent)
            }
            BBSLinkUtil.LinkInfo.LinkType.COLLECTION -> {
                val intent = Intent(mContext, CollectionActivity::class.java).apply {
                    putExtra(Constant.IntentKey.COLLECTION_ID, linkInfo.id)
                }
                mContext?.startActivity(intent)
            }
            BBSLinkUtil.LinkInfo.LinkType.RESET_PSW -> {
                if (mContext is FragmentActivity) {
                    ResetPasswordFragment
                        .getInstance(Bundle().apply {
                            putString(Constant.IntentKey.TYPE, ResetPswType.TYPE_RESET)
                        })
                        .show((mContext as FragmentActivity).supportFragmentManager, TimeUtil.getStringMs())
                }
            }
        }
    }
}