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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class AdCreator {

	private static final String TAG = "AdCreator";

	public static final int AD_TYPE_IMAGE_ONLY = 1;
	public static final int AD_TYPE_TEXT_ONLY = 2;
	public static final int AD_TYPE_IMAGE_AND_TEXT = 3;
	public static final int AD_TYPE_FULL = 4;

	public static final int ERROR_NETWORK_NOT_ENABLED = 101;
	public static final int ERROR_AD_TYPE = 102;
	public static final int ERROR_URL= 103;
	public static final int ERROR_START= 104;

	private Activity mContext;
	public RelativeLayout adViewParent;
	private CallBack mCallback;
	private String mAdUrl;
	private int ad_width = 300;
	private int ad_height = 200;

    private static final int POLL = 100;
	private static final int STOP = POLL + 100;
	private  Timer timer;
	private int mCurrentPhotoIndex = 0;
	private Handler handler;

	public AdCreator(Activity context,String adUrl,CallBack callback) {
		mContext = context;
		mCallback = callback;
		mAdUrl = adUrl;
		adViewParent = new RelativeLayout(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_PARENT_TOP);
		mContext.addContentView(adViewParent,params );
	}

	public void start(){
		if(!checkNetwork(mContext)){
			mCallback.onError(new Exception("the network is not enabled！！"), ERROR_NETWORK_NOT_ENABLED);
			Log.w(TAG, "the network is not enabled！！the no network model has been started");
			startNoNetworkModel();
			return;
		}
		if(mAdUrl.equals("")||mAdUrl==null){
			mCallback.onError(new Exception("the AdUrl is null "), ERROR_URL);
			Log.w(TAG, "the AdUrl is null ");
			startNoNetworkModel();
			return;
		}
		URL url;
		Map<String, Object> data = null;
		try {
			url = new URL(mAdUrl);
			data = parseXml(url.openConnection().getInputStream());
		} catch (Exception e1) {
			mCallback.onError(e1,ERROR_START);
			e1.printStackTrace();
		}
		String type = (String) data.get("type");//tbd
		if (type=="1") {//image only
			showAdType_1(data);
		} else if (type=="2") {//text only
			showAdType_2(data);
		} else if(type=="3"){//image + text
			showAdType_3(data);
		}else if(type=="4"){//full
			showAdType_4(data);
		}
	}

	/**
	 * 貌似没必要、、TBD
	 */
	private void startNoNetworkModel() {
		Map<String, Object> data = null;
		try {
			InputStream in = null;
			in = getClass().getClassLoader().getResourceAsStream("data4.xml");
			data = parseXml(in);
		} catch (Exception e1) {
			mCallback.onError(e1,ERROR_START);
			e1.printStackTrace();
		}
		int type = Integer.parseInt(data.get("type").toString());//tbd
		if (type==1) {//image only
			showAdType_1(data);
		} else if (type==2) {//text only
			showAdType_2(data);
		} else if(type==3){//image + text
			showAdType_3(data);
		}else if(type==4){//full
			showAdType_4(data);
		}
	}

	public CallBack getmCallback() {
		return mCallback;
	}


	public interface CallBack{
		public void onError(Exception e,int errorCode);
	}


	/**
	 * Check the network connection is available
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwi = cm.getActiveNetworkInfo();
		if(nwi!=null){
			return nwi.isAvailable();
		}
		return false;
	}


	/**
	 * Dynamic layout
	 * @param position
	 * @param layoutId
	 * @return
	 */
	private View setAdByPosition(String position, int layoutId) {
		View v = LinearLayout.inflate(mContext, layoutId, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ad_width,ad_height);
		if(position.equals("left")){
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		}else if(position.equals("right")){
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		}else if(position.equals("top")){
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		}else if(position.equals("bottom")){                     //没有data.xml文件设置过position为bottom的，这个条件有用吗？
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		}else if(position.equals("center")){
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		} else
			try {
				throw new Exception("unKnown position-->"+position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		v.setLayoutParams(params);
		return v;
	}

	/**
	 * initialize
	 * @param mKind
	 * @return data
	 * @throws FactoryConfigurationError
	 */
	private Map<String, Object> parseXml(InputStream is) throws FactoryConfigurationError {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			Dom2Map root = Dom2Map.parse(doc);

			String position = root.get("ad").attr("position");
			data.put("position", position);
			Log.i(TAG,"position-->" + position);

			String type = root.get("ad").attr("type");
			data.put("type", type);
			Log.i(TAG,"type-->" + type);


			String title = root.get("ad").get("title").value();
			data.put("title", title);
			Log.i(TAG,"title-->" + title);

			String interval = root.get("ad").get("images").attr("interval");
			data.put("interval", interval);
			Log.i(TAG,"interval-->" + interval);

			ArrayList<HashMap<String,String>> images = new ArrayList<HashMap<String,String>>();
			ArrayList<Dom2Map> nodes = root.get("ad").get("images").get("image").getGroup();
			Log.i(TAG,"images.size-->" + nodes.size());
			for (Dom2Map node : nodes) {
				HashMap<String,String> image = new HashMap<String, String>();
				image.put("url", node.attr("url"));
				image.put("anim", node.attr("anim"));
				Log.i(TAG,"anim-->"+node.attr("anim")+"--url-->" + node.attr("url"));
				images.add(image);
			}
			data.put("images", images);

			HashMap<String,String> text = new HashMap<String, String>();
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
			Log.i(TAG,"Text :anim-->"+anim+"-direction-->"+direction+"-position->"+position+"-scroll->"+scroll+"-value->"+value);
			data.put("text", text);


			String contact = root.get("ad").get("contact").value();
			data.put("contact", contact);
			Log.i(TAG,"contact-->" + contact);

			String phone = root.get("ad").get("phone").value();
			data.put("phone", phone);
			Log.i(TAG,"phone-->" + phone);

			String address = root.get("ad").get("address").value();
			data.put("address", address);
			Log.i(TAG,"address-->" + address);

			String email = root.get("ad").get("email").value();
			data.put("email", email);
			Log.i(TAG,"email-->" + email);

			String website = root.get("ad").get("website").value();
			data.put("website", website);
			Log.i(TAG,"website-->" + website);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * full
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_4(Map<String, Object> data) {
		String position = (String) data.get("position");
		View v = setAdByPosition(position,R.layout.fourth);
		adViewParent.addView(v);

		String title = (String) data.get("title");
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data.get("images");
		@SuppressWarnings("unchecked")
		HashMap<String,String> text = ((HashMap<String, String>) data.get("text"));
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
		emailTv.setText(email);
		webTv.setText(website);

		ViewFlipper iv = (ViewFlipper) v.findViewById(R.id.image);
		imageTransfer(iv,images,Integer.parseInt(data.get("interval").toString()));
		TextView textView = (TextView) v.findViewById(R.id.text);
		textTransfer(textView,text,R.id.image);
	}


	/**
	 * image + text
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_3(Map<String, Object> data) {
		String position = (String) data.get("position");
		View v = setAdByPosition(position,R.layout.third);//判断此时ad需要按照哪种position来放，并且以third的布局来安排
		adViewParent.addView(v);
		//------------------------image---------------------------
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data.get("images");
		ViewFlipper iv = (ViewFlipper) v.findViewById(R.id.image);
		imageTransfer(iv,images,Integer.parseInt(data.get("interval").toString()));
		//-----------------------text------------------------------
		TextView textView = (TextView) v.findViewById(R.id.text);
		@SuppressWarnings("unchecked")
		HashMap<String,String> text = ((HashMap<String, String>) data.get("text"));
		textTransfer(textView,text,R.id.image);
	}


	/**
	 * text only
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_2(Map<String, Object> data) {
		String position = (String) data.get("position");
		View v = setAdByPosition(position,R.layout.second);
		adViewParent.addView(v);
		TextView textView = (TextView) v.findViewById(R.id.text);
		@SuppressWarnings("unchecked")
		HashMap<String,String> text = ((HashMap<String, String>) data.get("text"));
		textTransfer(textView,text,0);  //对text做一些操作
	}

	/**
	 * image Only
	 * @param data
	 * @param downloader
	 * @param images
	 */
	private void showAdType_1(Map<String, Object> data) {
		String position = (String) data.get("position");
		Log.i(TAG,"position->"+position);
		View v = setAdByPosition(position,R.layout.first);
		adViewParent.addView(v);
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> images = (ArrayList<HashMap<String, String>>) data.get("images");
		ViewFlipper iv = (ViewFlipper) v.findViewById(R.id.image);
		imageTransfer(iv,images,Integer.parseInt(data.get("interval").toString()));  //对image做一些操作
	}

	/**
	 *  Picture switch
	 * @param vf the ViewFlipper to be attached
	 * @param images -one image contain the key "anim" "url"
	 * @param delay -the images transfer interval
	 */
	private void imageTransfer(final ViewFlipper vf,final List<HashMap<String,String>> images,int delay){
		  //显示的动画效果
		  final int animList[] = new int[]{R.anim.push_in_left,R.anim.push_out_left,R.anim.push_in_right,
				  R.anim.push_out_right,R.anim.push_in_top,R.anim.push_out_top,R.anim.push_in_bottom,R.anim.push_out_bottom};
			 for (int i = 0; i < images.size(); i++) {
				 HashMap<String, String> imagesInfo =images.get(i);	
				 final String imagepath=imagesInfo.get("url");
				 /**
				  * 开启子线程，下载对应path相对的bitmap
				  */
				 new AsyncTask<Void, Void, Bitmap>(){
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
						if(result!=null){
								ImageView iv = new ImageView(mContext);
								iv.setLayoutParams(new LayoutParams(ad_width, 100));
								iv.setScaleType(ScaleType.FIT_XY);
								iv.setImageBitmap(result);
								iv.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_in_left));
								vf.addView(iv);
							}
						super.onPostExecute(result);
					}
				 }.execute();
			}
			 handler = new Handler() {
				 public void handleMessage(Message msg) {
					 super.handleMessage(msg);
					 switch (msg.what) {
					 case POLL:
						 vf.clearAnimation();
						 mCurrentPhotoIndex = mCurrentPhotoIndex % (images.size());
						 Log.d("current", "mCurrentPhotoIndex:>>>" + mCurrentPhotoIndex);
						 String animPosition =images.get(mCurrentPhotoIndex).get("anim");
						 //判断动画显示的方向
						 if("left".equals(animPosition)){
						 vf.clearAnimation();
						 vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[0]));
						 vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[1]));
						 }
						 else if("right".equals(animPosition)){
							 vf.clearAnimation();
							 vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[2]));
							 vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[3]));
						 }
						 else if("top".equals(animPosition)){
							 vf.clearAnimation();
							 vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[4]));
							 vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[5]));
						 }
						 else if("bottom".equals(animPosition)){
							 vf.clearAnimation();
							 vf.setInAnimation(AnimationUtils.loadAnimation(mContext, animList[6]));
							 vf.setOutAnimation(AnimationUtils.loadAnimation(mContext, animList[7]));
						 }
						 vf.showNext();
					     mCurrentPhotoIndex++;
						 break;
					 case STOP:
						 //vf.stopFlipping();
						 break;
					 }
				 }
			 };
			 timer= new Timer();
			 timer.schedule(task, 2000, delay*1000);
	}
	
	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = POLL;
			handler.removeMessages(POLL);
			handler.sendEmptyMessageDelayed(POLL, 500);
		}
	};
	
	private void stopTimer() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		if (task != null) {
			task.cancel();
			task = null;
		}
	}


	/**
	 * Text switch
	 * @param textView
	 * @param anim
	 * @param direction
	 * @param position
	 * @param scroll
	 */
	private void textTransfer(TextView textView,HashMap<String,String> text,int imageId) {
		textView.setTextSize(25);
		textView.setTextColor(Color.WHITE);
		textView.setText(text.get("value"));
		if(imageId!=0){
			String position = text.get("position");
			RelativeLayout.LayoutParams params = null;
			if(position.equals("left")){
				params = new RelativeLayout.LayoutParams(28,ad_height);
				params.addRule(RelativeLayout.LEFT_OF,imageId);
			}else if(position.equals("right")){
				params = new RelativeLayout.LayoutParams(28,ad_height);
				params.addRule(RelativeLayout.RIGHT_OF,imageId);
			}else if(position.equals("top")){
				params = new RelativeLayout.LayoutParams(ad_width,28);
				params.addRule(RelativeLayout.ABOVE,imageId);
				params.addRule(RelativeLayout.ALIGN_LEFT,imageId);
			}else if(position.equals("bottom")){
				params = new RelativeLayout.LayoutParams(ad_width,28);
				params.addRule(RelativeLayout.BELOW,imageId);
				params.addRule(RelativeLayout.ALIGN_LEFT,imageId);
			}else
				try {
					throw new Exception("unDefined text position-->"+position);
				} catch (Exception e) {
					e.printStackTrace();
				}
			textView.setLayoutParams(params);
		}
		String anim = text.get("anim");
		String direction = text.get("direction");
		String scroll = text.get("scroll");
		
		if(anim.equals("left")){
			textView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_in_left));
		}else if(anim.equals("right"))
		{
			textView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_in_right));
		}
	}

	/**
	 * you can set the ad size or not
	 * @param width
	 * @param height
	 */
	public void setAdSize(int width, int height) {
		ad_width = width;
		ad_height = height;
	}
	 /**
     * Downloader imager
     * @param path
     * @return
     * @throws Exception
     */
  private Bitmap getBitMap(String path) throws Exception{
    	URL url =new  URL(path);
    	URLConnection connection = url.openConnection();
    	InputStream inputStream = connection.getInputStream();
    	Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    	return bitmap;
    }

}