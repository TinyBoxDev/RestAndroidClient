package org.tiny.box.restandroidclient.test;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.tiny.box.restandroidclient.socket.SocketFactorySelector;
import org.tiny.box.restandroidclient.socket.ssl.SelfSignedSSLSocketFactory;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class SocketFactorySelectorBehavior extends TestCase {

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
	public void testShouldHaveDefaultSchemeValues() {
		assertEquals(SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME, "http");
		assertEquals(SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME, "https");
	}
	
	@SmallTest
	public void testShouldCreateNewPlainSocketFactory() {
		assertEquals(PlainSocketFactory.class, SocketFactorySelector.get(SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME).getClass());
	}
	
	@SmallTest
	public void testShouldCreateNewSelfSignedSSLSocketFactory() {
		assertEquals(SelfSignedSSLSocketFactory.class, SocketFactorySelector.get(SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME).getClass());
	}

}
