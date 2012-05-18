package com.rushfusion.ad.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.w3c.dom.Document;

import android.util.Log;

import com.rushfusion.ad.Ad;

public class AdXmlParser {
	
	private static final String TAG = "AdXmlParser";
	
	public static HashMap<String,Object> parseXml(InputStream is)throws FactoryConfigurationError {
		HashMap<String,Object> result = new HashMap<String, Object>();
		List<Ad> Ads = new ArrayList<Ad>();
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			Dom2Map root = Dom2Map.parse(doc);
			result.put("interval", Integer.parseInt(root.get("ads").attr("interval")));
			for(Dom2Map tempAd:root.get("ads").get("ad").getGroup()){
				Ad ad = new Ad();
				//-------------------------------------------------------
				String position = tempAd.attr("position");
				if(!position.equals(""))
				ad.setPosition(Integer.parseInt(position));
				//-------------------------------------------------------
				String type = tempAd.attr("type");
				if(!type.equals(""))
				ad.setType(Integer.parseInt(type));
				//-------------------------------------------------------
				String playtime = tempAd.attr("playtime");
				if(!playtime.equals(""))
				ad.setPlayTime(Integer.parseInt(playtime));
				//-------------------------------------------------------
				String times = tempAd.attr("times");
				if(!times.equals(""))
				ad.setTimes(Integer.parseInt(times));
				//-------------------------------------------------------
				String title = tempAd.get("title").value();
				ad.setTitle(title);
				//-------------------------------------------------------
				String interval = tempAd.get("images").attr("interval");
				if(!interval.equals(""))
				ad.setImageInterval(Integer.parseInt(interval));
				//-------------------images-----------------------------
				ArrayList<HashMap<String, String>> images = new ArrayList<HashMap<String, String>>();
				ArrayList<Dom2Map> nodes = tempAd.get("images").get("image").getGroup();
				for (Dom2Map node : nodes) {
					HashMap<String, String> image = new HashMap<String, String>();
					image.put("url", node.attr("url"));
					image.put("anim", node.attr("anim"));
					images.add(image);
				}
				ad.setImages(images);
				//-------------------content-----------------------------
				HashMap<String, String> content = new HashMap<String, String>();
				String anim = tempAd.get("content").attr("anim");
				content.put("anim", anim);
				String direction = tempAd.get("content").attr("direction");
				content.put("direction", direction);
				String text_position = tempAd.get("content").attr("position");
				content.put("position", text_position);
				String scroll = tempAd.get("content").attr("scroll");
				content.put("scroll", scroll);
				String value = tempAd.get("content").value();
				content.put("value", value);
				ad.setContent(content);
				//-------------------company-----------------------------
				HashMap<String,String> company = new HashMap<String, String>();
				String contact = tempAd.get("company").get("contact").value();
				company.put("contact", contact);
				String phone = tempAd.get("company").get("phone").value();
				company.put("phone", phone);
				String address = tempAd.get("company").get("address").value();
				company.put("address", address);
				String email = tempAd.get("company").get("email").value();
				company.put("email", email);
				String website = tempAd.get("company").get("website").value();
				company.put("website", website);
				ad.setCompany(company);
				//-------------------------------------------------------
				String links = tempAd.get("links").value();
				ad.setLinks(links);
				//-------------------------------------------------------
				Log.i(TAG, ad.toString());
				Ads.add(ad);
			}
			result.put("ads", Ads);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
