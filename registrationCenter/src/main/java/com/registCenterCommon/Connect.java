package com.registCenterCommon;

/**
 * 连接对象
 */
public class Connect {

    /**
     * ip地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;


    public Connect(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Connect{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}