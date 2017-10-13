# 创建服务提供者
服务向注册中心注册时，它会提供一些元数据，例如主机和端口，URL，主页等。
注册中心从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。

## 使用eureka注册中心

增加依赖：

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
	</dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
让服务使用eureka服务器，只需添加通过注解@EnableEurekaClient 表明自己是一个eurekaclient

	@SpringBootApplication
	@EnableEurekaClient
	@RestController
	public class Application {  
	    public static void main(String[] args) {
	        SpringApplication.run(Application.class, args);
	    }
	    @Value("${server.port}")
	    String port;
	    @RequestMapping("/index")
	    public String home(@RequestParam String name) {
	        return "hi "+name+",i am from port:" +port;
	    }
	}
其中：

- @SpringBootApplication相当于@Configuration、@EnableAutoConfiguration、@ComponentScan三个注解合用。
- @EnableEurekaClient配置本应用将使用服务注册和服务发现，注意：注册和发现用这一个注解。

然后在配置文件中添加服务注册中心的地址：
	
	server:
	  port: 8762
	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	#      defaultZone=http://peer1:8761/eureka/
	spring:
	  application:
	    name: simple-service


其中defaultZone是指定eureka服务器的地址，无论是注册还是发现服务都需要这个地址。application.name是指定进行服务注册时该服务的名称。这个名称就是后面调用服务时的服务标识符,服务与服务之间相互调用一般都是根据这个name.

这样该服务启动后会自动注册到eureka服务器。如果在该服务中还需要调用别的服务，那么直接使用那个服务的服务名称加方法名构成的url即可

启动工程，打开eureka server的网址：http://localhost:8761 

可以发现一个服务（simple-service）已经注册到注册中心服务中

## 使用zookeeper注册中心

加入依赖：

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
	</dependency>
启动类:

	@SpringBootApplication
	@EnableDiscoveryClient  
	@RestController
	public class ZkClientApplication {
	    @Autowired  
	    private LoadBalancerClient loadBalancer;  
	    @Autowired  
	    private DiscoveryClient discovery; 
	    
	    public static void main(String[] args) {
	        SpringApplication.run(ZkClientApplication.class, args);
	    }
	      
	    @RequestMapping("/discovery")  
	    public Object discovery() {  
	        return loadBalancer.choose("zookeeper-server");  
	    }  
	      
	    @RequestMapping("/all")  
	    public Object all() {  
	        return discovery.getServices();  
	    }  
	}


bootstrap.yml
	
	server: 
	  port: 8762  
	spring: 
	  application: 
	    name: simple-zk-service  
	  cloud: 
	    zookeeper: 
	      connectString: localhost:2181  
	      discovery: 
	        register: false 
	
注释eureka依赖、启动类、配置文件,启动服务,访问：http://localhost:8763/discovery