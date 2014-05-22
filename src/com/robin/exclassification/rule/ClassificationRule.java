package com.robin.exclassification.rule;

import java.util.ArrayList;

import com.robin.exclassification.ExceptionInfo;
import com.robin.exclassification.rule.feature.ClassificationFeature;

public abstract class ClassificationRule {
	protected ArrayList<ClassificationFeature> featureList=new ArrayList<ClassificationFeature>();
	protected String type;
	protected Integer currentFeatureNum=0;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	} 

	public void addFeature(ClassificationFeature feature) {
		featureList.add(feature);
	}

	public boolean onClassify(ExceptionInfo info) {
		for(ClassificationFeature f:featureList){
			boolean isSuccess=f.validate(info);
			if(isSuccess)
				return true;
		}
		return false;
	}

	
}
