package com.scatl.uestcbbs.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.util.CommonUtil;
import com.scatl.uestcbbs.widget.textview.EmojiEditText;
import com.scatl.widget.sapn.CenterImageSpan;
import com.scatl.util.ImageUtil;
import com.scatl.util.ScreenUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author: sca_tl
 * description: 图文混排编辑器，改自于：https://github.com/scatl/XRichText
 */
public class ContentEditor extends ScrollView {

	private static final int EDIT_TEXT_PADDING = 10; // edittext常规padding是10dp
	public static final int CONTENT_TYPE_TEXT = 0;  //文本
	public static final int CONTENT_TYPE_IMAGE = 1;  //图片

	private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
	private LinearLayout rootLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
	private LayoutInflater inflater;
	private OnKeyListener keyListener; // 所有EditText的软键盘监听器
	private OnClickListener btnListener; // 图片右上角删除按钮监听器
	private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
	private EmojiEditText lastFocusEdit; // 最近被聚焦的EditText
	private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
	private int editNormalPadding = 0; //
	private int disappearingImageIndex = 0;


	private ArrayList<String> imagePaths;//图片地址集合


	/** 自定义属性 **/
	//插入的图片高度
	private int imageHeight = 600;
	//两张相邻图片间距
	private int imageBottom = 10;
	//文字相关属性，初始提示信息，文字大小和颜色
	private String textInitHint = "写点什么吧~";
	private String textHint = "请输入内容";
	private int textSize = 17;
	private int textColor = getContext().getColor(R.color.text_color);
	private int textLineSpace = 10;

	//删除图片的接口
	private OnRtImageDeleteListener onRtImageDeleteListener;
	private OnRtImageClickListener onRtImageClickListener;


	public ContentEditor(Context context) {
		this(context, null);
	}

