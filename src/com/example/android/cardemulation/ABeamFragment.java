package com.example.android.cardemulation;

import com.example.android.common.logger.Log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("NewApi")
public final class ABeamFragment extends Fragment {
	
    public static final String TAG = "ABeamFragment";
    
    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	Log.i(TAG, "onCreateView");
    	return inflater.inflate(R.layout.abeam_fragment, container, false);
    }
    
    @Override
    public void onResume() {
    	Log.i(TAG, "onResume");
        super.onResume();
    	Button mButton = (Button) getActivity().findViewById(R.id.start_abeam);
    	if(mButton != null) {
    		mButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "Onclick!!!");
					Intent mIntent = new Intent();
					//if(Build.VERSION.SDK_INT > 19) {
					    mIntent.setAction(Intent.ACTION_GET_CONTENT);
					/*} else {
						mIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
						mIntent.addCategory(Intent.CATEGORY_OPENABLE);
					}*/
				    mIntent.setType("image/*");
					getActivity().startActivityForResult(mIntent, MainActivity.PICK_IMAGE_REQUEST);

				}
			});
    	}
    }
    
    @Override
    public void onPause() {
    	Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
    	Log.i(TAG, "onDestroy");
    	super.onDestroy();
    }

	boolean processIntent(Intent mIntent) {
		Log.d(TAG, "processIntent");
		boolean result = false;
		
		//To-do
		if(mIntent != null) {
			result = true;
		}
		
		return result;
	}

}
