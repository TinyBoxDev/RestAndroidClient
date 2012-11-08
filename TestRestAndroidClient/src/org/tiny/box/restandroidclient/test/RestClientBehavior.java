package org.tiny.box.restandroidclient.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.tiny.box.restandroidclient.RestClient;
import org.tiny.box.restandroidclient.socket.SocketFactorySelector;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class RestClientBehavior extends TestCase {

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
	public void testShouldPerformPlainGetRequest() throws JSONException {
		RestClient testClient = new RestClient("http://tinyurl.com/api-create.php");
		List<NameValuePair> queryParameters = new ArrayList<NameValuePair>(1);
		queryParameters.add(new BasicNameValuePair("url", "www.google.com"));
		String response = testClient.doGet(queryParameters);
		assertNotNull(response);
		assertTrue(response.startsWith("http://tinyurl.com/"));
	}

}
