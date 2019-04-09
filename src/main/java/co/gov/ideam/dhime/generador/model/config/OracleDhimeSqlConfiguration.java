package co.gov.ideam.dhime.generador.model.config;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import co.gov.ideam.dhime.generador.model.dhime.SeriesTiempoValores;



/**
 * Spring configuration of the "OracleSQL" database.
 * 
 * @author Deivid Suarez.
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "oracleDhimeEntityManager", 
		transactionManagerRef = "oracleDhimeTransactionManager", 
		basePackages = "co.gov.ideam.dhime.localdata.repository.dhime"
)
public class OracleDhimeSqlConfiguration {

	/**
	 * PostgreSQL datasource definition.
	 * 
	 * @return datasource.
	 */
	@Bean
	@ConfigurationProperties("spring.dhime.datasource")
	public DataSource oracleDhimeDataSource() {
		return DataSourceBuilder
					.create()
					.build();
	}
	
	

	/**
	 * Entity manager definition. 
	 *  
	 * @param builder an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */

	@Bean(name = "oracleDhimeEntityManager")
	public LocalContainerEntityManagerFactoryBean oracleDhimeEntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder
					.dataSource(oracleDhimeDataSource())
					.properties(hibernateProperties())
					.packages(SeriesTiempoValores.class)					
					.persistenceUnit("oraclesqlPUDhime")
					.build();
	}

	
	@Bean(name = "oracleDhimeTransactionManager")
	public PlatformTransactionManager oracleDhimeTransactionManager(@Qualifier("oracleDhimeEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource("hibernateDhime.properties");
		
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
											.collect(Collectors.toMap(
														e -> e.getKey().toString(),
														e -> e.getValue())
													);
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}
}
