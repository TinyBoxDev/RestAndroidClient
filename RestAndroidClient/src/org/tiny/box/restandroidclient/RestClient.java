package org.tiny.box.restandroidclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
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
	
	public String doGet() throws RestClientConnectionException{
		return this.doGet(null);
	}
	
	public String doGet(List<NameValuePair> parameters) throws RestClientConnectionException {
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
		
		return this.performARequest(httpGet);
	}
	
	public String doPost() throws RestClientConnectionException{
		return this.doPost(new ArrayList<NameValuePair>());
	}
	
	public String doPost(List<NameValuePair> parameters) throws RestClientConnectionException{
		HttpPost httpPost = new HttpPost(this.serverAddress);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
		} catch (UnsupportedEncodingException e) {
			throw new RestClientConnectionException(e);
		}
		
		return this.performARequest(httpPost);
	}
	
	private String performARequest(HttpRequestBase request) throws RestClientConnectionException{
		String stringfiedResponse = null;
		
		HttpResponse response;
		try {
			response = httpClient.execute(request);
			stringfiedResponse = EntityUtils.toString(response.getEntity()).toString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RestClientConnectionException(e);
		}
		
		return stringfiedResponse;
	}
	
	private Scheme getCurrentScheme() {
		return this.httpClient.getConnectionManager().getSchemeRegistry().getScheme(httpClient.getConnectionManager().getSchemeRegistry().getSchemeNames().get(0));
	}
	
}
