package com.robin.mqconnection.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ParamParser {
	ArrayList<String> keyList=new ArrayList<String>();
	public ParamParser (ArrayList<String> params){
		if(params==null){
			params=new ArrayList<String>();
			params.add("ip");
			params.add("url");
		}
		keyList=params;
	}
	public String getString(HashMap<String, String> paraMap) {
		String s="";
		for(String key:keyList){
			String value=paraMap.get(key);
			s+=value+"!";
		}
		return s;
	}
	public HashMap<String, String> getParams(String message){
		HashMap<String, String> resmap=new HashMap<String,String>();
		String[] ss=message.split("!");
		if(ss.length!=keyList.size())
			throw new RuntimeException(" The received message nums don't match the para nums!");
		int i=0;
		for(String key:keyList){
			resmap.put(key, ss[i]);
			i++;
		}
		return resmap;
	}
	public static void main(String[] args) {
		ArrayList<String> list=new ArrayList<String>();
		list.add("a1");
		list.add("s2");
		list.add("a3");
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("a1", "1");
		map.put("s2", "abcs2");
		map.put("a3", "33");
		ParamParser pp=new ParamParser(list);
		String message=pp.getString(map);
		System.out.println(message);
		map=pp.getParams(message);
		Iterator<String> it=map.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			String value=map.get(key);
			System.out.println(key+":"+value);
		}
	}
}
