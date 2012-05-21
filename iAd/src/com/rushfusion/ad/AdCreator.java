package com.rushfusion.ad;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.rushfusion.ad.util.AdXmlParser;

public class AdCreator {

	public String TEST_XML = "data.xml";

	private static final String TAG = "AdCreator";

	public static final int AD_TYPE_IMAGE_ONLY = 1;
	public static final int AD_TYPE_TEXT_ONLY = 2;
	public static final int AD_TYPE_IMAGE_AND_TEXT = 3;
	public static final int AD_TYPE_FULL = 4;

	public static final int ERROR_NETWORK_NOT_ENABLED = 101;
	public static final int ERROR_AD_TYPE = 102;
	public static final int ERROR_URL = 103;
	public static final int ERROR_UNKNOWN_POSITION = 104;
	public static final int ERROR_START = 105;

	private static Activity mContext;
	public static RelativeLayout adViewParent;
	private static CallBack mCallback;
	private static String mAdUrl;

	private int animList[] = new int[] { R.anim.push_in_left,
			R.anim.push_out_left, R.anim.push_in_right, R.anim.push_out_right,
			R.anim.push_in_top, R.anim.push_out_top, R.anim.push_in_bottom,
			R.anim.push_out_bottom };

	private RelativeLayout.LayoutParams adParams;
	private RelativeLayout.LayoutParams imageParams ;
	private RelativeLayout.LayoutParams textParams  ;
	private RelativeLayout.LayoutParams view1params ;
	private RelativeLayout.LayoutParams view2params ;
	private RelativeLayout.LayoutParams view3params ;
	private RelativeLayout.LayoutParams view4params ;
	private int adAlpha = 170;
	private float adTextSize = 24;
	private int textColor = Color.WHITE;
	
	private static final int ChangeAd = 200;
	private static final int ChangeImage = 201;
	private static final int ChangeContent = 202;

	private ViewFlipper imageVF;
	private ArrayList<HashMap<String, String>> images;
	private ViewFlipper textVF;
	private HashMap<String, String> content;
	private String[] strs;

	private int mCurrentPhotoIndex = 0;
	private int mCurrentTextIndex = 0;
	private int mCurrentAdIndex = 0;
	
	private static AdCreator creator;
	private Handler adHandler;
	private Timer adTimer;
	private Timer textTimer;
	private Timer imageTimer;
	
	
	private AdCreator() {

	}

