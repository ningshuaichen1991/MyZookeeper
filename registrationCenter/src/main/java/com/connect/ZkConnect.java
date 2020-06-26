package com.connect;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @Classname ZkConnect
 * @Description TODO
 * @Date 2019/8/26 11:32
 * @Created by csn
 */
public class ZkConnect implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static final String ADDRESS = "192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181";

    private ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        System.out.println("receive the event:" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }

    Watcher watch = new Watcher(){
        @Override
        public void process(WatchedEvent event) {
            System.out.println("开始监听");
            System.out.println(event.getState());
        };
    };

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void connect(){
        try {
            zooKeeper = new ZooKeeper(ADDRESS, 5000, this);
            countDownLatch.await();
            System.out.println("已连接！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String createPersistentNode(String path,String data) throws Exception{
        return this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}