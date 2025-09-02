package net.ideahut.springboot.template.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

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
import net.ideahut.springboot.definition.DatabaseAuditDefinition;
import net.ideahut.springboot.entity.DatasourceProperties;
import net.ideahut.springboot.entity.JpaProperties;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Primary Transaction Manager & Entity Manager
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
(
	entityManagerFactoryRef = "mainEntityManagerFactory",
	transactionManagerRef = "mainTransactionManager",
	basePackages = {
		Application.Package.APPLICATION + ".repo"
	}
)
class TrxManagerConfigMain {
	
	@Primary
	@Bean(name = "mainDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	DataSource dataSource(
		Environment environment		
	) {
		String jndi = environment.getProperty("spring.datasource.jndi-name", "").trim();
		if (!jndi.isEmpty()) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create().build();
		}
    }
	
	@Primary
	@Bean(name = "mainPersistenceManagedTypes")
	PersistenceManagedTypes persistenceManagedTypes(
		ResourceLoader resourceLoader
	) {
		return 
		new PersistenceManagedTypesScanner(resourceLoader)
		.scan(
			Application.Package.LIBRARY + ".api.entity",
			Application.Package.LIBRARY + ".job.entity",
			Application.Package.LIBRARY + ".message.entity",
			Application.Package.LIBRARY + ".sysparam.entity",
			Application.Package.APPLICATION + ".entity.app"
		);
	}
	
	@Primary
	@Bean(name = "mainEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean entityManagerFactory(
		Environment environment,
		ResourceLoader resourceLoader,
		EntityManagerFactoryBuilder builder,
		@Qualifier("mainPersistenceManagedTypes")
		PersistenceManagedTypes persistenceManagedTypes,
		@Qualifier("mainDataSource") 
		DataSource dataSource
	) {
		Map<String, Object> properties = FrameworkHelper.getHibernateSettings(environment, "spring.jpa.properties");
		/*
		 * Session Factory audit dapat di-set disini
		 *    EntityIntegrator.setAuditSessionFactory("spring_sample_main", properties, mainAuditSessionFactory);//-
		 * atau bisa juga di application.properties / application.yml di property:
		 * - "spring.jpa.properties.hibernate.audit_identifier": audit id yang digunakan agar terhubung dengan AuditHandler, contoh: spring_sample_main
		 * - "spring.jpa.properties.hibernate.audit_bean_name": nama bean audit session factory, contoh: mainAuditSessionFactory
		 * 
		 */
		return builder
			.dataSource(dataSource)	
			.managedTypes(persistenceManagedTypes)
			.properties(properties)			
			.build();
	}

	@Primary
	@Bean(name = "mainTransactionManager")
	PlatformTransactionManager transactionManager(
		@Qualifier("mainEntityManagerFactory") 
		EntityManagerFactory entityManagerFactory
	) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(name = "mainAuditDatasource")
	DataSource auditDatasource(
		AppProperties appProperties		
	) {
		DatabaseAuditDefinition audit = ObjectHelper.callOrElse(
			appProperties.getAudit() != null, 
			appProperties::getAudit, 
			DatabaseAuditDefinition::new
		);
		DatasourceProperties datasource = ObjectHelper.useOrDefault(audit.getDatasource(), DatasourceProperties::new);
		String jndi = datasource.getJndiName();
		jndi = jndi != null ? jndi.trim() : "";
		if (!jndi.isEmpty()) {
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(jndi);
		} else {
			return DataSourceBuilder.create()
			.driverClassName(datasource.getDriverClassName())
			.url(datasource.getJdbcUrl())
			.username(datasource.getUsername())
			.password(datasource.getPassword())
			.build();
		}
    }	
	
	@Bean(name = "mainAuditSessionFactory")
	LocalSessionFactoryBean auditSessionFactory(
		AppProperties appProperties,
		@Qualifier("mainAuditDatasource") 
		DataSource datasource
	) {
		DatabaseAuditDefinition audit = ObjectHelper.callOrElse(
			appProperties.getAudit() != null, 
			appProperties::getAudit, 
			DatabaseAuditDefinition::new
		);
		JpaProperties jpa = ObjectHelper.useOrDefault(audit.getJpa(), JpaProperties::new);
		Properties properties = FrameworkHelper.getHibernateProperties(jpa.getProperties());
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(datasource);
        sessionFactory.setHibernateProperties(properties);
        return sessionFactory;
	}
	
}
