package com.zk.run;

import com.zk.connection.ZKConnection;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.List;

public class HelloZk {

    public static void main(String[] args) throws Exception {
        ZKConnection connection =new ZKConnection();
        connection.connect();
        //connection.setData("/myZk","123");
       // connection.createPersistentNode("/myZk/1.0.2","888");
        connection.createEphemeralNode("/myZk/1.0.2/127.0.0.2:8080","123");
        connection.createEphemeralNode("/myZk/1.0.2/127.0.0.2:8081","123");
        List<String> strings = connection.getChildren("/myZk/1.0.2");
        System.out.println(strings);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
