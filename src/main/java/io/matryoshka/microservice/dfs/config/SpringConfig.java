package io.matryoshka.microservice.dfs.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
@PropertySource(value={"classpath:application.properties"})
@ComponentScan(basePackages="io.matryoshka.microservice.dfs")
public class SpringConfig {

	@Value("${jdbc.url}")
	private String jdbcUrl;
	@Value("${jdbc.driverClassName}")
	private String jdbcDriverClassName;
	@Value("${jdbc.username}")
	private String jdbcUsername;
	@Value("${jdbc.password}")
	private String jdbcPassword;
	
	@Bean(destroyMethod="close")
	public DataSource dataSource(){
		BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
		boneCPDataSource.setDriverClass(jdbcDriverClassName);//数据库驱动
		boneCPDataSource.setJdbcUrl(jdbcUrl);//相应驱动的jdbcUrl
		boneCPDataSource.setUsername(jdbcUsername);// 数据库的用户名
		boneCPDataSource.setPassword(jdbcPassword);// 数据库的密码
		// 检查数据库连接池中空闲连接的连接的间隔时间,单位是分,默认值:240 如果要取消设置为0
		boneCPDataSource.setIdleConnectionTestPeriodInMinutes(60);
		// 连接池中未使用的连接最大存活时间,单位是分 默认值:60 如果要永久存活则设置为0 
		boneCPDataSource.setIdleMaxAgeInMinutes(30);		
		boneCPDataSource.setMaxConnectionsPerPartition(100);// 每个分区最大的连接数
		boneCPDataSource.setMinConnectionsPerPartition(5); // 每个分区最小的连接数
		return boneCPDataSource;
	}
}
