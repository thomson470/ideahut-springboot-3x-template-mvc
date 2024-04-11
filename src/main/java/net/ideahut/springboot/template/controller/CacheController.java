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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.ideahut.springboot.cache.CacheGroupHandler;
import net.ideahut.springboot.cache.CacheGroupProperties;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.object.CacheData;
import net.ideahut.springboot.template.properties.AppProperties;
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
	private AppProperties appProperties;
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private CacheGroupHandler cacheGroupHandler;
	
	@GetMapping("/groups")
	public Result groups() {
		ArrayNode items = dataMapper.createArrayNode();
		List<CacheGroupProperties> groups = appProperties.getCache().getGroups();
		for (CacheGroupProperties group : groups) {
			if (0 != group.getLimit()) {
				Long size = cacheGroupHandler.size(group.getName());
				ObjectNode item = items.addObject();
				item.put("name", group.getName());
				item.put("limit", group.getLimit());
				item.put("size", size);
			}
		}
		return Result.success(items);
	}
	
	@GetMapping("/get")
	protected Result get(
		@RequestParam(value = "group", required = false) String group,
		@RequestParam("key") String key
	) {
		List<Boolean> flag = new ArrayList<>();
		flag.add(Boolean.TRUE);
		CacheData data = cacheGroupHandler.get(CacheData.class, group(group), key, new Callable<CacheData>() {
			@Override
			public CacheData call() throws Exception {
				CacheData data = new CacheData();
				data.setContent("Contoh cache - " + UUID.randomUUID());
				data.setGroup(group(group));
				data.setKey(key);
				data.setTimestamp(TimeUtil.currentEpochMillis());
				flag.set(0, Boolean.FALSE);
				return data;
			}
		});
		return Result.success(data).setInfo("cached", flag.remove(0));
	}
	
	@GetMapping("/size")
	protected Result size(
		@RequestParam(value = "group", required = false) String group
	) {
		Long size = cacheGroupHandler.size(group(group));
		return Result.success(size);
	}
	
	@GetMapping("/keys")
	protected Result keys(
		@RequestParam(value = "group", required = false) String group
	) {
		List<String> keys = cacheGroupHandler.keys(group(group));
		return Result.success(keys);
	}
	
	@DeleteMapping("/delete")
	protected Result delete(
		@RequestParam(value = "group", required = false) String group,
		@RequestParam("key") String key
	) {
		cacheGroupHandler.delete(group(group), key);
		return Result.success();
	}
	
	@DeleteMapping("/clear")
	protected Result clear(
		@RequestParam(value = "group", required = false) String group
	) {
		cacheGroupHandler.clear(group(group));
		return Result.success();
	}
	
	private String group(String input) {
		String group = input != null ? input.trim() : "";
		return !group.isEmpty() ? group : GROUP;
	}
	
}
