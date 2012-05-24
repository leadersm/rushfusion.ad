package com.rushfusion.ad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.FactoryConfigurationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.ViewGroup;
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
	public static final int ERROR_URL_CONNECTION = 105;
	public static final int ERROR_PARSE_DATA = 106;
	
	
	private Context mContext;
	public RelativeLayout adViewParent;
	private CallBack mCallback;
	private String mAdUrl;
	
	private int ad_width = 300;
	private int ad_height = 300;
	private int image_w = LayoutParams.MATCH_PARENT;
	private int image_h = 150;
	private int text_w;
	private int text_h;
	private int title_h = 20;
	private ViewGroup mparentGroup;
	private RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
			image_w, image_h);
	private RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
			text_w, text_h);

	private static final int ChangeImage = 201;
	private int animList[] = new int[] { R.anim.push_in_left,R.anim.push_out_left,
								R.anim.push_in_right,R.anim.push_out_right, R.anim.push_in_top, 
								R.anim.push_out_top,R.anim.push_in_bottom, R.anim.push_out_bottom };

	private ViewFlipper imageVF;
	private List<HashMap<String, String>> images;
	private int mCurrentPhotoIndex = 0;
	private float textSize = 15;

	public AdCreator(ViewGroup mContainer,Context context, String adUrl, CallBack callback) {
		mContext = context;
		mCallback = callback;
		mAdUrl = adUrl;
		adViewParent = new RelativeLayout(context);
		adViewParent.setBackgroundColor(context.getResources().getColor(R.color.bg));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.ALIGN_PARENT_TOP);
		mparentGroup=mContainer;
		mparentGroup.addView(adViewParent,params);
	}

	public void start() {
		if (!checkNetwork(mContext)) {
			if(mCallback!=null)
			mCallback.onError(new Exception("the network is not enabled！！"),ERROR_NETWORK_NOT_ENABLED);
			Log.w(TAG,"the network is not enabled！！the DebugModel has been started");
			startDebugModel();
			return;
		}
		if (mAdUrl.equals("") || mAdUrl == null) {
			if(mCallback!=null)
			mCallback.onError(new Exception("the AdUrl is null "), ERROR_URL);
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
			if(mCallback!=null)
			mCallback.onError(e1, ERROR_URL_CONNECTION);
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
			in = getClass().getClassLoader().getResourceAsStream("data1.txt");
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
			if(mCallback!=null)
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
			if(mCallback!=null)
			mCallback.onError(new Exception("unKnown position-->" + position),ERROR_UNKNOWN_POSITION);
		v.setLayoutParams(params);
		return v;
	}

	  public String convertStreamToString(InputStream is) {      
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
	         StringBuilder sb = new StringBuilder();      
	     
	         String line = null;      
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
	 * @throws FactoryConfigurationError
	 */
	private Map<String, Object> parseXml(InputStream is)throws FactoryConfigurationError {
		Map<String, Object> data = new HashMap<String, Object>();
		String json = convertStreamToString(is);
		try {
			JSONArray ads = new JSONArray(json);
			JSONObject ad = ads.getJSONObject(0);
			String position = ad.getString("position");
			data.put("position", position);
			Log.i(TAG, "position-->" + position);

			String type = ad.getString("type");
			data.put("type", type);
			Log.i(TAG, "type-->" + type);

			String title = ad.getString("title");
			data.put("title", title);
			Log.i(TAG, "title-->" + title);

			String interval = ad.getJSONObject("images").getString("interval");
			data.put("interval", interval);
			Log.i(TAG, "interval-->" + interval);
			ArrayList<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
			int i=0;
			while(!ad.getJSONObject("images").isNull(i+"")){
				JSONObject node = ad.getJSONObject("images").getJSONObject(i+"");
				HashMap<String, String> image = new HashMap<String, String>();
				image.put("url", node.getString("url"));
				image.put("anim", node.getString("anim"));
				Log.i(TAG,"anim-->" + node.getString("anim") + "--url-->"+ node.getString("url"));
				images.add(image);
				i++;
			}
			data.put("images", images);

			HashMap<String, String> text = new HashMap<String, String>();
			String anim = ad.getJSONObject("text").getString("anim");
			text.put("anim", anim);
			
			String direction = ad.getJSONObject("text").getString("direction");
			text.put("direction", direction);
			
			String text_position = ad.getJSONObject("text").getString("position");
			text.put("position", text_position);
			
			String scroll = ad.getJSONObject("text").getString("scroll");
			text.put("scroll", scroll);
			
			String value = ad.getJSONObject("text").getString("value");
			text.put("value", value);
			Log.i(TAG, "Text :anim-->" + anim + "-direction-->" + direction
					+ "-position->" + position + "-scroll->" + scroll
					+ "-value->" + value);
			data.put("text", text);

			String contact = "contect: "+ad.getString("contact");
			data.put("contact", contact);
			Log.i(TAG, "contact-->" + contact);

			String phone = "phone: "+ad.getString("phone");
			data.put("phone", phone);
			Log.i(TAG, "phone-->" + phone);

			String address = "address: "+ad.getString("address");
			data.put("address", address);
			Log.i(TAG, "address-->" + address);

			String email = "email: "+ad.getString("email");
			data.put("email", email);
			Log.i(TAG, "email-->" + email);

			String website = "website: "+ad.getString("website");
			data.put("website", website);
			Log.i(TAG, "website-->" + website);
			
		} catch (JSONException e1) {
			if(mCallback!=null)
				mCallback.onError(new Exception("ERROR_PARSE_DATA"), ERROR_PARSE_DATA);
			e1.printStackTrace();
		}
		return data;
	}

	private void setRelationBy(View adView, ViewFlipper imageVF,
			ViewFlipper textVF, String textposition,int type) {

		RelativeLayout.LayoutParams userParams = new RelativeLayout.LayoutParams(
				imageParams.width, ad_height - imageParams.height);
		RelativeLayout userInfo = (RelativeLayout) adView
				.findViewById(R.id.userinfo);

		if (textposition.equals("left")) {
			textParams.width = ad_width/3;//100
			textParams.height = ad_height;
			userParams.width = ad_width-textParams.width;//ad_width - 100;
			userParams.height = ad_height/4;
			imageParams.width = ad_width - textParams.width;//ad_width - 100;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if(type == 3){
				if (title == null || title.getText().equals("")){
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height;
				}else{
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height -= title_h;
					imageParams.height = ad_height-title_h;
				}
			}else if(type == 4){
				if (title == null || title.getText().equals("")){
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height - userParams.height;
				}else{
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height -= title_h;
					imageParams.height = ad_height - userParams.height-title_h;
				}
			}
			userParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			userParams.addRule(RelativeLayout.ALIGN_LEFT, R.id.image);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.RIGHT_OF, R.id.text);
		} else if (textposition.equals("right")) {
			textParams.width = ad_width/3;//100
			textParams.height = ad_height;
			userParams.width = ad_width-textParams.width;//ad_width - 100;
			userParams.height = ad_height/4;
			imageParams.width = ad_width - textParams.width;//ad_width - 100;
			TextView title = (TextView) adView.findViewById(R.id.title);
			if(type == 3){
				if (title == null || title.getText().equals("")){
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height;				
				}else{
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height -= title_h;
					imageParams.height = ad_height-title_h;
				}
			}else if(type == 4){
				if (title == null || title.getText().equals("")){
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					imageParams.height = ad_height - userParams.height;				
				}else{
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height -= title_h;
					imageParams.height = ad_height - userParams.height-title_h;
				}
			}
			
			userParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//RelativeLayout.BELOW, R.id.image
			textParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imageParams.addRule(RelativeLayout.ALIGN_TOP, R.id.text);
			imageParams.addRule(RelativeLayout.LEFT_OF, R.id.text);
		} else if (textposition.equals("top")) {
			textParams.width = ad_width;
			userParams.addRule(RelativeLayout.BELOW, R.id.image);
			imageParams.addRule(RelativeLayout.BELOW, R.id.text);
			TextView title = (TextView) adView.findViewById(R.id.title);
			if(type == 3){
				imageParams.height = ad_height/2;
				if (title == null || title.getText().equals("")){
					textParams.height = ad_height - imageParams.height;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}else{
					textParams.height = ad_height - imageParams.height - title_h;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
				}
			}else if(type == 4){
				imageParams.height = ad_height/3;//RelativeLayout.LayoutParams.MATCH_PARENT;
				if (title == null || title.getText().equals("")){
					textParams.height = (ad_height - imageParams.height)/3;
					textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				}else{
					textParams.height = (ad_height - imageParams.height - title_h)/3;
					textParams.addRule(RelativeLayout.BELOW, R.id.title);
				}
			}
		} else if (textposition.equals("bottom")) {
			textParams.width = ad_width;
			textParams.addRule(RelativeLayout.BELOW, R.id.image);
			if (userInfo != null) {
				userParams.addRule(RelativeLayout.BELOW, R.id.text);
				userInfo.setLayoutParams(userParams);
			}
			TextView title = (TextView) adView.findViewById(R.id.title);
			if(type == 3){
				imageParams.height = ad_height/2;
				if (title == null || title.getText().equals(""))
				{
					imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					textParams.height = ad_height - imageParams.height;//textParams.height = ad_height - image_h;
				}else{
					imageParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = ad_height - imageParams.height - title_h;//textParams.height = ad_height - image_h - title_h;
				}
			}else if(type == 4){
				imageParams.height = ad_height/3;
				if (title == null || title.getText().equals(""))
				{
					imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					textParams.height = (ad_height - imageParams.height)/3;//textParams.height = ad_height - image_h;
				}else{
					imageParams.addRule(RelativeLayout.BELOW, R.id.title);
					textParams.height = (ad_height - imageParams.height - title_h)/3;//textParams.height = ad_height - image_h - title_h;
				}
			}else{
				if(mCallback!=null)
					mCallback.onError(new Exception("text position error-->"+textposition), ERROR_UNKNOWN_POSITION);
			}
				
		}
		if (userInfo != null)
			userInfo.setLayoutParams(userParams);
//		imageVF.setLayoutParams(imageParams);
//		textVF.setLayoutParams(textParams);
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
		HashMap<String, String> text = ((HashMap<String, String>) data.get("text"));
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

		setRelationBy(v, imageVF, textVF, text.get("position"),4);

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

		setRelationBy(v, vf, textView, text.get("position"),3);

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
		if(data.get("title").toString().equals("")){
			textParams.width = ad_width;
			textParams.height = ad_height;
			textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else{
			textParams.width = ad_width;
			textParams.height = ad_height - title_h;
		}
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
		if(data.get("title").toString().equals("")){
			imageParams.width = ad_width;
			imageParams.height = ad_height;
			imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}else{
			imageParams.width = ad_width;
			imageParams.height = ad_height-title_h;
			imageParams.addRule(RelativeLayout.BELOW,R.id.title);
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}
		imageTransfer(vf, images,Integer.parseInt(data.get("interval").toString()));
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case ChangeImage:
				setImageAnimation(imageVF,images);
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
					ImageView iv = new ImageView(mContext);
					if (result != null) {
						iv.setImageBitmap(result);
					}else
						iv.setImageResource(R.drawable.error);
					iv.setAlpha(170);
					iv.setLayoutParams(imageParams);
					iv.setScaleType(ScaleType.FIT_XY);
					iv.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_in_left));
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
		vf.setLayoutParams(textParams);
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
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private Bitmap getBitMap(String path) throws Exception {
		URL url = new URL(path);
		URLConnection connection = url.openConnection();
		InputStream inputStream = null;
		try{
			 inputStream = connection.getInputStream();
		}catch(Exception e){
			if(mCallback!=null)
				mCallback.onError(new Exception("downloading image error"), ERROR_URL_CONNECTION);
		}
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
