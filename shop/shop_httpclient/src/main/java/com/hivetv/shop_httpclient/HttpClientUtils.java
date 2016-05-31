package com.hivetv.shop_httpclient;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


/**
 * Created by Administrator on 2015/10/10.
 */
public class HttpClientUtils{

    public Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
	
    private static final String UTF_8 = "UTF-8";


    private static String defaultSessionId;

    private static Integer maxTotal = 200/4;

    private static Integer maxPreRoute=100/4;
    //存活时间 单位秒
    private static Integer timeToLive = 1800;

    private  static Object [] lock =new Object[0];

    private static PoolingHttpClientConnectionManager manager;

    public void setMaxTotal(Integer maxTotal) {
        HttpClientUtils.maxTotal = maxTotal;
    }

    public void setTimeToLive(Integer timeToLive) {
        HttpClientUtils.timeToLive = timeToLive;
    }
    public  void setMaxPreRoute(Integer maxPreRoute) {
        HttpClientUtils.maxPreRoute = maxPreRoute;
    }
    public static PoolingHttpClientConnectionManager getManager() {
        return manager;
    }

    public void setDefaultSessionId(String defaultSessionId) {
        HttpClientUtils.defaultSessionId = defaultSessionId;
    }

    private static HttpClient getHttpClient() {
        if(null==manager) {
            synchronized (lock){
                if(null==manager){
                    manager = new PoolingHttpClientConnectionManager();
                    //设置每个路由的最大连接数 不设置默认是2！请一定要设置
                    manager.setDefaultMaxPerRoute(maxPreRoute);
                    System.out.println("默认路由连接数" + manager.getDefaultMaxPerRoute());
                    //设置连接池中的最大连接数 默认是20 请一定要设置
                    //当前就访问一个route 就是最大连接数与路由最大连接数相同即可
                    manager.setMaxTotal(maxTotal);
                }
            }
        }
        return HttpClients.custom().setConnectionManager(manager).setConnectionTimeToLive(timeToLive,TimeUnit.SECONDS).build();
    }

    public static String sendGet(String url) {
        return get(url, null);
    }

    public static String sendGet(String url, String cookie) {
        return get(url, cookie);
    }

    public static String sendGet(String url, Cookie[] cookies) {
        String cookie = "";
        if (StringUtils.isEmpty(HttpClientUtils.defaultSessionId)) {
            if (null != cookies)
                for (int i = 0; i < cookies.length; i++) {
                    cookie += cookies[i].getName() + "=" + cookies[i].getValue();
                    if (i < cookies.length - 1) {
                        cookie += ",";
                    }
                }
        } else {
            cookie = "tathmcus-user-session-id=" + defaultSessionId;
        }
        return get(url, cookie);
    }

