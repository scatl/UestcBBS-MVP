package com.scatl.util

object TextUtil {

    @JvmStatic
    fun unicode2String(unicode: String): String {
        val string = StringBuilder()
        try {
            val tmp: List<String> = unicode.split(";&#","&#",";")
            for (i in tmp.indices) {
                if (tmp[i].matches("\\d{5}".toRegex())) {
                    string.append(tmp[i].toInt().toChar())
                } else {
                    string.append(tmp[i])
                }
            }
        } catch (e: Exception) {
            string.append(unicode)
        }
        return string.toString()
    }

}