package com.ha;
import	java.awt.Event;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class ZKConnect implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static final String ADDRESS = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";

    private ZooKeeper zooKeeper;

    private String status;

    @Override
    public void process(WatchedEvent event) {
        System.out.println("receive the event:" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }

    /**
     * 连接zk
     * @throws InterruptedException
     */
    public void connect() throws InterruptedException {
        try {
            zooKeeper = new ZooKeeper(ADDRESS, 1000, this);
            countDownLatch.await();
            System.out.println("已连接！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

}
