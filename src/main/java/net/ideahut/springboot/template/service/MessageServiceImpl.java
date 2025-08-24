package net.ideahut.springboot.template.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.bean.BeanReload;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.StringHelper;
import net.ideahut.springboot.helper.WebMvcHelper;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.message.MessageHandler;
import net.ideahut.springboot.message.RedisMessageHandler;
import net.ideahut.springboot.message.dto.LanguageDto;
import net.ideahut.springboot.object.Message;
import net.ideahut.springboot.object.Option;
import net.ideahut.springboot.object.StringMap;
import net.ideahut.springboot.redis.RedisParam;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

@Service
class MessageServiceImpl implements MessageService, BeanReload, BeanConfigure<MessageService> {
	
	private static final class Keys {
		private Keys() {}
		private static String resources(String prefix) {
			return prefix + "RESOURCES";
		}
		private static String resource(String prefix, String type, String language) {
			return prefix + "RESOURCE-" + type + "-" + language;
		}
	}

	private static final String DEFAULT_LANGUAGE = "id";
	
	private boolean configured = false;
	private String redisPrefix;
	private RedisMessageHandler messageHandler;
	private List<Option> activeLanguages;
	
	
	private AppProperties appProperties;
	private DataMapper dataMapper;
	private EntityTrxManager entityTrxManager;
	private RedisTemplate<String, byte[]> redisTemplate;
	
	
	@Override
	public Callable<MessageService> onConfigureBean(ApplicationContext applicationContext) {
		MessageServiceImpl self = this;
		return new Callable<MessageService>() {
			@SuppressWarnings("unchecked")
			@Override
			public MessageService call() throws Exception {
				appProperties = applicationContext.getBean(AppProperties.class);
				dataMapper = applicationContext.getBean(DataMapper.class);
				entityTrxManager = applicationContext.getBean(EntityTrxManager.class);
				redisTemplate = applicationContext.getBean(AppConstants.Bean.Redis.COMMON, RedisTemplate.class);
				RedisParam<String, byte[]> redisParam = new RedisParam<String, byte[]>()
				.setAppIdEnabled(true)
				.setEncryptEnabled(true)
				.setPrefix("MESSAGE")
				.setRedisTemplate(redisTemplate)
				.prepareDefault();
				messageHandler = new RedisMessageHandler()
				.setDataMapper(dataMapper)
				.setDefaultLanguage(DEFAULT_LANGUAGE)
				.setEntityTrxManager(entityTrxManager)
				.setLimitReloadData(100)
				.setMaxReloadThread(3)
				.setRedisParam(redisParam);
				messageHandler.afterPropertiesSet();
				messageHandler.onConfigureBean(applicationContext).call();
				activeLanguages = new ArrayList<>();
				for (LanguageDto language : messageHandler.getActiveLanguages().values()) {
					activeLanguages.add(new Option(language.getLanguageCode(), language.getName()));
				}
				redisPrefix = FrameworkHelper.createStorageKeyPrefix(redisParam, applicationContext);
				onReloadBean();
				configured = true;
				return self;
			}
		};
	}

	@Override
	public boolean isBeanConfigured() {
		return configured;
	}

	@Override
	public boolean onReloadBean() throws Exception {
		if (configured) {
			if (!messageHandler.onReloadBean()) {
				return false;
			}
			clearResources();
			loadResources();
		} else {
			clearResources();
			loadResources();
		}
		return true;
	}
	
	@Override
	public List<Option> getActiveLanguages() {
		return activeLanguages;
	}

	@Override
	public String getDefaultLanguage() {
		return DEFAULT_LANGUAGE;
	}

	@Override
	public JsonNode getResource(String type) {
		String language = getRequestLanguage();
		ObjectNode node = dataMapper.createObjectNode();
		node.putArray("languages").addAll(dataMapper.convert(activeLanguages, ArrayNode.class));
		node.put("active", language);
		String ckey = Keys.resource(redisPrefix, type, language);
		ValueOperations<String, byte[]> valops = redisTemplate.opsForValue();
		byte[] bytes = valops.get(ckey);
		if (bytes != null) {
			JsonNode message = dataMapper.read(bytes, JsonNode.class);
			node.set("message", message);
		}
		return node;
	}
	
