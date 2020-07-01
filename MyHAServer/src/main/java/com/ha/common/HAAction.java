package com.ha.common;

import com.ha.ZKConnect;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

public class HAAction {
    /**
     * ip地址
     */
    private String ip;
    /**
     * 端口号
     */
    private int port;
    /**
     * 服务名称
     */
    private String serviceName;

    private ZooKeeper zooKeeper;

    existsWatcher existsWatcher = null;

    public HAAction(String ip,int port,String serviceName) throws InterruptedException {
        this.ip = ip;
        this.port = port;
        this.serviceName = serviceName;

        ZKConnect connect = new ZKConnect();
        connect.connect();//建立连接会堵塞
        zooKeeper = connect.getZooKeeper();
    }


    /**
     * 获取Active
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void createActive(){
        System.out.println("服务器ip "+ip+",port "+port+"正在占用active");
        try {
            String serviceNamePath = "/"+serviceName;

            if(exists(serviceNamePath)){
                System.out.println("服务器状态是Standby");
            }else{
                System.out.println("开始创建节点！！！！");
                createTempData(serviceNamePath,ip+":"+port);
                System.out.println(ip+":"+port+"的服务器状态是Active");
            }
        } catch (KeeperException e) {
            System.out.println("切换active出现异常，已被其他服务器占用");
            try {
                zooKeeper.exists("/serverRun",existsWatcher);//继续监听
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
     * 该路径是否存在
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public boolean exists(String path) throws KeeperException, InterruptedException {
        existsWatcher = (existsWatcher == null?new existsWatcher():existsWatcher);
        Stat s= zooKeeper.exists(path,existsWatcher);
        return s!=null;
    }

    /**
     * 服务器节点删除的监听器类
     */
    class existsWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType() == Event.EventType.NodeDeleted){
                System.out.println("有服务器已经宕机，其他服务器正在抢占资源节点！！！");
                createActive();
            }
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

}