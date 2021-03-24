package com.scatl.uestcbbs.custom.postview;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.photoview.PhotoView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.annotation.ContentDataType;
import com.scatl.uestcbbs.custom.MarqueeTextView;
import com.scatl.uestcbbs.custom.MyLinearLayoutManger;
import com.scatl.uestcbbs.custom.SBGASortableNinePhotoLayout;
import com.scatl.uestcbbs.custom.imageview.RoundImageView;
import com.scatl.uestcbbs.entity.ContentViewBean;
import com.scatl.uestcbbs.entity.ContentViewBeanEx;
import com.scatl.uestcbbs.entity.PostDetailBean;
import com.scatl.uestcbbs.module.post.adapter.ContentViewPollAdapter;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;
import com.scatl.uestcbbs.util.AudioPlayerUtil;
import com.scatl.uestcbbs.util.Constant;
import com.scatl.uestcbbs.util.FileUtil;
import com.scatl.uestcbbs.util.ImageUtil;
import com.scatl.uestcbbs.util.SharePrefUtil;
import com.scatl.uestcbbs.util.TimeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * author: sca_tl
 * description: 0、纯文本  1、图片  5、附件  3、音频  4、链接  2、应该是视频,info是链接
 * 网页论坛只允许上传这些格式的文件：flv,mp3,mp4,zip,rar,tar,gz,xz,bz2,7z,apk,ipa,crx,pdf,caj,
 * ppt,pptx,doc,docx,xls,xlsx,txt,png,jpg,jpe,jpeg,gif
 */
public class ContentView extends RelativeLayout {

    private LayoutInflater inflater;
    private LinearLayout root_layout;
    private List<String> imagesUrl = new ArrayList<>(); //所有类型1的url
    private List<String> allImagesUrl = new ArrayList<>(); //类型1和类型5的图片类型的所有url

    private OnClickListener imageClickListener, pollBtnClickListener;
    private OnClickListener playClickListener;

    private OnImageClickListener onImageClickListener;
    private OnPollBtnClickListener onPollBtnClickListener;
    private onViewVoterClickListener onViewVoterClickListener;
//    private OnPlayClickListener onPlayClickListener;

    private final int TEXT_SIZE = 16;
    private final String TAG_TEXT_VIEW = "textView";
    private final String TAG_IMAGE_VIEW = "imageView";
    private final String TAG_ATTACHMENT_VIEW = "attachment";

    private PostDetailBean.TopicBean.PollInfoBean voteBean;
    private ContentViewPollAdapter contentViewPollAdapter;
    private int pollViewIndex;

