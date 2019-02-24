package com.example.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CountSharedCountListener implements SharedCountListener {

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		System.out.println("State changed: " + newState.toString());

	}

	@Override
	public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
		System.out.println("Counter's value is changed to " + newCount);
	}

}
