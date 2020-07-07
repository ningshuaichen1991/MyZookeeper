package com.zk.run;

import com.common.ZkConnect;


public class HelloZk {

    public static void main(String[] args) throws Exception {
        ZkConnect connection =new ZkConnect();
        connection.connect();//连接zookeeper服务
        connection.createPersistentNode("/HelloMyZKxxo","888");//创建一个持久化节点
        String zkData = connection.getData("/HelloMyZKxxo");//获取HelloMyZKxxo节点数据
        System.out.println(zkData);
        connection.deleteNode("/HelloMyZKxxo");//删除HelloMyZKxxo
        Thread.sleep(Integer.MAX_VALUE);
    }
}
