package com.scatl.widget.download

import android.content.Context
import okhttp3.internal.threadFactory
import java.util.Collections
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by sca_tl at 2023/5/31 20:32
 */
class DownloadDispatcher private constructor() {

    companion object {
        val INSTANCE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DownloadDispatcher() }
    }

    private val pendingTaskList = Collections.synchronizedMap(HashMap<String, DownloadTask>())

    private val executorService by lazy {
        ThreadPoolExecutor(
            0,
            Int.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            SynchronousQueue(),
            threadFactory("download dispatcher", false)
        )
    }

    fun add(task: DownloadTask) {
        if (!pendingTaskList.containsKey(task.mTaskTag)) {
            pendingTaskList.put(task.mTaskTag, task)
        }
    }

}