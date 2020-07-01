package com.zk.connection;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;


public class ZKConnection implements Watcher {

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	public static final String ADDRESS = "192.168.56.102:2181,192.168.56.103:2181,192.168.56.104:2181";

	private ZooKeeper zooKeeper;

	@Override
	public void process(WatchedEvent event) {
		System.out.println("receive the event:" + event);
		if (Event.KeeperState.SyncConnected == event.getState()) {
				countDownLatch.countDown();
		}
	}
	
	
	Watcher watch = new Watcher(){
		@Override
		public void process(WatchedEvent event) {
			System.out.println("开始监听");
			System.out.println(event.getState());
		};
	};
	
	public void register(){
		zooKeeper.register(watch);
	}

	public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}



	public void connect() throws InterruptedException {
		try {
			zooKeeper = new ZooKeeper(ADDRESS, 50000, this);
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
	 * 创建临时节点
	 * @param path
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String createEphemeralNode(String path,String data) throws Exception{
		return this.zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}
	
	/**
	 * 获取路径下所有子节点
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public List<String> getChildren(String path) throws KeeperException, InterruptedException{
		return zooKeeper.getChildren(path, false);
	}
	
	public void exsit(String path) throws KeeperException, InterruptedException{
		Stat data = zooKeeper.exists(path, watch);
		zooKeeper.setData(path, "6666".getBytes(), data.getVersion());
	}
	
	
	public void setData(String path,String data) throws KeeperException, InterruptedException{
		zooKeeper.setData(path, data.getBytes(), -1);
	}
	
	/**
	 * 获取数据
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public String getData(String path) throws KeeperException, InterruptedException{
		final byte [] data = zooKeeper.getData(path,new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				try {
					System.out.println(112);
					String s = getData(path);
					System.out.println("改变后的data:");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, null);
		if(data == null){
			return "";
		}
		return new String(data);
	}
	
	
	/**
	 * 获取数据
	 * @param path
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
//	public String getData(String path) throws KeeperException, InterruptedException{
//		final byte [] data = zooKeeper.getData(path,true,null);
//		zooKeeper.register(watch);
//		if(data == null){
//			return "";
//		}
//		return new String(data);
//	}
}
