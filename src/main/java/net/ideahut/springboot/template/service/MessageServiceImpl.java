package net.ideahut.springboot.template.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.bean.BeanReload;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.message.MessageHandler;
import net.ideahut.springboot.object.Message;
import net.ideahut.springboot.object.Option;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.RequestUtil;
import net.ideahut.springboot.util.StringUtil;

@Service
public class MessageServiceImpl implements MessageService, BeanReload, BeanConfigure<MessageService> {
	
	private static final TypeReference<Set<String>> TYPEREF_KEYS = new TypeReference<Set<String>>() {};
	private static final List<Option> activeLanguages = Arrays.asList(
		new Option("id", "Bahasa"),
		new Option("en", "English")
	);
	private static final String defaultLanguage = "id";
	
	private static final class Keys {
		private Keys() {}
		private static final String PREFIX = "MessageService";
		private static final String LOCK = PREFIX + "--LOCK--";
		private static final String RESOURCE_KEYS = PREFIX + "--RESOURCE_KEYS--";
		private static final String LANGUAGE_LIST = PREFIX + "--LANGUAGE_LIST--";
		private static final String LANGUAGE_WORDS = PREFIX + "--LANGUAGE_WORDS--";
	}
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private RedisTemplate<String, byte[]> redisTemplate;
	@Autowired
	private AppProperties appProperties;
	
	@Override
	public Callable<MessageService> configureBean(ApplicationContext applicationContext) {
		MessageServiceImpl self = this;
		return new Callable<MessageService>() {
			@Override
			public MessageService call() throws Exception {
				reloadBean();
				return self;
			}
		};
	}

	@Override
	public boolean isBeanConfigured() {
		return true;
	}
	
	@Override
	public boolean reloadBean() throws Exception {
		if (!lock(true)) {
			return false;
		}
		try {
			clear();
			Map<String, byte[]> mobile = loadResource("mobile");
			Map<String, byte[]> portal = loadResource("portal");
			ValueOperations<String, byte[]> valueOps = redisTemplate.opsForValue();
			valueOps.multiSet(mobile);
			valueOps.multiSet(portal);
			Set<String> keys = new HashSet<>();
			keys.addAll(mobile.keySet());
			keys.addAll(portal.keySet());
			byte[] bytes = dataMapper.writeAsBytes(keys, DataMapper.JSON);
			valueOps.set(Keys.RESOURCE_KEYS, bytes);
			loadMessage();
		} catch (Exception e) {
			throw e;
		} finally {
			lock(false);
		}
		return true;
	}
	
	private synchronized boolean lock(boolean yes) {
		if (yes) {
			ValueOperations<String, byte[]> lockOps = redisTemplate.opsForValue();
			byte[] lockBytes = lockOps.get(Keys.LOCK);
			if (lockBytes != null) {
				return false;
			}
			lockOps.set(Keys.LOCK, "1".getBytes());
		} else {
			redisTemplate.delete(Keys.LOCK);
		}
		return true;
	}
	
	private void clear() {
		Set<String> cacheKeys = new HashSet<>();
		ValueOperations<String, byte[]> valueOps = redisTemplate.opsForValue();
		byte[] vResources = valueOps.get(Keys.RESOURCE_KEYS);
		if (vResources != null) {
			Set<String> resources = dataMapper.read(vResources, TYPEREF_KEYS);
			cacheKeys.addAll(resources);
			cacheKeys.add(Keys.RESOURCE_KEYS);
			
		}
		if (isMessageImplemented) {
			byte[] vLanguages = valueOps.get(Keys.LANGUAGE_LIST);
			cacheKeys.add(Keys.LANGUAGE_LIST);
			if (vLanguages != null) {
				Set<String> languages = dataMapper.read(vLanguages, TYPEREF_KEYS);
				cacheKeys.addAll(languages);
				for (String language : languages) {
					byte[] vWords = valueOps.get(Keys.LANGUAGE_WORDS + "-" + language);
					if (vWords != null) {
						Set<String> words = dataMapper.read(vWords, TYPEREF_KEYS);
						cacheKeys.addAll(words);
					}
				}
			}
		}
		if (!cacheKeys.isEmpty()) {
			redisTemplate.delete(cacheKeys);
		}
	}
	
	/*
	 * Menyimpan resource yang akan digunakan di client (Mobile / Portal)
	 */
	private Map<String, byte[]> loadResource(String type) throws Exception {
		Map<String, byte[]> map = new HashMap<>();
		String path = StringUtil.removeEnd(appProperties.getMessagePath(), "/");
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
		Resource[] resources = resolver.getResources(path + "/" + type + "/*.json");
		for (Resource resource : resources) {
			String language = resource.getFilename().replace(".json", "");
			byte[] bytes = resource.getContentAsByteArray();
			String ckey = Keys.PREFIX + "-" + type + "-" + language;
			// menvalidasi json
			JsonNode node = dataMapper.read(bytes, JsonNode.class);
			bytes = dataMapper.writeAsBytes(node, DataMapper.JSON);
			map.put(ckey, bytes);
		}
		return map;
	}
	
