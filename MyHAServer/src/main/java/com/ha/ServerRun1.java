package com.ha;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Random;


public class ServerRun1 {

    private ZooKeeper zooKeeper;

    String ipAddress = "172.20.10."+new Random().nextInt(100);

    existsWatcher existsWatcher = null;


    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {

        com.ha.ZKConnect connect = new com.ha.ZKConnect();
        connect.connect();//建立连接会堵塞
        ServerRun1 run = new ServerRun1();
        run.setZooKeeper(connect.getZooKeeper());
        run.createActive();
        Thread.sleep(Integer.MAX_VALUE);
    }


    /**
     * 获取Active
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createActive(){
        System.out.println("服务器ip"+ipAddress+"正在占用active");
        try {
            if(exists("/serverRun")){
                System.out.println("服务器状态是Standby");
            }else{
                System.out.println("开始创建节点！！！！");
                createTempData("/serverRun",ipAddress);
                System.out.println(ipAddress+"的服务器状态是Active");
            }
        } catch (KeeperException e) {
            System.out.println("切换active出现异常，已被其他服务器占用");
            try {
                zooKeeper.exists("/serverRun",existsWatcher);
            } catch (KeeperException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建临时节点
     * @param path
     * @param data
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createTempData(String path, String data) throws KeeperException, InterruptedException {
        this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    /**
     * 该路径是否存在
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean exists(String path) throws KeeperException, InterruptedException {
        existsWatcher = (existsWatcher == null?new existsWatcher():existsWatcher);
        Stat s= zooKeeper.exists(path,existsWatcher);
        System.out.println(existsWatcher);
        return s!=null;
    }

    /**
     * 服务器节点删除的监听器类
     */
    class existsWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                System.out.println("有服务器已经宕机！！！");
                createActive();
            }
        }
    }
}
