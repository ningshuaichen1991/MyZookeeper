package com.common;

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

    public static final String ADDRESS = "192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181/testLock";

    private ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        System.out.println("receive the event:" + event);
        if (Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
    }

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
     * 创建持久化节点
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String createPersistentNode(String path,String data) throws Exception{
        return this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    /**
     *创建临时节点
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String createEphemeralNode(String path,String data) throws Exception{
        return this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }



    private String getDataByPath(String path,Watcher watcher) throws KeeperException, InterruptedException {
        byte [] b =  this.zooKeeper.getData(path, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        },null);
        return new String(b);
    }

    /**
     * 获取节点数据
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String getData(String path) throws KeeperException, InterruptedException {
        byte [] b =  this.zooKeeper.getData(path, new Watcher(){

            @Override
            public void process(WatchedEvent event) {

                if(event.getType() == Event.EventType.NodeDeleted){
                    System.out.println("节点路径："+event.getPath()+"已被删除……");
                }
            }
        },null);
        return new String(b);
    }

    /**
     * 删除节点
     * @param path
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void deleteNode(String path) throws KeeperException, InterruptedException {
        this.zooKeeper.delete(path,-1);
    }
}