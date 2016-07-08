package terminal.spectre.com.terminalsystem.core;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import terminal.spectre.com.terminalsystem.bean.Request;

/**
 * Created by Administrator on 2016/7/8.
 */
//  GET / HTTP/1.1
//  Host: 10.0.10.109:10086
//  User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0
//  Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//  Accept-Language:zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3
//  Accept-Encoding: gzip, deflate
//  Connection: keep-alive

public class RequsetParser {

    public static final String TAG = "RequsetParser";

    public static Request parse(InputStream in) throws IOException {
        Request requset = new Request();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = null;
        while((line = reader.readLine())!=null&&!line.equals("")){
            Log.i(TAG,line);
            parseLine(line,requset);
        }
        return requset;
    }

    private static void parseLine(String line, Request requset) {
        if(line == null||requset==null){
            return;
        }

        //请求行
        if(line.startsWith("GET")){
            //设置请求方法
            requset.setMethod("GET");

            //设置url
            int httpIndex = line.indexOf("HTTP");
            if(httpIndex-1>=4){
                String url = line.substring(4, httpIndex - 1);
                requset.setRequestURL(url);
            }

            //设置协议版本
            String version = line.substring(httpIndex,line.length());
            requset.setProtocol(version);
        }else if(line.startsWith("Host:")){
            String host = line.substring("Host:".length() + 1);
            requset.setHost(host);
        }
    }

}
