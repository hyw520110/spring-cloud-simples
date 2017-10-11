# 路由网关(zuul)

微服务架构

![](http://upload-images.jianshu.io/upload_images/2279594-6b7c148110ebc56e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/600)

注意：A服务和B服务是可以相互调用的，作图的时候忘记了。并且配置服务也是注册到服务注册中心的。

在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），然后再到具体的服务。服务统一注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理，配置服务的配置文件放在git仓库，方便开发人员随时改配置。

## Zuul简介

Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如/api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。

zuul有以下功能：

- Authentication
- Insights
- Stress Testing
- Canary Testing
- Dynamic Routing
- Service Migration
- Load Shedding
- Security
- Static Response handling
- Active/Active traffic management

创建zuul-service工程，增加依赖

	<dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-zuul</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

类加上注解@EnableZuulProxy，开启zuul的功能：

	@EnableZuulProxy
	@EnableEurekaClient
	@SpringBootApplication
	public class ZuulServiceApplication {	
	    public static void main(String[] args) {
	        SpringApplication.run(ZuulServiceApplication.class, args);
	    }
	}
配置：

	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	server:
	  port: 8769
	spring:
	  application:
	    name: zuul-service
	zuul:
	  routes:
	    api-a:
	      path: /api-a/**
	      serviceId: ribbon-service
	    api-b:
	      path: /api-b/**
	      serviceId: feign-service

依次运行enureka-server、config-server、simple-service、ribbon-service、feign-service、zuul-service

打开浏览器访问：

http://localhost:8769/api-a/say?name=jack

http://localhost:8769/api-b/say?name=jack

## 服务过滤

zuul不仅只是路由，并且还能过滤，做一些安全验证
	
	@Component
	public class MyFilter extends ZuulFilter{	
	    private static Logger logger = LoggerFactory.getLogger(MyFilter.class);
	    @Override
	    public String filterType() {
	        return "pre";
	    }
	
	    @Override
	    public int filterOrder() {
	        return 0;
	    }
	
	    @Override
	    public boolean shouldFilter() {
	        return true;
	    }
	
	    @Override
	    public Object run() {
	        RequestContext ctx = RequestContext.getCurrentContext();
	        HttpServletRequest request = ctx.getRequest();
	        logger.info("{} >>> {}", request.getMethod(), request.getRequestURL());
	        Object accessToken = request.getParameter("token");
	        if(accessToken == null) {
	            logger.warn("token is empty");
	            ctx.setSendZuulResponse(false);
	            ctx.setResponseStatusCode(401);
	            try {
	                ctx.getResponse().getWriter().write("token is empty");
	            }catch (Exception e){}
	
	            return null;
	        }
	        logger.info("ok");
	        return null;
	    }
	}

- filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下： 

	- pre：路由之前
	- routing：路由之时
	- post： 路由之后
	- error：发送错误调用
- filterOrder：过滤的顺序
- shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
- run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。

启动服务访问：

http://localhost:8769/api-a/say?name=jack 

http://localhost:8769/api-a/say?name=jack&token=22