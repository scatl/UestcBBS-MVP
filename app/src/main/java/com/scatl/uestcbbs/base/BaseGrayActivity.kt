package com.scatl.uestcbbs.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.widget.GrayFrameLayout

/**
 * Created by sca_tl at 2023/5/26 14:55
 */
open class BaseGrayActivity: AppCompatActivity() {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if ("FrameLayout" == name) {
            for (i in 0 until attrs.attributeCount) {
                if (attrs.getAttributeName(i) == "id") {
                    val id = attrs.getAttributeValue(i)?.substring(1)?.toInt() ?: -1
                    val idVal = resources.getResourceName(id)
                    if ("android:id/content" == idVal) {
                        return GrayFrameLayout(SharePrefUtil.getGraySaturation(context), context, attrs)
                    }
                }
            }
        }
        return super.onCreateView(name, context, attrs)
    }

}