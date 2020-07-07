package com.ha.client;

import com.common.ZkConnect;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.Socket;


public class Client {

   private static ZooKeeper zooKeeper;

    private static final String serverName = "MyHAServer";

   static String serverHost = null;

    public static void main(String[] args) throws InterruptedException, KeeperException {

        ZkConnect connect = new ZkConnect();
        connect.connect();//建立连接会堵塞
        zooKeeper = connect.getZooKeeper();

        setServerHost();//设置端口号

        for(int i = 0;i<50;i++){
            System.out.println("连接服务器地址为:"+serverHost);
            String ip = serverHost.substring(0,serverHost.indexOf(":"));
            String port = serverHost.substring(serverHost.indexOf(":")+1,serverHost.length());

            try {
                Socket socket = new Socket(ip,Integer.parseInt(port));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("连接失败！！！");
                continue;
            }
            System.out.println("连接成功！！！");

            Thread.sleep(5000);
        }
    }
//
    private static void setServerHost(){
        try{
            byte b [] = zooKeeper.getData("/" + serverName, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                        System.out.println("开始重新获取服务节点数据……");
                        setServerHost();
                    }
                }
            },null);
            System.out.println("最新的服务器地址为："+new String(b));
            serverHost = new String(b);
        }catch (KeeperException e) {
            try {
                Thread.sleep(1000);//停顿一秒，让其他服务有时间创建节点
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            setServerHost();//出现异常后重新获取
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}