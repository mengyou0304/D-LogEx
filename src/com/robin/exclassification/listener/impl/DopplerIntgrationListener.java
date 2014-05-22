package com.robin.exclassification.listener.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.robin.exclassification.ExceptionInfo;
import com.robin.exclassification.listener.OnExceptionListener;

public class DopplerIntgrationListener extends OnExceptionListener {
	private static Logger logger = Logger.getLogger(DopplerIntgrationListener.class);  
    
	@Override
	public void onExcetionArrive(ExceptionInfo info) {
		sendExceptionInfo(info);
	}

	public void sendExceptionInfo(ExceptionInfo info) {
		String url = "http://doppler-external.staging.hulu.com/doppler/1.0/ingest?source=site";
		String params=genJsonDataForDoppler(info);
		sendPostRequest(url,params);
	}
	//TODO find out how to gen json for doppler
	private String genJsonDataForDoppler(ExceptionInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sendPostRequest(String url, String content) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(content);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) {
		DopplerIntgrationListener lst = new DopplerIntgrationListener();
		String s=lst.sendPostRequest("http://www.baidu.com", "hello");
		System.out.println(s);
	}

}