	/*
	 * Menyimpan daftar kata (berdasarkan bahasa) dari database atau resource bundle ke redis
	 */
	private boolean isMessageImplemented = false;
	private void loadMessage() throws Exception {
		// TODO: impelementasi pengambilan data dari database untuk disimpan ke redis
	}
	
	private String getRequestLanguage() {
		String language = RequestContext.currentContext().getAttribute(MessageHandler.Attribute.LANGUAGE);
		if (language != null) {
			return language;
		}
		language = RequestUtil.getHeader(HttpHeaders.ACCEPT_LANGUAGE, "");
		String flang = language;
		Option option = activeLanguages.stream()
		  .filter(o -> flang.equals(o.getValue()))
		  .findAny()
		  .orElse(null);
		if (option == null) {
			language = defaultLanguage;
		}
		RequestContext.currentContext().setAttribute(MessageHandler.Attribute.LANGUAGE, language);
		return language;
	}

	@Override
	public List<Option> getActiveLanguages() {
		return activeLanguages;
	}

	@Override
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	@Override
	public JsonNode getResource(String type) {
		ObjectNode node = dataMapper.createObjectNode();
		String language = getRequestLanguage();
		node.putArray("languages").addAll(dataMapper.convert(activeLanguages, ArrayNode.class));
		node.put("active", language);
		ValueOperations<String, byte[]> valueOps = redisTemplate.opsForValue();
		byte[] bytes = valueOps.get(Keys.PREFIX + "-" + type + "-" + language);
		if (bytes != null) {
			JsonNode message = dataMapper.read(bytes, JsonNode.class);
			node.set("message", message);
		}
		return node;
	}

	@Override
	public String getText(String code, boolean checkArgs, String... args) {
		if (!isMessageImplemented) {
			return code != null ? code : "";
		}
		String text = "";
		if (code != null) {
			if (checkArgs) {
				if (args != null) {
					List<String> codes = new ArrayList<>();
					codes.add(code);
					codes.addAll(Arrays.asList(args));
					List<String> words = getList(codes.toArray(new String[0]));
					text = words.remove(0);
					codes.remove(0);
					if (text == null) {
						text = code;
					}
					if (codes.size() == words.size()) {
						int i = 0;
						while (!words.isEmpty()) {
							String word = words.remove(0);
							if (word == null) {
								word = codes.get(i);
							}
							text = text.replace("{" + i + "}", word + "");
							i++;
						}
						codes.clear();
					}
				} else {
					text = getWord(code);
				}
			} else {
				text = getWord(code);
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						text = text.replace("{" + i + "}", args[i] + "");
					}
				}
			}
		}
		return text;
	}

	@Override
	public String getText(String code, String... args) {
		return getText(code, false, args);
	}

	@Override
	public String getText(String code) {
		return getText(code, false);
	}

	@Override
	public Message getMessage(String code, boolean checkArgs, String... args) {
		String text = getText(code, checkArgs, args);
		return new Message(code, text);
	}

	@Override
	public Message getMessage(String code, String... args) {
		return getMessage(code, false, args);
	}

	@Override
	public Message getMessage(String code) {
		return getMessage(code, false);
	}

	@Override
	public Map<String, String> getMap(String... codes) {
		Map<String, String> map = new LinkedHashMap<>();
		if (codes != null) {
			List<String> words = getList(codes);
			if (codes.length == words.size()) {
				for (int i = 0; i < codes.length; i++) {
					map.put(codes[i], words.get(i));
				}
			} else {
				for (int i = 0; i < codes.length; i++) {
					map.put(codes[i], codes[i]);
				}
			}
		}
		return map;
	}

	@Override
	public List<String> getList(String... codes) {
		if (!isMessageImplemented) {
			return codes != null ? new ArrayList<>(Arrays.asList(codes)) : new ArrayList<>();
		}
		
		List<String> list = new ArrayList<>();
		if (codes != null && codes.length != 0) {
			String language = getRequestLanguage();
			List<String> lcodes = new ArrayList<>();
			List<String> lkeys = new ArrayList<>();
			for (String code : codes) {
				lcodes.add(code);
				lkeys.add(getKeyCode(language, code));
			}
			ValueOperations<String, byte[]> valueops = redisTemplate.opsForValue();
			List<byte[]> lvalues = valueops.multiGet(lkeys);
			if (lvalues != null && lvalues.size() == lcodes.size()) {
				int i = 0;
				while (!lvalues.isEmpty()) {
					byte[] value = lvalues.remove(0);
					list.add(value != null ? new String(value) : lcodes.get(i));
					i++;
				}
			}
			lkeys.clear();
			lcodes.clear();
		}		
		return list;
	}

	private String getKeyCode(String language, String code) {
		return Keys.PREFIX + "-language-" + language + "-" + code;
	}
	
	private String getWord(String code) {
		if (code != null) {
			String language = getRequestLanguage();
			String key = getKeyCode(language, code);
			ValueOperations<String, byte[]> valueops = redisTemplate.opsForValue();
			byte[] value = valueops.get(key);
			if (value != null) {
				return new String(value);
			} else {
				return code;
			}
		}
		return "";
	}

}
