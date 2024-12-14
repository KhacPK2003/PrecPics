package com.example.prepics.config.database;

import com.example.prepics.entity.User;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.prepics.entity",
    entityManagerFactoryRef = "slaveEntityManagerFactory",
    transactionManagerRef = "slaveTransactionManager"
)
public class SlaveDbConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.slave")
  public DataSourceProperties slaveDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.slave.configuration")
  public DataSource slaveDataSource() {
    HikariDataSource dataSource = slaveDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class).build();
    dataSource.setMaximumPoolSize(30);
    dataSource.setConnectionTimeout(10000);
    dataSource.setReadOnly(true); // Slave chỉ đọc
    return dataSource;
  }

  @Bean
  public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(false);
    return new EntityManagerFactoryBuilder(vendorAdapter, new HashMap<>(), null);
  }

  @Bean(name = "slaveEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean clientEntityManagerFactory(
      EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(slaveDataSource())
        .packages(User.class)
        .build();
  }

  @Bean(name = "slaveTransactionManager")
  public PlatformTransactionManager slaveTransactionManager(
      final @Qualifier("slaveEntityManagerFactory") LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory) {
    return new JpaTransactionManager(Objects.requireNonNull(slaveEntityManagerFactory.getObject()));
  }
}