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
import android.view.View;
import android.view.ViewGroup;

public class TestActivity extends Activity {
	
	Activity mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.testButton).setOnClickListener(new View.OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		setContentView(R.layout.main);
        		ViewGroup mRoot = (ViewGroup) getWindow().getDecorView();
        		IAdActivity y = new IAdActivity();
        		y.init(mContext, mRoot);
        		y.run();			
        		
        	}
        });
        
    }
    
   
}