    public ContentView(Context context) {
        this(context, null);
        init(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * author: sca_tl
     * description: 初始化
     */
    private void init(Context context) {
        inflater = LayoutInflater.from(context);

        setOnItemClick();

        root_layout = new LinearLayout(context);
        root_layout.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        root_layout.setPadding(0,15,0,15);//设置间距，防止生成图片时文字太靠边
        addView(root_layout, layoutParams);
    }


    /**
     * author: sca_tl
     * description: 清除所有的view
     */
    public void clearAllLayout(){
        root_layout.removeAllViews();
    }

    /**
     * author: sca_tl
     * description: 点击事件
     */
    public void setOnItemClick() {

        //图片点击
        imageClickListener = view -> {
            if (view instanceof RoundImageView) {
                RoundImageView roundImageView = (RoundImageView) view;
                int selected = allImagesUrl.indexOf(roundImageView.getAbsolutePath());
                ImageUtil.showImages(getContext(), allImagesUrl, selected);
                //onImageClickListener.onImageClick(roundImageView, allImagesUrl, selected);
            }

        };

        //投票按钮点击
        pollBtnClickListener = view -> onPollBtnClickListener.onPollBtnClick(contentViewPollAdapter.getPollItemIds());

    }

    /**
     * author: sca_tl
     * description:创建一个textview
     */
    private TextView createTextView() {
        TextView textView = new TextView(getContext());
        textView.setTag(TAG_TEXT_VIEW);
        textView.setId(generateViewId());
        textView.setTextIsSelectable(true);
        textView.setPadding(0, 10, 0, 10);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
        textView.setLineSpacing(8, 1.2f);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return textView;
    }

    /**
     * author: sca_tl
     * description: 创建一个textview
     * 注意表情信息放在文本里：[mobcent_phiz=http:\/\/bbs.uestc.edu.cn\/static\/image\/smiley\/bzmh\/bzmh138.gif]
     * 纯文本还可能有编辑信息：本帖最后由 xxx 于 2019-7-1 22:11 编辑
     *
     * @param index  index代表添加的child的view在linearlayout的行数，从0开始
     */
    private void insertTextView(ContentViewBeanEx contentViewBean, int index){
        String text = contentViewBean.infor;

        View view = root_layout.getChildAt(root_layout.getChildCount() - 1);
        //在某个view的后面且前一个view是textview(url)
        if (view != null && index != 0 && view.getTag().toString().equals(TAG_TEXT_VIEW)) {

            //获取textview
            TextView textView1 = (TextView) view;
            textView1.setTextColor(getContext().getColor(R.color.text_color));

            setTextWithEmotion(textView1, text, true);

        } else {  //是第一个view或者前一个view不是textview

            //判断帖子是否编辑过，编辑信息只有一条
            Matcher matcher = Pattern.compile("(.*?)本帖最后由(.*?)于(.*?)编辑").matcher(text);
            if (matcher.find()){
                String name = matcher.group(2).replaceAll(" ", "");
                String time = matcher.group(3);

                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.view_content_view_modify_info, new LinearLayout(getContext()));
                MarqueeTextView info = layout.findViewById(R.id.view_content_view_modify_info_text);
                long t = TimeUtil.getMilliSecond(time, "yyyy-MM-dd HH:mm");
                info.setText(String.format("%s 在 %s 编辑了帖子", name, TimeUtil.formatTime(String.valueOf(t), R.string.post_time1, getContext())));
                root_layout.addView(layout);

                TextView textView1 = createTextView();
                textView1.setTextColor(getContext().getColor(R.color.text_color));
                String s = text.replace(matcher.group(), "");
                setTextWithEmotion(textView1, s.replaceFirst("\r\n\r\n", ""), false);

            } else {
                TextView textView = createTextView();
                textView.setTextColor(getContext().getColor(R.color.text_color));
                setTextWithEmotion(textView, text, false);
            }

        }
    }

    /**
     * author: TanLei
     * description: 处理纯文本里的表情
     * 表情信息格式：[mobcent_phiz=http:\/\/bbs.uestc.edu.cn\/static\/image\/smiley\/bzmh\/bzmh138.gif]
     * 用html展示表情
     */
    private void setTextWithEmotion(final TextView textView, String text, boolean append) {
        final Matcher matcher = Pattern.compile("(\\[mobcent_phiz=(.*?)])").matcher(text);

        if (matcher.find()) {
            do {
                text = text.replace(matcher.group(0)+"", "<img src = " + matcher.group(2) + ">");
            } while (matcher.find());
            text = text.replaceAll("\n", "<br>");
            if (append) {
                textView.append(Html.fromHtml(text, new MyImageGetter(getContext(), textView), null));
            } else {
                textView.setText(Html.fromHtml(text, new MyImageGetter(getContext(), textView), null));
                root_layout.addView(textView);
            }

        } else {
            if (append) {
                textView.append(text);
            } else {

                textView.setText(text);

                root_layout.addView(textView);
            }
        }

    }


    /**
     * author: sca_tl
     * description: 若是链接，高亮显示，支持点击
     * 如果是论坛的帖子链接，需要直接在应用内跳转到对应的帖子
     */
    private void insertUrlView(ContentViewBeanEx contentViewBean, int index){

        String text = contentViewBean.infor;
        String url = contentViewBean.url;

        View view = root_layout.getChildAt(root_layout.getChildCount() - 1);

        //在某个view的后面且前一个view是textview
        if (index != 0 && view.getTag().toString().equals(TAG_TEXT_VIEW)) {

            //获取textview
            TextView textView1 = (TextView) view;

            SpannableString spannableString = new SpannableString(text);
            MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), url);
            spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            textView1.append(spannableString);

        } else {  //是第一个view或者前一个view不是textview
            TextView textView = createTextView();

            SpannableString spannableString = new SpannableString(text);
            MyClickableSpan clickableSpan = new MyClickableSpan(getContext(), url);
            spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);

            root_layout.addView(textView);
        }

    }

    /**
     * author: sca_tl
     * description: 创建一个imageview，并且点击图片能够浏览，提供下载功能
     * glide4.0 若into中设置的是target，占位符（placeholder、error）需要在回调中再次设置，否则无效
     */
    //TODO 加个加载失败图片
    private void insertImageView(ContentViewBean contentViewBean, int index) {
        String img_url = contentViewBean.infor;
        allImagesUrl.add(img_url);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.view_content_view_imageview, new RelativeLayout(getContext()));
        RoundImageView imageView = layout.findViewById(R.id.post_detail_custom_imageView);

        layout.setTag(TAG_IMAGE_VIEW);
        layout.setPadding(5, 10, 5, 10); //图片左右不和文本内容对齐的话可能会好看点?
