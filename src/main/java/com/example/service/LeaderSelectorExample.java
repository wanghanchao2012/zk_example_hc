package com.example.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class LeaderSelectorExample {
	private static final int CLIENT_QTY = 10;
	private static final String PATH = "/examples/leader";
	@Autowired
	CuratorFramework client;

	public void start() throws Exception {
		List<CuratorFramework> clients = Lists.newArrayList();
		List<ExampleClient> examples = Lists.newArrayList();
		try {
			for (int i = 0; i < CLIENT_QTY; ++i) {
				clients.add(client);
				ExampleClient example = new ExampleClient(client, PATH, "Client #" + i);
				examples.add(example);
				example.start();
			}

			System.out.println("Press enter/return to quit\n");
			Thread.sleep(1000000);
		} finally {
			System.out.println("Shutting down...");
			for (ExampleClient exampleClient : examples) {
				CloseableUtils.closeQuietly(exampleClient);
			}
			for (CuratorFramework client : clients) {
				CloseableUtils.closeQuietly(client);
			}
		}
	}
}