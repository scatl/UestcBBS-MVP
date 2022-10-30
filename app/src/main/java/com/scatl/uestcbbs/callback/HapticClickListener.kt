package com.scatl.uestcbbs.callback

import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Created by sca_tl on 2022/9/23 13:58
 */
abstract class HapticClickListener: View.OnClickListener {
    override fun onClick(v: View) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        onViewClick(v)
    }

    abstract fun onViewClick(v: View)
}