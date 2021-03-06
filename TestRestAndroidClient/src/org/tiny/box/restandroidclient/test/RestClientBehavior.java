package org.tiny.box.restandroidclient.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.tiny.box.restandroidclient.RequestCallback;
import org.tiny.box.restandroidclient.RestClient;
import org.tiny.box.restandroidclient.errors.RestClientConnectionException;
import org.tiny.box.restandroidclient.socket.SocketFactorySelector;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class RestClientBehavior extends TestCase {
	
	class commonCallback implements RequestCallback{

		@Override
		public void onRequestEnd(boolean status, String response) {
			Log.v("callback", "called!");
			
		}
		
	}

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SmallTest
	public void testShouldHaveDefaultPortValues() {
		assertEquals(RestClient.DEFAULT_PLAIN_SOCKET_PORT, 80);
		assertEquals(RestClient.DEFAULT_SECURE_SOCKET_PORT, 443);
	}
	
	@SmallTest
	public void testShouldHaveHttpClientField() throws NoSuchFieldException {
		Field wrappedClient = RestClient.class.getDeclaredField("httpClient");
		assertEquals(DefaultHttpClient.class, wrappedClient.getGenericType());
	}
	
	@SmallTest
	public void testShouldHaveServerAddressField() throws NoSuchFieldException {
		Field wrappedAddress = RestClient.class.getDeclaredField("serverAddress");
		assertEquals(String.class, wrappedAddress.getGenericType());
	}
	
	@SmallTest
	public void testShouldCreateClientWithInitialValues() {
		RestClient testClient = new RestClient("http://www.google.it", SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME, 80);
		assertEquals(80, testClient.getSocketPort());
		assertEquals("http://www.google.it", testClient.getServerAddress());
		assertEquals(PlainSocketFactory.class, testClient.getSocketFactory().getClass());
	}
	
	@SmallTest
	public void testShouldCreateClientWithDefaultPortNumber() {
		RestClient testClient = new RestClient("http://www.google.it");
		assertEquals(80, testClient.getSocketPort());
		assertEquals("http://www.google.it", testClient.getServerAddress());
		assertEquals(PlainSocketFactory.class, testClient.getSocketFactory().getClass());
	}
	
	@SmallTest
	public void testShouldPerformAGetRequestWithNoParameters() throws JSONException, RestClientConnectionException, InterruptedException, ExecutionException {
		
		RestClient testClient = new RestClient("http://www.google.it");
		testClient.doGet(new commonCallback());
		assertNotNull(testClient.get());

	}
	
	@SmallTest
	public void testShouldPerformPlainGetRequest() throws JSONException, RestClientConnectionException, InterruptedException, ExecutionException {
		RestClient testClient = new RestClient("http://tinyurl.com/api-create.php");
		List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();
		queryParameters.add(new BasicNameValuePair("url", "www.google.com"));
		testClient.doGet(queryParameters, new commonCallback());
		String response = testClient.get();
		assertNotNull(response);
		assertTrue(response.startsWith("http://tinyurl.com/"));
	}
	
	@SmallTest
	public void testShouldPerformSecureGetRequest() throws JSONException, RestClientConnectionException, InterruptedException, ExecutionException {
		RestClient testClient = new RestClient("https://tinyurl.com/api-create.php", SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME, RestClient.DEFAULT_SECURE_SOCKET_PORT);
		List<NameValuePair> queryParameters = new ArrayList<NameValuePair>(1);
		queryParameters.add(new BasicNameValuePair("url", "www.google.com"));
		testClient.doGet(queryParameters, new commonCallback());
		String response = testClient.get();
		assertNotNull(response);
		assertTrue(response.startsWith("http://tinyurl.com/"));
	}
	
	@SmallTest
	public void testShouldCallonRequestFailCallbackIfServerIsUnreachableWhenPerformsAGetRequest() throws RestClientConnectionException{
		class localCallback implements RequestCallback{

			@Override
			public void onRequestEnd(boolean status, String response) {
				fail("Called success callback");
				
			}
			
		}
		
		RestClient testClient = new RestClient("http://www.indirizzofasullochenonesiste.it");
		testClient.doGet(new localCallback());
	
	}
	
	@SmallTest
	public void testShouldPerformPlainPostRequest() throws JSONException, RestClientConnectionException, InterruptedException, ExecutionException{
		String fieldToSend = "test_field";
		String valueToSend = "here I am";
		
		RestClient testClient = new RestClient("http://httpbin.org/post");
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(fieldToSend, valueToSend));
		testClient.doPost(postParameters, new commonCallback());
		String response = testClient.get();
		assertNotNull(response);
		JSONObject responseObject = new JSONObject(response);
		assertEquals(valueToSend,
				((JSONObject)responseObject.get("form")).get(fieldToSend));
	}
	
	@SmallTest
	public void testShouldPerformSecurePostRequest() throws RestClientConnectionException, JSONException, InterruptedException, ExecutionException {
		String fieldToSend = "test_field";
		String valueToSend = "here I am";
		
		RestClient testClient = new RestClient("https://httpbin.org/post", SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME, RestClient.DEFAULT_SECURE_SOCKET_PORT);
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(fieldToSend, valueToSend));
		testClient.doPost(postParameters, new commonCallback());
		String response = testClient.get();
		assertNotNull(response);
		JSONObject responseObject = new JSONObject(response);
		assertEquals(valueToSend,
				((JSONObject)responseObject.get("form")).get(fieldToSend));
	}
	
	@SmallTest
	public void testShouldPerformAPostRequestWithNoParameters() throws RestClientConnectionException, InterruptedException, ExecutionException {
		RestClient testClient = new RestClient("http://httpbin.org/post");
		testClient.doPost(new commonCallback());
		String response = testClient.get();
		assertNotNull(response);

	}
	

}
