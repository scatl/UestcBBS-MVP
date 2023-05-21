package com.scatl.uestcbbs.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.scatl.uestcbbs.services.DayQuestionService

/**
 * Created by sca_tl at 2023/5/17 19:45
 */
class RetryDayQuestionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, DayQuestionService::class.java))
    }
}