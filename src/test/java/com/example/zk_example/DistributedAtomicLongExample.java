package com.example.zk_example;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

public class DistributedAtomicLongExample {
	private static final int QTY = 5;
	private static final String PATH = "/examples/counter";

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.newClient("123.59.17.83:2181,123.59.17.180:2181,123.59.17.181:2181",
				new ExponentialBackoffRetry(1000, 3));
		client.start();
		ExecutorService service = Executors.newFixedThreadPool(QTY);
		for (int i = 0; i < QTY; i++) {
			final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));
			Callable<Void> task = new Callable<Void>() {
				public Void call() throws Exception {
					try {
						AtomicValue<Long> value = count.increment();
						System.out.println("succeed:" + value.succeeded());
						if (value.succeeded()) {
							System.out.println("Increment : from " + value.preValue() + "  to " + value.postValue());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			service.submit(task);
		}
		service.shutdown();
		service.awaitTermination(10, TimeUnit.MINUTES);
	}
}
