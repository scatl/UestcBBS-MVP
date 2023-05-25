package com.scatl.util

object RegexUtil {

    @JvmStatic
    fun matchUrl(s: String?): List<MatchResult> {

        val result = mutableListOf<MatchResult>()

        if ((s?.length?:0) < 4) {
            return result
        }

        val urlPattern = "(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]".toRegex()

        var slow = 0
        var fast = 4
        var insertTimes = 0

        val tmp = StringBuilder(s!!)

        try {
            while (fast < s.length) {
                val c = s.substring(slow, fast)
                if (c == "http") {
                    tmp.insert(slow + insertTimes, " ")
                    insertTimes += 1
                }
                slow += 1
                fast += 1
            }

            urlPattern.findAll(tmp).forEachIndexed { index, matchResult ->
                result.add(MatchResult(
                    matchResult.value,
                    IntRange(matchResult.range.first - (1 + index),
                        matchResult.range.first - (1 + index) + matchResult.value.length))
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    data class MatchResult(
        var value: String,
        var range: IntRange
    )

}