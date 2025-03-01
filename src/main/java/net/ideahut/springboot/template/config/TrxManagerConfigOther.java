package net.ideahut.springboot.template.config;

import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import net.ideahut.springboot.definition.DatabaseAuditDefinition;
import net.ideahut.springboot.entity.DatasourceProperties;
import net.ideahut.springboot.entity.JpaProperties;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.properties.OtherProperties;

/*
 * Konfigurasi Transaction Manager & Entity Manager yang kedua
 * 
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
(
	entityManagerFactoryRef = "otherEntityManagerFactory",
	transactionManagerRef = "otherTransactionManager"
)
class TrxManagerConfigOther {
	
	@Bean(name = "otherDataSource")
	DataSource dataSource(
		OtherProperties otherProperties		
	) {
		DatasourceProperties datasource = otherProperties.getTrxManager().getSecond().getDatasource();
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
	
	@Bean(name = "otherPersistenceManagedTypes")
	PersistenceManagedTypes persistenceManagedTypes(
		ResourceLoader resourceLoader
	) {
		return PersistenceManagedTypes.of(
			Application.Package.APPLICATION + ".entity.app.Information",
			Application.Package.APPLICATION + ".entity.app.CompositeHardDel"
		);
	}
	
	@Bean(name = "otherEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean entityManagerFactory(
		OtherProperties otherProperties,
		EntityManagerFactoryBuilder builder,
		@Qualifier("otherPersistenceManagedTypes")
		PersistenceManagedTypes persistenceManagedTypes,
		@Qualifier("otherDataSource") 
		DataSource dataSource
	) {
		JpaProperties jpa = otherProperties.getTrxManager().getSecond().getJpa();
		Map<String, Object> properties = FrameworkHelper.getHibernateSettings(jpa.getProperties());
		return builder
		.dataSource(dataSource)
		.managedTypes(persistenceManagedTypes)
		.properties(properties)			
		.build();
	}
	
	@Bean(name = "otherTransactionManager")
	PlatformTransactionManager transactionManager(
		@Qualifier("otherEntityManagerFactory") 
		EntityManagerFactory entityManagerFactory
	) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(name = "otherAuditDatasource")
	DataSource auditDatasource(
		OtherProperties otherProperties		
	) {
		DatabaseAuditDefinition audit = otherProperties.getTrxManager().getSecond().getAudit();
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
	
	@Bean(name = "otherAuditSessionFactory")
	LocalSessionFactoryBean auditSessionFactory(
		OtherProperties otherProperties,
		@Qualifier("otherAuditDatasource") 
		DataSource datasource
	) {
		DatabaseAuditDefinition audit = otherProperties.getTrxManager().getSecond().getAudit();
		Properties properties = FrameworkHelper.getHibernateProperties(audit.getJpa().getProperties());
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(datasource);
        sessionFactory.setHibernateProperties(properties);
        return sessionFactory;
	}
	
}
