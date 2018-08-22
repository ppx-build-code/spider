package com.links86.spider.util;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dyu
 * @date 2018/08/22
 */
public class DynamicSpiderUtils {
    public static String authHeader(String orderno, String secret, int timestamp){
        //拼装签名字符串
        String planText = String.format("orderno=%s,secret=%s,timestamp=%d", orderno, secret, timestamp);

        //计算签名
        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(planText).toUpperCase();

        //拼装请求头Proxy-Authorization的值
        String authHeader = String.format("sign=%s&orderno=%s&timestamp=%d", sign, orderno, timestamp);
        return authHeader;
    }

    public static String request(String url) throws IOException {

        final String ip = "forward.xdaili.cn";//这里以正式服务器ip地址为准
        final int port = 80;//这里以正式服务器端口地址为准

        int timestamp = (int) (new Date().getTime()/1000);
        //以下订单号，secret参数 须自行改动
        final String authHeader = authHeader("ZF20188220784hdE23h", "3588ac35b7f043b09d1d58762bb31000", timestamp);
        ExecutorService thread = Executors.newFixedThreadPool(1);

        Connection.Response response = null;
        try {
            response = Jsoup.connect(url)
                    .proxy(ip, port)
                    .validateTLSCertificates(false) //忽略证书认证,每种语言客户端都有类似的API
                    .header("Proxy-Authorization", authHeader)
                    .execute();
        } catch (HttpStatusException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.body();
    }

    public static void main(String[] args) throws IOException {
        String content = DynamicSpiderUtils.request("https://m.tianyancha.com/search?key=集商网络科技上海");
        System.out.println(content);
    }
}
