package com.robin.exclassification.rule.feature;

import org.apache.log4j.Logger;

import com.robin.exclassification.ExceptionEngine;
import com.robin.exclassification.ExceptionInfo;
import com.robin.util.BeanManager;

public class PropertyContainFeature extends ClassificationFeature{
	private static Logger logger = Logger.getLogger(PropertyContainFeature.class);  

	String propertyName;
	String content;
	public void init(String property,String content){
		propertyName=property;
		this.content=content;
		
	}
	public PropertyContainFeature(String string) {
		super(string);
	}

	@Override
	public boolean validate(ExceptionInfo info) {
		String value=BeanManager.getProperty(info,propertyName);
		
		if(value==null||value.length()==0)
			return false;
		if(value.toLowerCase().indexOf(content.toLowerCase())>=0){
			logger.debug("validating....["+propertyName+"]="+value+".......fit!");
			return true;
		}else
			logger.debug("validating....["+propertyName+"]="+value+".......Doesn't  fit!");
		return false;
	}

}
