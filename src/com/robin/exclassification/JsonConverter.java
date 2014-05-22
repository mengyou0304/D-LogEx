package com.robin.exclassification;

import java.util.Set;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.robin.util.LogEXFileUtility;

public class JsonConverter {
	public String getTestString() {
		String s = LogEXFileUtility.readAll("conf/test1.json");
		return s;
	}

	public void test(String content) {
		JSONObject obj = new JSONObject(new JSONTokener(content));
		Set<String> set = obj.keySet();
		for (String key : set) {
			System.out.print(key + " :\t");
			System.out.println(obj.get(key));
		}
	}
	public static ExceptionInfo parse(String jsonContent){
		JSONObject obj = new JSONObject(new JSONTokener(jsonContent));
		ExceptionInfo info=new ExceptionInfo();
		info.json=jsonContent;
		Set<String> set = obj.keySet();
		for (String key : set) {
			String value=String.valueOf(obj.get(key));
			if(key.equals("exception"))
				info.setException(value);
			if(key.equals("httpCookies"))
				info.setHttpCookies(value);
			if(key.equals("source"))
				info.setSource(value);
			if(key.equals("httpGetInfo"))
				info.setHttpGetInfo(value);
			if(key.equals("httpPostInfo"))
				info.setHttpPostInfo(value);
			if(key.startsWith("ENV_"))
				info.getEnvMap().put(key.substring(4), value);
		}
		return info;
	}
	public static void main(String[] args) {
		JsonConverter j = new JsonConverter();
		j.test(j.getTestString());
	}


}
