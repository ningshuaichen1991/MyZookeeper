package com.registCenterCommon;

import com.common.ZkConnect;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

@Data
@AllArgsConstructor
public class RegistCenterProvider {

    /**
     * ip地址
     */
    private String  ip;
    /**
     * 端口号
     */
    private String version;
    /**
     * 端口号
     */
    private int port;
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务提供方注册
     * @throws Exception
     */
    public  void register() throws Exception {
        ZkConnect zk = new ZkConnect();
        zk.connect();
        ZooKeeper zooKeeper = zk.getZooKeeper();
        if(zooKeeper.exists("/"+serviceName,false)==null){
            zk.createPersistentNode("/"+serviceName,"");
        }
        Stat stat =  zooKeeper.exists("/"+serviceName+"/"+version,false);
        if(stat == null){
            zk.createPersistentNode("/"+serviceName+"/"+version,"");
        }
        zk.createEphemeralNode("/"+serviceName+"/"+version+"/"+ip+":"+port,"");
        System.out.println("服务提供方注册成功，注册信息为：/"+serviceName+"/"+version+"/"+ip+":"+port);
    }
}