package com.rushfusion.ad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ad {

	private int position;//广告的位置
	private int type;//广告类型
	private int playTime;//广告播放时间
	private int times;//广告播放次数
	private String title;//广告标题
	private ArrayList<HashMap<String,String>> images;//广告的图片
	private int imageInterval;//广告的图片切换时间
	private HashMap<String,String> content;//广告的内容
	private HashMap<String,String> company;//哪个公司
	private String links;//广告的链接
	
	public Ad() {
		
	}
	
	/**
	 * if the type is 1  ，use this constructor
	 * @param position
	 * @param type
	 * @param playTime
	 * @param times
	 * @param title
	 * @param images
	 * @param links
	 */
	public Ad(int position, int type, int playTime, int times, String title,
			ArrayList<HashMap<String, String>> images, String links) {
		super();
		this.position = position;
		this.type = type;
		this.playTime = playTime;
		this.times = times;
		this.title = title;
		this.images = images;
		this.links = links;
	}


	/**
	 * if the type is 2  ，use this constructor
	 * @param position
	 * @param type
	 * @param playTime
	 * @param times
	 * @param title
	 * @param content
	 * @param links
	 */
	public Ad(int position, int type, int playTime, int times, String title,HashMap<String,String> content, String links) {
		super();
		this.position = position;
		this.type = type;
		this.playTime = playTime;
		this.times = times;
		this.title = title;
		this.content = content;
		this.links = links;
	}


	

	/**
	 * if the type is 3 or 4  ，use this constructor
	 * @param position
	 * @param type
	 * @param playTime
	 * @param times
	 * @param title
	 * @param images
	 * @param imageInterval
	 * @param content
	 * @param company
	 * @param links
	 */
	public Ad(int position, int type, int playTime, int times, String title,
			ArrayList<HashMap<String, String>> images, int imageInterval,
			HashMap<String,String> content, HashMap<String, String> company, String links) {
		super();
		this.position = position;
		this.type = type;
		this.playTime = playTime;
		this.times = times;
		this.title = title;
		this.images = images;
		this.imageInterval = imageInterval;
		this.content = content;
		this.company = company;
		this.links = links;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPlayTime() {
		return playTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<HashMap<String, String>> getImages() {
		return images;
	}

	public void setImages(ArrayList<HashMap<String, String>> images) {
		this.images = images;
	}

	public int getImageInterval() {
		return imageInterval;
	}

	public void setImageInterval(int imageInterval) {
		this.imageInterval = imageInterval;
	}

	public HashMap<String,String> getContent() {
		return content;
	}

	public void setContent(HashMap<String,String> content) {
		this.content = content;
	}

	public HashMap<String, String> getCompany() {
		return company;
	}

	public void setCompany(HashMap<String, String> company) {
		this.company = company;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	@Override
	public String toString() {
		return "Ad [position=" + position + ", type=" + type + ", playTime="
				+ playTime + ", times=" + times + ", title=" + title
				+ ", images=" + images + ", imageInterval=" + imageInterval
				+ ", content=" + content + ", company=" + company + ", links="
				+ links + "]";
	}
	
}
