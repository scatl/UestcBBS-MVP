/**
 * created by sca_tl at 2022/4/20 19:23
 */
object Dependencies {
    const val material          =       "com.google.android.material:material:${Version.material}"
    const val junit             =       "junit:junit:${Version.junit}"
    const val extJunit          =       "androidx.test.ext:junit:${Version.ext_junit}"
    const val espressoCore      =       "androidx.test.espresso:espresso-core:${Version.espresso_core}"

    const val flowLayout        =       "com.hyman:flowlayout-lib:${Version.flowlayout}"
    const val statusBar         =       "com.jaeger.statusbarutil:library:${Version.statusbarutil}"
    const val magicIndicator    =       "com.github.hackware1993:MagicIndicator:${Version.MagicIndicator}"
    const val glide             =       "com.github.bumptech.glide:glide:${Version.glide}"
    const val luban             =       "top.zibin:Luban:${Version.luban}"
    const val banner            =       "com.youth.banner:banner:${Version.youthbanner}"
    const val lottie            =       "com.airbnb.android:lottie:${Version.lottie}"
    const val okhttp            =       "com.squareup.okhttp3:okhttp:${Version.okhttp}"
    const val litepal           =       "org.litepal.guolindev:core:${Version.litpal}"
    const val fastjson          =       "com.alibaba:fastjson:${Version.fastjson}"
    const val eventbus          =       "org.greenrobot:eventbus:${Version.eventbus}"
    const val jsoup             =       "org.jsoup:jsoup:${Version.jsoup}"
    const val bugly             =       "com.tencent.bugly:crashreport:${Version.bugly}"
    const val rxpermissions     =       "com.github.tbruyelle:rxpermissions:${Version.rxpermissions}"
    const val brvah             =       "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Version.brvah}"
    const val smoothinputlayout =       "am.widget:smoothinputlayout:${Version.smoothinputlayout}"
    const val pictureselector   =       "com.github.LuckSiege.PictureSelector:picture_library:${Version.PictureSelector}"
    const val gridpager         =       "com.github.mtjsoft:GridPager:${Version.GridPager}"
    const val marqueeView       =       "com.sunfusheng:MarqueeView:${Version.MarqueeView}"
    const val shadowLayout      =       "com.github.lihangleo2:ShadowLayout:${Version.ShadowLayout}"
    const val toasty            =       "com.github.GrenderG:Toasty:${Version.Toasty}"
    const val imageViewer       =       "com.github.SherlockGougou:BigImageViewPager:${Version.BigImageViewPager}"
    const val coil              =       "io.coil-kt:coil:${Version.coil}"
    const val slidingUpPanel    =       "com.github.hannesa2:AndroidSlidingUpPanel:${Version.slidingUpPanel}"

    @JvmStatic
    val rxJava = mutableListOf<String>(
        "io.reactivex.rxjava2:rxjava:${Version.rxjava2}",
        "io.reactivex.rxjava2:rxandroid:${Version.rxandroid}"
    )

    @JvmStatic
    val androidX = mutableListOf<String>(
        "androidx.appcompat:appcompat:${Version.appcompat}",
        "androidx.core:core-ktx:${Version.ktx}",
        "androidx.constraintlayout:constraintlayout:${Version.constraint}",
        "androidx.preference:preference-ktx:${Version.preference}",
    )

    @JvmStatic
    val dkplayer = mutableListOf<String>(
        "xyz.doikki.android.dkplayer:dkplayer-java:${Version.dkplayer}",
        "xyz.doikki.android.dkplayer:player-exo:${Version.dkplayer}",
        "xyz.doikki.android.dkplayer:dkplayer-ui:${Version.dkplayer}"
    )

    @JvmStatic
    val retrofit2 = mutableListOf<String>(
        "com.squareup.retrofit2:retrofit:${Version.retrofit2}",
        "com.squareup.retrofit2:converter-gson:${Version.retrofit2}",
        "com.squareup.retrofit2:converter-scalars:${Version.retrofit2}",
        "com.squareup.retrofit2:adapter-rxjava2:${Version.retrofit2}"
    )

    @JvmStatic
    val agentWeb = mutableListOf<String>(
        "com.github.Justson.AgentWeb:agentweb-core:${Version.agentweb}",
        "com.github.Justson.AgentWeb:agentweb-filechooser:${Version.agentweb}"
    )

    @JvmStatic
    val refersh = mutableListOf<String>(
        "com.scwang.smartrefresh:SmartRefreshLayout:${Version.smartrefresh}",
        "com.scwang.smartrefresh:SmartRefreshHeader:${Version.smartrefresh}"
    )

    @JvmStatic
    val immersionbar = mutableListOf<String>(
        "com.geyifeng.immersionbar:immersionbar:${Version.immersionbar}",
        "com.geyifeng.immersionbar:immersionbar-ktx:${Version.immersionbar}"
    )

//    @JvmStatic
//    val pictureSelector = mutableListOf<String>(
//        "io.github.lucksiege:pictureselector:${Version.PictureSelector}",
//        "io.github.lucksiege:compress:${Version.PictureSelector}",
//        "io.github.lucksiege:ucrop:${Version.PictureSelector}",
//        "io.github.lucksiege:camerax:${Version.PictureSelector}",
//    )
}

private object Version {
    const val appcompat         =       "1.6.0-alpha05"
    const val ktx               =       "1.9.0-alpha05"
    const val preference        =       "1.2.0-beta01"
    const val material          =       "1.7.0-alpha03"
    const val constraint        =       "2.2.0-alpha03"
    const val glide             =       "4.10.0"
    const val fastjson          =       "1.2.61"
    const val okhttp            =       "4.9.2"
    const val flowlayout        =       "1.1.2"
    const val luban             =       "1.1.8"
    const val eventbus          =       "3.1.1"
    const val youthbanner       =       "1.4.10"
    const val statusbarutil     =       "1.5.1"
    const val litpal            =       "3.2.3"
    const val coil              =       "2.1.0"
    const val BigImageViewPager =       "androidx-5.0.4"
    const val MagicIndicator    =       "1.7.0"
    const val immersionbar      =       "3.2.2"
    const val smartrefresh      =       "1.1.0-andx-12"
    const val retrofit2         =       "2.7.1"
    const val rxjava2           =       "2.2.17"
    const val rxandroid         =       "2.1.1"
    const val rxpermissions     =       "0.10.2"
    const val brvah             =       "2.9.50"
    const val agentweb          =       "v5.0.0-alpha.1-androidx"
    const val jsoup             =       "1.13.1"
    const val PictureSelector   =       "v2.6.0"
    const val GridPager         =       "v3.6.0"
    const val smoothinputlayout =       "1.1.2"
    const val MarqueeView       =       "1.4.1"
    const val ShadowLayout      =       "3.2.0"
    const val Toasty            =       "1.5.2"
    const val dkplayer          =       "3.3.7"
    const val lottie            =       "3.4.0"
    const val slidingUpPanel    =       "4.4.1"
    const val bugly             =       "3.1.0"
    const val junit             =       "4.13.2"
    const val ext_junit         =       "1.1.3"
    const val espresso_core     =       "3.4.0"
}