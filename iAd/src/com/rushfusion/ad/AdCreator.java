package com.rushfusion.ad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 1、imageOnly i
 * @author rushfusion.lsm
 *
 */
public class AdCreator {

	public String TEST_DATA = "data1.txt";

	private static final String TAG = "AdCreator";

	public static final int AD_TYPE_IMAGE_ONLY = 1;
	public static final int AD_TYPE_TEXT_ONLY = 2;
	public static final int AD_TYPE_IMAGE_AND_TEXT = 3;
	public static final int AD_TYPE_FULL = 4;

	public static final int ERROR_NETWORK_NOT_ENABLED = 101;
	public static final int ERROR_AD_TYPE = 102;
	public static final int ERROR_URL = 103;
	public static final int ERROR_UNKNOWN_POSITION = 104;
	public static final int ERROR_URL_CONNECTION = 105;
	public static final int ERROR_PARSE_DATA = 106;
	public static final int ERROR_ANIM_TYPE = 107;

	private Context mContext;
	public RelativeLayout adViewParent;
	private CallBack mCallback;
	private String mAdUrl;
	// ======================================
	private int ad_width = 300;
	private int ad_height = 300;
	private int image_w = LayoutParams.MATCH_PARENT;
	private int image_h = 150;
	private int text_w = LayoutParams.MATCH_PARENT;
	private int text_h = LayoutParams.MATCH_PARENT;
	private int title_h = 50;
	private int alpha = 210;
	// ================================
	private ViewGroup mContainer;
	private RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
			image_w, image_h);
	private RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
			text_w, text_h);

	private static final int ChangeImage = 201;
	private int animList[] = new int[] { R.anim.push_in_left,
			R.anim.push_out_left, R.anim.push_in_right, R.anim.push_out_right,
			R.anim.push_in_top, R.anim.push_out_top, R.anim.push_in_bottom,
			R.anim.push_out_bottom };

	private ViewFlipper imageVF;
	private ViewFlipper textVF;
	private TextView title;

	private List<HashMap<String, String>> images;
	private int mCurrentPhotoIndex = 0;
	private float textSize = 25;
	private boolean isStart = false;
	private DisplayMetrics dm;
	private WindowManager manager;

	public AdCreator(ViewGroup container, Context context, String adUrl,
			CallBack callback) {
		mContext = context;
		mCallback = callback;
		mContainer = container;
		mAdUrl = adUrl;
		init();
	}

	private void init() {
		adViewParent = new RelativeLayout(mContext);
		adViewParent.setBackgroundColor(mContext.getResources().getColor(R.color.parent_bg));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1,-1);
		mContainer.removeAllViews();
		mContainer.addView(adViewParent, params);
		dm = new DisplayMetrics();
		manager = (WindowManager) mContext.getSystemService(Service.WINDOW_SERVICE);
	}

	public void start() {
		isStart = true;
		if (mAdUrl.equals("") || mAdUrl == null) {
			if (mCallback != null)
				mCallback.onError(new Exception("the AdUrl is null "),
						ERROR_URL);
			Log.w(TAG, "the AdUrl is null ,the DebugModel has been started");
			startDebugModel();
			return;
		}
		URL url;
		Map<String, Object> data = null;
		try {
			url = new URL(mAdUrl);
			data = parseXml(url.openConnection().getInputStream());
		} catch (Exception e1) {
			if (mCallback != null)
				mCallback.onError(e1, ERROR_URL_CONNECTION);
			Log.w(TAG, "url error");
			e1.printStackTrace();
			return;
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
			in = getClass().getClassLoader().getResourceAsStream(TEST_DATA);
			data = parseXml(in);
		} catch (Exception e1) {
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
			if (mCallback != null)
				mCallback.onError(new Exception("type error, type-->" + type),ERROR_AD_TYPE);
			return null;
		}
		View v = getViewByPosition(position, layoutId);

		v.setFocusable(true);
		v.requestFocus();
		ArrayList<View> views = v.getFocusables(View.FOCUSABLES_ALL);
		for (View vv : views) {
			vv.setOnKeyListener(l);
		}
		dispatchViewByType(v, type, data);
		return v;
	}

	View.OnKeyListener l = new View.OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				Log.d(TAG, " KeyEvent: " + event);
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (isStart)
						stop();
				}
			}
			return false;
		}
	};

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
		v.setBackgroundColor(mContext.getResources()
				.getColor(R.color.parent_bg));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ad_width, ad_height);
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
		} else if (position.equals("random")) {
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
		} else if (mCallback != null)
			mCallback.onError(new Exception("unKnown position-->" + position),
					ERROR_UNKNOWN_POSITION);
		v.setLayoutParams(params);
		return v;
	}

	public String convertStreamToString(InputStream is) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * initialize
	 * 
	 * @throws FactoryConfigurationError
	 */
	private Map<String, Object> parseXml(InputStream is)
			throws FactoryConfigurationError {
		Map<String, Object> data = new HashMap<String, Object>();
		String json = convertStreamToString(is);
		try {
			JSONArray ads = new JSONArray(json);
			JSONObject ad = ads.getJSONObject(0);
			String position = ad.getString("position");
			data.put("position", position);
			Log.i(TAG, "position-->" + position);

			String title = ad.getString("title");
			data.put("title", title);
			Log.i(TAG, "title-->" + title);

			String interval = ad.getJSONObject("images").getString("interval");
			data.put("interval", interval);
			Log.i(TAG, "interval-->" + interval);

			String anim = ad.getJSONObject("images").getString("anim");
			data.put("anim", anim);
			Log.i(TAG, "anim-->" + anim);

			ArrayList<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
			int i = 0;
			while (!ad.getJSONObject("images").isNull(i + "")) {
				JSONObject node = ad.getJSONObject("images").getJSONObject(i + "");
				HashMap<String, String> image = new HashMap<String, String>();
				image.put("url", node.getString("url"));
				image.put("anim", anim);
				Log.i(TAG, "url-->" + node.getString("url"));
				images.add(image);
				i++;
			}
			data.put("images", images);

			HashMap<String, String> text = new HashMap<String, String>();
			String textanim = ad.getJSONObject("text").getString("anim");
			text.put("anim", textanim);

			String direction = ad.getJSONObject("text").getString("direction");
			text.put("direction", direction);

			String text_position = ad.getJSONObject("text").getString(
					"position");
			text.put("position", text_position);

			String scroll = ad.getJSONObject("text").getString("scroll");
			text.put("scroll", scroll);

			String value = ad.getJSONObject("text").getString("value");
			text.put("value", value);
			Log.i(TAG, "Text :anim-->" + textanim + " direction-->" + direction
					+ " position->" + text_position + " scroll->" + scroll
					+ " value->" + value);
			data.put("text", text);

			String contact = ad.getString("contact");
			data.put("contact", contact);
			Log.i(TAG, "contact-->" + contact);

			String phone = ad.getString("phone");
			data.put("phone", phone);
			Log.i(TAG, "phone-->" + phone);

			String address = ad.getString("address");
			data.put("address", address);
			Log.i(TAG, "address-->" + address);

			String email = ad.getString("email");
			data.put("email", email);
			Log.i(TAG, "email-->" + email);

			String website = ad.getString("website");
			data.put("website", website);
			Log.i(TAG, "website-->" + website);

			String type = getTypeAndSetAdSize(value, images.size(), contact,title) + "";
			data.put("type", type);
			Log.i(TAG, "type-->" + type);

		} catch (JSONException e1) {
			if (mCallback != null)
				mCallback.onError(new Exception("ERROR_PARSE_DATA"),ERROR_PARSE_DATA);
			Log.w(TAG, "data parse error--");
			e1.printStackTrace();
		}
		return data;
	}
