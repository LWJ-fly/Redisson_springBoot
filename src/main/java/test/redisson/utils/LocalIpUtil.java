package test.redisson.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 类描述：获取本机地址工具类
 * @author 技匠
 * @date 2023-02-14 13:16:22
 * 版权所有 Copyright www.wenmeng.online
 */
public class LocalIpUtil {
    
    public static List<String> getIpAddress() {
        List<String> list = new LinkedList<>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = enumeration.nextElement();
                if (network.isVirtual() || !network.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if ((address instanceof Inet4Address || address instanceof Inet6Address)) {
                        if (address instanceof Inet6Address) {
                            System.out.println(address.getHostAddress());
                        }
                        list.add(address.getHostAddress());
                    }
                }
    
            }
        } catch (Exception ignore) {}
        
        return list;
    }
    
    public static String getInet6Address() {
        String inet6Address = "";
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = enumeration.nextElement();
                if (network.isVirtual() || !network.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if ((address instanceof Inet6Address)) {
                        String hostAddress = address.getHostAddress();
                        if (inet6Address.length() < hostAddress.length()) {
                            inet6Address = hostAddress;
                        }
                    }
                }
            }
        } catch (Exception ignore) {}
        
        return inet6Address;
    }
    
}
