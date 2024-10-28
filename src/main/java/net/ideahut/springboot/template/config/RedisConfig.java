package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.redis.RedisHelper;
import net.ideahut.springboot.redis.RedisProperties;
import net.ideahut.springboot.template.AppConstants;
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
		RedisProperties properties = appProperties.getRedis().getCommon();
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, false);
	}
	
	@Bean(name = AppConstants.Bean.Redis.ACCESS)
	RedisTemplate<String, byte[]> accessRedis(
		DataMapper dataMapper,
		AppProperties appProperties		
	) throws Exception {
		RedisProperties properties = dataMapper.copy(appProperties.getRedis().getCommon());
		properties.getStandalone().setDatabase(1);
		RedisConnectionFactory connectionFactory = RedisHelper.createRedisConnectionFactory(properties, true);
		return RedisHelper.createRedisTemplate(connectionFactory, false);
	}
	
}
