package com.robin.exclassification;

import java.util.Stack;

import org.apache.log4j.Logger;

import com.robin.exclassification.listener.OnExceptionListener;
import com.robin.exclassification.listener.impl.ClassificationListener;
import com.robin.exclassification.listener.impl.DopplerIntgrationListener;
import com.robin.exclassification.listener.impl.LogstashPersistenceListener;
import com.robin.exclassification.rule.impl.PropertyContainRule;
import com.robin.util.ConfInfo;
import com.robin.util.LogEXFileUtility;
/**
 * This engine will finish tree things by three listener.
 * 
 * 1. Tag exceptions by rule and features user deploy.
 * 2. Write the tagged exceptions into file for LogStash.
 * 3. Send the exceptions to Doppler by HTTP post.
 * 
 * @author you.meng
 *
 */
public class ExceptionEngine {
	private static Logger logger = Logger.getLogger(ExceptionEngine.class);  
    

	Stack<OnExceptionListener> elistenerStack = new Stack<OnExceptionListener>();
	static ExceptionEngine instance;

	private ExceptionEngine() {
	}

	public static  ExceptionEngine getInstance() {
		if (instance == null) {
			instance = new ExceptionEngine();
			instance.init();
		}
		return instance;
	}

	public void init() {
		String logStashOutUrl=ConfInfo.getInstance("conf/config.conf").getOUT_EX_Logstash();
		DopplerIntgrationListener dlistenr=new DopplerIntgrationListener();
		LogstashPersistenceListener lsListener=new LogstashPersistenceListener(logStashOutUrl);
		regist(dlistenr);
		regist(lsListener);
	}

	public void regist(OnExceptionListener listener) {
		elistenerStack.add(listener);
	}

	public void addNewException(String jsonContent) {
		for (OnExceptionListener list : elistenerStack) {
			ExceptionInfo info = JsonConverter.parse(jsonContent);
			list.onExcetionArrive(info);
		}
	}
	public  void useCase(){
		PropertyContainRule r1=new PropertyContainRule("rule1");
		r1.addPCFeature("Source", "Sinatra");
		
		PropertyContainRule r2=new PropertyContainRule("rule2");
		r2.addPCFeature("", "");
		r2.addPCFeature("", "");

		ClassificationListener bl = new ClassificationListener("cl1");
		bl.registRule(r1);
		bl.registRule(r2);
		regist(bl);
	}

	public static void main(String[] args) {
		ExceptionEngine engine= ExceptionEngine.getInstance();
		engine.useCase();
		String jsonContent = LogEXFileUtility.readAll("conf/test1.json");
		engine.addNewException(jsonContent);
		engine.addNewException(jsonContent);
		engine.addNewException(jsonContent);

	}

}
