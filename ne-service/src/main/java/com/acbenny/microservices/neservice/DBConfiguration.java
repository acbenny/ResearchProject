package com.acbenny.microservices.neservice;

import java.net.URI;
import java.util.Optional;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfiguration {

    Logger logger = LoggerFactory.getLogger(DBConfiguration.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${db.name}")
    private String databaseName;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;
 
    private Optional<URI> serviceUrl() {
        return discoveryClient.getInstances("orientdb-svc-microservices")
          .stream()
          .findFirst() 
          .map(si -> si.getUri());
    }
    
    OrientDB orient;
    
    @Bean
    public ODatabaseSession orientDBSessionFactory(){
        String path = serviceUrl().map(x -> x.getHost()).orElseThrow();
        logger.info("Path:"+path);
        orient = new OrientDB("remote:" + path, OrientDBConfig.defaultConfig());
        return orient.open(databaseName, user, password);
    }

    public void close(){
        logger.debug("close function:"+orient.isOpen());
        if (orient.isOpen()){
            orient.close();
        }
    }
}