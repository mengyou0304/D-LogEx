package com.robin.mqconnection.rpc;

import java.util.HashMap;

public abstract class RCPServerListener {
	protected String rcpname;
	
	public abstract String onCall(HashMap<String,String> params);

}
