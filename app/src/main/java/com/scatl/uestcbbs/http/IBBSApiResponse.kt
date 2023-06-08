package com.scatl.uestcbbs.http

/**
 * Created by sca_tl at 2023/6/8 17:11
 */
interface IBBSApiResponse<T> {
    fun success(): Boolean
    fun message(): String?
}