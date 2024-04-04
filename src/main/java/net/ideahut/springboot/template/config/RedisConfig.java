package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.redis.RedisHelper;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Redis
 */
@Configuration
class RedisConfig {
	
	@Autowired
	private AppProperties appProperties;
	
	@Primary
	@Bean(name = AppConstants.Bean.Redis.COMMON)
	protected RedisTemplate<String, byte[]> commonRedis() throws Exception {
		RedisProperties properties = appProperties.getRedis().getCommon();
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, false);
	}
	
}
