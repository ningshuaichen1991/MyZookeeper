package com.connect.run;

import	java.util.Scanner;

import com.connect.ZkConnect;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Random;

/**
 * @Classname RegistrationCenterClient
 */
public class RegistrationCenterClient {

    private ZooKeeper zooKeeper;

    private static List<String> serverList;

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public static void main(String[] args) throws InterruptedException {

        ZkConnect connect = new ZkConnect();
        connect.connect();
        ZooKeeper zooKeeper = connect.getZooKeeper();

        RegistrationCenterClient registrationCenterClient = new RegistrationCenterClient();
        registrationCenterClient.setZooKeeper(zooKeeper);

        serverList = registrationCenterClient.getServerList("/registrationCenterNode");
        System.out.println("服务器列表："+serverList);
        if(serverList!=null&&!serverList.isEmpty()){
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String next = scanner.next();
                int randomIndex = new Random().nextInt(serverList.size());
                System.out.println("随机访问一台服务："+registrationCenterClient.getData("/registrationCenterNode/"+serverList.get(randomIndex)));
            }
        }
    }


    /**
     * 功能描述: <br>
     * 〈获取集群的服务列表〉
     * @Param: [path]
     * @Return: java.util.List<java.lang.String>
     * @Author: csn
     * @Date: 2019/8/26 11:45
     */
    public List<String> getServerList(String path) {
        try {
            return zooKeeper.getChildren(path, new serverListWatch());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getData(String path){
        try {
            return new String(zooKeeper.getData(path, null,null));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    class serverListWatch implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged
                    ||watchedEvent.getType()== Event.EventType.NodeDataChanged){
                System.out.println("服务列表节点数据产生变化~~~~~~");
                serverList = getServerList(watchedEvent.getPath());
                System.out.println("最新服务器列表："+serverList);
            }
        }

    }
}