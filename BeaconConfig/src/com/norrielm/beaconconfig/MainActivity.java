package com.norrielm.beaconconfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Sample to get started with the Kontakt REST API on Android.
 * 
 * See url(http://docs.kontakt.io/rest-api/quickstart/)
 * 
 * Note: Authentication requires Internet connectivity, 
 * and communication with the beacon requires Bluetooth enabled.
 * 
 * @author norrielm
 */
public class MainActivity extends ActionBarActivity {

	private static final String TAG = "BeaconConfig";

	private static final String host = "http://api.kontakt.io";
	private static final String key = "YOUR-API-KEY";

	private String beaconId = "YOUR-BEACON-ID";
	private int newTxPower = 3;
	private int newInterval = 350;

	private class BeaconTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// Read the current parameters
			getBeacon(beaconId);
			// Set new parameters
			setBeacon(beaconId, newTxPower, newInterval);
			// Read the updated parameters
			getBeacon(beaconId);
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		new BeaconTask().execute();
	}

	/**
	 * Read the parameters of a beacon.
	 * 
	 * @param beaconId Unique 4 character id displayed on the back of the beacon 
	 */
	public void getBeacon(String beaconId) {
		String path = "/beacon/"+beaconId;
		String params = String.format("?host=%s&path=%s", host, path);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(host+path+params);
		try {
			httpget.setHeader("Api-Key", key); 	
			HttpResponse response = httpclient.execute(httpget);
			String responseText = EntityUtils.toString(response.getEntity());

			// Log the response.
			Log.i(TAG, responseText);
		} catch (ParseException e) {
			Log.d(TAG, "ParseException: " + e);
		} catch (ClientProtocolException e) {
			Log.d(TAG, "ClientProtocolException: " + e);
		} catch (IOException e) {
			Log.d(TAG, "IOException: " + e);
		} 
	}
	
	/**
	 * Configure the parameters of a beacon.
	 * 
	 * @param beaconId Unique 4 character id displayed on the back of the beacon
	 * @param power Value between 1 - 7
	 * @param interval Frequency of advertisement packets in milliseconds 
	 */
	public void setBeacon(String beaconId, int power, int interval) {
		String path = "/beacon/"+beaconId;

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(host+path);
		try {
			httppost.setHeader("Api-Key", key);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);  
			nameValuePairs.add(new BasicNameValuePair("host", host));  
			nameValuePairs.add(new BasicNameValuePair("path", path));  
			nameValuePairs.add(new BasicNameValuePair("uniqueId", beaconId));
			nameValuePairs.add(new BasicNameValuePair("tx_power", String.valueOf(power)));
			nameValuePairs.add(new BasicNameValuePair("interval", String.valueOf(interval)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  

			HttpResponse response = httpclient.execute(httppost);
			String responseText = EntityUtils.toString(response.getEntity());

			Log.i(TAG, responseText);
		} catch (ParseException e) {
			Log.d(TAG, "ParseException: " + e);
		} catch (ClientProtocolException e) {
			Log.d(TAG, "ClientProtocolException: " + e);
		} catch (IOException e) {
			Log.d(TAG, "IOException: " + e);
		} 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
