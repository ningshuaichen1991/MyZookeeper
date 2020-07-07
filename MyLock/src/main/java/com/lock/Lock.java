package com.lock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.common.ZkConnect;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * AsyncCallback.StringCallback 获取节点名称的时候回调
 * AsyncCallback.Children2Callback 获取子节点和stat的回调
 *
 */


public class Lock implements Watcher ,AsyncCallback.StringCallback,AsyncCallback.Children2Callback{


	private String lockPath = "/lock";

	private static ZooKeeper zooKeeper;

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	private String lockDataName;

	private String threadName;


    public String getLockDataName() {
        return lockDataName;
    }

    public void setLockDataName(String lockDataName) {
        this.lockDataName = lockDataName;
    }

    public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}

	public void setZooKeeper(ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * 加锁
	 * @return
	 */
	public void tryLock() throws KeeperException, InterruptedException {
		threadName = Thread.currentThread().getName();
        System.out.println(threadName+" 开始抢锁……");
        zooKeeper.create(lockPath,threadName.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL,this,Thread.currentThread().getName());
		countDownLatch.await();
	}

	/**
	 * 解锁
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void unlock() throws KeeperException, InterruptedException {
		System.out.println(threadName+" "+lockDataName+" 释放锁……");
		zooKeeper.delete(lockDataName,-1);
	}


    /**
     * getChildren
     * @param i
     * @param s
     * @param o
     * @param list
     * @param stat
     */
	@Override
	public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
		Collections.sort(list);//排序
		int index = list.indexOf(lockDataName.substring(1));
		if(index == 1){//位于第一个位置
			System.out.println(threadName+","+lockDataName+" 已抢到锁……");
			countDownLatch.countDown();
		}else{
			//System.out.println(threadName+" 等待锁释放 , 监听的节点："+list.get(index-1));
			try {
				zooKeeper.exists("/"+list.get(index-1),this);//监听它前面的节点数据，如果删除则触发wacher
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}




    /**
     * String
     * @param i
     * @param s
     * @param o
     * @param s1
     */
	@Override
	public void processResult(int i, String s, Object o, String s1) {
      zooKeeper.getChildren("/",false,this,"myLock");
      lockDataName = s1;
	}


	@Override
	public void process(WatchedEvent event) {
		if (Event.EventType.NodeDeleted == event.getType()) {
			zooKeeper.getChildren("/",false,this,"myLock");
		}
	}
}