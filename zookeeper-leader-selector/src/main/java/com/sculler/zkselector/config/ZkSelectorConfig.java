package com.sculler.zkselector.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sculler.zkselector.service.LeaderSelectorClient;

@Configuration
public class ZkSelectorConfig {
	private static final String SELECTOR_PATH="/zkselector/leader";
			
	@Value("${zookeeper.server}")
	String zookeeperServer;
	
	@Value("${zookeeper.namespace}")
	String nameSpace;
	
	@Value("${zookeeper.instance}")
	String instance;

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
	
	@Bean
	public LeaderSelectorClient leaderSelector(CuratorFramework client) {
		LeaderSelectorClient selectClient = new LeaderSelectorClient(client, SELECTOR_PATH, instance);
		try {
			selectClient.start();
		} catch (Exception except) {
			System.out.println(except);
		}
		return selectClient;
	}
}
