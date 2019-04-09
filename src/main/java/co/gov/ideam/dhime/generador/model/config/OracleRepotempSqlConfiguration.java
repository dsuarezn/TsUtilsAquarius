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

import co.gov.ideam.dhime.generador.model.repotemp.CombinadorGenRuth;
import co.gov.ideam.dhime.generador.model.repotemp.EtiquetasTs;
import co.gov.ideam.dhime.generador.model.repotemp.Logmigcompara;
import co.gov.ideam.dhime.generador.model.repotemp.LogmigcomparaDetalle;
import co.gov.ideam.dhime.generador.model.repotemp.TablasAsociadas;
import co.gov.ideam.dhime.generador.model.repotemp.Westaciones;



/**
 * Spring configuration of the "OracleSQL" database.
 * 
 * @author Deivid Suarez.
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "oracleRepoTempEntityManager", 
		transactionManagerRef = "oracleRepoTempTransactionManager", 
		basePackages = "co.gov.ideam.dhime.localdata.repository.repotemp"
)
public class OracleRepotempSqlConfiguration {

	/**
	 * PostgreSQL datasource definition.
	 * 
	 * @return datasource.
	 */
	@Bean
	@Primary
	@ConfigurationProperties("spring.repotemp.datasource")
	public DataSource oracleRepoTempDataSource() {
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
	@Primary
	@Bean(name = "oracleRepoTempEntityManager")
	public LocalContainerEntityManagerFactoryBean oracleRepoTempEntityManager(EntityManagerFactoryBuilder builder) {
		return builder
					.dataSource(oracleRepoTempDataSource())
					.properties(hibernateProperties())
					.packages(CombinadorGenRuth.class, LogmigcomparaDetalle.class, Logmigcompara.class, TablasAsociadas.class, Westaciones.class, EtiquetasTs.class)					
					.persistenceUnit("oraclesqlPURepotemp")
					.build();
	}

	@Primary
	@Bean(name = "oracleRepoTempTransactionManager")
	public PlatformTransactionManager oracleRepoTempTransactionManager(@Qualifier("oracleRepoTempEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource("hibernateRepoTemp.properties");
		
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
