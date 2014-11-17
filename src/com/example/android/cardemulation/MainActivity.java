/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.cardemulation;

import java.io.FileNotFoundException;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.*;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import com.example.android.cardemulation.R;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1) public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";
    
    // Request code --s
    public static final int PICK_CONTACT_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final int TAG_READ = 3;
    // Request code --e

    Uri[] cantactUri = new Uri[1];
    Uri[] imageUri = new Uri[1];
    Uri[] videoUri = new Uri[1];
    
    NfcAdapter mAdapter;
    
    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    
    FragmentManager mFragmentManager;
    FragmentTransaction transaction;
    
    PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] techListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mFragmentManager = getSupportFragmentManager();
        
        final Button aBeamBtnButton = (Button) findViewById(R.id.ABeam);
        final Button cardEmuBtnButton = (Button) findViewById(R.id.cardEmu);
        final Button tagButton = (Button) findViewById(R.id.writeTag);

        tagButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick!!");
				TagFragment mTagFragment;
				mTagFragment = (TagFragment) mFragmentManager.findFragmentByTag(TagFragment.TAG);
		        if (mTagFragment == null) {
		        	mTagFragment = new TagFragment();
		        }
		        if (!mTagFragment.isVisible()) {
		            transaction = mFragmentManager.beginTransaction();
		            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		            transaction.replace(R.id.sample_content_fragment, mTagFragment, TagFragment.TAG);
					transaction.commit();
				}
				Log.i(TAG, "show TagFragment");
			}
		});
        
        cardEmuBtnButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// start Card Emulation fragment
				Log.i(TAG, "onClick!!");
				CardEmulationFragment mCardEmulationFragment;
				mCardEmulationFragment = (CardEmulationFragment) mFragmentManager.findFragmentByTag(CardEmulationFragment.TAG);
		        if (mCardEmulationFragment == null) {
		        	mCardEmulationFragment = new CardEmulationFragment();
		        }
		        if (!mCardEmulationFragment.isVisible()) {
		            transaction = mFragmentManager.beginTransaction();
		            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		            transaction.replace(R.id.sample_content_fragment, mCardEmulationFragment, CardEmulationFragment.TAG);
					transaction.commit();
				}
				Log.i(TAG, "show CardEmulationFragment");
			}
        });
        aBeamBtnButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// start ABeam fragment
					Log.i(TAG, "onClick!!");
					ABeamFragment mABeamFragment;
			        mABeamFragment = (ABeamFragment) mFragmentManager.findFragmentByTag(ABeamFragment.TAG);
			        if (mABeamFragment == null) {
			        	mABeamFragment = new ABeamFragment();
			        }
			        if (!mABeamFragment.isVisible()) {
			            transaction = mFragmentManager.beginTransaction();
			            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			            transaction.replace(R.id.sample_content_fragment, mABeamFragment, ABeamFragment.TAG);
						transaction.commit();
					}
					Log.i(TAG, "show ABeamFragment");
				}
        });
        
        // read TAGs --s
        mPendingIntent = PendingIntent.getActivity(this, TAG_READ, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter mFilter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
        	mFilter.addDataType("*/*");
        } catch(MalformedMimeTypeException e) {
        	throw new RuntimeException("Malformed mime type", e);
        }
        mFilters = new IntentFilter[]{ mFilter };
        techListArray = new String[][] { 
        		new String[]{ NfcA.class.getName() },
        		new String[]{ NfcB.class.getName() },
        		new String[]{ NfcF.class.getName() },
        		new String[]{ NfcV.class.getName() },
        		new String[]{ IsoDep.class.getName() },
        		new String[]{ MifareClassic.class.getName() },
        		new String[]{ MifareUltralight.class.getName() } };
        // read TAGs --e
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.i(TAG, "onResume");
    	
    	// read TAG --s
    	mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, techListArray);
    	// read TAG --e
    	
    	
    	// check for NDEF message  --s
    	Intent mIntent = getIntent();
    	if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(mIntent.getAction())) {
    		Log.d(TAG, "NDEF received!!!");
			ABeamFragment mABeamFragment;
	        mABeamFragment = (ABeamFragment) mFragmentManager.findFragmentByTag(ABeamFragment.TAG);
	        if (mABeamFragment != null && mABeamFragment.isVisible()) {
	        	if(mABeamFragment.processIntent(mIntent)) {
	        		Log.d(TAG, "Process Intent success!!!");
	        	}
	        }
    	} else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(mIntent.getAction())) {
    		// avoid 2nd launch when press home to idle and come back to this activity via recent task
    		if(mIntent.getBooleanExtra("TECH_DISCOVERED_FIRST_TIME", true)) {
    			mIntent.putExtra("TECH_DISCOVERED_FIRST_TIME", false);
	    		Log.d(TAG, "Tech discover");
	    		Tag mTag = mIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

	    		// start tag IO if needed
	    		TagFragment mTagFragment = (TagFragment) mFragmentManager.findFragmentByTag(TagFragment.TAG);

	    		if(mTagFragment != null && mTagFragment.isVisible() && mTagFragment.isReadyToWrite()) {
	    			Log.d(TAG, "Writing mode");
	    			mTagFragment.startToWrite(mTag);
	    		} else {
	    			Log.d(TAG, "Reading mode");
	    		}
    		} else {
    			Log.d(TAG, "Not the first time, don't do anything.");
    		}
    	}
    	// check for NDEF message --e
    }
    
    @Override
    public void onPause() {
    	Log.d(TAG, "Main activity - onPause");
    	super.onPause();
    	mAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    public void onNewIntent(Intent mIntent) {
    	//Log.d(TAG, "onNewIntent: " + mIntent.toString());
    	setIntent(mIntent);
    }
    
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	Log.d(TAG, "requestCode: " + requestCode);
    	switch(requestCode) {
            case PICK_IMAGE_REQUEST:
    	        if (resultCode == RESULT_OK) {
    	    	    imageUri[0] = data.getData();
					Log.d(TAG, "new imageUri: " + imageUri[0]);
					//Log.d(TAG, getPath(getApplication(), imageUri[0]));
					//Uri mUri = Uri.parse(getPath(getApplication(), imageUri[0]));
					//Log.d(TAG, "converted Uri: " + mUri);
					ContentResolver cr = this.getContentResolver();
					ABeamFragment mABeamFragment;
			        mABeamFragment = (ABeamFragment) mFragmentManager.findFragmentByTag(ABeamFragment.TAG);
			        if (mABeamFragment != null && mABeamFragment.isVisible()) {
						try {
							Log.i(TAG, "start getting bitmaps");
							Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(imageUri[0]));
							ImageView imageView = (ImageView) findViewById(R.id.photo_view);
			                imageView.setImageBitmap(bitmap);
						} catch (FileNotFoundException e) {
							Log.e(TAG, "Exception"); //TO-DO: 4.4 Kitkat will have exception here
						}
                        // Android Beam
						// TO-DO: disable this imageUri when ABeamfragment is not visible.
						mAdapter.setBeamPushUris(imageUri, this);
			        }
			    }
    	        break;
    	        
    	    default:
		}
    	
		super.onActivityResult(requestCode, resultCode, data);
	}  
	/*
	public static String getPath(Context context, Uri mUri) {
		
		Log.d(TAG + " File -",
				"Authority: " + mUri.getAuthority() +
				", Fragment: " + mUri.getFragment() +
				", Port: " + mUri.getPort() +
				", Query: " + mUri.getQuery() +
				", Scheme: " + mUri.getScheme() +
				", Host: " + mUri.getHost() +
				", Segments: " + mUri.getPathSegments().toString()
				);
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		
		// MediaStore and general
        if("content".equalsIgnoreCase(mUri.getScheme())) {
        	return getDataColumn(context, mUri, null, null);
        }
        return null;
	}
	
	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {
			Cursor cursor = null;
			final String column = "_data";
			final String[] projection = {
			column
			};
			try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
			null);
			if (cursor != null && cursor.moveToFirst()) {
			DatabaseUtils.dumpCursor(cursor);
			final int column_index = cursor.getColumnIndexOrThrow(column);
			return cursor.getString(column_index);
			}
			} finally {
			if (cursor != null)
			cursor.close();
			}
			return null;
			}
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        
        // fix null pointer
        if(logFragment != null)
            msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }

}
