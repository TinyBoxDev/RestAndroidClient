package org.tiny.box.restandroidclient;

public interface RequestCallback {

    public void onRequestEnd(int responseCode, String response, String cookies);
	
}
