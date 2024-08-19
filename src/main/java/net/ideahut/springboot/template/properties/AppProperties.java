package net.ideahut.springboot.template.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.audit.DatabaseAuditProperties;
import net.ideahut.springboot.cache.CacheGroupProperties;
import net.ideahut.springboot.entity.DatabaseProperties;
import net.ideahut.springboot.mail.MailProperties;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.task.TaskProperties;

/*
 * Class properties yang definisinya sama dengan application.yml
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class AppProperties {
	
	private Boolean loggingError;
	private String messagePath;
	private String reportPath;
	
	private Map<String, String> cors = new HashMap<>();
	private List<Class<?>> ignoredHandlerClasses = new ArrayList<>();
	
	private MailProperties mail = new MailProperties();
	private Audit audit = new Audit();
	private Task task = new Task();
	private Redis redis = new Redis();
	private Cache cache = new Cache();
	private Grid grid = new Grid();
	//private TrxManager trxManager = new TrxManager();
	private Admin admin = new Admin();
	private Api api = new Api();
	
	
	@Setter
	@Getter
	public static class Cache {
		private List<CacheGroupProperties> groups = new ArrayList<>();
	}
	
	@Setter
	@Getter
	public static class Audit extends DatabaseProperties {
		private DatabaseAuditProperties properties = new DatabaseAuditProperties();
	}
	
	@Setter
	@Getter
	public static class Redis {
		private RedisProperties common 	= new RedisProperties();
	}
	
	@Setter
	@Getter
	public static class Task {
		private TaskProperties common 	= new TaskProperties();
		private TaskProperties audit 	= new TaskProperties();
	}
	
	@Setter
	@Getter
	public static class Admin extends AdminProperties {
		private String configFile;
		private String credentialFile;
	}
	
	@Setter
	@Getter
	public static class Grid {
		private String location;
		private String definition;
	}
	
	@Setter
	@Getter
	public static class Api {
		private String name;
		private Enable enable = new Enable();
		private Consumer consumer = new Consumer();
		private JwtProcessor jwtProcessor = new JwtProcessor();
		private RedisExpiry redisExpiry = new RedisExpiry();
		private Integer signatureTimeSpan;
		private String defaultDigest;
		
		@Setter
		@Getter
		public static class Enable {
			private Boolean consumer;
			private Boolean crud;
			private Boolean sync;
		}
		
		// Service akan di-consume oleh service lain
		@Setter
		@Getter
		public static class Consumer {
			private String secret;
			private String digest;
			private Long expiry;
		}
		
		// Parameter untuk membuat Jwt Token
		@Setter
		@Getter
		public static class JwtProcessor {
			private String secret;
			private String digest;
			private Long expiry;
			private Boolean check;
		}
		
		// Berapa lama object disimpan di redis dalam detik
		@Setter
		@Getter
		public static class RedisExpiry {
			private Integer accessNull;
			private Integer accessItem;
			private Integer consumerNull;
			private Integer consumerItem;
		}
	}
	
}
