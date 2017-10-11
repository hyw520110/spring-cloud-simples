# 断路器（Hystrix）

在微服务架构中，根据业务来拆分成一个个的服务，服务与服务之间可以相互调用（RPC），在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。为了保证其高可用，单个服务通常会集群部署。由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。

为了解决这个问题，业界提出了断路器模型。

Netflix开源了Hystrix组件，实现了断路器模式，SpringCloud对这一组件进行了整合。 在微服务架构中，一个请求需要调用多个服务是非常常见的，较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开，断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。

## 准备工作

启动eureka-server工程；启动simple-service工程，它的端口为8762

## 在ribbon使用断路器

改造ribbon-serice工程，加入spring-cloud-starter-hystrix的依赖：

	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-hystrix</artifactId>
	</dependency>
启动类上加@EnableHystrix注解开启Hystrix

在HelloService的say方法上@HystrixCommand(fallbackMethod = "fallback")该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法，熔断方法直接返回了一个字符串
	
	@Service
	public class HelloService { 
	    @Autowired
	    RestTemplate restTemplate;
	    
	    @HystrixCommand(fallbackMethod="fallback")
	    public String say(String name) {
	        return restTemplate.getForObject("http://simple-service/index?name="+name,String.class);
	    }   
	    private String fallback(String name) {
	        return "hi,"+name+",sorry,error!";
	    }
	}

断路器的基本作用就是@HystrixCommand注解的方法失败后，系统将自动切换到fallbackMethod方法执行。断路器Hystrix具备回退机制、请求缓存和请求打包以及监控和配置等功能，这里只使用了它的核心功能：回退机制，使用该功能允许你快速失败并迅速恢复或者回退并优雅降级。

这里使用restTemplate进行服务调用，因为使用了服务注册和发现，所以我们只需要传入服务名称SERVICE_NAME作为url的根路径，如此restTemplate就会去EurekaServer查找服务名称所代表的服务并调用。而这个服务名称就是在服务提供端simple-service中spring.application.name所配置的名字，这个名字在服务启动时连同它的IP和端口号都注册到了EurekaServer。

启动工程，访问：http://localhost:8764/say?name=jack，正常显示结果，关闭simple-service服务,刷新页面，查看结果.

## Feign中使用断路器

Feign是自带断路器的，在D版本的Spring Cloud中，它没有默认打开。

需要在配置文件中配置打开它，在配置文件加以下代码：

	feign.hystrix.enabled=true
然后在FeignClient注解中指定fallback类即可


	@FeignClient(value = "simple-service",fallback=SchedualServiceImpl.class)
	public interface SchedualService {
	    @RequestMapping(value = "/index",method = RequestMethod.GET)
	    String say(@RequestParam(value = "name") String name);
	}

	@Component
	public class SchedualServiceImpl implements SchedualService {
	    @Override
	    public String say(String name) {
	        return "sorry "+name;
	    }
	}

启动服务，关闭simple-service，访问：http://localhost:8765/say?name=jack

## 断路器Hystrix仪表盘（Hystrix Dashboard）

Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面

在ribbon-service或feign-service中增加依赖：

	<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
	<dependency>
        <groupId>org.springframework.cloud</groupId>
    		<artifactId>spring-cloud-starter-hystrix</artifactId>
	</dependency>
	<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
	</dependency>

启动类中加上@EnableHystrix注解开启断路器,并且需要在程序中声明断路点HystrixCommand.加入@EnableHystrixDashboard注解，开启hystrixDashboard

启动服务，访问http://localhost:8765/hystrix

在输入框中输入http://localhost:8765/hystrix.stream，
titile输入标题，如hello,点击monitor stream，进入下一个界面

## 断路器聚合监控(Hystrix Turbine)

看单个的Hystrix Dashboard的数据并没有什么多大的价值，要想看这个系统的Hystrix Dashboard数据就需要用到Hystrix Turbine。Hystrix Turbine将每个服务Hystrix Dashboard数据进行了整合。Hystrix Turbine的使用非常简单，只需要引入相应的依赖和加上注解和配置就可以了。

### 创建turbine-service

	<dependencies>
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-starter-turbine</artifactId>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.cloud</groupId>
	        <artifactId>spring-cloud-netflix-turbine</artifactId>
	    </dependency>
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-actuator</artifactId>
	    </dependency>	
	    <dependency>
	        <groupId>org.springframework.boot</groupId>
	        <artifactId>spring-boot-starter-test</artifactId>
	        <scope>test</scope>
	    </dependency>
	</dependencies>

启动类：

	@SpringBootApplication
	@EnableTurbine
	public class TurbineServiceApplication {
	    public static void main(String[] args) {	
	            new SpringApplicationBuilder(TurbineServiceApplication.class).web(true).run(args);
	    }
	}
@EnableTurbine注解包含了@EnableDiscoveryClient注解，即开启了注册服务

配置文件：
	
	spring:
	  application.name: turbine-service
	server:
	  port: 8769
	#security.basic.enabled: false
	turbine: 
	  aggregator:
	    # 指定聚合哪些集群，多个使用","分割，默认为default。可使用http://.../turbine.stream?cluster={clusterConfig之一}访问 
	    clusterConfig: default   
	    # 配置Eureka中的serviceId列表，表明监控哪些服务  
	  appConfig: simple-service,lucy-service
	  clusterNameExpression: new String("default")
	    # 1. clusterNameExpression指定集群名称，默认表达式appName；此时：turbine.aggregator.clusterConfig需要配置想要监控的应用名称
	    # 2. 当clusterNameExpression: default时，turbine.aggregator.clusterConfig可以不写，因为默认就是default
	    # 3. 当clusterNameExpression: metadata['cluster']时，假设想要监控的应用配置了eureka.instance.metadata-map.cluster: ABC，则需要配置，同时turbine.aggregator.clusterConfig: ABC
	eureka:
	    instance:
	      #心跳间隔
	      leaseRenewalIntervalInSeconds: 10
	    client:
	      #注册本工程为服务
	      registerWithEureka: true 
	      fetchRegistry: true
	      serviceUrl:
	        defaultZone: http://localhost:8761/eureka/

创建lucy-service工程：
	
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
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
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>	
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-hystrix</artifactId>
	</dependency>
启动类：
	
	import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
	import org.springframework.cloud.netflix.hystrix.EnableHystrix;
	import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	
	@SpringBootApplication
	@EnableEurekaClient
	@RestController
	@EnableHystrix
	@EnableHystrixDashboard
	public class LucyServiceApplication {
	
		public static void main(String[] args) {
			SpringApplication.run(LucyServiceApplication.class, args);
		}
	
		@Value("${server.port}")
		String port;
		@RequestMapping("/hi")
		@HystrixCommand(fallbackMethod = "hiError")
		public String home(@RequestParam String name) {
			return "hi "+name+",i  am lucy and from port:" +port;
		}
	
		public String hiError(String name) {
			return "hi,"+name+",sorry,error!";
		}
	}
配置文件bootstrap.yml:
	
	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	server:
	  port: 8763
	spring:
	  application:
	    name: service-lucy
依次开启eureka-server、simple-service、lucy-service、turbine-service工程。

打开浏览器访问：http://localhost:8769/turbine.stream

依次请求：

	http://localhost:8762/index?name=jack
	
	http://localhost:8763/hi?name=jack

打开:http://localhost:8763/hystrix,输入监控流http://localhost:8769/turbine.stream

点击monitor stream 进入页面,可以看到这个页面聚合了2个service的hystrix dashbord数据。