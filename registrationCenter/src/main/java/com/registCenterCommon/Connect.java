package com.registCenterCommon;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Connect {

    /**
     * ip地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;


    @Override
    public String toString() {
        return "Connect{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}