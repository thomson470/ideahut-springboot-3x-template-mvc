package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.redis.RedisHelper;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.template.app.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Redis
 */
@Configuration
class RedisConfig {
	
	@Primary
	@Bean(name = AppConstants.Bean.Redis.COMMON)
	RedisTemplate<String, byte[]> commonRedis(
		AppProperties appProperties
	) throws Exception {
		RedisProperties.Connection properties = appProperties.getRedis().getCommon();
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, getTemplate(), false);
	}
	
	@Bean(name = AppConstants.Bean.Redis.ACCESS)
	RedisTemplate<String, byte[]> accessRedis(
		DataMapper dataMapper,
		AppProperties appProperties
	) throws Exception {
		RedisProperties.Connection properties = dataMapper.copy(appProperties.getRedis().getCommon());
		properties.getStandalone().setDatabase(1);
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, getTemplate(), false);
	}
	
	private RedisProperties.Template getTemplate() {
		return new RedisProperties.Template()
		.setKeyType(String.class)
		.setValueType(byte[].class);
	}
	
}
