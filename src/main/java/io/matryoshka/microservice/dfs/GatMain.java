package io.matryoshka.microservice.dfs;

import org.apache.servicecomb.springboot.starter.provider.EnableServiceComb;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.matryoshka.microservice.dfs.config.SpringConfig;

/**
 * 该类为文档和目录管理的启动类
 */
@SpringBootApplication
@EnableServiceComb
@MapperScan("io.matryoshka.microservice.dfs.mapper")
public class GatMain {
	public static void main(String[] args) {	
		SpringApplication.run(SpringConfig.class, args);
	}
}