	public ContentEditor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ContentEditor(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

		imagePaths = new ArrayList<>();

		inflater = LayoutInflater.from(context);

		// 1. 初始化allLayout
		rootLayout = new LinearLayout(context);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		setupLayoutTransitions();//禁止载入动画

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rootLayout.setPadding(0,15,0,15);//设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
		addView(rootLayout, layoutParams);

		// 2. 初始化键盘退格监听
		// 主要用来处理点击回删按钮时，view的一些列合并操作
		keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
					EmojiEditText edit = (EmojiEditText) v;
					onBackspacePress(edit);
				}
				return false;
			}
		};

		// 3. 图片删除处理
		btnListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v instanceof ShapeableImageView){
					ShapeableImageView imageView = (ShapeableImageView)v;
					// 开放图片点击接口
					if (onRtImageClickListener != null){
						onRtImageClickListener.onRtImageClick(imageView, (String) imageView.getTag());
					}
				} else if (v instanceof ImageView){
					RelativeLayout parentView = (RelativeLayout) v.getParent();
					onImageCloseClick(parentView);
				}
			}
		};

		focusListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lastFocusEdit = (EmojiEditText) v;
				}
			}
		};

		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		EmojiEditText firstEdit = createEditText(textInitHint, ScreenUtil.dip2px(context, EDIT_TEXT_PADDING));
		rootLayout.addView(firstEdit, firstEditParam);
		lastFocusEdit = firstEdit;
	}


	/**
	 * author: sca_tl
	 * description: 回退键处理
	 */
	private void onBackspacePress(EmojiEditText editTxt) {
		try {
			int startSelection = editTxt.getSelectionStart();
			// 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
			if (startSelection == 0) {
				int editIndex = rootLayout.indexOfChild(editTxt);
				View preView = rootLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,则返回的是null
				if (null != preView) {
					if (preView instanceof RelativeLayout) {
						// 光标EditText的上一个view对应的是图片
						onImageCloseClick(preView);
					} else if (preView instanceof EmojiEditText) {
						// 光标EditText的上一个view对应的还是文本框EditText
						String str1 = editTxt.getText().toString();
						EmojiEditText preEdit = (EmojiEditText) preView;
						String str2 = preEdit.getText().toString();

						// 合并文本view时，不需要transition动画
						rootLayout.setLayoutTransition(null);
						rootLayout.removeView(editTxt);
						rootLayout.setLayoutTransition(mTransitioner); // 恢复transition动画

						// 文本合并
						preEdit.setText(String.valueOf(str2 + str1));
						preEdit.requestFocus();
						preEdit.setSelection(str2.length(), str2.length());
						lastFocusEdit = preEdit;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * author: sca_tl
	 * description: 处理图片叉掉的点击事件
	 * @param view 整个image对应的relativeLayout view
	 */
	private void onImageCloseClick(View view) {
		try {
			if (!mTransitioner.isRunning()) {
				disappearingImageIndex = rootLayout.indexOfChild(view);
				//删除编辑器里的图片
				List<EditData> dataList = buildEditorData();
				EditData editData = dataList.get(disappearingImageIndex);
				if (editData.imagePath != null){
					if (onRtImageDeleteListener != null){
						onRtImageDeleteListener.onRtImageDelete(editData.imagePath);
					}
					imagePaths.remove(editData.imagePath);
				}
				rootLayout.removeView(view);
				mergeEditText();//合并上下EditText内容
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 清空所有布局
	 */
	public void clearAllLayout(){
		rootLayout.removeAllViews();
	}

	/**
	 * 获取索引位置
	 */
	public int getLastIndex(){
		return rootLayout.getChildCount();
	}

	/**
	 * author: sca_tl
	 * description: 生成文本输入框
	 */
	public EmojiEditText createEditText(String hint, int paddingTop) {
		EmojiEditText editText = (EmojiEditText) inflater.inflate(R.layout.view_content_editor_edittext, null);

		editText.setOnKeyListener(keyListener);

		editText.setTag(viewTagIndex++);
		editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
		editText.setHint(hint);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		editText.setTextColor(textColor);
		editText.setLineSpacing(textLineSpace, 1.0f);
		editText.setOnFocusChangeListener(focusListener);

		return editText;
	}

	/**
	 * author: sca_tl
	 * description: 生成图片View
	 */
	private RelativeLayout createImageLayout() {

		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.view_content_editor_imageview, null);
		layout.setTag(viewTagIndex++);
		View closeView = layout.findViewById(R.id.content_editor_img_close);
		closeView.setTag(layout.getTag());
		closeView.setOnClickListener(btnListener);
		ShapeableImageView imageView = layout.findViewById(R.id.content_editor_imageView);
		imageView.setOnClickListener(btnListener);
		return layout;
	}

	/**
	 * 根据绝对路径添加view
	 */
	public void insertImage(String imagePath, int width) {
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		Bitmap bmp = getScaledBitmap(imagePath, width);
		insertImage(bmp, imagePath);
	}

	/**
	 * author: sca_tl
	 * description: 插入图片
	 */
	public void insertImage(Bitmap bitmap, String imagePath) {
		//bitmap == null时，可能是网络图片，不能做限制
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		try {
			//lastFocusEdit获取焦点的EditText
			String lastEditStr = lastFocusEdit.getText().toString();
			int cursorIndex = lastFocusEdit.getSelectionStart();//获取光标所在位置
			String editStr1 = lastEditStr.substring(0, cursorIndex).trim();//获取光标前面的字符串
			String editStr2 = lastEditStr.substring(cursorIndex).trim();//获取光标后的字符串
			int lastEditIndex = rootLayout.indexOfChild(lastFocusEdit);//获取焦点的EditText所在位置

			if (lastEditStr.length() == 0) {
				//如果当前获取焦点的EditText为空，直接在EditText下方插入图片，并且插入空的EditText
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, bitmap, imagePath);
			} else if (editStr1.length() == 0) {
				//如果光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
				addImageViewAtIndex(lastEditIndex, bitmap, imagePath);
				//同时插入一个空的EditText，防止插入多张图片无法写文字
				addEditTextAtIndex(lastEditIndex + 1, "");
			} else if (editStr2.length() == 0) {
				// 如果光标已经顶在了editText的最末端，则需要添加新的imageView和EditText
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, bitmap, imagePath);
			} else {
				//如果光标已经顶在了editText的最中间，则需要分割字符串，分割成两个EditText，并在两个EditText中间插入图片
				//把光标前面的字符串保留，设置给当前获得焦点的EditText（此为分割出来的第一个EditText）
				lastFocusEdit.setText(editStr1);
				//把光标后面的字符串放在新创建的EditText中（此为分割出来的第二个EditText）
				addEditTextAtIndex(lastEditIndex + 1, editStr2);
				//在第二个EditText的位置插入一个空的EditText，以便连续插入多张图片时，有空间写文字，第二个EditText下移
				addEditTextAtIndex(lastEditIndex + 1, "");
				//在空的EditText的位置插入图片布局，空的EditText下移
				addImageViewAtIndex(lastEditIndex + 1, bitmap, imagePath);
			}
			//hideKeyBoard();
			CommonUtil.hideSoftKeyboard(getContext(), lastFocusEdit);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * author: sca_tl
	 * description: 在特定位置插入EditText
	 * @param index 位置
	 * @param editStr EditText显示的文字
	 */
	public void addEditTextAtIndex(final int index, CharSequence editStr) {
		try {
			EmojiEditText editText2 = createEditText(textHint, EDIT_TEXT_PADDING);
			if (!TextUtils.isEmpty(editStr)) {//判断插入的字符串是否为空，如果没有内容则显示hint提示信息
				editText2.setText(editStr);
			}
			editText2.setOnFocusChangeListener(focusListener);

			// 请注意此处，EditText添加、或删除不触动Transition动画
			rootLayout.setLayoutTransition(null);
			rootLayout.addView(editText2, index);
			rootLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
			//插入新的EditText之后，修改lastFocusEdit的指向
			lastFocusEdit = editText2;
			lastFocusEdit.requestFocus();
			lastFocusEdit.setSelection(editStr.length(), editStr.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * author: sca_tl
	 * description: 在光标处插入文字
	 */
	public void insertText(String text) {
		lastFocusEdit.getText().insert(lastFocusEdit.getSelectionStart(), text);
	}

	/**
	 * author: sca_tl
	 * description: 在光标处插入表情
	 * @param emotion_path 表情路径，表情文件名[s_123]需要改成河畔服务器可识别的[s:123]
	 */
	public void insertEmotion(String emotion_path) {
		lastFocusEdit.insertEmotion(emotion_path, lastFocusEdit.getSelectionStart());
	}

	/**
	 * author: sca_tl
	 * description: 在特定位置添加ImageView
	 */
	public void addImageViewAtIndex(final int index, Bitmap bmp, String imagePath) {
		if (TextUtils.isEmpty(imagePath)){ return; }
		try {
			imagePaths.add(imagePath);
			RelativeLayout imageLayout = createImageLayout();
			ShapeableImageView imageView = imageLayout.findViewById(R.id.content_editor_imageView);
			Glide.with(getContext()).load(imagePath).into(imageView);
			imageView.setTag(imagePath);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//裁剪居中

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, imageHeight);
			lp.bottomMargin = imageBottom;
			imageView.setLayoutParams(lp);

			Glide.with(getContext()).load(imagePath).into(imageView);

			rootLayout.addView(imageLayout, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * author: sca_tl
	 * description: 在特定位置添加ImageView
	 */
	public void addImageViewAtIndex(final int index, final String imagePath) {
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		try {
			imagePaths.add(imagePath);
			RelativeLayout imageLayout = createImageLayout();
			final ShapeableImageView imageView = imageLayout.findViewById(R.id.content_editor_imageView);
			imageView.setTag(imagePath);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//裁剪居中

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
			lp.bottomMargin = imageBottom;
			imageView.setLayoutParams(lp);

			Glide.with(getContext()).load(imagePath).into(imageView);
			rootLayout.addView(imageLayout, index);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * author: sca_tl
	 * description: 根据view的宽度，动态缩放bitmap尺寸
	 * @param width view的宽度
	 */
	public Bitmap getScaledBitmap(String filePath, int width) {
		if (TextUtils.isEmpty(filePath)){ return null; }
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			int sampleSize = options.outWidth > width ? options.outWidth / width
					+ 1 : 1;
			options.inJustDecodeBounds = false;
			options.inSampleSize = sampleSize;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 初始化transition动画
	 */
	private void setupLayoutTransitions() {
		mTransitioner = new LayoutTransition();
		rootLayout.setLayoutTransition(mTransitioner);
		mTransitioner.addTransitionListener(new LayoutTransition.TransitionListener() {

			@Override
			public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) { }

			@Override
			public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
				if (!transition.isRunning() && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
					// transition动画结束，合并EditText
					mergeEditText();
				}
			}
		});
		mTransitioner.setDuration(300);
	}

	/**
	 * 图片删除的时候，如果上下方都是EditText，则合并处理
	 */
	private void mergeEditText() {
		try {
			View preView = rootLayout.getChildAt(disappearingImageIndex - 1);
			View nextView = rootLayout.getChildAt(disappearingImageIndex);
			if (preView instanceof EmojiEditText && nextView instanceof EmojiEditText) {
				EmojiEditText preEdit = (EmojiEditText) preView;
				EmojiEditText nextEdit = (EmojiEditText) nextView;
				String str1 = preEdit.getText().toString();
				String str2 = nextEdit.getText().toString();
				String mergeText = "";
				if (str2.length() > 0) {
					mergeText = str1 + "\n" + str2;
				} else {
					mergeText = str1;
				}

				rootLayout.setLayoutTransition(null);
				rootLayout.removeView(nextEdit);
				preEdit.setText(mergeText);
				preEdit.requestFocus();
				preEdit.setSelection(str1.length(), str1.length());
				rootLayout.setLayoutTransition(mTransitioner);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * author: sca_tl
	 * description: 生成编辑数据
	 */
	public List<EditData> buildEditorData() {
		List<EditData> dataList = new ArrayList<>();
		try {
			for (int index = 0; index < rootLayout.getChildCount(); index++) {
				View itemView = rootLayout.getChildAt(index);
				EditData itemData = new EditData();
				if (itemView instanceof EmojiEditText) {  //是文本
					EmojiEditText item = (EmojiEditText) itemView;
					itemData.inputStr = item.getText().toString();
					itemData.content_type = CONTENT_TYPE_TEXT;
				} else if (itemView instanceof RelativeLayout) {  //是图片
					ShapeableImageView item = itemView.findViewById(R.id.content_editor_imageView);
					itemData.imagePath = (String) item.getTag();
					itemData.content_type = CONTENT_TYPE_IMAGE;
				}
				dataList.add(itemData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dataList;
	}


	/**
	 * author: sca_tl
	 * description: 编辑器是否为空
	 */
	public boolean isEditorEmpty() {
		return TextUtils.isEmpty(lastFocusEdit.getText()) && imagePaths.size() == 0;
	}

	/**
	 * author: sca_tl
	 * description: 获取图片集合
	 */
	public List<String> getImgPathList(){
		List<String> path_list = new ArrayList<>();
		List<EditData> editList = buildEditorData();
		for (ContentEditor.EditData itemData : editList) {
			if (itemData.imagePath != null) {
				path_list.add(itemData.imagePath);
			}
		}
		return path_list;
	}

	/**
	 * author: sca_tl
	 * description: 异步方式显示数据
	 */
	public void setEditorData(final String content) {
		rootLayout.removeAllViews();
		Observable.create(new ObservableOnSubscribe<JSONObject>() {
					@Override
					public void subscribe(ObservableEmitter<JSONObject> emitter) {
						try{
							JSONArray jsonArray = JSONObject.parseArray(content);
							for (int i = 0; i < jsonArray.size(); i ++) {
								emitter.onNext(jsonArray.getJSONObject(i));
							}

							emitter.onComplete();
						}catch (Exception e){
							e.printStackTrace();
							emitter.onError(e);
						}
					}
				})  .subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<JSONObject>() {
					@Override
					public void onComplete() {

						if (rootLayout.getChildAt(0) instanceof EmojiEditText) {
							EmojiEditText editText = (EmojiEditText) rootLayout.getChildAt(0);
							if (TextUtils.isEmpty(editText.getText().toString())) {
								rootLayout.removeView(editText);
							}
						}

						//在全部插入完毕后，再插入一个EditText，防止最后一张图片后无法插入文字
						if (rootLayout.getChildAt(getLastIndex()) instanceof RelativeLayout) {
							addEditTextAtIndex(getLastIndex(), "");
						}

					}

					@Override
					public void onError(Throwable e) { }

					@Override
					public void onSubscribe(Disposable d) { }

					@Override
					public void onNext(JSONObject content_json) {
						try {

							int type = content_json.getIntValue("content_type");
							String content = content_json.getString("content");
							if (type == CONTENT_TYPE_TEXT) {
								addEditTextAtIndex(getLastIndex(), content);
							}

							if (type == CONTENT_TYPE_IMAGE) {
								//addEditTextAtIndex(getLastIndex(), "");
								addImageViewAtIndex(getLastIndex(), content);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	public class EditData {
		public String inputStr;  //文本内容
		public String imagePath;  //图片路径
		public int content_type;  //0为文本，1为图片
		public Bitmap bitmap;
	}


	public interface OnRtImageDeleteListener{
		void onRtImageDelete(String imagePath);
	}

	public void setOnRtImageDeleteListener(OnRtImageDeleteListener onRtImageDeleteListener) {
		this.onRtImageDeleteListener = onRtImageDeleteListener;
	}

	public interface OnRtImageClickListener{
		void onRtImageClick(View view, String imagePath);
	}

	public void setOnRtImageClickListener(OnRtImageClickListener onRtImageClickListener) {
		this.onRtImageClickListener = onRtImageClickListener;
	}


}
