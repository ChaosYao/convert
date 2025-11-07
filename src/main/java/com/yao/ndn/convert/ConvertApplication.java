package com.yao.ndn.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class ConvertApplication {

	@Value("${app.ndn.mode}")
	private String mode;

	@Autowired
	private NDNClient ndnClient;

	@Autowired
	private NDNServer ndnServer;
	public static void main(String[] args) {
		SpringApplication.run(ConvertApplication.class, args);
	}

	@PostConstruct
	private void init() {
		if ("server".equals(mode)) {
			ndnServer.init();
		} else if ("client".equals(mode)) {
			ndnClient.init();
		}
	}

}
