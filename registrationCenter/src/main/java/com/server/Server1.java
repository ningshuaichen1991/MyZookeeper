package com.server;
import com.connect.ZkConnect;
import org.apache.zookeeper.ZooKeeper;

import	java.net.Socket;

import java.net.ServerSocket;

public class Server1 {

    public static boolean isRunning = true;

    private static String nodePath = "/registrationCenterNode";

    public static void main(String[] args) throws Exception {

        ZkConnect zk = new ZkConnect();
        zk.connect();
        ZooKeeper zooKeeper = zk.getZooKeeper();

        ServerSocket serverSocket = new ServerSocket(8080);
        while(isRunning){
            Socket socket = serverSocket.accept();
            String data = zk.createPersistentNode(nodePath+"/1.0.1","");
            socket.close();
        }
        serverSocket.close();
    }
}