package org.tiny.box.restandroidclient.test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.tiny.box.restandroidclient.RestClient;


public class RestClientTest {
	
	@Test
	public void shouldHaveDefaultPortValues() {
		assertEquals(RestClient.defaultPlainSocketPort, 80);
		assertEquals(RestClient.defaultSecureSocketPort, 443);
	}
	
	@Test
	public void shouldHaveDefaultHTTPClient() throws NoSuchFieldException {
		RestClient client = new RestClient();
		Field wrappedClient = RestClient.class.getDeclaredField("httpClient");
	}
	
}
