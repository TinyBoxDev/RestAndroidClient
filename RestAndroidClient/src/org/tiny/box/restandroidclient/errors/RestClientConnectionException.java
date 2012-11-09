package org.tiny.box.restandroidclient.errors;

import java.io.IOException;

public class RestClientConnectionException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2625065952925695524L;
	
	public RestClientConnectionException(IOException exception){
		super(exception);
	}

}
