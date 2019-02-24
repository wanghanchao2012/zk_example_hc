package com.example.common;

import org.apache.curator.framework.recipes.queue.QueueSerializer;

public class ZkQueueStringSerializer implements QueueSerializer<String> {
	@Override
	public byte[] serialize(String item) {
		return item.getBytes();
	}

	@Override
	public String deserialize(byte[] bytes) {
		return new String(bytes);
	}

}
