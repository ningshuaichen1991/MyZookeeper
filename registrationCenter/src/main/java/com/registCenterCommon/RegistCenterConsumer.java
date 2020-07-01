package com.registCenterCommon;

import com.common.ZkConnect;
import lombok.Data;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class RegistCenterConsumer {

    /**
     * 端口号
     */
    private String version;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * zk对象
     */
    private ZooKeeper zooKeeper;

    /**
     * 服务列表容器
     */
    private  final List<Connect> serviceList = new CopyOnWriteArrayList();


    public RegistCenterConsumer(String serviceName,String version){
        this.serviceName = serviceName;
        this.version = version;
    }


    /**
     * 服务提供方注册
     * @throws Exception
     */
    public  List<Connect> pullServiceList() throws Exception {
        ZkConnect zk = new ZkConnect();
        zk.connect();
        zooKeeper = zk.getZooKeeper();
        List<String> serverList = this.getServerList("/"+serviceName+"/"+version);
        serviceList.addAll(this.getConnectByString(serverList));
        return serviceList;
    }


    /**
     * 根据服务列表获取连接对象列表
     * @param list
     * @return
     */
    private  List<Connect> getConnectByString(List<String> list){
        List<Connect> connectList = new ArrayList<>();
        for(String str : list){
            String ip = str.substring(0,str.indexOf(":"));
            String port = str.substring(str.indexOf(":")+1,str.length());
            connectList.add(new Connect(ip,Integer.parseInt(port)));
        }
        return connectList;
    }

    /**
     * 功能描述: <br>
     * 〈获取集群的服务列表〉
     * @Param: [path]
     * @Return: java.util.List<java.lang.String>
     */
    private List<String> getServerList(String path) {
        try {
            return zooKeeper.getChildren(path, new serverListWatch());
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    class serverListWatch implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType()== Event.EventType.NodeChildrenChanged
                    ||watchedEvent.getType()== Event.EventType.NodeDataChanged){
                System.out.println("服务列表节点数据产生变化~~~~~~");
                serviceList.clear();
                serviceList.addAll(getConnectByString(getServerList(watchedEvent.getPath())));
                System.out.println("最新服务器列表："+serviceList);
            }
        }

    }

}