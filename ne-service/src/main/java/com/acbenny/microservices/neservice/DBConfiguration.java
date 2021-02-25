package com.acbenny.microservices.neservice;

import java.net.URI;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class DBConfiguration {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${db.name:${spring.application.name}}")
    private String databaseName;

    @Value("${db.user:admin}")
    private String user;

    @Value("${db.password:admin}")
    private String password;

    private URI serviceUrl() {
        return discoveryClient.getInstances("orientdb-svc-microservices")
          .stream()
          .findFirst() 
          .map(si -> si.getUri())
          .orElse(URI.create("http://devbox"));
    }
    
    OrientDB orient;
    
    @Bean
    @RequestScope
    public ODatabaseSession orientDBSessionFactory() {
        String path = serviceUrl().getHost();
        orient = new OrientDB("remote:" + path, OrientDBConfig.defaultConfig());
        return orient.open(databaseName, user, password);
    }

    public void close(){
        if (orient.isOpen()){
            orient.close();
        }
    }
}