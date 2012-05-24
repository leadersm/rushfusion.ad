package com.rushfusion.ad;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class IAdActivity implements Runnable {
	/** Called when the activity is first created. */

	boolean isShowing1 = false;
	AdCreator creator1;
	Context mContext;
	ViewGroup mContainer = null;
	String url = "";
	Node mParams = null;
	
	public void init(Context context, ViewGroup container, Node params) {
		mContext = context;
		mContainer = container;
		mParams = params;
		parse();
	}

	public void run() {
		mContainer.post(r1);
	}

	private void parse() {
		if (mParams != null) {
			Node node = mParams.getFirstChild();
			while (node != null) {
				if ("param".equalsIgnoreCase(node.getNodeName())) {
					NamedNodeMap attrs = node.getAttributes();
					Node name = attrs.getNamedItem("name");
					if (name != null) {
						if ("url".equalsIgnoreCase(name.getNodeValue())) {
							Node value = attrs.getNamedItem("value");
							if (value != null) {
								url = value.getNodeValue();
								break;
							}
						}
					}
				}
				node = node.getNextSibling();
			}
		}
	}
	
	Runnable r1 = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			creator1 = new AdCreator(mContainer, mContext, url,new AdCreator.CallBack() {

						@Override
						public void onError(Exception e, int errorCode) {
							// TODO Auto-generated method stub
							System.out.println(e.getMessage());
						}
					});
			creator1.setAdSize(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			creator1.start();
		}
	};

	// Runnable r2 = new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// creator2 = new AdCreator(mContainer,(Activity)mContext, "", new
	// AdCreator.CallBack() {
	//
	// @Override
	// public void onError(Exception e,
	// int errorCode) {
	// // TODO Auto-generated method stub
	// System.out.println(e.getMessage());
	// }
	// });
	// creator2.TEST_XML = "data2.xml";
	// creator2.setAdSize(500, 100);
	// creator2.start();
	// }
	// };

}
