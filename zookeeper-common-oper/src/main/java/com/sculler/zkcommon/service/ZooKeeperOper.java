package com.sculler.zkcommon.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZooKeeperOper {
	@Autowired
	private CuratorFramework client;

	public boolean checkPath(String path) {
		Stat stat = null;
		try {
			stat = client.checkExists().forPath(path);
			if (stat != null) {
				System.out.println(path + " exist.");
				return true;
			}
		} catch (Exception except) {
			System.out.println(except);
		}
		
		return false;
	}

	public void createPath(String path, String value, boolean isPersistent) {
		try {
			if (isPersistent) {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(Ids.OPEN_ACL_UNSAFE)
						.forPath(path, value.getBytes());
			} else {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).withACL(Ids.OPEN_ACL_UNSAFE)
				.forPath(path, value.getBytes());
			}
			
			System.out.println("create success path: " + path);
		} catch (Exception except) {
			System.out.println(except);
		}
		
	}
	
	public void updateData(String path, String value) {
		try {
			client.setData().forPath(path, value.getBytes());
			System.out.println("set success path: " + path + " value: " + value);
		} catch (Exception except) {
			System.out.println(except);
		}
	}
	
	public String getData(String path) {
		String result = null;
		try {
			Stat stat = new Stat();
			result = new String(client.getData().storingStatIn(stat).forPath(path));
			stat.getVersion();
			System.out.println("get success path: " + path + " result: " + result);
		} catch (Exception except) {
			System.out.println(except);
		}
		
		return result;
	}
	
	public void delete(String path) {
		try {
			client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
			System.out.println("delete success path: " + path);
		} catch (Exception except) {
			System.out.println(except);
		}
	}
	
	
	public void addWatcher(String path, CuratorWatcher watcher) {
		try {
			client.getData().usingWatcher(watcher).forPath(path);
			System.out.println("add watcher success");
		} catch (Exception except) {
			System.out.println(except);
		}
	}
	
	public NodeCache addMultiWatcher(String path) {
		try {	
			final NodeCache nodeCache = new NodeCache(client, path);
			nodeCache.start(true);
			if (nodeCache.getCurrentData() != null) {
				System.out.println(path + " current data: " + new String(nodeCache.getCurrentData().getData()));
			} else {
				System.out.println(path + " current data: null");
			}
			
			nodeCache.getListenable().addListener(new NodeCacheListener() {
				@Override
				public void nodeChanged() throws Exception {
					if (nodeCache.getCurrentData() != null) {
						String re = new String(nodeCache.getCurrentData().getData());
						System.out.println("path: " + nodeCache.getCurrentData().getPath() + " result: " + re);
					} else {
						System.out.println("path: " + path + " deleted");
					}
				}
			});
			
			System.out.println("set listener success");
			
			return nodeCache;
		} catch (Exception except) {
			System.out.println(except);
		} 
		
		return null;
	}

}
