package net.ideahut.springboot.template.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.template.properties.AppProperties.Audit;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * Konfigurasi Primary Transaction Manager & Entity Manager
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
(
	entityManagerFactoryRef = "entityManagerFactory_1",
	transactionManagerRef = "transactionManager_1",
	basePackages = {
		AppConstants.PACKAGE + ".repo"
	}
)
class TrxManagerConfig1 {
	
	@Autowired
	private Environment environment;	
	@Autowired
	private AppProperties appProperties;
	

	@Primary
	@Bean(name = "dataSource_1")
	@ConfigurationProperties(prefix = "spring.datasource")
	protected DataSource dataSource() {
		String jndi = environment.getProperty("spring.datasource.jndi-name", "").trim();
		if (!jndi.isEmpty()) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create().build();
		}
    }
	
	// https://docs.spring.io/spring-framework/reference/core/aot.html#aot.bestpractices.jpa
	@Bean(name = "persistenceManagedTypes_1")
	protected PersistenceManagedTypes persistenceManagedTypes(
		ResourceLoader resourceLoader
	) {
		return 
		new PersistenceManagedTypesScanner(resourceLoader)
		.scan(
			AppConstants.PACKAGE + ".entity"
		);
	}
	
	@Primary
	@Bean(name = "entityManagerFactory_1")
	protected LocalContainerEntityManagerFactoryBean entityManagerFactory(
		EntityManagerFactoryBuilder builder,
		@Qualifier("persistenceManagedTypes_1") PersistenceManagedTypes persistenceManagedTypes,
		@Qualifier("dataSource_1") DataSource dataSource,
		@Qualifier("auditSessionFactory_1") SessionFactory auditSessionFactory
	) {
		Map<String, Object> properties = FrameworkUtil.getHibernateSettings(environment, "spring.jpa.properties");
		/*
		 * Session Factory audit dapat di-set disini
		 * atau bisa juga di application.properties / application.yml di property:
		 * - "spring.jpa.properties.hibernate.audit_identifier": audit id yang digunakan agar terhubung dengan AuditHandler, contoh: spring_sample
		 * - "spring.jpa.properties.hibernate.audit_bean_name": nama bean audit session factory, contoh: auditSessionFactory
		 * 
		 * EntityIntegrator.setAuditSessionFactory("spring_sample", properties, auditSessionFactory);
		 */
		return builder
			.dataSource(dataSource)		
			.persistenceUnit("default")
			.managedTypes(persistenceManagedTypes)
			.properties(properties)			
			.build();
	}

	@Primary
	@Bean(name = "transactionManager_1")
	protected PlatformTransactionManager transactionManager(
		@Qualifier("entityManagerFactory_1") EntityManagerFactory entityManagerFactory
	) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(name = "auditDatasource_1")
	protected DataSource auditDatasource() {
		Audit audit = appProperties.getAudit();
		String jndi = audit.getDatasource().getJndiName();
		jndi = jndi != null ? jndi.trim() : "";
		if (jndi.length() != 0) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create()
			.driverClassName(audit.getDatasource().getDriverClassName())
			.url(audit.getDatasource().getJdbcUrl())
			.username(audit.getDatasource().getUsername())
			.password(audit.getDatasource().getPassword())
			.build();
		}
    }	
	
	@Bean(name = "auditSessionFactory_1")
	protected LocalSessionFactoryBean auditSessionFactory(
		@Qualifier("auditDatasource_1") DataSource datasource
	) {
		Audit audit = appProperties.getAudit();
		Properties properties = FrameworkUtil.getHibernateProperties(audit.getJpa().getProperties());
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(datasource);
        sessionFactory.setHibernateProperties(properties);
        sessionFactory.setEntityInterceptor(null);
        return sessionFactory;
	}
	
}
