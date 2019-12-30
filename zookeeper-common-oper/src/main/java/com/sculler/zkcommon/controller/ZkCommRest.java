package com.sculler.zkcommon.controller;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sculler.zkcommon.service.ZooKeeperOper;

@RestController
public class ZkCommRest {
	@Autowired
	private ZooKeeperOper oper;
	
	private static final String ZOO_PATH="/ztest/foo";
	private static final String FAR_PATH="/ztest/far";
	
	@RequestMapping(value = "/zookeeper/test", method = RequestMethod.GET)
	public String testZoo() {
		// test zoo path with watcher
		if (!oper.checkPath(ZOO_PATH)) {
			oper.createPath(ZOO_PATH, "initial", false);
		}
		
		CuratorWatcher watcher = new CuratorWatcher() {
			@Override
			public void process(WatchedEvent event) throws Exception {
				System.out.println("trigger watcher path: " + ZOO_PATH);
			}
		};
		
		oper.addWatcher(ZOO_PATH, watcher);
		
		oper.updateData(ZOO_PATH, "firstupdate");
		
		String zoo_value = oper.getData(ZOO_PATH);
		System.out.println("get new value: " + zoo_value);
		
		oper.delete(ZOO_PATH);
		
		//test far path with listener
		if (!oper.checkPath(FAR_PATH)) {
			oper.createPath(FAR_PATH, "initial", false);
		}
		
		NodeCache nodeCache = oper.addMultiWatcher(FAR_PATH);
		
		oper.updateData(FAR_PATH, "firstfar");
		
		String far_value = oper.getData(FAR_PATH);
		System.out.println("get new value: " + far_value);
		
		oper.updateData(FAR_PATH, "secondfar");
		
		oper.delete(FAR_PATH);
		
		CloseableUtils.closeQuietly(nodeCache);
		
		return "OK";
	}

}
