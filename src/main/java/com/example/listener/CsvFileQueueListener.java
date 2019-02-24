package com.example.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CsvFileQueueListener implements QueueConsumer<String> {

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		log.info("zookeeper csv consumer state ==>" + newState.name());
	}

	@Override
	public void consumeMessage(String message) throws Exception {
		log.info("cvs file listener message-->" + message);
	}

}
