package com.robin.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Robin1
 * 
 */
public class BeanManager {
	public static DateFormat dateFormat;

	public static String getStringOfDate(Date date) {
		if (dateFormat == null)
			dateFormat = new SimpleDateFormat("HH:mm:ss");
		return dateFormat.format(date);
	}

	private static Object getBean(Class clazz, int randomNum) throws Exception {
		Object returnOB = null;
		Method[] methods = clazz.getMethods();
		returnOB = clazz.newInstance();
		Random r = new Random();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().length() < 3)
				continue;
			String methodname = methods[i].getName();
			if (methodname.substring(0, 3).equals("set")) {
				Class paratype = methods[i].getParameterTypes()[0];
				Object param = null;
				if (paratype.equals(Integer.class)
						|| paratype.equals(int.class))
					param = new Integer(randomNum + i);
				if (paratype.equals(Double.class)
						|| paratype.equals(double.class))
					param = new Double(new Double(randomNum + i));
				if (paratype.equals(Boolean.class))
					param = false;
				if (paratype.equals(String.class))
					param = String.valueOf(randomNum + i);
				if (paratype.equals(Date.class))
					param = new Date();
				try {
					methods[i].invoke(returnOB, new Object[] { param, });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return returnOB;
	}

	public static Object getComboxBean(Class clazz) throws Exception {
		Object returnOB = null;
		Method[] methods = clazz.getMethods();
		returnOB = clazz.newInstance();
		Random r = new Random();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().length() < 3)
				continue;
			String methodname = methods[i].getName();
			if (methodname.substring(0, 3).equals("set")) {
				Class paratype = methods[i].getParameterTypes()[0];
				Object param = null;
				if (paratype.equals(Integer.class)
						|| paratype.equals(int.class))
					param = new Integer(i);
				if (paratype.equals(String.class))
					param = methodname + String.valueOf("1");
				if (paratype.equals(Date.class))
					param = new Date();
				try {
					methods[i].invoke(returnOB, new Object[] { param, });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return returnOB;
	}

	public static Object getBean(Class clazz) {
		try {
			return getBean(clazz, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object[] getBeanArray(Class clazz, int arrayLenth) {
		if (arrayLenth > 10000) {
			System.out.println("Array Too Long!!");
			return null;
		}
		Object[] os = new Object[arrayLenth];
		for (int i = 0; i < arrayLenth; i++) {
			try {
				os[i] = getBean(clazz, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return os;
	}

	public static List getBeanList(Class clazz, int listnum) {
		if (listnum > 10000) {
			System.out.println("List Too Long!!");
			return null;
		}
		List list = new ArrayList();
		for (int i = 0; i < listnum; i++) {
			try {
				list.add(getBean(clazz, i + 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void showproperty(Object o) {
		System.out.println("\n************************");
		System.out.println("show property of " + o.getClass().getSimpleName());
		System.out.println("************************");
		Method[] methods = o.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().length() < 3)
				continue;
			if (methods[i].getName().substring(0, 3).equals("get"))
				try {
					Object value = methods[i].invoke(o, new Object[] {});
					System.out.println(methods[i].getName().substring(3)
							+ ":    \t\t" + value);
				} catch (IllegalArgumentException e) {
					System.out.println("wrong method name is :"
							+ methods[i].getName());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
		}
		System.out.println("************************");
	}

	public static Object fillBean(Class clazz, HashMap<String, String> valueMap)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Object o = null;
		o = clazz.newInstance();
		Iterator<String> it = valueMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = valueMap.get(key);
			Object param=null;
			for (Method m : clazz.getMethods()) {
				if (m.getName().equalsIgnoreCase("set" + key)) {
					Class paratype = m.getParameterTypes()[0];
					if (paratype.equals(Integer.class)
							|| paratype.equals(int.class))
						param = Integer.parseInt(value);
					if (paratype.equals(Double.class)
							|| paratype.equals(double.class))
						param =Double.parseDouble(value);
					if (paratype.equals(Boolean.class))
						param = Boolean.parseBoolean(value);
					if (paratype.equals(String.class))
						param = value;
					m.invoke(o, param);
				}
			}
		}
		return o;
	}

	public static void main(String[] args) {
	}

	public static String getProperty(Object info, String propertyName) {
		Method[] methods = info.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().length() < 3)
				continue;
			if (methods[i].getName().equalsIgnoreCase("get"+propertyName))
				try {
					Object value = methods[i].invoke(info, new Object[] {});
					return String.valueOf(value);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

}
