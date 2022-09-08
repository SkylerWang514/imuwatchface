package com.zheolls.zenfacedigit.utils;

import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerAddress {
    public String getLocalIpAddress() {
        try {
            boolean flag = false;
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String address = inetAddress.getHostAddress().toString();
                        if (address.contains("wlan0")) {
                            flag = true;
                        }
                        String ip = re.getpairstring("[0-9]+.[0-9]+.[0-9]+.[0-9]+", address);
                        if (flag && ip != null) {
                            Log.i("localip", ip);
                            return ip;
                        }

                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    public String getGatwayAddress() {
        String localAddress = getLocalIpAddress();
        if (localAddress == null) return null;
        String head = re.getpairstring("[0-9]+.[0-9]+.[0-9]+.", localAddress);
        Log.i("head", head);


        for (int i = 1; i < 255; i++) {
            String ip = head + i;
            Log.i("findip", ip);
            if (isServiceAlive(ip)) {
                Log.i("gatewayip", head + i);
                return head + i;
            }
        }
        return null;
    }

    public static boolean isServiceAlive(String ip) {

        Socket socket = new Socket();
        Log.i("serviceAlive", "isServiceAlicve");
        SocketAddress address = new InetSocketAddress(ip, 8088);
        try {
            socket.connect(address, 200);
            socket.close();
            Log.i("serviceAlive", "serviceAlicve");
            return true;
        } catch (Exception e) {
            Log.i("socket", ip + "socketError");
            return false;
        }
    }
}