	@Override
	public String getText(String code, boolean checkArgs, String... args) {
		getRequestLanguage();
		return messageHandler.getText(code, checkArgs, args);
	}

	@Override
	public String getText(String code, String... args) {
		return messageHandler.getText(code, args);
	}

	@Override
	public String getText(String code) {
		return messageHandler.getText(code);
	}

	@Override
	public Message getMessage(String code, boolean checkArgs, String... args) {
		return messageHandler.getMessage(code, checkArgs, args);
	}

	@Override
	public Message getMessage(String code, String... args) {
		return messageHandler.getMessage(code, args);
	}

	@Override
	public Message getMessage(String code) {
		return messageHandler.getMessage(code);
	}

	@Override
	public StringMap getMap(String... codes) {
		return messageHandler.getMap(codes);
	}

	@Override
	public List<String> getList(String... codes) {
		getRequestLanguage();
		return messageHandler.getList(codes);
	}

	
	private String getRequestLanguage() {
		String language = RequestContext.currentContext().getAttribute(MessageHandler.Attribute.LANGUAGE);
		if (language != null) {
			return language;
		}
		HttpServletRequest request = WebMvcHelper.getRequest();
		language = WebMvcHelper.getHeader(request, HttpHeaders.ACCEPT_LANGUAGE, "");
		if (!isValidLanguage(language)) {
			language = DEFAULT_LANGUAGE;
		}
		RequestContext.currentContext().setAttribute(MessageHandler.Attribute.LANGUAGE, language);
		return language;
	}
	
	private boolean isValidLanguage(String language) {
		Option option = getActiveLanguages().stream()
		.filter(o -> language.equals(o.getValue()))
		.findAny()
		.orElse(null);
		return option != null;
	}
	
	private Map<String, byte[]> getResources(String type) throws Exception {
		Map<String, byte[]> map = new HashMap<>();
		String path = FrameworkHelper.replacePath(StringHelper.removeEnd(appProperties.getMessagePath(), "/"));
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
		Resource[] resources = resolver.getResources(path + "/" + type + "/*.json");
		for (Resource resource : resources) {
			String filename = ObjectHelper.useOrDefault(resource.getFilename(), "");
			ObjectHelper.callIf(
				!filename.isEmpty(), 
				() -> {
					String language = filename.replace(".json", "");
					return ObjectHelper.callIf(
						isValidLanguage(language), 
						() -> {
							byte[] bytes = IOUtils.toByteArray(resource.getInputStream());
							String ckey = Keys.resource(redisPrefix, type, language);
							// support format yaml, json, & xml
							// validasi format sebelum disimpan ke redis dalam format json
							JsonNode node = FrameworkHelper.loadConfiguration(bytes, JsonNode.class);
							bytes = dataMapper.writeAsBytes(node, DataMapper.JSON);
							map.put(ckey, bytes);
							return null;
						}
					);
				}
			);
		}
		return map;
	}
	
	private void loadResources() throws Exception {
		Map<String, byte[]> cvalues = new HashMap<>();
		cvalues.putAll(getResources("mobile"));
		cvalues.putAll(getResources("portal"));
		Set<byte[]> bkeys = new LinkedHashSet<>();
		for (String key : cvalues.keySet()) {
			bkeys.add(key.getBytes());
		}
		if (!bkeys.isEmpty()) {
			ListOperations<String, byte[]> lisops = redisTemplate.opsForList();
			lisops.rightPushAll(Keys.resources(redisPrefix), bkeys);
			ValueOperations<String, byte[]> valops = redisTemplate.opsForValue();
			valops.multiSet(cvalues);
		}
	}
	
	private void clearResources() {
		Set<String> allkeys = new HashSet<>();
		ListOperations<String, byte[]> lisops = redisTemplate.opsForList();
		String rkey = Keys.resources(redisPrefix);
		Long size = lisops.size(rkey);
		if (size != null && size > 0) {
			List<byte[]> bkeys = lisops.leftPop(rkey, size);
			if (bkeys != null) {
				while(!bkeys.isEmpty()) {
					allkeys.add(new String(bkeys.remove(0)));
				}
			}
		}
		redisTemplate.delete(allkeys);
	}

}
