package com.rushfusion.ad.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Dom2Map {

	
	public static final String VERSION = "0.2.2";
	
	final static int OTHERH_NODE = 0;
	final static int ELEMENT_NODE = Node.ELEMENT_NODE;
	final static int TEXT_NODE = Node.TEXT_NODE;

	final static String TAG_TEXT_NODE = "__TEXT_NODE__";
	final static String TAG_OTHER_NODE = "_OTERH_NODE__";
	
	int mType = OTHERH_NODE;
	String mTag = null;
	String mValue = null;
	int mSize = 0;

	HashMap<String, ArrayList<Dom2Map>> mNamedChildren = null;
	ArrayList<Dom2Map> mGroup = null;

	ArrayList<Dom2Map> mChildren = null;

	HashMap<String, String> mAttrs = null;
	
	public Dom2Map() {
	}

	public Dom2Map(Node node) {
		if (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				mType = ELEMENT_NODE;
				mTag = node.getNodeName();
				NamedNodeMap attrs = node.getAttributes();
				int length = attrs.getLength();
				if( length > 0 ){
					if (mAttrs == null) {
						mAttrs = new HashMap<String, String>();
					}					
				}
				for (int i = 0; i < length; i++) {
					Node attr = attrs.item(i);
					mAttrs.put(attr.getNodeName(), attr.getNodeValue());
				}
			} else if (node.getNodeType() == Node.TEXT_NODE
					|| node.getNodeType() == Node.CDATA_SECTION_NODE) {
				mType = TEXT_NODE;
				mValue = node.getNodeValue();
			} else {
				mTag = TAG_OTHER_NODE;
				mValue = "";
			}
		}
	}

	public boolean validated(){
		return mTag != null;
	}
	
	public int getType() {
		return mType;
	}

	public String getTag() {
		if (mType == ELEMENT_NODE) {
			return mTag;
		} else if (mType == TEXT_NODE) {
			return TAG_TEXT_NODE;
		} else {
			return TAG_OTHER_NODE;
		}
	}

	public int getSize() {
		return mSize;
	}

	public ArrayList<Dom2Map> getGroup() {
		return mGroup;
	}

	public void add(Dom2Map element) {

		String tag = element.getTag();
		if (mNamedChildren == null) {
			mNamedChildren = new HashMap<String, ArrayList<Dom2Map>>();
		}
		ArrayList<Dom2Map> c = mNamedChildren.get(tag);

		if (c == null) {
			c = new ArrayList<Dom2Map>();
			mNamedChildren.put(tag, c);
		}
		if (mChildren == null) {
			mChildren = new ArrayList<Dom2Map>();
		}

		element.mGroup = c;
		c.add(element);
		
		mChildren.add(element);
		mSize++;
	}

	public String value() {
		if (mType == ELEMENT_NODE) {
			if (mValue == null) {
				for (int i = 0; i < mSize; i++) {
					Dom2Map child = mChildren.get(i);
					if ( child != null && child.getType() == Dom2Map.TEXT_NODE ) {
						if (mValue == null)
							mValue = child.value();
						else
							mValue += child.value();
					}
				}
			}
		}
		if (mValue == null)
			return "";
		else
			return mValue;
	}

	public String attr(String tag) {
		String content = "";
		if (mAttrs != null) {
			String attr = mAttrs.get(tag);
			if (attr != null)
				content = attr;
		}
		return content;
	}

	public int size() {
		return mSize;
	}

	public Dom2Map get(String tag) {
		if (mNamedChildren != null) {
			ArrayList<Dom2Map> c = mNamedChildren.get(tag);
			if (c != null && c.size() > 0) {
				return c.get(0);
			}
		}
		return new Dom2Map();
	}
	
	public static void parse_traval(Node node, Dom2Map parent) {
		if (node == null)
			return;
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Dom2Map n1 = new Dom2Map(node);
			parent.add(n1);

			Node child = node.getFirstChild();
			while (child != null) {
				parse_traval(child,n1);
				try{
					child = child.getNextSibling();
				}catch(IndexOutOfBoundsException e){ // android bug? http://code.google.com/p/android/issues/detail?id=779
					child = null;
				}
			}
		} else if (node.getNodeType() == Node.TEXT_NODE
				|| node.getNodeType() == Node.CDATA_SECTION_NODE) {
			Dom2Map n1 = new Dom2Map(node);
			parent.add(n1);
		}
	}
	
	public static Dom2Map parse(Node node){
		Dom2Map root = new Dom2Map();
		Node child = null;
		if( node.getNodeType() != Node.ELEMENT_NODE){
			child = node.getFirstChild();
			while(child != null){
				if( child.getNodeType() == Node.ELEMENT_NODE){
					break;
				}
				child = child.getNextSibling();
			}
		}
		if(child != null)
			parse_traval(child,root);
		
		return root;
	}
}
