package com.robin.mqconnection.rpc;
/**
 * For the client RCPListener, it is used for performing action after the server
 * returns the result.
 * 
 * @author you.meng
 *
 */
public abstract class RCPClientListener {
	protected String rcpname;
	
	public abstract void onCallBack(String value);

}
