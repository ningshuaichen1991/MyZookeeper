package com.provider;

import com.registCenterCommon.RegistCenterProvider;

import java.net.ServerSocket;
import java.net.Socket;

public class Provider3 {

    public static boolean isRunning = true;

    private static String serviceName = "myServer";

    private static int port = 8082;

    private static String ip = "127.0.0.1";

    private static String version = "1.0.1";

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(port);

        RegistCenterProvider registCenterProvider = new RegistCenterProvider(ip,version,port,serviceName);
        registCenterProvider.register();

        while(isRunning){
            Socket socket = serverSocket.accept();
            System.out.println("当前连接的服务版本、ip和端口号为：/"+version+"/"+ip+":"+port);
        }
        serverSocket.close();
    }

}