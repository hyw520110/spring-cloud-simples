# 分布式配置应用

## 创建服务使用远程配置
创建工程config-client增加依赖：

	<dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
bootstrap.properties配置固定(不更改)配置

	server.port=8881
	spring.application.name=config-client
	spring.cloud.config.name=config
	spring.cloud.config.profile=${config.profile:dev}
	spring.cloud.config.label=master
	spring.cloud.config.uri= http://localhost:${config.port:8888}

其中

- config.uri指定远程加载配置信息的地址，就是前面建立的配置管理服务器的地址，绑定端口8888，其中config.port:8888，表示如果在命令行提供了config.port参数，我们就用这个端口，否则就用8888端口。
- config.name表示配置文件名称，配置文件名称为:config-dev.properties: {application}- {profile}.properties所以我们配置config.name为config，config.profile为dev

启动类：

	@SpringBootApplication
	public class ConfigClientApplication {  
	    public static void main(String[] args) {        
	        SpringApplication.run(ConfigClientApplication.class, args);     
	    }    
	}
写一个API接口从配置中心读取变量的值

	@RestController
	//@RefreshScope
	public class ConfigReadController {
	    @Value("${logging.level.org.springframework.web}")
	    private String loglevel;
	    @Value("${mysqldb.datasource.password}")
	    private String pwd;
	    
	    @RequestMapping(value = "/config")
	    public String loglevel(){
	        return loglevel;
	    }
	    
	    @RequestMapping(value = "/pwd")
	    public String pwd(){
	        return pwd;
	    }
	}
打开网址访问：http://localhost:8881/config,网页显示从config-server获取的属性，而config-server是从git仓库读取的


## 使用集群配置服务

以上是都从单一的配置中心读取文件，当服务实例很多时，单一的配置中心服务不可靠(负载很大),生产环境一般是从集群配置中心读取数据


将其注册微到服务注册中心，作为Eureka客户端，需要pom文件加上起步依赖spring-cloud-starter-eureka

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
配置文件bootstrap.properties，注意是bootstrap
	
	server.port=8881
	spring.application.name=config-client
	spring.cloud.config.name=config
	spring.cloud.config.profile=${config.profile:dev}
	spring.cloud.config.label=master
	#spring.cloud.config.uri= http://localhost:${config.port:8888}
	#从集群配置中心读取配置
	eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
	spring.cloud.config.discovery.enabled=true
	spring.cloud.config.discovery.serviceId=config-server
	
其中：

- spring.cloud.config.discovery.enabled 是从配置中心读取文件。
- spring.cloud.config.discovery.serviceId 配置中心的servieId，即服务名。

在读取配置文件不再写ip地址，而是服务名，这时如果配置服务(config-server)部署多份，通过负载均衡，从而实现高可用。

依次启动eureka-server,config-server,config-client 访问网址：http://localhost:8881/config


## zookeeper中心化配置

zookeeper中心化配置不需要配置中心服务，即不需要服务端，只需要客户端,即在应用中直接获取配置

依赖：

	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-zookeeper-config</artifactId>
	</dependency>
	<!-- zookeeper 依赖的健康检查 -->
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
bootstrap.yml

	server: 
  		port: 8881 
	spring:
	  application:
	    name: app
	  #会去/config/application,dev和/config/app,dev(等同application-dev.yml)节点下读取配置  
	  profiles:
	      active: dev
	  cloud:
	    zookeeper:
	      # true:开启zookeeper外部化配置, false:读取本地配置; 需要将config.enabled,config.watcher.enabled同时设置
	      enabled: true
	      root: config
	      defaultContext: application
	      profileSeparator: ","  
	      connect-string: localhost:2181
	      config:
	        enabled: true
	        watcher:
	          enabled: true
这样默认情况下应用会去/config/application,dev 和 /config/app,dev节点下读取配置

注意：

- /config/application节点下的配置项会应用到所有应用
- /config/app节点配置只应用到对应的应用

zk配置数据脚本

	create /config '' 
	create /config/application,dev ''
	create /config/app ''
	create /config/application,dev/logging.level.org.springframework.web WARN
	create /config/app/mysqldb.datasource.password 123456
获取配置
	
	@Value("${logging.level.org.springframework.web}")
	private String logLevel;
	@Value("${mysqldb.datasource.password}")
	private String pwd;

获取实时更新配置(更改配置无需重启,自动获取最新配置),只需在类上加上注解@RefreshScope






