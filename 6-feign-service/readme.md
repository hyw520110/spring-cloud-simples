# 简介

Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。

Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。

简而言之：

- Feign 采用的是基于接口的注解
- Feign 整合了ribbon

## 准备工作：

启动eureka-server，端口为8761; 启动两个simple-service实例，端口分别为8762 、8763.

创建fegin-service工程，增加依赖：

	<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-feign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
配置

	server:
	  port: 8765
	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	spring:
	  application:
	    name: feign-service

启动类：

	@SpringBootApplication
	@EnableDiscoveryClient
	@EnableFeignClients
	public class FeignServiceApplication {
	    public static void main(String[] args) {
	        SpringApplication.run(FeignServiceApplication.class, args);
	    }
	}
加上@EnableFeignClients注解开启Feign的功能,value指定调用哪个服务

	@FeignClient(value = "simple-service")
	public interface SchedualService {
	    @RequestMapping(value = "/index",method = RequestMethod.GET)
	    String say(@RequestParam(value = "name") String name);
	}
	
暴露http接口服务
	
	@RestController
	public class SchedualController {
	    @Autowired
	    SchedualService schedualService;
	    @RequestMapping(value = "/say",method = RequestMethod.GET)
	    public String sayHi(@RequestParam String name){
	        return schedualService.say(name);
	    }
	}

启动程序，多次访问http://localhost:8765/say?name=jack,浏览器交替显示端口号 