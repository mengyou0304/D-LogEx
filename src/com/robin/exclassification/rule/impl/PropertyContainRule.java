package com.robin.exclassification.rule.impl;

import com.robin.exclassification.rule.ClassificationRule;
import com.robin.exclassification.rule.feature.PropertyContainFeature;

public class PropertyContainRule extends ClassificationRule {

	public PropertyContainRule(String type) {
		super();
		this.type = type;
	}

	public void addPCFeature(String propertyName, String content) {
		if (propertyName == null || propertyName.length() == 0
				|| content == null || content.length() == 0)
			return;
		PropertyContainFeature pcfeature = new PropertyContainFeature(
				"autoGen_pcFeature" + currentFeatureNum);
		pcfeature.init(propertyName, content);
		addFeature(pcfeature);
		currentFeatureNum++;
	}
}
