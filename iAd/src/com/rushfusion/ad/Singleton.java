package com.rushfusion.ad;

public class Singleton {
    private int Textsize;
	private static final Singleton instance =new Singleton();
	public static Singleton getInstance() {
		return instance;
	}
	public int getTextsize() {
		return Textsize;
	}
	public void setTextsize(int textsize) {
		Textsize = textsize;
	}
	

}
