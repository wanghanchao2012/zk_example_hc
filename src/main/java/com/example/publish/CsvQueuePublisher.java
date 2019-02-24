package com.example.publish;

import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

@Component
public class CsvQueuePublisher {

	@Autowired
	DistributedQueue<String> cvsQueue;

	@SneakyThrows
	public void publish(String file) {
		cvsQueue.put(file);
	}

}
