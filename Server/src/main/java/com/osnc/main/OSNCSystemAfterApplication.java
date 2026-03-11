package com.osnc.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@ServletComponentScan
@MapperScan("com.osnc.main.mapper")
public class OSNCSystemAfterApplication {

	public static void main(String[] args) {
		SpringApplication.run(OSNCSystemAfterApplication.class, args);
	}

}
