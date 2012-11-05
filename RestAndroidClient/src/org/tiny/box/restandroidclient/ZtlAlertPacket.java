package org.tiny.box.restandroidclient;

import java.util.HashMap;

public abstract class ZtlAlertPacket extends HashMap<String,String> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6703912810295677700L;

	public abstract String toString();
	
	public String preparePacketToSend() {
		String myselfAsString = new String();
		for(String key : this.keySet())
			myselfAsString += key + "=" + this.get(key) + "&";
		
		return myselfAsString.substring(0, myselfAsString.length()-1);
	}
	
}
