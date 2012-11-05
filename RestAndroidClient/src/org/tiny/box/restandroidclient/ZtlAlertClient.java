package org.tiny.box.restandroidclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ZtlAlertClient {
	
private DefaultHttpClient httpClient;
	
	private SchemeRegistry schemeRegistry;
	private EasySSLSocketFactory easySocketFactory;
	private HttpParams connectionParams;
	
	public ZtlAlertClient(String plainSocketPort, String secureSocketPort) {
		this.schemeRegistry = new SchemeRegistry();
		this.easySocketFactory = new EasySSLSocketFactory();
		
		this.schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), (plainSocketPort == null ? 80 : Integer.valueOf(plainSocketPort))));
		this.schemeRegistry.register(new Scheme("https", this.easySocketFactory, (secureSocketPort == null ? 443 : Integer.valueOf(secureSocketPort))));
		
		this.connectionParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(connectionParams, HttpVersion.HTTP_1_1);
		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(this.connectionParams, this.schemeRegistry);
		this.httpClient = new DefaultHttpClient(connectionManager, this.connectionParams);
		Log.v("Client", "Client succesfully created!");
	}
	
	public HashMap<String,Object> doGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		HashMap<String,Object> getResults = new HashMap<String,Object>();
		try {
			HttpResponse response = httpClient.execute(httpGet);
			Log.v("GET req", "GET executed.");
			Log.v("GET req", response.getStatusLine().toString());
			for (Header h : response.getAllHeaders()) {
				Log.v("GET resp header", h.getName() + " : " + h.getValue() + "\n");
			}
			String res = EntityUtils.toString(response.getEntity()).toString();
			Log.v("GET resp body", res);
			//JSONObject jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()).toString());
			JSONObject jsonResponse = new JSONObject(res);
			Iterator<String> iterator = jsonResponse.keys();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				getResults.put(key, jsonResponse.get(key));
			}
			
		} catch (ClientProtocolException e) {
			Log.v("GET req", "ClientProtocolException");
			getResults.put("status", 1);
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("GET req", "IOException");
			getResults.put("status", 1);
			e.printStackTrace();
		} catch (JSONException e) {
			Log.v("GET req", "JSONException");
			getResults.put("status", 1);
			e.printStackTrace();
		}
		return getResults;
	}
	
	public HashMap<String,Object> doPost(String url, HashMap<String, String> postParameters) {
		HashMap<String,Object> postResults = new HashMap<String,Object>();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		for(String key : postParameters.keySet())
			nameValuePairs.add(new BasicNameValuePair(key, postParameters.get(key)));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			Log.v("POST req", response.getStatusLine().toString());
			for (Header h : response.getAllHeaders()) {
				Log.v("POST resp header", h.getName() + " : " + h.getValue() + "\n");
			}
			String res = EntityUtils.toString(response.getEntity()).toString();
			JSONObject jsonResponse = new JSONObject(res);
			Log.v("POST resp body", res);
			Iterator<String> iterator = jsonResponse.keys();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				postResults.put(key, jsonResponse.get(key));
			}
		} catch (ClientProtocolException e) {
			Log.v("POST req", "ClientProtocolException");
			postResults.put("status", 1);
			e.printStackTrace();
		} catch (IOException e) {
			Log.v("POST req", "IOException");
			postResults.put("status", 1);
			e.printStackTrace();
		} catch (ParseException e) {
			Log.v("POST req", "ParseException");
			postResults.put("status", 1);
			e.printStackTrace();
		} catch (JSONException e) {
			Log.v("POST req", "JSONException");
			postResults.put("status", 1);
			e.printStackTrace();
		}
		return postResults;
	}
	
}
