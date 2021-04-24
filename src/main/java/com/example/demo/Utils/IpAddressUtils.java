package com.example.demo.Utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author Jyo
 * @Date 2020/3/21
 */

/**
 *X-Forwarded-For：Squid服务代理
 *
 * Proxy-Client-IP：apache服务代理
 *
 * WL-Proxy-Client-IP：weblogic服务代理
 *
 * HTTP_CLIENT_IP：http客户端IP
 *
 * X-Real-IP：nginx服务代理
 *
 *HTTP_X_FORWARDED_FOR:	http代理服务器IP
 */
public class IpAddressUtils {

    public static String getIPAddress(HttpServletRequest request) {

        String ipAddress = request.getHeader("X-Forwarded-For");
        String UNKNOWN_IP = "unknown";

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        //如果有多层代理，第一个ip为客户端的真实IP
        if (ipAddress != null && ipAddress.length() != 0) {
            ipAddress = ipAddress.split(",")[0];
        }

        //若ip还无法获取，最后通过request.getRemoteAddr()获取主机ip
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1") ) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        return ipAddress;
    }
}
