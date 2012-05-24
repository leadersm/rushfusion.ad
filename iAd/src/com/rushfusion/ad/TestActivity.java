package com.rushfusion.ad;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

public class TestActivity extends Activity {
	
	Activity mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad);
        ViewGroup mRoot = (ViewGroup) getWindow().getDecorView();
        AdPage ad1 = new AdPage();
        ad1.init(mContext, mRoot,parseXml("ad1.txt"));
        ad1.run();			
//        AdPage ad2 = new AdPage();
//        ad2.init(mContext, mRoot,parseXml("ad2.xml"));
//        ad2.run();			
//        AdPage ad3 = new AdPage();
//        ad3.init(mContext, mRoot,parseXml("ad3.xml"));
//        ad3.run();			
//        AdPage ad4 = new AdPage();
//        ad4.init(mContext, mRoot,parseXml("ad4.xml"));
//        ad4.run();			
    }
    
    
    public Node parseXml(String assetsFile){
    	try {
			InputStream inputStream = getResources().getAssets().open(assetsFile);
			 Document doc = null;
			 doc = DocumentBuilderFactory.newInstance()
			 .newDocumentBuilder().parse(inputStream);
			 inputStream.close();
			 
			 Node item = doc.getFirstChild();
			 while(item != null){
				 if(item.getNodeType() == Node.ELEMENT_NODE && "iapplication".equals(item.getNodeName())){
					 return item;
				 }
				 item = item.getNextSibling();
			 }
		} catch (IOException e) {

			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return null;
    }
   
}