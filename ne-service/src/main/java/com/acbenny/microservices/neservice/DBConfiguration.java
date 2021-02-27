package com.acbenny.microservices.neservice;

import java.net.URI;

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
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class DBConfiguration {
    Logger log = LoggerFactory.getLogger(DBConfiguration.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${db.name:${spring.application.name}}")
    private String databaseName;

    @Value("${db.user:admin}")
    private String user;

    @Value("${db.password:admin}")
    private String password;

    @Value("${HOSTNAME}")
    private String hostname;

    private URI serviceUrl() {
        log.info("Hostname:"+hostname);
        int num = Integer.parseInt(hostname.replaceAll(".*[^[0-9]+]", ""));
        String svcName = "orientdb-svc-microservices";
        if (num%2!=0)
            svcName = "orientdb-svc-secondary-microservices";
        return discoveryClient.getInstances(svcName)
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
        log.info("Path:"+path);
        orient = new OrientDB("remote:" + path, OrientDBConfig.defaultConfig());
        return orient.open(databaseName, user, password);
    }

    public void close(){
        if (orient.isOpen()){
            orient.close();
        }
    }
}