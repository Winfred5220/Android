package com.example.administrator.yanfoxconn.utils;

/**
 * yanling
 * Created by 10405 on 2016/6/6.
 */

import android.util.Log;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class HttpConnectionUtil {

    static String BOUNDARY = java.util.UUID.randomUUID().toString();
    static String PREFIX = "--", LINEND = "\r\n";
    static String MULTIPART_FROM_DATA = "multipart/form-data";
    static String CHARSET = "UTF-8";

    public static String doPostPictureLog(String urlStr, Map<String, String> paramMap, Map<String, File> fileMap)
            throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);// 允許輸入
        conn.setDoOutput(true);// 允許輸出
        conn.setUseCaches(false);
        conn.setReadTimeout(10 * 1000); // 緩存的最長時間
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);


        DataOutputStream os = new DataOutputStream(conn.getOutputStream());

//        StringBuilder sb = new StringBuilder(); //用StringBuilder拼接報文，用於上傳圖片資料

        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) { //在for迴圈中拼接報文，上傳文本資料

            text.append("--");
            text.append(BOUNDARY);
            text.append("\r\n");

            text.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            text.append(entry.getValue());

            text.append("\r\n");
        }

        os.write(text.toString().getBytes("utf-8")); //寫入文本資料
        if (fileMap != null) {
            for (Map.Entry<String, File> entryFile : fileMap.entrySet()) { //在for迴圈中拼接報文，上傳圖片資料
                StringBuilder sb = new StringBuilder();//用StringBuilder拼接報文，用於上傳圖片資料
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entryFile.getKey() + "\"; filename=\"" + entryFile.getValue().getName() + "\"" + LINEND);
                sb.append("Content-Type: image/jpg; charset=" + CHARSET + LINEND);
                sb.append(LINEND);

                os.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(entryFile.getValue());

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len); //寫入圖片資料
                }
                is.close();

                os.write(LINEND.getBytes());
            }
        }
        Log.i("======", text.toString());
        // 請求結束標誌
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        os.write(end_data);
        os.flush();
        os.close();

        /* 取得Response內容 */
        InputStream is = conn.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn
                .getInputStream(), "utf-8"));
        String readLine = "";
        while ((readLine = in.readLine()) != null) {
            b.append(readLine);
        }
        in.close();
        Log.e("--oo-----------",b.toString());

//         得到回應碼
        int res = conn.getResponseCode();
        Log.e("oo----------oo",res+"");
        Log.e("oo----------oo",conn.getResponseMessage()+"");
        Log.e("oo----------oo","asdf " + conn.getResponseMessage());

        conn.disconnect();
        return b.toString();
    }
    public static String doPostJsonObject(String urlStr,  JsonObject object)
            throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);// 允許輸入
        conn.setDoOutput(true);// 允許輸出
        conn.setUseCaches(false);
        conn.setReadTimeout(10 * 1000); // 緩存的最長時間
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        //转换为字节数组
        byte[] data = (object.toString()).getBytes();
        // 设置文件长度
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Content-Type", "application/json");
        conn.connect();

        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
        os.write((object.toString()).getBytes("utf-8")); //寫入文本資料

        // 請求結束標誌
//        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
//        os.write(end_data);
        os.flush();
        os.close();

        /* 取得Response內容 */
        InputStream is = conn.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn
                .getInputStream(), "utf-8"));
        String readLine = "";
        while ((readLine = in.readLine()) != null) {
            b.append(readLine);
        }
        in.close();
        Log.e("--oo-----------",b.toString());

//         得到回應碼
        int res = conn.getResponseCode();
        Log.e("oo----------oo",res+"");
        Log.e("oo----------oo",conn.getResponseMessage()+"");
        Log.e("oo----------oo","asdf " + conn.getResponseMessage());

        conn.disconnect();
        return b.toString();
    }
    public static HttpURLConnection doPostPicture(String urlStr, Map<String, String> paramMap, Map<String, File> fileMap)
            throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);// 允許輸入
        conn.setDoOutput(true);// 允許輸出
        conn.setUseCaches(false);
        conn.setReadTimeout(10 * 1000); // 緩存的最長時間
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);


        DataOutputStream os = new DataOutputStream(conn.getOutputStream());

//        StringBuilder sb = new StringBuilder(); //用StringBuilder拼接報文，用於上傳圖片資料

        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) { //在for迴圈中拼接報文，上傳文本資料

            text.append("--");
            text.append(BOUNDARY);
            text.append("\r\n");

            text.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
            text.append(entry.getValue());

            text.append("\r\n");
        }

        os.write(text.toString().getBytes("utf-8")); //寫入文本資料
        if (fileMap != null) {
            for (Map.Entry<String, File> entryFile : fileMap.entrySet()) { //在for迴圈中拼接報文，上傳圖片資料
                StringBuilder sb = new StringBuilder();//用StringBuilder拼接報文，用於上傳圖片資料
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entryFile.getKey() + "\"; filename=\"" + entryFile.getValue().getName() + "\"" + LINEND);
                sb.append("Content-Type: image/jpg; charset=" + CHARSET + LINEND);
                sb.append(LINEND);

                os.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(entryFile.getValue());

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len); //寫入圖片資料
                }
                is.close();

                os.write(LINEND.getBytes());
            }
        }
        Log.i("======", text.toString());
        // 請求結束標誌
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        os.write(end_data);
        os.flush();
        os.close();


        /* 取得Response內容 */
        InputStream is = conn.getInputStream();
        int ch;
        StringBuffer b = new StringBuffer();
        while ((ch = is.read()) != -1) {
            b.append((char) ch);
        }


        BufferedReader in = new BufferedReader(new InputStreamReader(conn
                .getInputStream(), "utf-8"));
        String readLine = "";
        while ((readLine = in.readLine()) != null) {
            b.append(readLine);
        }
        in.close();
        Log.e("--oo-----------",b.toString());


//         得到回應碼
        int res = conn.getResponseCode();
        Log.e("oo----------oo",res+"");
        Log.e("oo----------oo",conn.getResponseMessage()+"");
        Log.e("oo----------oo","asdf " + conn.getResponseMessage());
//ToastUtils.showShort(this,"上傳成功");
//        System.out.println("asdf code "+ res);
//        System.out.println("asdf " + conn.getResponseMessage());

        conn.disconnect();
        return conn;
    }

}

