package net.ideahut.springboot.template.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.object.CacheData;
import net.ideahut.springboot.util.TimeUtil;

/*
 * Contoh penggunaan CacheGroupHandler
 */
@ComponentScan
@RestController
@RequestMapping("/cache")
class CacheController {
	
	private static final String GROUP = "TEST1";

	@Autowired
	private CacheGroupHandler cacheGroupHandler;
	
	
	@GetMapping("/get")
	protected Result get(
		@RequestParam("key") String key
	) {
		List<Boolean> flag = new ArrayList<>();
		flag.add(Boolean.TRUE);
		CacheData data = cacheGroupHandler.get(CacheData.class, GROUP, key, new Callable<CacheData>() {
			@Override
			public CacheData call() throws Exception {
				CacheData data = new CacheData();
				data.setContent("Contoh cache - " + UUID.randomUUID());
				data.setGroup(GROUP);
				data.setKey(key);
				data.setTimestamp(TimeUtil.currentEpochMillis());
				flag.set(0, Boolean.FALSE);
				return data;
			}
		});
		return Result.success(data).setInfo("cached", flag.remove(0));
	}
	
	@GetMapping("/size")
	protected Result size() {
		Long size = cacheGroupHandler.size(GROUP);
		return Result.success(size);
	}
	
	@GetMapping("/keys")
	protected Result keys() {
		List<String> keys = cacheGroupHandler.keys(GROUP);
		return Result.success(keys);
	}
	
	@DeleteMapping("/delete")
	protected Result delete(
		@RequestParam("key") String key
	) {
		cacheGroupHandler.delete(GROUP, key);
		return Result.success();
	}
	
	@DeleteMapping("/clear")
	protected Result clear() {
		cacheGroupHandler.clear(GROUP);
		return Result.success();
	}
	
}
