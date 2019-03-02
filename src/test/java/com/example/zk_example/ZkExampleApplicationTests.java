package com.example.zk_example;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.publish.CsvQueuePublisher;
import com.example.service.LeaderSelectorExample;
import com.example.service.NodeCacheExample;
import com.example.service.lock.InterProcessMutexExample;

import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZkExampleApplicationTests {

	@Autowired
	CsvQueuePublisher csvQueuePublisher;
	@Autowired
	SharedCount sharedCount;
	@Autowired
	DistributedAtomicLong distributedAtomicLong;
	@Autowired
	LeaderSelectorExample leaderSelectorExample;
	@Autowired
	NodeCacheExample nodeCacheExample;
	@Autowired
	InterProcessMutexExample interProcessMutexExample;

	@SneakyThrows
	@Test
	public void contextLoads() {
		for (int i = 0; i < 10000; i++) {
			csvQueuePublisher.publish("wanghanchao11111" + i);
			Thread.sleep(5000);
		}
		Thread.sleep(10000000);
	}

	@SneakyThrows
	@Test
	public void SharedCountTest() {
		/*
		 * for (int i = 0; i < 20; i++) {
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { // sharedCount.trySetCount(i); }
		 * catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } }).start(); ; } Thread.sleep(4000);
		 * System.out.println(sharedCount.getCount()); Thread.sleep(10000000);
		 */
	}

	ExecutorService service = Executors.newFixedThreadPool(20);

	@SneakyThrows
	@Test
	public void sss() {
		for (int i = 0; i < 20; i++) {
			Callable<Void> task = new C(distributedAtomicLong);
			service.submit(task);
		}
		service.shutdown();
		service.awaitTermination(10, TimeUnit.MINUTES);
	}

	@SneakyThrows
	@Test
	public void sssa() {
		leaderSelectorExample.start();
	}

	@SneakyThrows
	@Test
	public void bbb() {
		nodeCacheExample.start();
	}

	public class C implements Callable<Void> {
		DistributedAtomicLong distributedAtomicLong;

		public C(DistributedAtomicLong distributedAtomicLong) {
			this.distributedAtomicLong = distributedAtomicLong;
		}

		@Override
		public Void call() throws Exception {
			try {
				// Thread.sleep(rand.nextInt(1000));
				AtomicValue<Long> value = distributedAtomicLong.increment();
				// AtomicValue<Long> value = count.decrement();
				// AtomicValue<Long> value =
				// count.add((long)rand.nextInt(20));
				System.out.println("succeed: " + value.succeeded());
				if (value.succeeded())
					System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SneakyThrows
	@Test
	public void startLock() {
		interProcessMutexExample.start();
	}
}
