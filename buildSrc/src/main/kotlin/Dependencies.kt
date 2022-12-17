/**
 * created by sca_tl at 2022/4/20 19:23
 */
object Dependencies {
    const val appcompat         =       "androidx.appcompat:appcompat:${Version.appcompat}"
    const val material          =       "com.google.android.material:material:${Version.material}"
    const val ktx               =       "androidx.core:core-ktx:${Version.ktx}"
    const val constraint        =       "androidx.constraintlayout:constraintlayout:${Version.constraint}"
    const val preference        =       "androidx.preference:preference-ktx:${Version.preference}"

    const val flowlayout        =       "com.hyman:flowlayout-lib:${Version.flowlayout}"
    const val statusBar         =       "com.jaeger.statusbarutil:library:${Version.statusbarutil}"
    const val magicindicator    =       "com.github.hackware1993:MagicIndicator:${Version.MagicIndicator}"

    //******* refresh ******//
    const val refreshlayout     =       "com.scwang.smartrefresh:SmartRefreshLayout:1.1.0-andx-12"
    const val refreshheader     =       "com.scwang.smartrefresh:SmartRefreshHeader:1.1.0-andx-12"

    //******* image ******//
    const val glide             =       "com.github.bumptech.glide:glide:${Version.glide}"
    const val luban             =       "top.zibin:Luban:${Version.luban}"
    const val banner            =       "com.youth.banner:banner:${Version.youthbanner}"
    const val lottie            =       "com.airbnb.android:lottie:${Version.lottie}"

    //******* network ******//
    const val retrofit2         =       "com.squareup.retrofit2:retrofit:${Version.retrofit2}"
    const val okhttp            =       "com.squareup.okhttp3:okhttp:${Version.okhttp}"
    const val converter_gson    =       "com.squareup.retrofit2:converter-gson:2.7.1"
    const val converter_scalars =       "com.squareup.retrofit2:converter-scalars:2.7.1"
    const val adapter_rxjava2   =       "com.squareup.retrofit2:adapter-rxjava2:2.7.1"

    //******* data ******//
    const val litepal           =       "org.litepal.guolindev:core:${Version.litpal}"
    const val fastjson          =       "com.alibaba:fastjson:${Version.fastjson}"
    const val eventbus          =       "org.greenrobot:eventbus:${Version.eventbus}"
    const val jsoup             =       "org.jsoup:jsoup:${Version.jsoup}"

    const val bugly             =       "com.tencent.bugly:crashreport:${Version.bugly}"

    const val rxjava2           =       "io.reactivex.rxjava2:rxjava:${Version.rxjava2}"
    const val rxandroid         =       "io.reactivex.rxjava2:rxandroid:${Version.rxandroid}"

    const val junit             =       "junit:junit:${Version.junit}"
    const val ext_junit         =       "androidx.test.ext:junit:${Version.ext_junit}"
    const val espresso_core     =       "androidx.test.espresso:espresso-core:${Version.espresso_core}"
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
    const val BigImageViewPager =       "androidx-5.0.4"
    const val MagicIndicator    =       "1.7.0"
    const val smartrefresh      =       "1.1.0-andx-12"
    const val retrofit2         =       "2.7.1"
    const val rxjava2           =       "2.2.17"
    const val rxandroid         =       "2.1.1"
    const val rxpermissions     =       "0.10.2"
    const val brvah             =       "2.9.50"
    const val agentweb          =       "v5.0.0-alpha.1-androidx"
    const val jsoup             =       "1.13.1"
    const val xstream           =       "1.4.12"
    const val bga               =       "2.0.2@aar"
    const val PictureSelector   =       "v2.6.0"
    const val GridPager         =       "v3.6.0"
    const val smoothinputlayout =       "1.1.2"
    const val MarqueeView       =       "1.4.1"
    const val ShadowLayout      =       "3.2.0"
    const val viewpager_bottomsheet  =  "1.0.0"
    const val reveallayout      =       "0.5.3"
    const val Toasty            =       "1.5.2"
    const val dkplayer          =       "3.3.7"
    const val lottie            =       "3.4.0"
    const val bugly             =       "3.1.0"
    const val junit             =       "4.13.2"
    const val ext_junit         =       "1.1.3"
    const val espresso_core     =       "3.4.0"
}