	public static AdCreator getInstance(Activity context, String adUrl,CallBack callback) {
		if (creator == null) {
			creator = new AdCreator();
			mContext = context;
			mCallback = callback;
			mAdUrl = adUrl;
			adViewParent = new RelativeLayout(context);
			adViewParent.setBackgroundColor(context.getResources().getColor(
					R.color.bg));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.ALIGN_PARENT_TOP);
			mContext.addContentView(adViewParent, params);
		}
		return creator;
	}

	public void start() {
		if (!checkNetwork(mContext)) {
			mCallback.onError(new Exception("the network is not enabled！！"),ERROR_NETWORK_NOT_ENABLED);
			Log.w(TAG,"the network is not enabled！！the no network model has been started");
			return;
		}
		if (mAdUrl.equals("") || mAdUrl == null) {
			mCallback.onError(new Exception("the AdUrl is null "), ERROR_URL);
			Log.w(TAG, "the AdUrl is null ");
			startDebugModel();
			return;
		}
		URL url;
		Map<String, Object> data = null;
		try {
			url = new URL(mAdUrl);
			data = AdXmlParser.parseXml(url.openConnection().getInputStream());
		} catch (Exception e1) {
			mCallback.onError(e1, ERROR_START);
			e1.printStackTrace();
		}
		int interval = (Integer) data.get("interval");
		@SuppressWarnings("unchecked")
		List<Ad> ads = (List<Ad>) data.get("ads");
		startAdTask(interval, ads);
	}


	/**
	 * for debug
	 */
	private void startDebugModel() {
		Map<String, Object> data = null;
		try {
			InputStream in = null;
			in = getClass().getClassLoader().getResourceAsStream(TEST_XML);
			data = AdXmlParser.parseXml(in);
		} catch (Exception e1) {
			mCallback.onError(e1, ERROR_START);
			e1.printStackTrace();
		}
		int interval = (Integer) data.get("interval");
		@SuppressWarnings("unchecked")
		List<Ad> ads = (List<Ad>) data.get("ads");
		startAdTask(interval, ads);
	}

	public CallBack getmCallback() {
		return mCallback;
	}

	public interface CallBack {
		public void onError(Exception e, int errorCode);
	}

	/**
	 * Check the network connection is available
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if (nwi != null) {
			return nwi.isAvailable();
		}
		return false;
	}


	TimerTask adTask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = ChangeAd;
			adHandler.sendMessage(message);
		}
	};


	private void startAdTask(int interval, final List<Ad> ads) {
		adTimer = new Timer();
		adTimer.schedule(adTask,2000,interval * 1000);
		adHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case ChangeAd:
					showNextAd(ads);
					break;
				case ChangeImage:
					showNextImage(imageVF, images, animList);
					break;
				case ChangeContent:
					showNextText(textVF, content, strs);
					break;
				default:
					break;
				}
			}
		};
	}

	protected void showNextAd(List<Ad> ads) {
		if(imageTimer!=null){
			imageTimer.cancel();
			imageTimer = null;
		}
		if(textTimer!=null){
			textTimer.cancel();
			textTimer = null;
		}
		if(adViewParent.getChildCount()!=0)
		adViewParent.removeAllViews();
		
		Ad ad = ads.get(mCurrentAdIndex++ % ads.size());
		int type = ad.getType();
		int position = ad.getPosition();
		
		View v = setAdViewPosition(position, type);
		dispatchViewByType(v, type, ad);
	}

	private void dispatchViewByType(View v, int type, Ad ad) {
		if (type == 1) {// image only
			initAdView1(v, ad);
		} else if (type == 2) {// text only
			initAdView2(v, ad);
		} else if (type == 3) {// image + text
			initAdView3(v, ad);
		} else if (type == 4) {// full
			initAdView4(v, ad);
		}
	}

	private View setAdViewPosition(int position, int type) {
		int layoutId = 0;
		if (type == 1) {// image only
			setAdParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 150));
			layoutId = R.layout.first;
		} else if (type == 2) {// text only
			setAdParams(new RelativeLayout.LayoutParams(300, 300));
			layoutId = R.layout.second;
		} else if (type == 3) {// image + text
			setAdParams(new RelativeLayout.LayoutParams(300, 300));
			layoutId = R.layout.third;
		} else if (type == 4) {// full
			setAdParams(new RelativeLayout.LayoutParams(300, 300));
			layoutId = R.layout.fourth;
		} else {
			mCallback.onError(new Exception("type error, type-->" + type),ERROR_AD_TYPE);
		}
		RelativeLayout.LayoutParams params = getAdParams();
		View v = LayoutInflater.from(mContext).inflate(layoutId, null);
		switch (position) {
		case 1:
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 2:
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 3:
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			break;
		case 4:
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			break;
		case 5:
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			break;
		case 6:
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			break;
		case 7:
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case 8:
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;
		case 9:
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			break;

		default:
			mCallback.onError(new Exception("unKnown position-->" + position),ERROR_UNKNOWN_POSITION);
			break;
		}
		v.setLayoutParams(params);
		adViewParent.addView(v);
		return v;
	}

	private void setRelationBy(View adView, String textposition) {

		RelativeLayout.LayoutParams userParams = new RelativeLayout.LayoutParams(getImageParams().width, getAdParams().height - getImageParams().height);
		RelativeLayout userInfo = (RelativeLayout) adView.findViewById(R.id.userinfo);

		if (textposition.equals("left")) {
			getTextParams().width = 100;
			getTextParams().height = getAdParams().height;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				getTextParams().addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else
				getTextParams().addRule(RelativeLayout.BELOW, R.id.title);

			userParams.width = imageParams.width = getAdParams().width - 100;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			userParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.image);
			getTextParams().addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			getImageParams().addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			getImageParams().addRule(RelativeLayout.RIGHT_OF, R.id.text);
		} else if (textposition.equals("right")) {
			getTextParams().width = 100;
			getTextParams().height = getAdParams().height;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				getTextParams().addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else
				getTextParams().addRule(RelativeLayout.BELOW, R.id.title);
			userParams.width = getImageParams().width = getAdParams().width - 100;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			getTextParams().addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			getImageParams().addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			getImageParams().addRule(RelativeLayout.LEFT_OF, R.id.text);
		} else if (textposition.equals("top")) {
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				getTextParams().addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else
				getTextParams().addRule(RelativeLayout.BELOW, R.id.title);
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			getImageParams().addRule(RelativeLayout.BELOW, R.id.text);
			getImageParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
			getTextParams().width = getAdParams().width;
			getTextParams().height = getAdParams().height - getImageParams().height - title.getHeight();
		} else if (textposition.equals("bottom")) {
			if (userInfo != null) {
				userParams.addRule(RelativeLayout.BELOW, R.id.text);
				userInfo.setLayoutParams(userParams);
			}
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				getImageParams().addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else
				getImageParams().addRule(RelativeLayout.BELOW, R.id.title);
			getTextParams().width = getAdParams().width;
			getTextParams().height = getAdParams().height - getImageParams().height - title.getHeight();
			getTextParams().addRule(RelativeLayout.BELOW, R.id.image);
		}
		if (userInfo != null)
			userInfo.setLayoutParams(userParams);
		imageVF.setLayoutParams(getImageParams());
		textVF.setLayoutParams(getTextParams());
	}

	/**
	 * full
	 * 
	 * @param v
	 * @param ad
	 * @param downloader
	 * @param images
	 */
	private void initAdView4(View v, Ad ad) {
		String title = ad.getTitle();
		images = ad.getImages();
		content = ad.getContent();
		String contact = (String) ad.getCompany().get("contact");
		String phone = (String) ad.getCompany().get("phone");
		String address = (String) ad.getCompany().get("address");
		String email = (String) ad.getCompany().get("email");
		String website = (String) ad.getCompany().get("website");
		//-------------------------------------------------------
		TextView titleTv = (TextView) v.findViewById(R.id.title);
		TextView contactTv = (TextView) v.findViewById(R.id.contact);
		TextView phoneTv = (TextView) v.findViewById(R.id.phone);
		TextView adsTv = (TextView) v.findViewById(R.id.address);
		TextView emailTv = (TextView) v.findViewById(R.id.email);
		TextView webTv = (TextView) v.findViewById(R.id.website);
		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		textVF = (ViewFlipper) v.findViewById(R.id.text);
		//-------------------------------------------------------
		titleTv.setText(title);
		contactTv.setText(contact);
		phoneTv.setText(phone);
		adsTv.setText(address);
		adsTv.requestFocus();
		emailTv.setText(email);
		webTv.setText(website);
		//-------------------------------------------------------
		setRelationBy(v,content.get("position"));
		imageTransfer(imageVF, images, ad.getImageInterval());
		textTransfer(textVF, content);
	}

	/**
	 * image + text
	 * 
	 * @param v2
	 * @param ad
	 * @param downloader
	 * @param images
	 */
	private void initAdView3(View v, Ad ad) {
		setImageParams(new RelativeLayout.LayoutParams(300,150));
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(ad.getTitle());
		images = ad.getImages();
		content = ad.getContent();

		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		textVF = (ViewFlipper) v.findViewById(R.id.text);

		setRelationBy(v,content.get("position"));
		imageVF.setLayoutParams(getImageParams());
		imageTransfer(imageVF, images, ad.getImageInterval());
		textTransfer(textVF, content);

	}

	/**
	 * text only
	 * 
	 * @param v2
	 * @param ad
	 * @param downloader
	 * @param images
	 */
	private void initAdView2(View v, Ad ad) {
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(ad.getTitle());
		title.setTextColor(getAdTextColor());
		textVF = (ViewFlipper) v.findViewById(R.id.text);
		content = ad.getContent();
		setTextParams(new RelativeLayout.LayoutParams(getAdParams().width,getAdParams().height-title.getHeight()));
		v.setLayoutParams(getAdParams());
		textTransfer(textVF, content);
	}

	/**
	 * image Only
	 * 
	 * @param v
	 * @param ad
	 * @param downloader
	 * @param images
	 */
	private void initAdView1(View v, Ad ad) {
		setImageParams(getAdParams());
		images = ad.getImages();
		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		v.setLayoutParams(getImageParams());
		imageTransfer(imageVF, images, ad.getImageInterval());
	}


	private void showNextImage(ViewFlipper vf,List<HashMap<String, String>> images,int[] animList) {
		vf.clearAnimation();
		mCurrentPhotoIndex = mCurrentPhotoIndex % (images.size());
		String animPosition = images.get(mCurrentPhotoIndex).get("anim");
		// 判断动画显示的方向
		if ("left".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[0]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[1]));
		} else if ("right".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[2]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[3]));
		} else if ("top".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[4]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[5]));
		} else if ("bottom".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[6]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[7]));
		}
		vf.showNext();
		mCurrentPhotoIndex++;
	}

	private void showNextText(final ViewFlipper vf,HashMap<String, String> content, final String[] strs) {
		final String direction = content.get("direction");
		vf.clearAnimation();
		mCurrentTextIndex = mCurrentTextIndex % (strs.length);
		if ("left".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[0]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[1]));
		} else if ("right".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[2]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[3]));
		} else if ("top".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[4]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[5]));
		} else if ("bottom".equals(direction)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[6]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[7]));
		}
		vf.showNext();
		mCurrentTextIndex++;
	}
	/**
	 * Picture switch
	 * 
	 * @param vf
	 *            the ViewFlipper to be attached
	 * @param images
	 *            -one image contain the key "anim" "url"
	 * @param delay
	 *            -the images transfer interval
	 */
	private void imageTransfer(final ViewFlipper imageVf,final List<HashMap<String, String>> images, int delay) {
		imageVF = imageVf;
		// 显示的动画效果
		for (int i = 0; i < images.size(); i++) {
			HashMap<String, String> imagesInfo = images.get(i);
			final String imagepath = imagesInfo.get("url");
			/**
			 * 开启子线程，下载对应path相对的bitmap
			 */
			new AsyncTask<Void, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(Void... params) {
					Bitmap bitMap = null;
					try {
						bitMap = getBitMap(imagepath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return bitMap;
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						ImageView iv = new ImageView(mContext);
						iv.setAlpha(getAdAlpha());
						iv.setLayoutParams(imageParams);
						iv.setScaleType(ScaleType.FIT_XY);
						iv.setImageBitmap(result);
						iv.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_in_left));
						imageVf.addView(iv);
					}
					super.onPostExecute(result);
				}
			}.execute();
		}

		TimerTask imageTask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = ChangeImage;
				adHandler.sendMessage(message);
			}
		};
		imageTimer = new Timer();
		imageTimer.schedule(imageTask, delay * 1000, delay * 1000);
	}

	/**
	 * Text switch
	 * 
	 * @param textView
	 * @param anim
	 * @param direction
	 * @param position
	 * @param scroll
	 */
	private void textTransfer(final ViewFlipper vf,HashMap<String, String> content) {
		textVF = vf;
		String anim = content.get("anim");
		String value = content.get("value");
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(value);
		value = m.replaceAll("").trim();
		String scroll = content.get("scroll");
		if (anim.equals("left")) {vf.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_in_left));
		
		} else if (anim.equals("right")) {vf.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_in_right));
		
		}

		TextView textview = new TextView(mContext);
		textview.setTextColor(getAdTextColor());
		vf.addView(textview);// tbd

		FontMetrics fm = textview.getPaint().getFontMetrics();
		float baseline = fm.descent - fm.ascent + fm.leading;
		if (textParams.height < baseline) {
			setAdTextSize(textview.getTextSize());
			textview.setText(value);
			return;// tbd
		}
		System.out.println("textParams.H-->" + textParams.height
				+ " baseLineH-->" + baseline + "maxLines-->"
				+ Math.ceil((textParams.height / baseline)));
		int maxLines = (int) Math.ceil((textParams.height / baseline));

		strs = getValuesByLines(textParams.width, value, maxLines,
				textview.getPaint());
		if (strs.length == 1) {
			setAdTextSize(textview.getTextSize());
			textview.setText(strs[0]);
			return;// tbd
		}
		for (int i = 0; i < strs.length; i++) {
			System.out.println("strs[" + i + "]-->" + strs[i]);
			if (strs[i] != null && !strs[i].equals("")) {
				TextView textView = new TextView(mContext);
				setAdTextSize(textView.getTextSize());
				textView.setTextColor(getAdTextColor());
				textView.setText(strs[i]);
				Log.d("AdCreator", "textView1:>>>" + strs[i]);
				vf.addView(textView);
			}
		}
		vf.removeView(textview);
		TimerTask textTask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = ChangeContent;
				adHandler.sendMessage(message);
			}
		};
		textTimer = new Timer();
		textTimer.schedule(textTask, Integer.parseInt(scroll) * 1000, Integer.parseInt(scroll) * 1000);
	}


	/**
	 * Download image
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private Bitmap getBitMap(String path) throws Exception {
		URL url = new URL(path);
		URLConnection connection = url.openConnection();
		InputStream inputStream = connection.getInputStream();
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
		return bitmap;
	}

	public void stop() {
		adTask.cancel();
		creator = null;
		if (adViewParent != null)
			adViewParent.removeAllViews();
	}

	private String[] getValuesByLines(int w, String value, int maxlines,
			Paint paint) {
		String[] linestrs = getLineStrs(value, paint, w, getAdTextSize());

		String[] values = new String[(int) Math.ceil((double) linestrs.length
				/ maxlines)];
		System.out.println("累计行数-->" + linestrs.length + "  最大行数-->" + maxlines
				+ "  页数-->" + values.length);
		for (int i = 0; i < values.length; i++) {
			values[i] = getValueFrom(linestrs, maxlines, i);
			System.out.println("value-->" + i + "<-->" + values[i]);
		}
		return values;
	}


	private String getValueFrom(String[] linestrs, int maxlines, int num) {
		StringBuffer result = new StringBuffer();
		for (int i = num * maxlines; i < (num + 1) * maxlines; i++) {
			if (i >= linestrs.length)
				break;
			if (linestrs[i] != null)
				result.append(linestrs[i]);
			System.out.println("sub-->" + linestrs[i]);
		}
		return result.toString();
	}

	private String[] getLineStrs(String content, Paint p, float width,float textSize) {
		p.setTextSize(textSize);
		int index = 0;
		int start = 0;
		int end = 0;
		float textLength = p.measureText(content);
		System.out.println("textLength->" + textLength);
		System.out.println("width->" + width);

		int lineNum = (int) Math.ceil(textLength / width);

		if (textLength < width) {
			return new String[] { content };
		}
		Log.d("split", "textView1 lineNum is:" + lineNum);
		String[] mSplitTextParts = new String[lineNum];
		for (int i = 0; i <= content.length(); i++, end++) {
			float measureLength = p.measureText(content, start, end);
			if (measureLength >= width) {
				Log.d("split", "textView1 measureLength is:" + measureLength);
				mSplitTextParts[index++] = content.substring(start, end);
				start = end;
			}
			if (end == content.length()) {
				mSplitTextParts[index++] = content.substring(start, end);
			}
		}
		return mSplitTextParts;
	}

	public float getAdTextSize() {
		return adTextSize;
	}

	public void setAdTextSize(float textSize) {
		this.adTextSize = textSize;
	}

	public RelativeLayout.LayoutParams getImageParams() {
		return imageParams;
	}

	public void setImageParams(RelativeLayout.LayoutParams imageParams) {
		this.imageParams = imageParams;
	}

	public RelativeLayout.LayoutParams getTextParams() {
		return textParams;
	}

	public void setTextParams(RelativeLayout.LayoutParams textParams) {
		this.textParams = textParams;
	}

	public RelativeLayout.LayoutParams getView1params() {
		return view1params;
	}

	public void setView1params(RelativeLayout.LayoutParams view1params) {
		this.view1params = view1params;
	}

	public RelativeLayout.LayoutParams getView2params() {
		return view2params;
	}

	public void setView2params(RelativeLayout.LayoutParams view2params) {
		this.view2params = view2params;
	}

	public RelativeLayout.LayoutParams getView3params() {
		return view3params;
	}

	public void setView3params(RelativeLayout.LayoutParams view3params) {
		this.view3params = view3params;
	}

	public RelativeLayout.LayoutParams getView4params() {
		return view4params;
	}

	public void setView4params(RelativeLayout.LayoutParams view4params) {
		this.view4params = view4params;
	}

	public int getAdTextColor() {
		return textColor;
	}

	public void setAdTextColor(int textColor) {
		this.textColor = textColor;
	}

	public RelativeLayout.LayoutParams getAdParams() {
		return adParams;
	}

	public void setAdParams(RelativeLayout.LayoutParams adParams) {
		this.adParams = adParams;
	}

	public int getAdAlpha() {
		return adAlpha;
	}

	public void setAdAlpha(int adAlpha) {
		this.adAlpha = adAlpha;
	}
	
	
}