//        imageView.setLayerType(LAYER_TYPE_SOFTWARE, null);
        imageView.setOnClickListener(imageClickListener);         //图片点击事件
        imageView.setAbsolutePath(img_url);

        Glide.with(getContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.img_loading_img)
                        .error(R.drawable.img_loading_img))
                .load(img_url)
                .into(imageView);

        root_layout.addView(layout);
    }

    /**
     * 将图片以九宫格形式展示
     * @param imgUrls
     */
    private void insertImageEx(List<String> imgUrls) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.view_content_view_all_img, new RelativeLayout(getContext()));
        layout.setTag(TAG_IMAGE_VIEW);
        SBGASortableNinePhotoLayout bgaSortableNinePhotoLayout = layout.findViewById(R.id.view_content_view_all_img_bga_layout);

        bgaSortableNinePhotoLayout.setData((ArrayList<String>) imgUrls);
        bgaSortableNinePhotoLayout.setDelegate(f);
        root_layout.addView(layout);

    }

    /**
     * author: sca_tl
     * description: 创建一个附件视图，提供附件下载功能
     *
     * infor:附件名  url：附件链接  desc：附件下载次数信息
     * 附件拓展名 flv,mp3,mp4,zip,rar,tar,gz,xz,bz2,7z,apk,ipa,crx,pdf,caj,ppt,pptx,doc,docx,xls,xlsx,txt,png,jpg,jpe,jpeg,gif
     *
     * 注意：附件有可能是图片，需要直接显示，图片拓展名：png,jpg,jpe,jpeg,gif
     * 但是要注意可能之前已显示了相同的图片（类型1）
     * 可以先获取所有图片的url集合，然后将附件url和获取的比对，若集合存在附件url，则该图片附件不显示
     * 否则显示（因为有可能附件是图片，但是服务器没有返回该图片附件的图片类型（1））
     * 先将文件下载再显示，因为通过链接好像不能直接加载出图片
     */
    private void insertAttachmentView(ContentViewBeanEx contentViewBean, int index) {
        String infor = contentViewBean.infor;
        final String url = contentViewBean.url;
        String desc = contentViewBean.desc;

        if (FileUtil.isPicture(infor)) {//附件也有可能是图片，直接显示

            if (! imagesUrl.contains(url)) {
                //TODO 下载文件再显示
            }

        } else {  //不是图片，创建一个附件视图
            RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_content_view_attachment, new RelativeLayout(getContext()));
            relativeLayout.setTag(TAG_ATTACHMENT_VIEW);
            ImageView imageView = relativeLayout.findViewById(R.id.post_detail_attachment_img);
            TextView name_textview = relativeLayout.findViewById(R.id.post_detail_attachment_name);
            TextView desc_textview = relativeLayout.findViewById(R.id.post_detail_attachment_desc);
            CardView cardView = relativeLayout.findViewById(R.id.post_detail_attachment_cardview);

            name_textview.setText(infor);
            desc_textview.setText(String.valueOf(desc + "，点击下载"));

            cardView.setOnClickListener(view -> {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    intent.putExtra(Constant.IntentKey.URL, url);
                    getContext().startActivity(intent);
                }
            );

            if (FileUtil.isVideo(infor)) imageView.setImageResource(R.drawable.ic_video);
            if (FileUtil.isAudio(infor)) imageView.setImageResource(R.drawable.ic_music);
            if (FileUtil.isCompressed(infor)) imageView.setImageResource(R.drawable.ic_compressed);
            if (FileUtil.isApplication(infor)) imageView.setImageResource(R.drawable.ic_app);
            if (FileUtil.isPlugIn(infor)) imageView.setImageResource(R.drawable.ic_plugin);
            if (FileUtil.isPdf(infor)) imageView.setImageResource(R.drawable.ic_pdf);
            if (FileUtil.isDocument(infor)) imageView.setImageResource(R.drawable.ic_document);

            if (! TextUtils.isEmpty(infor)) root_layout.addView(relativeLayout);
        }
    }


    /**
     * author: sca_tl
     * description: 创建一个投票视图
     * 一般投票都是放在帖子最后面
     */
    public void insertPollView(boolean update) {
        if (voteBean != null ) {

            RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_content_view_poll, new RelativeLayout(getContext()));
            RecyclerView recyclerView = relativeLayout.findViewById(R.id.view_content_poll_rv);
            TextView textView = relativeLayout.findViewById(R.id.view_content_poll_hint);
            TextView total_tv = relativeLayout.findViewById(R.id.view_content_poll_total);
            Button button = relativeLayout.findViewById(R.id.view_content_poll_btn);

            total_tv.setText(getResources().getString(R.string.total_voters, voteBean.voters));
            total_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            total_tv.getPaint().setAntiAlias(true);//抗锯齿

            total_tv.setOnClickListener(v -> onViewVoterClickListener.onViewVoterClick());

            if (voteBean.poll_status == 1) {
                textView.setText(getResources().getString(R.string.is_voted));
                button.setVisibility(GONE);
            }
            if (voteBean.poll_status == 3) {
                textView.setText(getResources().getString(R.string.no_vote_permission));
                button.setVisibility(GONE);
            }
            if (voteBean.poll_status == 4) {
                textView.setText(getResources().getString(R.string.vote_closed));
                button.setVisibility(GONE);
            }
            if (voteBean.poll_status == 2) {
                textView.setText(getResources().getString(R.string.can_vote, voteBean.type));
                button.setOnClickListener(pollBtnClickListener);
            }

            contentViewPollAdapter = new ContentViewPollAdapter(R.layout.item_content_view_poll);
            recyclerView.setLayoutManager(new MyLinearLayoutManger(getContext()));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(contentViewPollAdapter);
            contentViewPollAdapter.addPollData(voteBean.poll_item_list, voteBean.voters, voteBean.poll_status);

            if (!update) {
                root_layout.addView(relativeLayout);
                pollViewIndex = root_layout.indexOfChild(relativeLayout);
            } else {
                root_layout.removeViewAt(pollViewIndex);
                root_layout.addView(relativeLayout, pollViewIndex);
            }
        }
    }

    public PostDetailBean.TopicBean.PollInfoBean getVoteBean() {
        return voteBean;
    }

    public void setVoteBean(PostDetailBean.TopicBean.PollInfoBean voteBean) {
        this.voteBean = voteBean;
    }

    /**
     * author: sca_tl
     * description: 创建一个音频视图
     * infor:音频链接
     */
    private void insertAudioView(final ContentViewBeanEx contentViewBean, int index) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_content_view_media, new RelativeLayout(getContext()));
        final ImageView play = relativeLayout.findViewById(R.id.view_content_media_play);
        play.setOnClickListener(view -> {
            play.setImageResource(AudioPlayerUtil.getAudioPlayer().isPlaying() ? R.drawable.ic_play : R.drawable.ic_pause);
            AudioPlayerUtil.getAudioPlayer().playOrPause(contentViewBean.infor);
        });
        root_layout.addView(relativeLayout);
    }

    /**
     * author: sca_tl
     * description: 获取所有类型1（图片）的url
     */
    private List<String> getImagesUrl(List<ContentViewBean> contentViewBeans){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < contentViewBeans.size(); i ++) {
            if (contentViewBeans.get(i).type ==  ContentDataType.TYPE_IMAGE) {
                list.add(contentViewBeans.get(i).infor);
            }
        }
        return list;
    }

    /**
     * author: sca_tl
     * description: 加载数据
     */
    public void setContentData(final List<ContentViewBean> contentViewBeans) {
        clearAllLayout();
        allImagesUrl.clear();
        allImagesUrl = getImagesUrl(contentViewBeans);
        imagesUrl = getImagesUrl(contentViewBeans);

        List<ContentViewBeanEx> d = convert(contentViewBeans);

        Observable.create((ObservableOnSubscribe<ContentViewBeanEx>) emitter -> {

            try {

                for (int i = 0; i < d.size() + 1; i++) {
                    if (i < d.size()) {
                        ContentViewBeanEx contentViewBeanEx = d.get(i);
                        emitter.onNext(contentViewBeanEx);
                    } else {
                        emitter.onComplete();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        })  .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ContentViewBeanEx>() {
                @Override
                public void onComplete() {
                    if (SharePrefUtil.isShowAllImgAtTail(getContext())) {
                        insertImageEx(allImagesUrl);
                    }
                    insertPollView(false);
                }

                @Override
                public void onError(Throwable e) { }

                @Override
                public void onSubscribe(Disposable d) { }

                @Override
                public void onNext(ContentViewBeanEx contentViewBeanEx) {
                    try {

                        //若是文本
                        if (contentViewBeanEx.type == ContentDataType.TYPE_TEXT) {
                            insertTextView(contentViewBeanEx, d.indexOf(contentViewBeanEx));
                        }

                        if (!SharePrefUtil.isShowAllImgAtTail(getContext())) {
                            //若是图片
                            if (contentViewBeanEx.type ==  ContentDataType.TYPE_IMAGE) {
                                insertImageEx(contentViewBeanEx.images);
                                //insertImageView(contentViewBeanEx, contentViewBeans.indexOf(contentViewBean));
                            }
                        }
                        //若是视频
                        if (contentViewBeanEx.type ==  ContentDataType.TYPE_VIDEO) {
                            contentViewBeanEx.url = contentViewBeanEx.infor;
                            insertUrlView(contentViewBeanEx, d.indexOf(contentViewBeanEx));
                        }
                        //若是音频
                        if (contentViewBeanEx.type ==  ContentDataType.TYPE_AUDIO) {
                            insertAudioView(contentViewBeanEx, d.indexOf(contentViewBeanEx));
                        }
                        //若是链接
                        if (contentViewBeanEx.type ==  ContentDataType.TYPE_URL) {
                            insertUrlView(contentViewBeanEx, d.indexOf(contentViewBeanEx));
                        }

                        //若是附件
                        if (contentViewBeanEx.type ==  ContentDataType.TYPE_ATTACHMENT) {
                            insertAttachmentView(contentViewBeanEx, d.indexOf(contentViewBeanEx));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public interface OnImageClickListener {
        void onImageClick(View view, List<String> urls, int selected);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public interface OnPollBtnClickListener {
        void onPollBtnClick(List<Integer> ids);
    }

    public void setOnPollBtnClickListener(OnPollBtnClickListener onPollBtnClickListener) {
        this.onPollBtnClickListener = onPollBtnClickListener;
    }

    public interface onViewVoterClickListener {
        void onViewVoterClick();
    }

    public void setOnViewVoterClickListener(onViewVoterClickListener onViewVoterClickListener) {
        this.onViewVoterClickListener = onViewVoterClickListener;
    }

    private SBGASortableNinePhotoLayout.OnClickListener f = new SBGASortableNinePhotoLayout.OnClickListener() {
        @Override
        public void onClickNinePhotoItem(SBGASortableNinePhotoLayout sortableNinePhotoLayout, View view, int position, String model, ArrayList<String> models) {
            ImageUtil.showImages(getContext(), models, position);
        }

    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AudioPlayerUtil.getAudioPlayer().stopPlay();
    }

    /**
     * 将连续的图片放到数组里，这样就可以以九宫格形式展示
     * @param contentViewBeans
     * @return
     */
    private List<ContentViewBeanEx> convert(List<ContentViewBean> contentViewBeans) {
        List<ContentViewBeanEx> res = new ArrayList<>();

        //去掉类型为5并且是图片的数据
        Iterator<ContentViewBean> iterator = contentViewBeans.iterator();
        while (iterator.hasNext()) {
            ContentViewBean s = iterator.next();
            if (s.type == ContentDataType.TYPE_ATTACHMENT && s.infor != null && FileUtil.isPicture(s.infor)) {
                iterator.remove();
            }
        }

        int fast = 0, slow = 0;
        while (fast < contentViewBeans.size()) {
            if (contentViewBeans.get(fast).type != ContentDataType.TYPE_IMAGE) {
                ContentViewBeanEx c = new ContentViewBeanEx();
                c.type = contentViewBeans.get(fast).type;
                c.aid = contentViewBeans.get(fast).aid;
                c.desc = contentViewBeans.get(fast).desc;
                c.infor = contentViewBeans.get(fast).infor;
                c.url = contentViewBeans.get(fast).url;
                c.originalInfo = contentViewBeans.get(fast).originalInfo;

                res.add(c);
                fast ++;
            } else {
                slow = fast + 1;
                while (slow < contentViewBeans.size()) {
                    if (contentViewBeans.get(slow).type != ContentDataType.TYPE_IMAGE) {
                        break;
                    }
                    slow ++;
                }

                ContentViewBeanEx cc = new ContentViewBeanEx();
                cc.images = new ArrayList<>();
                cc.type = 1;
                for (int i = fast; i < slow; i ++) {
                    cc.images.add(contentViewBeans.get(i).infor);
                }

                res.add(cc);

                fast = slow;
            }
        }

        return res;
    }


}
