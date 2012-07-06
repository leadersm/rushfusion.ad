package com.rushfusion.ad;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AdPage extends BasePage {
	AdCreator creator1;
	String url = "";

	int w = -1;
	int h = -1;
	
	@Override
	public void run() {
		System.out.println("------------------->run<----------------------");
		System.out.println("----------------->online测试版本<--------------");
		creator1 = new AdCreator(mContainer, mContext, url,
				new AdCreator.CallBack() {

					@Override
					public void onError(Exception e, int errorCode) {
						// TODO Auto-generated method stub
						System.out.println(e.getMessage());
					}
				});
		if(w!=-1&&h!=-1)creator1.setAdSize(w, h);
		creator1.start();
	}
	
	@Override
	public void onParseXml(Node params) {
//		int count = 0;
		int i = 0;
		System.out.println("------------------->onParseParams<----------------------");
		if (params != null) {
			Node node = params.getFirstChild();
			System.out.println("params.getChildNodes().getLength()==========>"+params.getChildNodes().getLength());
			while (node != null) {//&&count<params.getChildNodes().getLength()
//				count++;
				if ("param".equalsIgnoreCase(node.getNodeName())) {
					NamedNodeMap attrs = node.getAttributes();
					Node name = attrs.getNamedItem("name");
					if (name != null) {
						Node value = attrs.getNamedItem("value");
						if ("url".equalsIgnoreCase(name.getNodeValue())) {
							if (value != null) {
								url = value.getNodeValue();
								System.out.println("url---->" + url);
								if(params.getChildNodes().getLength()==1)break;
									
							}
						}else if("width".equalsIgnoreCase(name.getNodeValue())){
							if (value != null) {
								w = Integer.parseInt(value.getNodeValue());
								System.out.println("w--"+i+"-->" + w);
								i++;
								if(i==2)break;
							}
						}else if("height".equalsIgnoreCase(name.getNodeValue())){
							if (value != null) {
								h = Integer.parseInt(value.getNodeValue());
								System.out.println("h--"+i+"-->" + h);
								i++;
								if(i==2)break;
							}
						}
					}
				}
				node = node.getNextSibling();
			}
		}
	}
	
	
	

}
