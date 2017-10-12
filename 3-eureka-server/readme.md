
# 服务注册中心

Spring cloud的服务注册及发现，不仅仅只有eureka,还支持Zookeeper和Consul。默认情况下是eureka，spring 封装了eureka，使其非常简单易用，只需要比传统应用增加一个注解。

建立eureka-server工程

依赖：

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka-server</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>

	<!-- 分布式配置 -->
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
启动类：

	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
	
	@SpringBootApplication
	@EnableEurekaServer
	public class EurekaServerApplication {  
	    public static void main(String[] args) {        
	        SpringApplication.run(EurekaServerApplication.class, args);     
	    }       
	}
只需要使用@EnableEurekaServer注解就可以让应用变为Eureka服务器，这是因为spring boot封装了Eureka Server，让你可以嵌入到应用中直接使用。至于真正的EurekaServer是Netflix公司的开源项目，也是可以单独下载使用的
 

eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳，在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。eureka server，在application.properties配置文件中使用如下配置：

	server: 
	  port: 8761
	eureka:
	  instance:
	    hostname: localhost
	  client:
	    registerWithEureka: false
	    fetchRegistry: false
	    serviceUrl:
	      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
	      
其中server.port配置eureka服务器端口号。Eureka的配置属性都在开源项目spring-cloud-netflix-master中定义，在这个项目中有两个类

	org/springframework/cloud/netflix/eureka/EurekaInstanceConfigBean
	org/springframework/cloud/netflix/eureka/EurekaClientConfigBean

分别含有eureka.instance和eureka.client相关属性的解释和定义。从中可以看到，registerWithEureka表示是否注册自身到eureka服务器，因为当前这个应用就是eureka服务器，没必要注册自身，所以这里是false。fetchRegistry表示是否从eureka服务器获取注册信息，同上，这里不需要(通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.)。defaultZone就比较重要了，是设置eureka服务器所在的地址，查询服务和注册服务都需要依赖这个地址。

eureka server 是有界面的，启动工程,打开浏览器访问：http://localhost:8761

当前服务注册（Instances currently registered with Eureka）中，没有注册服务时，显示：
	
	No application available 没有服务被发现
	

## 高可用的服务注册中心

当成千上万个服务向注册中心注册的时候，注册中心的负载是非常高的，这在生产环境上是不太合适的，需要将Eureka Server集群化具有高可用性。

Eureka通过运行多个实例，使其更具有高可用性，默认支持集群，你需要做的就是给对等的实例一个合法的关联serviceurl

### 改造enureka-server

创建配置文件application-peer1.yml:

	server:
	  port: 8761
	spring:
	  profiles: peer1
	eureka:
	  instance:
	    hostname: peer1
	  client:
	    serviceUrl:
	      defaultZone: http://peer2:8769/eureka/

创建配置文件application-peer2.yml：
	
	server:
	  port: 8769	
	spring:
	  profiles: peer2
	eureka:
	  instance:
	    hostname: peer2
	  client:
	    serviceUrl:
	      defaultZone: http://peer1:8761/eureka/
修改host映射

linux：

	vim /etc/hosts 
	127.0.0.1 peer1
	127.0.0.1 peer2

windows修改c:/windows/systems/drivers/etc/hosts


### 改造simple-service
	
	eureka.client.serviceUrl.defaultZone=http://peer1:8761/eureka/
	
启动两个eureka-server实例，分别传入参数：

	--spring.profiles.active=peer1 
	--spring.profiles.active=peer2
启动simple-service,发现两个注册中心都有simple-service的注册信息

	eureka.instance.preferIpAddress=true
是通过设置ip让eureka让其他服务注册它。

当有服务注册时，两个Eureka-eserver是对等的，它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服务器宕机了，服务并不会终止，因为另一台服务存有相同的数据。


# FAQ

服务已经关但是Eureka server里显示服务还是up ，出现一行红色大字：

	EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.
原因：自我保护机制。Eureka Server在运行期间，会统计心跳失败的比例在15分钟之内是否低于85%，如果出现低于的情况（在单机调试的时候很容易满足，实际在生产环境上通常是由于网络不稳定导致），Eureka Server会将当前的实例注册信息保护起来，同时提示这个警告。

解决方法：

关闭自我保护

	eureka.server.enable-self-preservation=false
or: 
	
	eureka: 
		server: 
			enableSelfPreservation: false  