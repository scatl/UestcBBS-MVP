package com.scatl.uestcbbs.manager

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.scatl.uestcbbs.entity.QuestionBean
import com.scatl.util.FileUtil
import kotlin.concurrent.thread

object DayQuestionManager {

    @JvmStatic
    var list = mutableListOf<QuestionBean>()
        private set

    @JvmStatic
    fun init(context: Context) {
        thread {
            val data = FileUtil.readAssetFileToString(context, "day_question.json")
            try {
                this.list = JSONObject.parseArray(data, QuestionBean::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun getAnswer(question: String?): String? {
        this.list.forEach {
            if (it.question == question) {
                return it.answer
            }
        }
        return ""
    }
}