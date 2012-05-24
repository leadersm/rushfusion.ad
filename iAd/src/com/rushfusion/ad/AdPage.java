package com.rushfusion.ad;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.view.KeyEvent;
import android.view.View;

public class AdPage extends BasePage {

	String url = "";
	int w = 300;
	int h = 300;
	
	@Override
	public void run() {
		AdCreator creator1 = new AdCreator(mContainer, mContext, url,
				new AdCreator.CallBack() {

					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});

		creator1.setAdSize(w,h);//应该从配置参数传过来？跟url一样、、tbd 默认广告大小300*300
		creator1.start();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	public void onParseXml(Node params) {
		if (params != null) {
			Node node = params.getFirstChild();
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
						if ("width".equalsIgnoreCase(name.getNodeValue())) {
							Node value = attrs.getNamedItem("value");
							if (value != null) {
								w = Integer.parseInt(value.getNodeValue());
								break;
							}
						}
						if ("height".equalsIgnoreCase(name.getNodeValue())) {
							Node value = attrs.getNamedItem("value");
							if (value != null) {
								h = Integer.parseInt(value.getNodeValue());
								break;
							}
						}
					}
				}
				node = node.getNextSibling();
			}
		}
	}

}
