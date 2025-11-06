package com.yao.ndn.convert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ConvertApplication {
	private static final Logger log = LoggerFactory.getLogger(ConvertApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ConvertApplication.class, args);
		if (args.length < 1) {
			log.error("Usage: java -jar demo.jar <client|server>");
			System.exit(1);
		}
		if (args[0].equals("client")) {
			NDNClient.main(args);
		} else if (args[0].equals("server")) {
			NDNServer.main(args);
		}
	}

}
