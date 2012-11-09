package org.tiny.box.restandroidclient.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.tiny.box.restandroidclient.RestClient;
import org.tiny.box.restandroidclient.errors.RestClientConnectionException;
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
	public void testShouldPerformAGetRequestWithNoParameters() throws JSONException, RestClientConnectionException {
		RestClient testClient = new RestClient("http://www.google.it");
		String response = testClient.doGet();
		assertNotNull(response);
	}
	
	@SmallTest
	public void testShouldPerformPlainGetRequest() throws JSONException, RestClientConnectionException {
		RestClient testClient = new RestClient("http://tinyurl.com/api-create.php");
		List<NameValuePair> queryParameters = new ArrayList<NameValuePair>(1);
		queryParameters.add(new BasicNameValuePair("url", "www.google.com"));
		String response = testClient.doGet(queryParameters);
		assertNotNull(response);
		assertTrue(response.startsWith("http://tinyurl.com/"));
	}
	
	@SmallTest
	public void testShouldPerformSecureGetRequest() throws JSONException, RestClientConnectionException {
		RestClient testClient = new RestClient("https://tinyurl.com/api-create.php", SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME, RestClient.DEFAULT_SECURE_SOCKET_PORT);
		List<NameValuePair> queryParameters = new ArrayList<NameValuePair>(1);
		queryParameters.add(new BasicNameValuePair("url", "www.google.com"));
		String response = testClient.doGet(queryParameters);
		assertNotNull(response);
		assertTrue(response.startsWith("http://tinyurl.com/"));
	}
	
	@SmallTest
	public void testShouldThrowAnExceptionIfServerIsUnreachable(){
		RestClient testClient = new RestClient("http://www.indirizzofasullochenonesiste.it");
		try{
			testClient.doGet(null);
			fail("Should have thrown RestClientConnection exception.");
		} catch (RestClientConnectionException e){
		} catch (Exception e){
			fail("Wrong exception thrown.");
		}
	}
	
	@SmallTest
	public void testShouldThrownAnExceptionIfWasPassedInvalidClientProtocol() {
		RestClient testClient = new RestClient("https://encrypted.google.com", SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME, RestClient.DEFAULT_SECURE_SOCKET_PORT);
		try{
			testClient.doGet();
			fail("Should have thrown RestClientConnection exception.");
		} catch (RestClientConnectionException e){
		} catch (Exception e){
			fail("Wrong exception thrown. " + e.getMessage());
		}
	}
	

}
