package com.example.android.cardemulation;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.Tag;
import android.nfc.tech.*;
import android.util.Log;

public class TagWriter {

	public static final String TAG = "TagWriter";
	
	private Tag mTag;
	private String[] techList;
	
	public TagWriter(Tag tag) {
		// construct with a tag object
		mTag = tag;
		techList = mTag.getTechList();
	}
	
	public void writeTag(String tagText) {
		// check existence
		//TO-DO: get it common
		MifareUltralight ultralight = null;
		Boolean isTechExist = false;
		for(String tech : techList) {
			if(MifareUltralight.class.getName().equals(tech)) {
				Log.d(TAG, tech + " is detected!");
				ultralight = MifareUltralight.get(mTag);
				Log.d(TAG, "TAG type: " + ultralight.getType());
				isTechExist = true;
				break;
			}
		}
		
		if(!isTechExist) {
			Log.e(TAG, "No matched tech!");
			return;
		}
		// start write
		try {
			ultralight.connect();
			ultralight.writePage(4, "abcd".getBytes(Charset.forName("US-ASCII")));
			ultralight.writePage(5, "efgh".getBytes(Charset.forName("US-ASCII")));
			ultralight.writePage(6, "ijkl".getBytes(Charset.forName("US-ASCII")));
			ultralight.writePage(7, "mnop".getBytes(Charset.forName("US-ASCII")));
		} catch (Exception e) {
			Log.e(TAG, "IOexception for tag...");
		} finally {
			try {
				ultralight.close();
			} catch (IOException e) {
				Log.e(TAG, "IOexception for closing tag...");
			}
		}
		Log.d(TAG, "Write completed");
	}
	
	public String readTag() {
		// check existence
		// TO-DO: get it common
		MifareUltralight ultralight = null;
		Boolean isTechExist = false;
		for(String tech : techList) {
			if(MifareUltralight.class.getName().equals(tech)) {
				Log.d(TAG, tech + " is detected!");
				ultralight = MifareUltralight.get(mTag);
				Log.d(TAG, "TAG type: " + ultralight.getType());
				isTechExist = true;
				break;
			}
		}
		
		if(!isTechExist) {
			Log.e(TAG, "No matched tech!");
			return null;
		}
		
		//start reading
		String result = null;
		try {
			ultralight.connect();
			byte[] payload = ultralight.readPages(4);
			result = new String(payload, Charset.forName("US_ASCII"));
		} catch (Exception e) {
			Log.e(TAG, "IOexception for tag...");
		} finally {
			try {
				ultralight.close();
			} catch (IOException e) {
				Log.e(TAG, "IOexception for closing tag...");
			}
		}
		
		Log.d(TAG, "Read completed");
		return result;
	}
}
