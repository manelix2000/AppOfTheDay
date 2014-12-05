/****************************************************************************

Copyright (c) 2012-2013 cocos2d-x.org
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/

package org.cocos2dx.plugin;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.FrameLayout;

import com.flurry.android.FlurryAdListener;
import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAgent;

import org.cocos2dx.plugin.AdsWrapper;

public class AdsFlurry implements InterfaceAds, FlurryAdListener
{
	private Context mContext = null;
	protected static String TAG = "AdsFlurry";
	
	private String adId = "AppOfTheDay";
	FrameLayout adLayout;
	SharedPreferences preferences;
	
	private static boolean isDebug = false;
	
	private float time = 0;
	
    protected static void LogD(String msg) 
    {
        if (isDebug){
        	Log.d(TAG, msg);
        }
    }
        
    public AdsFlurry(Context context)
    {
        mContext = context;
        preferences = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        
        Log.d(TAG, "****AdsFlurry****");
    }
    
    public void startSession(String appKey)
	{
    	Log.d(TAG, "startKey"+appKey);
    	adLayout = new FrameLayout(mContext);
        FlurryAgent.onStartSession(mContext, appKey);
        FlurryAds.setAdListener(this);
        //FlurryAds.enableTestAds(true);	//FlurryTestMode
    }
    
    public void setAdId (String adIdentifier)
    {
    	adId = adIdentifier;
    }
	
	public String getAdId()
	{
		return adId;
	}
            
	@Override
	public void configDeveloperInfo(Hashtable<String, String> devInfo)
	{
		Log.d(TAG,"configDeveloperInfo is not available");
	}

	@Override
	public void showAds(int type, int sizeEnum, int pos)
	{
		if(showAdvert() == true)
		{
			adLayout = new FrameLayout(mContext); 
			FlurryAds.fetchAd(mContext, adId, adLayout, FlurryAdSize.FULLSCREEN);
			Log.d(TAG, "fetching...");
		}
	}
	
	protected void saveTime()
	{
		//Log.d(TAG, "saveTime");
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		
		SharedPreferences.Editor editor = preferences.edit();
        editor.putString("time", format.format(date));
		editor.commit();
	}
	
	protected float difftime(String date1)
	{
		//Log.d(TAG, "diffTime");
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		float diffSeconds = 0;
		
		String getTime = preferences.getString("time", "");
		
		Date d1 = null;
		Date d2 = null;
		
		//Log.d(TAG, date1);
		//Log.d(TAG, getTime);
		
		try {
			d1 = format.parse(getTime);
			d2 = format.parse(date1);
 
			//in milliseconds
			float diff = d2.getTime() - d1.getTime();
			diffSeconds = diff / 1000;
			//Log.d(TAG, "diffSeconds"+diffSeconds);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return diffSeconds;
	}
	
	public boolean showAdvert()
	{	
		Log.d(TAG, "showAdvert");
		
		if (this.time > 0.0f)
		{
			String getTime = preferences.getString("time", "");
			
			String voidString = "";
			
			Log.d(TAG, getTime);
			
			if (getTime.equals(voidString))
			{
				Log.d(TAG, "time is empty");
				
				saveTime();
				return true;
			}
			else
			{
				Log.d(TAG, "time is *NOT* empty");
				
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date date = new Date();
				
				float seconds = difftime(format.format(date)); //difference
				
				Log.d(TAG, "diffTime "+seconds);
				
				if (seconds >= this.time)
				{
					//Log.d(TAG, "difference of time pased >= timeMax");
					saveTime();
					return true;
				}
				//Log.d(TAG, "difference of time pased < timeMax");
				return false;
			}
		}
		return true;
	}
	
	public void setTime(float timeInSeconds)
	{
		Log.d(TAG, "setTime Android");
		this.time = timeInSeconds;
	}
	
	public float getTime()
	{
		return this.time;
	}
	
	@Override
	public void spaceDidReceiveAd(String arg0)
	{	
		Log.d(TAG, "****spaceDidReceiveAd****");
		
		FlurryAds.displayAd(mContext, arg0, adLayout);
		
		AdsWrapper.onAdsResult(this, AdsWrapper.RESULT_CODE_AdsReceived, "spaceDidReceiveAd");
	}
	
	@Override
	public void spaceDidFailToReceiveAd(String arg0)
	{
		Log.d(TAG, "****spaceDidFailToReceiveAd****");
		
		AdsWrapper.onAdsResult(this, AdsWrapper.RESULT_CODE_UnknownError, "spaceDidFailToReceiveAd");
	}
	
	@Override
	public void onAdClosed(String arg0)
	{
		Log.d(TAG, "****onAdClosed****");
		
		AdsWrapper.onAdsResult(this, AdsWrapper.RESULT_CODE_FullScreenViewDismissed, "adClosed");
	}
	
	public void resetTime()
	{
		Log.d(TAG, "resetTime");
		
		SharedPreferences.Editor editor = preferences.edit();
        editor.putString("time", "");
		editor.commit();
	}
	
	@Override
	public void hideAds(int type)
	{
		Log.d(TAG,"****hideAds****");
	}

	@Override
	public void spendPoints(int points)
	{
		Log.d(TAG,"spendPoints option is not available");
	}
	
	@Override
	public void setDebugMode(boolean debug)
	{
		Log.d("AdsFlurry", "setDebug mode ads flurry");
		isDebug = debug;
        FlurryAgent.setLogEnabled(isDebug);
        if (debug)
        {
            FlurryAgent.setLogLevel(Log.DEBUG);
        }
	}
	
	@Override
	public String getSDKVersion()
	{
		return "4.2.0";
	}

	@Override
	public String getPluginVersion()
	{
		return "1.0";
	}

	@Override
	public void onAdClicked(String arg0)
	{
		Log.d(TAG, "****onAdClicked****");
	}

	@Override
	public void onAdOpened(String arg0)
	{
		Log.d(TAG, "****onAdOpened****");
	}

	@Override
	public void onApplicationExit(String arg0)
	{
		Log.d(TAG, "****onApplicationExit****");	
	}

	@Override
	public void onRenderFailed(String arg0)
	{
		Log.d(TAG, "****onRenderFailed****");
	}

	@Override
	public void onRendered(String arg0)
	{
		Log.d(TAG, "****onRendered****");
	}

	@Override
	public void onVideoCompleted(String arg0)
	{
		Log.d(TAG, "****onVideoCompleted****");
	}

	@Override
	public boolean shouldDisplayAd(String arg0, FlurryAdType arg1)
	{
		Log.d(TAG, "****shouldDisplayAd****");
		return true;
	}
}