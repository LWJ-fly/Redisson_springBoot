package test.redisson.utils;

import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import io.micrometer.core.instrument.util.StringUtils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 类描述：获取本机地址工具类
 * @author 技匠
 * @date 2023-02-14 13:16:22
 * 版权所有 Copyright www.wenmeng.online
 */
public class LocalIpUtil {
    
    private static String Ipv4;
    private static String Ipv6;
    private static Long lastRefreshTime = -1L;
    // 刷新间隔 2分钟
    private static final Long refreshTime = 2 * 60 * 1000L;
    
    private static void init() {
        try {
            if (lastRefreshTime + refreshTime > SystemClock.now() && StringUtils.isNotBlank(Ipv4) && StringUtils.isNotBlank(Ipv6)) {
                return;
            }
            lastRefreshTime = SystemClock.now();
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface network = enumeration.nextElement();
                if (network.isVirtual() || !network.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        // 非内网地址，并且是IPv4
                        Ipv4 = address.getHostAddress();
                    }
                    if (address.isLinkLocalAddress() && address instanceof Inet6Address) {
                        Ipv6 = address.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
    }
    
    public static String getInet6Address() {
        init();
        return Ipv6;
    }
    
    public static String getInet4Address() {
        init();
        return Ipv4;
    }
    
}
