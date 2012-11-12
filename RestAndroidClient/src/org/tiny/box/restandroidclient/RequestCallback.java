package org.tiny.box.restandroidclient;

public interface RequestCallback {

	public void onRequestSuccess(String response);
	public void onRequestFail();
	
}
