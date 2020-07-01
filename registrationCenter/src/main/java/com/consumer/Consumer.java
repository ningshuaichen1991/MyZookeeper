package com.consumer;
import	java.net.Socket;


import com.registCenterCommon.Connect;
import com.registCenterCommon.RegistCenterConsumer;

import java.util.List;
import java.util.Random;

/**
 * @Classname Consumer
 */
public class Consumer {


    private static String serviceName = "myServer";

    private static String version = "1.0.1";

    public static void main(String[] args) throws Exception {

        RegistCenterConsumer registCenterConsumer = new RegistCenterConsumer(serviceName,version);
        List<Connect> services = registCenterConsumer.pullServiceList();

        for(int i = 0;i<20;i++){
            int randomIndex = new Random().nextInt(services.size());
            Connect connect = services.get(randomIndex);
            Socket socket = new Socket(connect.getIp(),connect.getPort());
            System.out.println(connect+"连接成功！！！");
        }

        Thread.sleep(40000);

        System.out.println("重新访问…………………………");

        System.out.println("最新列表信息为："+services);

        for(int i = 0;i<20;i++){
            int randomIndex = new Random().nextInt(services.size());
            Connect connect = services.get(randomIndex);
            Socket socket = new Socket(connect.getIp(),connect.getPort());
            System.out.println(connect+"连接成功！！！");
        }
    }
}