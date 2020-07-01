package com.ha.server;

import com.ha.common.HAAction;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server2 {


    public static boolean isRunning = true;

    private static String serviceName = "MyHAServer";

    private static int port = 8091;

    private static String ip = "127.0.0.1";


    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocket serverSocket = new ServerSocket(port);

        HAAction acceptor = new HAAction(ip,port,serviceName);
        acceptor.createActive();

        while(isRunning){
            Socket socket = serverSocket.accept();
            System.out.println("连接当前连接的ip和端口号为："+ip+":"+port);
        }
    }
}