//===============================getTypeAndSetAdSize===================================
	public DisplayMetrics getMetrics() {
		manager.getDefaultDisplay().getMetrics(dm);
		System.out.println("分辨率--->w=" + dm.widthPixels + "  h="+ dm.heightPixels);
		return dm;
	}
	
	private int getTypeAndSetAdSize(String text, int images, String contact,String title) {
		if (text.equals("null")) {
			setAdSize(700*getMetrics().widthPixels/1920, 250*getMetrics().heightPixels/1080);
			return AD_TYPE_IMAGE_ONLY;
		} else if (images <= 0) {
			int H = text.length()>400?110:60;
			setAdSize(getMetrics().widthPixels, H*getMetrics().heightPixels/1080);
			return AD_TYPE_TEXT_ONLY;
		} else if (contact.equals("null")) {
			setAdSize(700*getMetrics().widthPixels/1920, 550*getMetrics().heightPixels/1080);
			return AD_TYPE_IMAGE_AND_TEXT;
		} else{
			int H = title.equals("null")?550:600;
			setAdSize(700*getMetrics().widthPixels/1920, H*getMetrics().heightPixels/1080);
			return AD_TYPE_FULL;
		}
	}
//=======================================================================================

	private void setRelationBy(View adView, ViewFlipper imageVF,
			ViewFlipper textVF, String textposition, int type) {

		RelativeLayout.LayoutParams userParams = new RelativeLayout.LayoutParams(
				imageParams.width, ad_height - imageParams.height);
		userParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		RelativeLayout userInfo = (RelativeLayout) adView
				.findViewById(R.id.userinfo);
		title = (TextView) adView.findViewById(R.id.title);
		if (textposition.equals("left")) {
			textParams.width = ad_width / 3;
			imageParams.width = ad_width - textParams.width;
			if (type == AD_TYPE_IMAGE_AND_TEXT) {
				imageParams.height = ad_height;
				if (title.getText().equals("")) {
					textParams.height = ad_height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					textParams.height = ad_height - title_h;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
				}
			} else if (type == AD_TYPE_FULL) {
				userParams.width = ad_width - textParams.width;
				userParams.height = ad_height / 3;
				if (title.getText().equals("")) {
					textParams.height = ad_height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height - userParams.height
							- title_h / 3;
				} else {
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = ad_height - title_h;
					imageParams.height = ad_height - userParams.height
							- title_h;
				}
				userParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.image);
			}
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.RIGHT_OF, R.id.text);
		} else if (textposition.equals("right")) {
			textParams.width = ad_width / 3;
			imageParams.width = ad_width - textParams.width;
			if (type == AD_TYPE_IMAGE_AND_TEXT) {
				imageParams.height = ad_height;
				if (title.getText().equals("")) {
					textParams.height = ad_height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					textParams.height = ad_height - title_h;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
				}
			} else if (type == AD_TYPE_FULL) {
				userParams.width = ad_width - textParams.width;
				userParams.height = ad_height / 3;
				if (title.getText().equals("")) {
					textParams.height = ad_height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height - userParams.height
							- title_h / 3;
				} else {
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = ad_height - title_h;
					imageParams.height = ad_height - userParams.height
							- title_h;
				}
				userParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			}
			textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.LEFT_OF, R.id.text);
		} else if (textposition.equals("up")) {
			textParams.width = ad_width;
			imageParams.addRule(RelativeLayout.BELOW, R.id.text);
			imageParams.topMargin = 5;
			if (type == AD_TYPE_IMAGE_AND_TEXT) {
				imageParams.height = ad_height / 2;
				if (title.getText().equals("")) {
					textParams.height = ad_height - imageParams.height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				} else {
					textParams.height = ad_height - imageParams.height
							- title_h;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
				}
			} else if (type == AD_TYPE_FULL) {
				imageParams.height = ad_height / 3;
				if (title.getText().equals("")) {
					textParams.height = (ad_height - imageParams.height) / 3;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					userParams.height = ad_height - imageParams.height
							- textParams.height - title_h / 3;
				} else {
					textParams.height = (ad_height - imageParams.height - title_h) / 3;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					userParams.height = ad_height - imageParams.height
							- textParams.height - title_h;
				}
			}
		} else if (textposition.equals("down")) {
			textParams.width = ad_width;
			textParams.addRule(RelativeLayout.BELOW, R.id.image);
			textParams.topMargin = 5;
			if (type == AD_TYPE_IMAGE_AND_TEXT) {
				imageParams.height = ad_height / 2;
				if (title.getText().equals("")) {
					imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					textParams.height = ad_height - imageParams.height;
				} else {
					imageParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = ad_height - imageParams.height
							- title_h;
				}
			} else if (type == AD_TYPE_FULL) {
				imageParams.height = ad_height / 3;
				if (title.getText().equals("")) {
					imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					textParams.height = (ad_height - imageParams.height) / 3;
					userParams.height = ad_height - imageParams.height
							- textParams.height - title_h / 3;
				} else {
					imageParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = (ad_height - imageParams.height - title_h) / 3;
					userParams.height = ad_height - imageParams.height
							- textParams.height - title_h;
				}
			}
		} else {
			if (mCallback != null)
				mCallback.onError(new Exception("text position error-->"
						+ textposition), ERROR_UNKNOWN_POSITION);
			Log.w(TAG, "text position error-->" + textposition);
			return;
		}
		if (type == AD_TYPE_FULL) {
			userInfo.setLayoutParams(userParams);
			userInfo.setGravity(Gravity.CENTER_VERTICAL);
			userInfo.setBackgroundResource(R.drawable.rush_bg);
		}
		if (!title.getText().equals("")) {
			title.setBackgroundResource(R.drawable.title);
		}

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

		if (!data.get("title").toString().equals("null"))
			titleTv.setText(title);

		contactTv.setText(contact);
		phoneTv.setText(phone);
		adsTv.setText(address);
		adsTv.requestFocus();
		emailTv.setText(email);
		webTv.setText(website);

		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		textVF = (ViewFlipper) v.findViewById(R.id.text);

		setRelationBy(v, imageVF, textVF, text.get("position"), 4);

		imageTransfer(imageVF, images,Integer.parseInt(data.get("interval").toString()));
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

		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		textVF = (ViewFlipper) v.findViewById(R.id.text);

		setRelationBy(v, imageVF, textVF, text.get("position"), 3);

		imageTransfer(imageVF, images,Integer.parseInt(data.get("interval").toString()));
		textTransfer(textVF, text);

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
		int title_w = 200;// AdPage.getX(100);
		title = (TextView) v.findViewById(R.id.title);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
				title_w, textParams.height);
		title.setLayoutParams(titleParams);
		if (!data.get("title").equals("null")) {
			title.setBackgroundResource(R.drawable.title);
			title.setText(data.get("title").toString());
		}
		textVF = (ViewFlipper) v.findViewById(R.id.text);
		@SuppressWarnings("unchecked")
		HashMap<String, String> text = ((HashMap<String, String>) data
				.get("text"));
		if (data.get("title").toString().equals("null")) {
			textParams.width = ad_width;
			textParams.height = ad_height;
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		} else {
			textParams.width = ad_width - title_w;
			textParams.height = ad_height;
			textParams.addRule(RelativeLayout.RIGHT_OF, R.id.title);
			titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		textTransfer(textVF, text);
	}

	/**
	 * image Only
	 * 
	 * @param v
	 * @param data
	 * @param downloader
	 * @param images
	 */
	@SuppressWarnings("unchecked")
	private void showAdType_1(View v, Map<String, Object> data) {
		title = (TextView) v.findViewById(R.id.title);
		if (!data.get("title").toString().equals("null")) {
			title.setBackgroundResource(R.drawable.title);
			title.setText(data.get("title").toString());
		}
		this.images = (ArrayList<HashMap<String, String>>) data.get("images");
		imageVF = (ViewFlipper) v.findViewById(R.id.image);
		if (data.get("title").toString().equals("null")) {
			imageParams.width = ad_width;
			imageParams.height = ad_height;
			imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		} else {
			imageParams.width = ad_width;
			imageParams.height = ad_height - title_h;
			imageParams.addRule(RelativeLayout.BELOW, R.id.title);
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}
		imageTransfer(imageVF, images,Integer.parseInt(data.get("interval").toString()));
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ChangeImage:
				setImageAnimation(imageVF, images);
				break;
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
		vf.setLayoutParams(imageParams);
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
					ImageView iv = new ImageView(mContext);
					if (result != null) {
						iv.setImageBitmap(result);
					} else
						iv.setImageResource(R.drawable.error);
					iv.setAlpha(getAlpha());
					iv.setLayoutParams(imageParams);
					iv.setScaleType(ScaleType.FIT_XY);
					iv.setAnimation(AnimationUtils.loadAnimation(mContext,
							R.anim.push_in_left));
					vf.addView(iv);
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
		vf.setBackgroundResource(R.drawable.adver_content_pic);
		vf.setLayoutParams(textParams);
		AdText at = new AdText(mContext, vf, text, textParams.width,
				textParams.height, getAdTextSize());
		at.start();
	}

	private void setImageAnimation(ViewFlipper vf,List<HashMap<String, String>> images) {
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
		} else if ("up".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[4]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[5]));
		} else if ("down".equals(animPosition)) {
			vf.clearAnimation();
			vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
					animList[6]));
			vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
					animList[7]));
		} else {
			if (mCallback != null)
				mCallback.onError(new Exception("anim type error-->"
						+ animPosition), ERROR_ANIM_TYPE);
			Log.w(TAG, "image anim type error！！");
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

	private static int NETWORK_CONNECT_TIMEOUT = 10000;
	private static int NETWORK_SO_TIMEOUT = 10000;

	/**
	 * Download image
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Bitmap getBitMap(String url) {
		if (url == null || "".equals(url)) {
			return null;
		}

		HttpParams p = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(p, NETWORK_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(p, NETWORK_SO_TIMEOUT);

		DefaultHttpClient mHttpClient = new DefaultHttpClient(p);

		HttpGet mHttpGet = null;
		try {
			mHttpGet = new HttpGet(url);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		InputStream inputStream = null;
		HttpEntity resEntity = null;
		try {
			HttpResponse response = mHttpClient.execute(mHttpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			resEntity = response.getEntity();
			inputStream = resEntity.getContent();
			int length = (int) resEntity.getContentLength();
			byte[] buffer = new byte[length + 4096];

			int n;
			int rlength = 0;
			while ((n = inputStream.read(buffer, rlength, 40960)) >= 0) {
				rlength += n;
				if (rlength > length) {
					buffer = null;
					inputStream.close();
					return null;
				}
			}
			inputStream.close();
			Bitmap bmp = BitmapFactory.decodeByteArray(buffer, 0, rlength);
			Log.d(TAG,
					"origin w -->" + bmp.getWidth() + " h-->" + bmp.getHeight());
			buffer = null;
			return bmp;
		} catch (OutOfMemoryError oe) {
			if (mCallback != null)
				mCallback.onError(new Exception(
						"downloading image error-->OutOfMemoryError"),
						ERROR_URL_CONNECTION);
			Log.w(TAG, "OutOfMemoryError : " + oe);
			return null;
		} catch (Exception e) {
			if (mCallback != null)
				mCallback.onError(new Exception("downloading image error-->"
						+ e.getMessage()), ERROR_URL_CONNECTION);
			Log.w(TAG, "Exception : " + e);
			return null;
		}
	}

	public void stop() {
		Log.w(TAG, "=========stop=========");
		if (adViewParent != null)
			adViewParent.removeAllViews();
	}

	public float getAdTextSize() {
		return textSize;
	}

	public void setAdTextSize(float textSize) {
		this.textSize = textSize;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

}
