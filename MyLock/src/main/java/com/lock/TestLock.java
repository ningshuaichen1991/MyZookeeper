package com.lock;

import com.common.ZkConnect;
import org.apache.zookeeper.KeeperException;

public class TestLock {

    public static void main(String[] args) throws InterruptedException {
        ZkConnect connect = new ZkConnect();
        connect.connect();


        for(int i = 0;i<5;i++){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Lock lock = new Lock();
                    lock.setZooKeeper(connect.getZooKeeper());
                    try {
                        lock.tryLock();
                        Thread.sleep(100);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    System.out.println(lock.getThreadName()+","+lock.getLockDataName()+" 业务处理……");


                    try {
                        lock.unlock();
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}