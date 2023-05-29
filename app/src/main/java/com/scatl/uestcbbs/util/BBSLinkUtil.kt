package com.scatl.uestcbbs.util

import android.net.Uri
import com.scatl.util.NumberUtil

object BBSLinkUtil {

    @JvmStatic
    fun getLinkInfo(url: String?): LinkInfo {
        val linkInfo = LinkInfo()
        val realUrl = "https://bbs.uestc.edu.cn/"
            .plus(url
                ?.replace("amp;", "")
                ?.replace("(", "")
                ?.replace(")", "")
                ?.replace("https://bbs.uestc.edu.cn/", "")
            )
        val params = getParameters(realUrl)

        when(params["mod"]) {
            "viewthread" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["tid"])
                    type = LinkInfo.LinkType.TOPIC
                }
            }
            "space" -> {
                val uid = params["uid"]
                val `do` = params["do"]
                val view = params["view"]
                if (uid.isNullOrEmpty()) {
                    if ("friend" == `do` && "blacklist" == view) {
                        linkInfo.apply {
                            type = LinkInfo.LinkType.BLACK_LIST
                        }
                    } else {
                        linkInfo.apply {
                            type = LinkInfo.LinkType.OTHER
                        }
                    }
                } else {
                    linkInfo.apply {
                        id = NumberUtil.parseInt(params["uid"])
                        type = LinkInfo.LinkType.USER_SPACE
                    }
                }
            }
            "forumdisplay" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["fid"])
                    type = LinkInfo.LinkType.FORUM
                }
            }
            "collection" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["ctid"])
                    type = LinkInfo.LinkType.COLLECTION
                }
            }
            "task" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["id"])
                    type = LinkInfo.LinkType.TASK
                }
            }
            "magic" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["id"])
                    type = LinkInfo.LinkType.MAGIC
                }
            }
            "spacecp" -> {
                linkInfo.apply {
                    id = NumberUtil.parseInt(params["id"])
                    type = LinkInfo.LinkType.SPACE_CP
                }
            }
            "redirect" -> {
                if (params["goto"] == "findpost") {
                    linkInfo.apply {
                        id = NumberUtil.parseInt(params["ptid"])
                        pid = NumberUtil.parseInt(params["pid"])
                        type = if (pid == 0) {
                            LinkInfo.LinkType.TOPIC
                        } else {
                            LinkInfo.LinkType.POST
                        }
                    }
                }
            }
            "misc" -> {
                if (params["action"] == "viewvote") {
                    linkInfo.apply {
                        id = NumberUtil.parseInt(params["tid"])
                        type = LinkInfo.LinkType.VIEW_VOTER
                    }
                }
            }
            else -> {
                if (url?.contains("bbs.stuhome.net/read.php") == true) {
                    linkInfo.apply {
                        id = NumberUtil.parseInt(params["tid"])
                        type = LinkInfo.LinkType.TOPIC
                    }
                } else if(url?.contains("plugin.php?id=rnreg:resetpassword") == true) {
                    linkInfo.apply {
                        type = LinkInfo.LinkType.RESET_PSW
                    }
                } else {
                    linkInfo.apply {
                        type = LinkInfo.LinkType.OTHER
                    }
                }
            }
        }
        return linkInfo
    }

    @JvmStatic
    fun getParameters(url: String?): HashMap<String, String?> {
        val parameters = HashMap<String, String?>()

        if (url.isNullOrBlank()) {
            return parameters
        }

        try {
            val uri = Uri.parse(url)
            uri.queryParameterNames.let {
                val iterator = it.iterator()
                while (iterator.hasNext()) {
                    val key = iterator.next() as String
                    parameters[key] = uri.getQueryParameter(key)
                }
            }
        } catch (e: Exception) {
            return parameters
        }
        return parameters
    }

    class LinkInfo {
        var id: Int = 0
        var pid: Int = 0
        var type: LinkType = LinkType.TOPIC

        enum class LinkType {
            TOPIC,
            USER_SPACE,
            FORUM,
            COLLECTION,
            TASK,
            POST,
            MAGIC,
            SPACE_CP,
            VIEW_VOTER,
            OTHER,
            RESET_PSW,
            BLACK_LIST
        }
    }
}