package com.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Lock01 implements Watcher {

	private String lockKey;

	private String lockPath = "/lock";

	private ZooKeeper zooKeeper;

	private CountDownLatch countDownLatch;

	private String lastLockKey;

	private String lockFilePath;

	public static void main(String[] args) {

		for(int i = 0;i<10;i++){
			new Thread(new Runnable() {

				@Override
				public void run() {
					Lock01 lock = new Lock01("lock");
					lock.tryLock(3000);
					try {
						System.out.println("线程"+Thread.currentThread().getName()+"已获取锁");
						Thread.sleep(1000);
						lock.unLock();
						System.out.println("线程"+Thread.currentThread().getName()+"已释放锁");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

	}

	/**
	 * 加锁
	 *
	 * @param times
	 * @return
	 */
	public boolean tryLock(long times) {
		try {
			lockFilePath = zooKeeper.create(lockPath+"/" , "".getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println(lockFilePath);
			List<String> childList = zooKeeper.getChildren(lockPath, false);//不需要监控
			System.out.println(childList+"线程："+Thread.currentThread().getName() );
			if (childList.size() == 1) {
				return true;
			}
			Collections.sort(childList);
			if (childList.get(0).equals(lockFilePath)) {
				return true;
			}
			lastLockKey =childList.get(this.getLockKeyIndex(lockFilePath.substring("/lock/".length(),lockFilePath.length()),childList)-1);//获取它前面的节点
			zooKeeper.exists(lockPath+"/"+lastLockKey, new lockWatch());
			countDownLatch = new CountDownLatch(1);
			countDownLatch.await(times, TimeUnit.MILLISECONDS);

		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private int getLockKeyIndex(String lockKey,List<String> dataList){
		for(int i=0;i<dataList.size(); i++){
			if(lockKey.equals(dataList.get(i))){
				return i;
			}
		}
		return -1;
	}

	/**
	 * 监听上一个删除的
	 */
	class lockWatch implements Watcher {

		@Override
		public void process(WatchedEvent watchedEvent) {
			if(watchedEvent.getType() == Event.EventType.NodeDeleted){
				System.out.println("释放锁！！！");
				countDownLatch.countDown();
			}
		}
	}

	public void unLock() {
		try {
			zooKeeper.delete(lockFilePath, -1);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Lock01(String lockKey) {
		try {
			countDownLatch = new CountDownLatch(1);
			zooKeeper = new ZooKeeper("192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181", 5000, this);
			countDownLatch.await();
			Stat stat = zooKeeper.exists(lockPath, false);
			if (stat == null) {
				// 如果根节点不存在，则创建根节点
				zooKeeper.create(lockPath, new byte[0],
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (IOException | InterruptedException | KeeperException e) {
			e.printStackTrace();
		}
		this.lockKey = lockKey;
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive the event:" + event);
		if (Event.KeeperState.SyncConnected == event.getState()) {
			countDownLatch.countDown();
		}
	}
}