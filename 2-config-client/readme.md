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



