# 服务消费者(ribbon)

在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。

Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign

## ribbon简介

ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。

ribbon已经默认实现了这些配置bean：

- IClientConfig ribbonClientConfig: DefaultClientConfigImpl
- IRule ribbonRule: ZoneAvoidanceRule
- IPing ribbonPing: NoOpPing
- ServerList ribbonServerList: ConfigurationBasedServerList
- ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter
- ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

### 准备工作：

前面已经启动 eureka-server、simple-service服务实例，修改端口（8763）再次启动simple-service服务实例，查看eureka-server注册了2个simple-service实例

主要是为了演示ribbon负载均衡。默认情况下使用ribbon不需要再作任何配置，不过它依赖于注册服务器。当然也可以对ribbon进行一些自定义设置，比如配置它的超时时间、重试次数等。启用了负载均衡后，当我们关掉前端页面上次指向的服务时（从日志中看），比如我们刷新页面看到它调用的服务，那么我们关掉这个服务。关掉后再刷新会发现执行了断路器，过几秒再刷新，它已经切换了另一个服务，这说明负载均衡起作用了。

### 创建服务消费者

加入依赖:

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

配置文件指定服务的注册中心地址为http://localhost:8761/eureka/，程序名称为 service-ribbon，程序端口为8764。配置文件application.yml如下：

	server:
	  port: 8764
	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	spring:
	  application:
	    name: ribbon-service

启动类：

	@SpringBootApplication
	@EnableDiscoveryClient
	public class RibbonServiceApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(RibbonServiceApplication.class, args);
	    }
	    @Bean
	    @LoadBalanced
	    RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
	}
@EnableDiscoveryClient向服务中心注册；并且向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。

服务调用测试类:

	@Service
	public class HelloService {	
	    @Autowired
	    RestTemplate restTemplate;	
	    public String say(String name) {
	        return restTemplate.getForObject("http://simple-service/index?name="+name,String.class);
	    }	
	}
注入ioc容器的restTemplate来消费simple-service服务的接口，url地址中用的程序名替代了具体的域名/IP，ribbon会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名

在controller中用调用HelloService

	@RestController
	public class HelloControler {	
	    @Autowired
	    HelloService helloService;
	    @RequestMapping(value = "/say")
	    public String say(@RequestParam String name){
	        return helloService.say(name);
	    }	
	}

在浏览器上多次访问http://localhost:8764/say?name=jack，浏览器交替显示不同端口号,这说明当我们通过调用restTemplate方法时，已经做了负载均衡，访问了不同的端口的服务实例。