package com.scatl.uestcbbs.entity

/**
 * author: sca_tl
 * date: 2022/5/11 10:56
 * description:
 */
enum class UserLevel(var levelName: String, var minScore: Int, var maxScore: Int) {

    SHUICAO("水草", Int.MIN_VALUE, 0),
    KEDOU("蝌蚪", 0, 29),
    XIAMI("虾米", 30, 99),
    HEXIE("河蟹", 100, 499),
    NIQIU("泥鳅", 500, 799),
    CAOYU("草鱼", 800, 1199),
    YONGYU("鳙鱼", 1200, 1999),
    LIYU("鲤鱼", 2000, 2999),
    NIANYU("鲶鱼", 3000, 4549),
    BAIQI("白鳍", 4500, 6999),
    HAITUN("海豚", 7000, 9999),
    SHAYU("鲨鱼", 10000, 14999),
    NIJIJING("逆戟鲸", 15000, 29999),
    CHUANQIKEDOU("传奇蝌蚪", 30000, Int.MAX_VALUE)

}