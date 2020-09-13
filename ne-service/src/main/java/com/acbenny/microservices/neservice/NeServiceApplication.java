package com.acbenny.microservices.neservice;

import com.orientechnologies.orient.core.db.ODatabaseSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NeServiceApplication {	
	public static void main(String[] args) {
		SpringApplication.run(NeServiceApplication.class, args);
	}

}
