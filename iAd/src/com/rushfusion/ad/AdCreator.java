package com.rushfusion.ad;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class AdCreator {

	public String TEST_XML = "data4.xml";

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

	private Activity mContext;
	public RelativeLayout adViewParent;
	private CallBack mCallback;
	private String mAdUrl;

	private int ad_width = 300;
	private int ad_height = 200;
	private int image_w = LayoutParams.MATCH_PARENT;
	private int image_h = 150;
	private int text_w;
	private int text_h;
	private int title_h = 20;
	
	
	private RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
			image_w, image_h);
	private RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
			text_w, text_h);

	private static final int ChangeImage = 201;
//	private static final int ChangeText = 202;
	private int animList[] = new int[] { R.anim.push_in_left,R.anim.push_out_left,
								R.anim.push_in_right,R.anim.push_out_right, R.anim.push_in_top, 
								R.anim.push_out_top,R.anim.push_in_bottom, R.anim.push_out_bottom };
	private ViewFlipper imageVF;
//	private ViewFlipper textVF;
	private List<HashMap<String, String>> images;
//	private String [] strs;
//	private String direction;
	
	private int mCurrentPhotoIndex = 0;
//	private int mCurrentTextIndex = 0;
	private float textSize = 20;
	
	
	

	public AdCreator(Activity context, String adUrl, CallBack callback) {
		mContext = context;
		mCallback = callback;
		mAdUrl = adUrl;
		adViewParent = new RelativeLayout(context);
		adViewParent.setBackgroundColor(context.getResources().getColor(R.color.bg));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.ALIGN_PARENT_TOP);
		mContext.addContentView(adViewParent, params);
	}

	public void start() {
		if (!checkNetwork(mContext)) {
			mCallback.onError(new Exception("the network is not enabled！！"),
					ERROR_NETWORK_NOT_ENABLED);
			Log.w(TAG,
					"the network is not enabled！！the no network model has been started");
			startDebugModel();
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
			data = parseXml(url.openConnection().getInputStream());
		} catch (Exception e1) {
			mCallback.onError(e1, ERROR_START);
			e1.printStackTrace();
		}
		showDynamicAdvertisement(data);
	}

	/**
	 * TBD
	 */
	private void startDebugModel() {
		Map<String, Object> data = null;
		try {
			InputStream in = null;
			in = getClass().getClassLoader().getResourceAsStream(TEST_XML);
			data = parseXml(in);
		} catch (Exception e1) {
			mCallback.onError(e1, ERROR_START);
			e1.printStackTrace();
		}
		showDynamicAdvertisement(data);
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
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if (nwi != null) {
			return nwi.isAvailable();
		}
		return false;
	}

	/**
	 * Dynamic layout
	 * 
	 * @param position
	 * @param layoutId
	 * @return
	 */
	private View showDynamicAdvertisement(Map<String, Object> data) {
		int type = Integer.parseInt((String) data.get("type"));
		String position = (String) data.get("position");
		int layoutId = 0;
		if (type == 1) {// image only
			layoutId = R.layout.first;
		} else if (type == 2) {// text only
			layoutId = R.layout.second;
		} else if (type == 3) {// image + text
			layoutId = R.layout.third;
		} else if (type == 4) {// full
			layoutId = R.layout.fourth;
		} else {
			mCallback.onError(new Exception("type error, type-->" + type),
					ERROR_AD_TYPE);
			return null;
		}
		View v = getViewByPosition(position, layoutId);
		dispatchViewByType(v, type, data);
		return v;
	}

	private void dispatchViewByType(View v, int type, Map<String, Object> data) {
		if (type == AD_TYPE_IMAGE_ONLY) {
			showAdType_1(v, data);
		} else if (type == AD_TYPE_TEXT_ONLY) {
			showAdType_2(v, data);
		} else if (type == AD_TYPE_IMAGE_AND_TEXT) {
			showAdType_3(v, data);
		} else if (type == AD_TYPE_FULL) {
			showAdType_4(v, data);
		}
		adViewParent.addView(v);
	}

	private View getViewByPosition(String position, int layoutId) {
		View v = LayoutInflater.from(mContext).inflate(layoutId, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ad_width, ad_height);
		if (position.equals("lefttop")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else if (position.equals("topcenter")) {
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else if (position.equals("righttop")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else if (position.equals("leftcenter")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		} else if (position.equals("center")) {
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		} else if (position.equals("rightcenter")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		} else if (position.equals("leftbottom")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		} else if (position.equals("bottomcenter")) {
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		} else if (position.equals("rightbottom")) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		}else if(position.equals("random")){
			Random r = new Random();
			int x = r.nextInt(9);
			switch (x) {
			case 0:
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				break;
			case 1:
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				break;
			case 2:
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				break;
			case 3:
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				break;
			case 4:
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				break;
			case 5:
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				break;
			case 6:
				params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				break;
			case 7:
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				break;
			case 8:
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				break;
			default:
				break;
			}
		}else
			mCallback.onError(new Exception("unKnown position-->" + position),ERROR_UNKNOWN_POSITION);
		v.setLayoutParams(params);
		return v;
	}

	/**
	 * initialize
	 * @throws FactoryConfigurationError
	 */
	private Map<String, Object> parseXml(InputStream is)throws FactoryConfigurationError {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(is);
			Dom2Map root = Dom2Map.parse(doc);

			String position = root.get("ad").attr("position");
			data.put("position", position);
			Log.i(TAG, "position-->" + position);

			String type = root.get("ad").attr("type");
			data.put("type", type);
			Log.i(TAG, "type-->" + type);

			String title = root.get("ad").get("title").value();
			data.put("title", title);
			Log.i(TAG, "title-->" + title);

			String interval = root.get("ad").get("images").attr("interval");
			data.put("interval", interval);
			Log.i(TAG, "interval-->" + interval);

			ArrayList<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
			ArrayList<Dom2Map> nodes = root.get("ad").get("images")
					.get("image").getGroup();
			Log.i(TAG, "images.size-->" + nodes.size());
			for (Dom2Map node : nodes) {
				HashMap<String, String> image = new HashMap<String, String>();
				image.put("url", node.attr("url"));
				image.put("anim", node.attr("anim"));
				Log.i(TAG,
						"anim-->" + node.attr("anim") + "--url-->"
								+ node.attr("url"));
				images.add(image);
			}
			data.put("images", images);

			HashMap<String, String> text = new HashMap<String, String>();
			String anim = root.get("ad").get("text").attr("anim");
			text.put("anim", anim);
			String direction = root.get("ad").get("text").attr("direction");
			text.put("direction", direction);
			String text_position = root.get("ad").get("text").attr("position");
			text.put("position", text_position);
			String scroll = root.get("ad").get("text").attr("scroll");
			text.put("scroll", scroll);
			String value = root.get("ad").get("text").value();
			text.put("value", value);
			Log.i(TAG, "Text :anim-->" + anim + "-direction-->" + direction
					+ "-position->" + position + "-scroll->" + scroll
					+ "-value->" + value);
			data.put("text", text);

			String contact = root.get("ad").get("contact").value();
			data.put("contact", contact);
			Log.i(TAG, "contact-->" + contact);

			String phone = root.get("ad").get("phone").value();
			data.put("phone", phone);
			Log.i(TAG, "phone-->" + phone);

			String address = root.get("ad").get("address").value();
			data.put("address", address);
			Log.i(TAG, "address-->" + address);

			String email = root.get("ad").get("email").value();
			data.put("email", email);
			Log.i(TAG, "email-->" + email);

			String website = root.get("ad").get("website").value();
			data.put("website", website);
			Log.i(TAG, "website-->" + website);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private void setRelationBy(View adView, ViewFlipper imageVF,
			ViewFlipper textVF, String textposition) {

		RelativeLayout.LayoutParams userParams = new RelativeLayout.LayoutParams(
				image_w, ad_height - image_h);
		RelativeLayout userInfo = (RelativeLayout) adView
				.findViewById(R.id.userinfo);

		if (textposition.equals("left")) {
			textParams.width = 100;
			textParams.height = ad_height;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else{
				textParams.addRule(RelativeLayout.BELOW, R.id.title);
				textParams.height -= title_h;
			}
			userParams.width = ad_width - 100;
			imageParams.width = ad_width - 100;
			image_w = imageParams.width;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			userParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.image);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.RIGHT_OF, R.id.text);
		} else if (textposition.equals("right")) {
			textParams.width = 100;
			textParams.height = ad_height;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
				textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			else
				textParams.addRule(RelativeLayout.BELOW, R.id.title);
			userParams.width = ad_width - 100;
			imageParams.width = ad_width - 100;
			image_w = ad_width - 100;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.LEFT_OF, R.id.text);
		} else if (textposition.equals("top")) {
			textParams.width = ad_width;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			imageParams.addRule(RelativeLayout.BELOW, R.id.text);
			imageParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals("")){
				textParams.height = ad_height - image_h;
				textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			}else{
				textParams.height = ad_height - image_h - title_h;
				textParams.addRule(RelativeLayout.BELOW, R.id.title);
			}
		} else if (textposition.equals("bottom")) {
			textParams.width = ad_width;
			textParams.addRule(RelativeLayout.BELOW, R.id.image);
			if (userInfo != null) {
				userParams.addRule(RelativeLayout.BELOW, R.id.text);
				userInfo.setLayoutParams(userParams);
			}
			TextView title = (TextView) adView.findViewById(R.id.title);
			if (title == null || title.getText().equals(""))
			{
				imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				textParams.height = ad_height - image_h;
			}else{
				imageParams.addRule(RelativeLayout.BELOW, R.id.title);
				textParams.height = ad_height - image_h - title_h;
			}
		}
		if (userInfo != null)
			userInfo.setLayoutParams(userParams);
		imageVF.setLayoutParams(imageParams);
		textVF.setLayoutParams(textParams);
	}

	/**
	 * full
	 * 
	 * @param v
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_4(View v, Map<String, Object> data) {

		String title = (String) data.get("title");
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data
				.get("images");
		@SuppressWarnings("unchecked")
		HashMap<String, String> text = ((HashMap<String, String>) data
				.get("text"));
		String contact = (String) data.get("contact");
		String phone = (String) data.get("phone");
		String address = (String) data.get("address");
		String email = (String) data.get("email");
		String website = (String) data.get("website");

		TextView titleTv = (TextView) v.findViewById(R.id.title);
		TextView contactTv = (TextView) v.findViewById(R.id.contact);
		TextView phoneTv = (TextView) v.findViewById(R.id.phone);
		TextView adsTv = (TextView) v.findViewById(R.id.address);
		TextView emailTv = (TextView) v.findViewById(R.id.email);
		TextView webTv = (TextView) v.findViewById(R.id.website);

		titleTv.setText(title);
		contactTv.setText(contact);
		phoneTv.setText(phone);
		adsTv.setText(address);
		adsTv.requestFocus();
		emailTv.setText(email);
		webTv.setText(website);

		ViewFlipper imageVF = (ViewFlipper) v.findViewById(R.id.image);
		ViewFlipper textVF = (ViewFlipper) v.findViewById(R.id.text);

		setRelationBy(v, imageVF, textVF, text.get("position"));

		imageTransfer(imageVF, images,
				Integer.parseInt(data.get("interval").toString()));
		textTransfer(textVF, text);
	}

	/**
	 * image + text
	 * 
	 * @param v2
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_3(View v, Map<String, Object> data) {
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(data.get("title").toString());
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data
				.get("images");
		@SuppressWarnings("unchecked")
		HashMap<String, String> text = ((HashMap<String, String>) data
				.get("text"));

		ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.image);
		ViewFlipper textView = (ViewFlipper) v.findViewById(R.id.text);

		setRelationBy(v, vf, textView, text.get("position"));

		imageTransfer(vf, images,
				Integer.parseInt(data.get("interval").toString()));
		textTransfer(textView, text);

	}

	/**
	 * text only
	 * 
	 * @param v2
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_2(View v, Map<String, Object> data) {
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(data.get("title").toString());
		ViewFlipper textView = (ViewFlipper) v.findViewById(R.id.text);
		@SuppressWarnings("unchecked")
		HashMap<String, String> text = ((HashMap<String, String>) data
				.get("text"));
		textParams.width = ad_width;
		textParams.height = ad_height - title.getHeight();
		textTransfer(textView, text);
	}

	/**
	 * image Only
	 * 
	 * @param v
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_1(View v, Map<String, Object> data) {
		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(data.get("title").toString());
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data
				.get("images");
		ViewFlipper vf = (ViewFlipper) v.findViewById(R.id.image);
		imageParams.width = ad_width;
		imageParams.height = ad_height;
		imageTransfer(vf, images,Integer.parseInt(data.get("interval").toString()));
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ChangeImage:
				setImageAnimation(imageVF,images);
				break;
//			case ChangeText:
//				setTextAnimation(textVF,direction,strs);
//				break;
			}
		}
	};
	
	TimerTask imagetask = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = ChangeImage;
			handler.sendMessage(message);
		}
	};
	
//	TimerTask tasktext = new TimerTask() {
//		public void run() {
//			Message message = new Message();
//			message.what = ChangeText;
//			handler.sendMessage(message);
//		}
//	};
//	

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
	private void imageTransfer(final ViewFlipper vf,final List<HashMap<String, String>> images, int delay) {
		this.imageVF = vf;
		this.images = images;
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
						iv.setAlpha(170);
						iv.setLayoutParams(imageParams);
						iv.setScaleType(ScaleType.FIT_XY);
						iv.setImageBitmap(result);
						iv.setAnimation(AnimationUtils.loadAnimation(mContext,
								R.anim.push_in_left));
						vf.addView(iv);
					}
					super.onPostExecute(result);
				}
			}.execute();
			
		}
			Timer imagetimer = new Timer();
			imagetimer.schedule(imagetask, delay * 1000, delay * 1000);
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
	private void textTransfer(final ViewFlipper vf, HashMap<String, String> text) {
		 AdText at = new AdText(mContext,vf,text,textParams.width,textParams.height,getAdTextSize());
		 at.start();
	}
	
	

	private void setImageAnimation(ViewFlipper vf,List<HashMap<String, String>> images) {
		vf.clearAnimation();
		mCurrentPhotoIndex = mCurrentPhotoIndex % (images.size());
		String animPosition = images.get(mCurrentPhotoIndex).get("anim");
		// 判断动画显示的方向
		if ("left".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[0]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[1]));
		} else if ("right".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[2]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[3]));
		} else if ("top".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[4]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[5]));
		} else if ("bottom".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[6]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[7]));
		}
		vf.showNext();
		mCurrentPhotoIndex++;
	}
	
	/**
	 * you can set the ad size or not
	 * 
	 * @param width
	 * @param height
	 */
	public void setAdSize(int width, int height) {
		ad_width = width;
		ad_height = height;
	}

	/**
	 * set imageView w and h
	 * 
	 * @param w
	 * @param h
	 */
	public void setImageSize(int w, int h) {
		imageParams.width = w;
		imageParams.height = h;
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
		if (adViewParent != null)
			adViewParent.removeAllViews();
	}



	
	public float getAdTextSize() {
		return textSize;
	}

	public void setAdTextSize(float textSize) {
		this.textSize = textSize;
	}
}
