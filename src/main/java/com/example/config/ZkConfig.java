package com.example.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common.ZkQueueStringSerializer;
import com.example.listener.CountSharedCountListener;
import com.example.listener.CsvFileQueueListener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ZkConfig {

	@Value("${zoo.connect.str}")
	public String zkConnection;
	private static final String cvsQueuePath = "/async_task/cvs/path";
	private static final String countPath = "/async_task/cvs/count1";
	private static final String PATH = "/examples/counter";

	@Bean
	public CuratorFramework getCuratorFramework() {
		RetryPolicy retry = new ExponentialBackoffRetry(1000, 10);
		CuratorFramework newClient = CuratorFrameworkFactory.newClient(zkConnection, retry);
		newClient.getCuratorListenable().addListener(new CuratorListener() {
			@Override
			public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
				log.info("curator event " + event.getType());
			}
		});
		newClient.start();
		return newClient;
	}
	
	@SneakyThrows
	@Bean
	public DistributedQueue<String> getCvsQueue(CuratorFramework newClient,CsvFileQueueListener csvFileQueryConsumer){
		QueueBuilder<String> builder = QueueBuilder.builder(newClient, csvFileQueryConsumer, new ZkQueueStringSerializer(), cvsQueuePath);
		DistributedQueue<String> buildQueue = builder.buildQueue();
		buildQueue.start();
		return buildQueue;
	}

	@SneakyThrows
	@Bean
	public SharedCount getSharedCount(CuratorFramework newClient,CountSharedCountListener countSharedCountListener){
		SharedCount count = new SharedCount(newClient, countPath, 0);
		count.addListener(countSharedCountListener);
		count.start();
		return count;
	}
	
	@SneakyThrows
	@Bean
	public DistributedAtomicLong getDistributedAtomicLong(CuratorFramework newClient,CountSharedCountListener countSharedCountListener){
		final DistributedAtomicLong count = new DistributedAtomicLong(newClient, PATH, new RetryNTimes(10, 10));
		return count;
	}
}
