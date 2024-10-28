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
import net.ideahut.springboot.api.ApiTokenServiceImpl;
import net.ideahut.springboot.api.WebMvcApiServiceImpl;
import net.ideahut.springboot.audit.DatabaseAuditProperties;
import net.ideahut.springboot.cache.CacheGroupProperties;
import net.ideahut.springboot.entity.DatabaseProperties;
import net.ideahut.springboot.entity.EntityForeignKeyParam;
import net.ideahut.springboot.mail.MailProperties;
import net.ideahut.springboot.object.TimeValue;
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
	
	private Boolean waitAllBeanConfigured;
	private Boolean loggingError;
	private Boolean autoStartScheduler;
	private String messagePath;
	private String reportPath;
	private EntityForeignKeyParam foreignKey;
	
	private Map<String, String> cors = new HashMap<>();
	private List<Class<?>> ignoredHandlerClasses = new ArrayList<>();
	
	private MailProperties mail = new MailProperties();
	private Audit audit = new Audit();
	private Task task = new Task();
	private Redis redis = new Redis();
	private Cache cache = new Cache();
	private Grid grid = new Grid();
	//private TrxManager trxManager = new TrxManager()
	private Admin admin = new Admin();
	private Api api = new Api();
	private TaskProperties webAsync = new TaskProperties();
	
	
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
		// batas atas / bawah timestamp signature yang dikirim oleh service lain
		// contoh: jika diset +- 1 menit, maka request dengan timestamp di luar range 1 menit akan ditolak
		private TimeValue signatureTimeSpan;
		private String defaultDigest;
		// Parameter Service yang akan di-consume oleh service lain
		private ApiTokenServiceImpl.Consumer consumer = new ApiTokenServiceImpl.Consumer();
		// Parameter untuk membuat Jwt Token
		private ApiTokenServiceImpl.JwtProcessor jwtProcessor = new ApiTokenServiceImpl.JwtProcessor();
		private WebMvcApiServiceImpl.RedisExpiry redisExpiry = new WebMvcApiServiceImpl.RedisExpiry();
		
		@Setter
		@Getter
		public static class Enable {
			// bisa di-consume oleh service lain atau tidak
			private Boolean consumer;
			// support api crud atau tidak
			private Boolean crud;
			// housekeeping, otomatis akan membersihkan crud dan request mapping yang tidak tersedia
			// dari semua konfigurasi api di database
			private Boolean sync;
		}
	}
	
}
