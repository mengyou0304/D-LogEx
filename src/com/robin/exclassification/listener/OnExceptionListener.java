package com.robin.exclassification.listener;

import com.robin.exclassification.ExceptionInfo;

public abstract class OnExceptionListener {
	protected String id;
	public abstract void onExcetionArrive(ExceptionInfo info);
}