    private static String get(String url, String cookie) {
        String result = null;
        HttpGet get = new HttpGet(url);
        HttpClient client = null;
        try {
            if (cookie != null && !"".equals(cookie)) {
                get.setHeader("Cookie", cookie);
            }
            client = getHttpClient();

            HttpResponse response = client.execute(get);
            result = getReuslt(response, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            if (null != get)
                get.releaseConnection();
            System.out.println("Pending:" + manager.getTotalStats().getPending());
            System.out.println("Leased:" + manager.getTotalStats().getLeased());
            System.out.println("Max:" + manager.getTotalStats().getMax());
            System.out.println("Available:" + manager.getTotalStats().getAvailable());
        }
        
        return result;
    }
    
    
    public static String sendPostPic(String url,Map<String,String> map,File file)
    {
        String result = null;
        HttpResponse response = null;
    	HttpClient httpclient = getHttpClient();
    	HttpPost httppost = new HttpPost(url);
    	MultipartEntity entity = new MultipartEntity();
    	FileBody fileBody = new FileBody(file);
    	entity.addPart("file", fileBody);
        if (null != map) {
            Set<Map.Entry<String, String>> set = map.entrySet();
            Iterator<Map.Entry<String, String>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                try {
					entity.addPart(entry.getKey(), new StringBody(entry.getValue(),Charset.forName("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
        }
        httppost.setEntity(entity);
        try {
			response = getHttpClient().execute(httppost);
            if (null != response)
                result = getReuslt(response, UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (null != httppost)
            	httppost.releaseConnection();
            System.out.println("Pending:" + manager.getTotalStats().getPending());
            System.out.println("Leased:" + manager.getTotalStats().getLeased());
            System.out.println("Max:" + manager.getTotalStats().getMax());
        }
    	return result;
    }

    
    public static String sendMultipartPostPic(String url,Map<String,String> map,File file) throws UnsupportedEncodingException
    {
        String result = null;
        HttpResponse response = null;
        HttpClient httpclient = getHttpClient();
        HttpPost httppost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        FileBody fileBody = new FileBody(file);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", fileBody);
        if (null != map) {
            Set<Map.Entry<String, String>> set = map.entrySet();
            Iterator<Map.Entry<String, String>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                try {
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.MULTIPART_FORM_DATA));
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        HttpEntity entity = builder.build();
        httppost.setEntity(entity);
        try {
            response = getHttpClient().execute(httppost);
            if (null != response)
                result = getReuslt(response, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != httppost)
                httppost.releaseConnection();
            System.out.println("Pending:" + manager.getTotalStats().getPending());
            System.out.println("Leased:" + manager.getTotalStats().getLeased());
            System.out.println("Max:" + manager.getTotalStats().getMax());
        }
        return result;
    }

    public static String sendPost(String url, Map<String, String> map) {
        return sendPost(url, map, null);
    }

    public static String sendPost(String url, Map<String, String> map, Cookie[] cookies) {
        String result = null;
        List<NameValuePair> list = new ArrayList<>();
        if (null != map) {
            Set<Map.Entry<String, String>> set = map.entrySet();
            Iterator<Map.Entry<String, String>> itr = set.iterator();
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                list.add(pair);
            }
        }
        String cookie = "";
        if (StringUtils.isEmpty(HttpClientUtils.defaultSessionId)) {
            if (null != cookies)
                for (int i = 0; i < cookies.length; i++) {
                    cookie += cookies[i].getName() + "=" + cookies[i].getValue();
                    if (i < cookies.length - 1) {
                        cookie += ",";
                    }
                }
        } else {
            cookie = "tathmcus-user-session-id=" + defaultSessionId;
        }
        result = post(url, list, cookie);
        return result;
    }

    private static String post(String url, List<NameValuePair> list, String cookie) {
        String result = null;
        HttpResponse response = null;
        HttpPost post = new HttpPost(url);
        if (cookie != null && !"".equals(cookie)) {
            post.setHeader("Cookie", cookie);
        }
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(list, UTF_8);
            post.setEntity(entity);
            response = getHttpClient().execute(post);
            if (null != response)
                result = getReuslt(response, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != post)
                post.releaseConnection();
            System.out.println("Pending:" + manager.getTotalStats().getPending());
            System.out.println("Leased:" + manager.getTotalStats().getLeased());
            System.out.println("Max:" + manager.getTotalStats().getMax());
        }

        return result;
    }

    private static String getReuslt(HttpResponse response, String charset) throws IOException {
        if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
            return EntityUtils.toString(response.getEntity(), charset);
        }else {
            System.out.println("请求异常,状态"+response.getStatusLine().getStatusCode());
        }
        return null;
    }

    public static String sendPost(String url, JSONObject jo, Cookie[] cookies) {
        String result = null;
        String cookie = "";
        if (StringUtils.isEmpty(HttpClientUtils.defaultSessionId)) {
            if (null != cookies)
                for (int i = 0; i < cookies.length; i++) {
                    cookie += cookies[i].getName() + "=" + cookies[i].getValue();
                    if (i < cookies.length - 1) {
                        cookie += ",";
                    }
                }
        } else {
            cookie = "tathmcus-user-session-id=" + defaultSessionId;
        }
        result = post(url, jo, cookie);
        return result;
    }

    private static String post(String url, JSONObject jo, String cookie) {
        String result = null;
        HttpPost post = new HttpPost(url);
        post.setHeader("Cookie", cookie);
        StringEntity entity = new StringEntity(jo.toString(), "utf-8");
        entity.setContentEncoding("utf-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        try {
            HttpResponse response = getHttpClient().execute(post);
            result = getReuslt(response, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != post)
                post.releaseConnection();
        }
        return result;
    }

}
