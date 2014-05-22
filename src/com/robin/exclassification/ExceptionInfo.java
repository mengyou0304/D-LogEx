package com.robin.exclassification;

import java.util.ArrayList;
import java.util.HashMap;

public class ExceptionInfo {
	HashMap<String,String> envMap=new HashMap<String,String>();
	String httpPostInfo;
	String httpGetInfo;
	String source;
	String httpCookies;
	String exception;
	String json;
	/**==================*/
	ArrayList<String> tagList=new ArrayList<String>();
	
	public HashMap<String, String> getEnvMap() {
		return envMap;
	}
	public void setEnvMap(HashMap<String, String> envMap) {
		this.envMap = envMap;
	}
	public String getHttpPostInfo() {
		return httpPostInfo;
	}
	public void setHttpPostInfo(String httpPostInfo) {
		this.httpPostInfo = httpPostInfo;
	}
	public String getHttpGetInfo() {
		return httpGetInfo;
	}
	public void setHttpGetInfo(String httpGetInfo) {
		this.httpGetInfo = httpGetInfo;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getHttpCookies() {
		return httpCookies;
	}
	public void setHttpCookies(String httpCookies) {
		this.httpCookies = httpCookies;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public ArrayList<String> getTagList() {
		return tagList;
	}
	public void setTagList(ArrayList<String> tagList) {
		this.tagList = tagList;
	}
	//TODO Generate the String for LogStash
	public String getLogStashString() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
