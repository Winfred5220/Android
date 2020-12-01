package com.example.administrator.yanfoxconn.utils;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

//	BASE_URL�`�q
	public static final String BASE_URL="http://60.212.41.39/Scan/";
	//public static final String BASE_URL="http://10.192.14.19/ApprovalServer/";
    // public static final String BASE_URL="http://10.194.208.53:8080/Scan/";
	
//	�q�LURL��oHttpGet��H
	public static HttpGet getHttpGet(String url){
		HttpGet request=new HttpGet(url);
		return request;
	}
	
//	�q�LURL��oHttpPost
	public static HttpPost getHttpPost(String url){
		HttpPost request=new HttpPost(url);
		return request;
	}
	
//	�q�LHttpGet��oHttpResponse
	public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException,IOException{
		HttpResponse response=new DefaultHttpClient().execute(request);
		return response;
	}
	
//	�q�LHttpPost��oHttpResponse
	public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException,IOException{
		HttpResponse response=new DefaultHttpClient().execute(request);
		return response;
	}
	
//	�q�LURL�o�epost�ШD,��^�ШD���G
	public static String queryStringForPost(String url){
		HttpPost request=HttpUtils.getHttpPost(url);
		String result=null;
		try{
			HttpResponse response=HttpUtils.getHttpResponse(request);
			if(response.getStatusLine().getStatusCode()==200){
				result=EntityUtils.toString(response.getEntity());
				return result;
			} 
		}catch(ClientProtocolException e){
			e.printStackTrace();
//			result="�������`1";
			return result;
		}catch(IOException e){
			e.printStackTrace();
//			result="�������`";
			return result;
		}
		return null;
		
	}
	
//	�q�LHttpPost�o�eget�ШD
	public static String queryStringForGet(String url){
		HttpGet request=HttpUtils.getHttpGet(url);
		String result=null;
		try{
			HttpResponse response=HttpUtils.getHttpResponse(request);
			if(response.getStatusLine().getStatusCode()==200){
				result=EntityUtils.toString(response.getEntity());
				return result;
			} 
		}catch(ClientProtocolException e){
			e.printStackTrace();
			result="�������`";
			return result;
		}catch(IOException e){
			e.printStackTrace();
			result="�������`";
			return result;			
		}
		return null;
	}
	
//	�q�LHttpPost�o�ePost�ШD,��^�ШD���G
	public static String queryStringForPost(HttpPost request){
		String result=null;
		try{
			HttpResponse response=HttpUtils.getHttpResponse(request);
			if(response.getStatusLine().getStatusCode()==200){
				result=EntityUtils.toString(response.getEntity());
				return result;
			} 
		}catch(ClientProtocolException e){
			e.printStackTrace();
			result="�������`";
			return result;
		}catch(IOException e){
			e.printStackTrace();
			result="�������`";
			return result;			
		}
		return null;
	}

}
