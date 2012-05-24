package com.rushfusion.ad;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import android.view.KeyEvent;
import android.view.View;

public class AdPage extends BasePage {
	AdCreator creator1;
	String url = "";
	int w = 300;
	int h = 400;
	String TEST_XML = "";

	@Override
	public void run() {
		creator1 = new AdCreator(mContainer, mContext, url,
				new AdCreator.CallBack() {

					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
		creator1.setAdSize(w, h);// 应该从配置参数传过来？跟url一样、、tbd 默认广告大小300*300
		if (TEST_XML.equals("data2.txt"))
			creator1.setAdTextSize(50);
		creator1.TEST_XML = TEST_XML;
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
						Node value = attrs.getNamedItem("value");
						if ("url".equalsIgnoreCase(name.getNodeValue())) {
							if (value != null) {
								url = value.getNodeValue();
								System.out.println("url---->" + url);
								if (url.equals("ad1")) {
									TEST_XML = "data1.txt";
								} else if (url.equals("ad2")) {
									TEST_XML = "data2.txt";
								} else if (url.equals("ad3")) {
									TEST_XML = "data3.txt";
								} else if (url.equals("ad4")) {
									TEST_XML = "data4.txt";
								}
								url = "";
							}
						}else if("width".equalsIgnoreCase(name.getNodeValue())){
							if (value != null) {
								w = Integer.parseInt(value.getNodeValue());
								System.out.println("w---->" + w);
							}
						}else if("height".equalsIgnoreCase(name.getNodeValue())){
							if (value != null) {
								h = Integer.parseInt(value.getNodeValue());
								System.out.println("h---->" + h);
							}
						}
					}
				}
				node = node.getNextSibling();
			}
		}
	}

}
