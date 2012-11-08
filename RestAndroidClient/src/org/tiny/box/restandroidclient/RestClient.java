package org.tiny.box.restandroidclient;

import java.io.IOException;
import java.net.URLEncoder;
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
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.tiny.box.restandroidclient.socket.SocketFactorySelector;
import org.tiny.box.restandroidclient.socket.ssl.SelfSignedSSLSocketFactory;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class RestClient {
	
	public static final int DEFAULT_PLAIN_SOCKET_PORT = 80;
	public static final int DEFAULT_SECURE_SOCKET_PORT = 443;
	
	private DefaultHttpClient httpClient;
	private String serverAddress;
	
	public RestClient(String address) {
		this(address, SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME, RestClient.DEFAULT_PLAIN_SOCKET_PORT);
	}
	
	public RestClient(String address, String connectionProtocol, int socketPort) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		this.serverAddress = address;
		schemeRegistry.register(new Scheme(connectionProtocol, SocketFactorySelector.get(connectionProtocol), socketPort));
		
		HttpParams connectionParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(connectionParams, HttpVersion.HTTP_1_1);
		
		ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(connectionParams, schemeRegistry);
		this.httpClient = new DefaultHttpClient(connectionManager, connectionParams);
	}
	
	public int getSocketPort() {
		return this.getCurrentScheme().getDefaultPort();
	}
	
	public String getServerAddress() {
		return this.serverAddress;
	}
	
	public SocketFactory getSocketFactory() {
		return this.getCurrentScheme().getSocketFactory();
	}
	
	public String doGet(List<NameValuePair> parameters) {
		String toQueryString = new String(this.serverAddress);
		String getResponse = null;
		
		if(parameters!=null){
			toQueryString += "?";
			for(int i =0; i< parameters.size(); i++)
				toQueryString += URLEncoder.encode(parameters.get(i).getName()) + "=" + URLEncoder.encode(parameters.get(i).getValue()) + "&";
			toQueryString = toQueryString.substring(0, toQueryString.length()-1);
		}
		
		
		try {
			HttpGet httpGet = new HttpGet(toQueryString);
			HttpResponse response = httpClient.execute(httpGet);
			getResponse = EntityUtils.toString(response.getEntity()).toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return getResponse;
	}
	
	private Scheme getCurrentScheme() {
		return this.httpClient.getConnectionManager().getSchemeRegistry().getScheme(httpClient.getConnectionManager().getSchemeRegistry().getSchemeNames().get(0));
	}
	
	
	/*
	public HashMap<String,Object> doGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		HashMap<String,Object> getResults = new HashMap<String,Object>();
		try {
			HttpResponse response = httpClient.execute(httpGet);
			JSONObject jsonResponse = new JSONObject(EntityUtils.toString(response.getEntity()).toString());
			
			Iterator<String> iterator = jsonResponse.keys();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				getResults.put(key, jsonResponse.get(key));
			}
			
		} catch (ClientProtocolException e) {
			getResults.put("status", 1);
			e.printStackTrace();
		} catch (IOException e) {
			getResults.put("status", 1);
			e.printStackTrace();
		} catch (JSONException e) {
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
	*/
	
}
