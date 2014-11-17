package com.example.android.cardemulation;

import com.example.android.common.logger.Log;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TagFragment extends Fragment {
	
    public static final String TAG = "TagFragment";
    
    private String content = null;
    private final Object contentLock = new Object();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");    
        return inflater.inflate(R.layout.tag_fragment, container, false);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume");
        Button writeButton = (Button) getActivity().findViewById(R.id.write_tag_btn);
        writeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				synchronized (contentLock) {
				    content = "Borong Li!";	
				    arg0.setClickable(false);
				}
			}
		});
    }
    
    void startToWrite(Tag tag) {
    	TagWriter mWriter = new TagWriter(tag);
    	Log.e(TAG, mWriter.readTag());
    	mWriter.writeTag(content);
    }
    
    public Boolean isReadyToWrite() {
    	Log.d(TAG, content);
		return (content == null) ? false : true;
	}
    
}
