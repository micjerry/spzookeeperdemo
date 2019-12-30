package com.sculler.zkcommon.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkCommConfig {

	@Value("${zookeeper.server}")
	String zookeeperServer;
	
	@Value("${zookeeper.namespace}")
	String nameSpace;


	@Bean
	public CuratorFramework curatorFramework() {
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(zookeeperServer)
				.sessionTimeoutMs(1000)
				.connectionTimeoutMs(1000)
				.retryPolicy(new ExponentialBackoffRetry(1000, 3))
				.namespace(nameSpace)
				.build();
		client.start();
		return client;
	}
}
