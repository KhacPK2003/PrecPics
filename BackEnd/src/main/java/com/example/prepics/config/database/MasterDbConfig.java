package com.example.prepics.config.database;

import javax.sql.DataSource;

import com.example.prepics.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.prepics.entity",
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager"
)
@EnableConfigurationProperties(JpaProperties.class)
public class MasterDbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.master.configuration")
    DataSource masterDataSource() {
        HikariDataSource dataSource = masterDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        dataSource.setMaximumPoolSize(20);
        dataSource.setConnectionTimeout(30000); // Set timeout to 300000ms or 5 minutes
        dataSource.setIdleTimeout(120000);
        return dataSource;
    }

    @Bean(name = "masterEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean masterEntityManagerfactory(EntityManagerFactoryBuilder builder, JpaProperties jpaProperties) {
        return builder.dataSource(masterDataSource())
                .packages(User.class)
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Bean(name = "masterTransactionManager")
    PlatformTransactionManager masterTransactionManager(
            final @Qualifier("masterEntityManagerFactory") LocalContainerEntityManagerFactoryBean masterEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(masterEntityManagerFactory.getObject()));
    }

}