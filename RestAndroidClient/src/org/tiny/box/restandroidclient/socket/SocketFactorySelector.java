package org.tiny.box.restandroidclient.socket;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.tiny.box.restandroidclient.socket.ssl.SelfSignedSSLSocketFactory;

public class SocketFactorySelector {
	
	public static final String DEFAULT_PLAIN_SOCKET_SCHEME = "http";
	public static final String DEFAULT_SECURE_SOCKET_SCHEME = "https";
	
	public static SocketFactory get(String type) {
		SocketFactory selectedFactory = null;
		if (type.equals(SocketFactorySelector.DEFAULT_PLAIN_SOCKET_SCHEME))
			selectedFactory = PlainSocketFactory.getSocketFactory();
		else if (type.equals(SocketFactorySelector.DEFAULT_SECURE_SOCKET_SCHEME))
			selectedFactory = new SelfSignedSSLSocketFactory();
		
		return selectedFactory;
	}
	
}
