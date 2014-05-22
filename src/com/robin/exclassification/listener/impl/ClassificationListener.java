package com.robin.exclassification.listener.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.robin.exclassification.ExceptionEngine;
import com.robin.exclassification.ExceptionInfo;
import com.robin.exclassification.listener.OnExceptionListener;
import com.robin.exclassification.result.ResultFilePusher;
import com.robin.exclassification.rule.ClassificationRule;

public class ClassificationListener extends OnExceptionListener{
	private static Logger logger = Logger.getLogger(ClassificationListener.class);  
    
	ArrayList<ClassificationRule> cRuleList=new ArrayList<ClassificationRule>();
	
	public ClassificationListener(String id){
		this.id=id;
	}
	@Override
	public void onExcetionArrive(ExceptionInfo info) {
		//BeanManager.showproperty(info);
		ArrayList<String> typeList=new ArrayList<String>();
		for(ClassificationRule rule: cRuleList){
			boolean canclasisfiy=rule.onClassify(info);
			if(canclasisfiy)
				typeList.add(rule.getType());
		}
		String typeString="";
		for(String type: typeList){
			logger.info("Judge this exception As type :"+type);
			typeString+=type+",";
		}
		if(typeString.length()>0)
			typeString=typeString.substring(0,typeString.length()-1);
		ResultFilePusher pusher=ResultFilePusher.getInstance();
		pusher.writeToFile("["+typeString+"]:"+info.getJson());
		info.setTagList(typeList);
	}

	public void registRule(ClassificationRule rule) {
		cRuleList.add(rule);
	}}
