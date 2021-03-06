package org.tiny.box.restandroidclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.tiny.box.restandroidclient.errors.RestClientConnectionException;
import org.tiny.box.restandroidclient.socket.SocketFactorySelector;

import android.os.AsyncTask;
import android.util.Log;

public class RestClient extends AsyncTask<HttpRequestBase, Void, HttpResponse> {
	
	public static final int DEFAULT_PLAIN_SOCKET_PORT = 80;
	public static final int DEFAULT_SECURE_SOCKET_PORT = 443;
	
	private DefaultHttpClient httpClient;
	private String serverAddress;
	
	private RequestCallback currentCallback;
	
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
	
	public void doGet(RequestCallback callback, String cookies) throws RestClientConnectionException {
      this.doGet(null, callback, cookies);
	}
	
	public void doGet(List<NameValuePair> parameters, RequestCallback callback, String cookies) throws RestClientConnectionException {
		this.currentCallback = callback;
		
		String toQueryString = new String(this.serverAddress);
		
		try {
			if(parameters!=null){
				toQueryString += "?";
				for(int i =0; i< parameters.size(); i++)
						toQueryString += URLEncoder.encode(parameters.get(i).getName(), "UTF-8") + "=" + URLEncoder.encode(parameters.get(i).getValue(), "UTF-8") + "&";
				toQueryString = toQueryString.substring(0, toQueryString.length()-1);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RestClientConnectionException(e);
		}
		
		HttpGet httpGet = new HttpGet(toQueryString);

    if(cookies != null)
        httpGet.setHeader("Cookie", cookies);
		
		this.execute(httpGet);
	}
	
	public void doPost(RequestCallback callback, String cookies) throws RestClientConnectionException{
      this.doPost(new ArrayList<NameValuePair>(), callback, cookies);
	}
	
	public void doPost(List<NameValuePair> parameters, RequestCallback callback, String cookies) throws RestClientConnectionException{
		this.currentCallback = callback;
		HttpPost httpPost = new HttpPost(this.serverAddress);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		} catch (UnsupportedEncodingException e) {
			throw new RestClientConnectionException(e);
		}

    if(cookies != null)
        httpPost.setHeader("Cookie", cookies);
    
		this.execute(httpPost);
	}
	
	private Scheme getCurrentScheme() {
		return this.httpClient.getConnectionManager().getSchemeRegistry().getScheme(httpClient.getConnectionManager().getSchemeRegistry().getSchemeNames().get(0));
	}

	@Override
	protected HttpResponse doInBackground(HttpRequestBase... requests) {

		HttpRequestBase currentRequest = requests[0];
		
		HttpResponse response = null;
		try {
			response = this.httpClient.execute(currentRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Override
	protected void onPostExecute(HttpResponse response) {
		if(response == null) {
        this.currentCallback.onRequestEnd(-1, "NULL RESP", null);
			return;
		}
		try {
        
        Header[] respHead = response.getHeaders("Set-Cookie");
        String cookie = (respHead.length == 0) ? null : respHead[0].getValue();
        
        this.currentCallback.onRequestEnd(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()).toString(), cookie);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
