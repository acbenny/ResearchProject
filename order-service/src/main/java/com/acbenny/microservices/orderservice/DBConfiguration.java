package com.acbenny.microservices.orderservice;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.OrientDBConfigBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfiguration {
    Logger log = LoggerFactory.getLogger(DBConfiguration.class);

    @Value("${db.name:${spring.application.name}}")
    private String databaseName;

    @Value("${db.user:admin}")
    private String user;

    @Value("${db.password:admin}")
    private String password;
    
    OrientDB orient;
    
    @Bean
    public ODatabasePool orientDBPool() {
        orient = new OrientDB("remote:orientdb-svc.microservices", OrientDBConfig.defaultConfig());
        OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
        poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
        poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 200);
        poolCfg.addConfig(OGlobalConfiguration.CLIENT_CONNECTION_STRATEGY, "ROUND_ROBIN_CONNECT");
        return new ODatabasePool(orient,databaseName,user,password,poolCfg.build());
    }

    public void close(){
        if (orient.isOpen()){
            orient.close();
        }
    }
}