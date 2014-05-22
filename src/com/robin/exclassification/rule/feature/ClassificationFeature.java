package com.robin.exclassification.rule.feature;

import com.robin.exclassification.ExceptionInfo;

public abstract class ClassificationFeature {
	
	public ClassificationFeature(String string) {
		
	}

	public abstract boolean validate(ExceptionInfo info);
		

}
