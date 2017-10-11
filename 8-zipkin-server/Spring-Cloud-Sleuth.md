

# 服务链路追踪(Spring-Cloud-Sleuth)

微服务架构是一个分布式架构，它按业务划分服务单元，一个分布式系统往往有很多个服务单元。由于服务单元数量众多，业务的复杂性，如果出现了错误和异常，很难去定位。主要体现在，一个请求可能需要调用很多个服务，而内部服务的调用复杂性，决定了问题难以定位。所以微服务架构中，必须实现分布式链路追踪，去跟进一个请求到底有哪些服务参与，参与的顺序又是怎样的，从而达到每个请求的步骤清晰可见，出了问题，很快定位。

目前，链路追踪组件有Google的Dapper，Twitter 的Zipkin，以及阿里的Eagleeye （鹰眼）等，它们都是非常优秀的链路追踪开源组件。

Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，你只需要在pom文件中引入相应的依赖即可。

微服务架构上通过业务来划分服务的，通过REST调用，对外暴露的一个接口，可能需要很多个服务协同才能完成这个接口功能，如果链路上任何一个服务出现问题或者网络超时，都会形成导致接口调用失败。随着业务的不断扩张，服务之间互相调用会越来越复杂。


Spring Cloud Sleuth采用的是Google的开源项目Dapper的专业术语

- Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址) 
span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
- Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
- Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束 
	- cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
	- sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
	- ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
	- cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间
 
	
将Span和Trace在一个系统中使用Zipkin注解的过程图形化：

![](http://upload-images.jianshu.io/upload_images/2279594-4b865f2a2c271def.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

## 实战

新建zipkin-server工程,它的主要作用使用ZipkinServer作为链路追踪服务中心，负责存储链路数据，并展示


增加依赖：

	<dependencies>
	  	<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>io.zipkin.java</groupId>
			<artifactId>zipkin-server</artifactId>
		</dependency>
		<dependency>
			<groupId>io.zipkin.java</groupId>
			<artifactId>zipkin-autoconfigure-ui</artifactId>
		</dependency>
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>Camden.SR6</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

配置文件设置端口

	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	server:
	  port: 9411
	spring:
	  application:
	    name: zipkin-server
启动类：

	
	import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	
	import zipkin.server.EnableZipkinServer;
	@SpringBootApplication
	@EnableZipkinServer
	@EnableEurekaClient
	public class ZipkinServerApplication {   
	    public static void main(String[] args) {
	        SpringApplication.run(ZipkinServerApplication.class, args);
	    }
	}

新建user-service为一个应用服务，对外暴露API接口，同时它也作为链路追踪客户端，负责产生数据。

增加依赖：

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>            
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Dalston.RC1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>



在配置文件applicatiom.yml

	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	server:
	  port: 8762
	spring:
	  application:
	    name: user-service
	  zipkin:
	    base-url: http://localhost:9411
	  sleuth:
	    sampler:
	      percentage: 1.0
spring.sleuth.sampler.percentage为1.0,即100%的概率将链路的数据上传给Zipkin Server，在默认的情况下，该值为0.1

	
	@RestController
	@RequestMapping("/user")
	public class UserController {	
	    @RequestMapping("/hi")
	    public String hi(){
	        return "I'm forezp";
	    }
	}
 
启动类：


	@SpringBootApplication
	@EnableEurekaClient
	public class AServiceApplication {  	
	    public static void main(String[] args) {
	        SpringApplication.run(AServiceApplication.class, args);
	    }	 
	}
构建gateway-service

gateway-service作为服务网关工程，负责请求的转发,同时它也作为链路追踪客户端，负责产生数据，并上传给zipkin-service；

增加依赖：

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.2.RELEASE</version>
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Dalston.RELEASE</spring-cloud.version>
	</properties>
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
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-zipkin</artifactId>	    
	</dependency>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
配置：
	
	eureka:
	  client:
	    serviceUrl:
	      defaultZone: http://localhost:8761/eureka/
	
	server:
	  port: 5000
	spring:
	  application:
	    name: gateway-service
	  sleuth:
	    sampler:
	      percentage: 1.0
	  zipkin:
	    base-url: http://localhost:9411
	
	zuul:
	  routes:
	    api-a:
	      path: /user-api/**
	      serviceId: user-service

启动类
	
	@SpringBootApplication
	@EnableZuulProxy
	@EnableEurekaClient
	public class GatewayServiceApplication {
	
	    public static void main(String[] args) {
	        SpringApplication.run(GatewayServiceApplication.class, args);
	    }
	}

依次启动eureka-server、zipkin-server、user-service、gateway-service。在浏览器上访问http://localhost:5000/user-api/user/hi

访问http://localhost:9411，即访问Zipkin的展示界面，这个界面主要用来查找服务的调用情况，可以根据服务名、开始时间、结束时间、请求消耗的时间等条件来查找

点击“Find Trackes”按钮，可知服务的调用情况，比如服务调用时间、服务的消耗时间，服务调用的链路情况。

点击Dependences按钮，可以查看服务的依赖关系

## 在链路数据中添加自定义数据

现在需要实现这样一个功能，需要在链路数据中加上操作人。这需要在gateway-service上实现。建一个ZuulFilter过滤器，它的类型为“post”，order为900，开启拦截。在拦截逻辑方法里，通过Tracer的addTag方法加上自定义的数据，比如本案例中加入了链路的操作人。另外也可以在这个过滤器中获取当前链路的traceId信息,traceId作为链路数据的唯一标识，可以存储在log日志中，方便后续查找。

	
	@Component
	public class LoggerFilter extends ZuulFilter {
	
	    @Autowired
	    Tracer tracer;
	    @Override
	    public String filterType() {
	        return FilterConstants.POST_TYPE;
	    }
	
	    @Override
	    public int filterOrder() {
	        return 900;
	    }
	
	    @Override
	    public boolean shouldFilter() {
	        return true;
	    }
	
	    @Override
	    public Object run() {
	
	        tracer.addTag("operator","forezp");
	        System.out.print(tracer.getCurrentSpan().traceIdString());
	        return null;
	    }
	}

## 使用spring-cloud-starter-stream-rabbit进行链路通讯


在上述的案例中，最终gateway-service收集的数据，是通过Http上传给zip-server的，在Spring Cloud Sleuth中支持消息组件来通讯的，在这一小节使用RabbitMQ来通讯。首先来改造zipkin-server，在pom文件将zipkin-server的依赖去掉，加上spring-cloud-sleuth-zipkin-stream和spring-cloud-starter-stream-rabbit

	
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-sleuth-zipkin-stream</artifactId>
	</dependency>
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
	</dependency>
在application.yml配置上RabbitMQ的配置，包括host、端口、用户名、密码，如下：
	
	spring:
	  rabbitmq:
	    host: localhost
	    port: 5672
	    username: guest
	    password: guest

在程序的启动类ZipkinServerApplication上@EnableZipkinStreamServer注解，开启ZipkinStreamServer。代码如下：

	@SpringBootApplication
	@EnableEurekaClient
	@EnableZipkinStreamServer
	public class ZipkinServerApplication {
	
	    public static void main(String[] args) {
	        SpringApplication.run(ZipkinServerApplication.class, args);
	    }
	}

现在来改造下Zipkin Client（包括gateway-service、user-service），在pom文件中将spring-cloud-starter-zipkin以来改为spring-cloud-sleuth-zipkin-stream和spring-cloud-starter-stream-rabbit，代码如下：

	
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-sleuth-zipkin-stream</artifactId>	
	</dependency>
	<dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
	</dependency>

同时在applicayion.yml文件加上RabbitMQ的配置，同zipkin-server工程。

这样，就将链路的上传数据从Http改了为用消息代组件RabbitMQ。

## 将链路数据存储在Mysql数据库

在上述的例子中，Zipkin Server是将数据存储在内存中，一旦程序重启，之前的链路数据全部丢失，那么怎么将链路数据存储起来呢？Zipkin支持Mysql、Elasticsearch、Cassandra存储。这一小节讲述用Mysql存储，下一节讲述用Elasticsearch存储。

首先，在zipkin-server工程加上Mysql的连接依赖mysql-connector-java，JDBC的起步依赖spring-boot-starter-jdbc，代码如下：
	
	<dependency>
	    <groupId>mysql</groupId>
	    <artifactId>mysql-connector-java</artifactId>
	</dependency>
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-jdbc</artifactId>
	</dependency>

在配置文件application.yml加上数据源的配置，包括数据库的Url、用户名、密码、连接驱动，另外需要配置zipkin.storage.type为mysql，代码如下：
	
	spring:
	  datasource:
	    url: jdbc:mysql://localhost:3306/spring-cloud-zipkin?useUnicode=true&characterEncoding=utf8&useSSL=false
	    username: root
	    password: 123456
	    driver-class-name: com.mysql.jdbc.Driver
	zipkin:
	  storage:
	    type: mysql

另外需要在Mysql数据库中初始化数据库脚本
	
	CREATE TABLE IF NOT EXISTS zipkin_spans (
	  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
	  `trace_id` BIGINT NOT NULL,
	  `id` BIGINT NOT NULL,
	  `name` VARCHAR(255) NOT NULL,
	  `parent_id` BIGINT,
	  `debug` BIT(1),
	  `start_ts` BIGINT COMMENT 'Span.timestamp(): epoch micros used for endTs query and to implement TTL',
	  `duration` BIGINT COMMENT 'Span.duration(): micros used for minDuration and maxDuration query'
	) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
	
	ALTER TABLE zipkin_spans ADD UNIQUE KEY(`trace_id_high`, `trace_id`, `id`) COMMENT 'ignore insert on duplicate';
	ALTER TABLE zipkin_spans ADD INDEX(`trace_id_high`, `trace_id`, `id`) COMMENT 'for joining with zipkin_annotations';
	ALTER TABLE zipkin_spans ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTracesByIds';
	ALTER TABLE zipkin_spans ADD INDEX(`name`) COMMENT 'for getTraces and getSpanNames';
	ALTER TABLE zipkin_spans ADD INDEX(`start_ts`) COMMENT 'for getTraces ordering and range';
	
	CREATE TABLE IF NOT EXISTS zipkin_annotations (
	  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit',
	  `trace_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.trace_id',
	  `span_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.id',
	  `a_key` VARCHAR(255) NOT NULL COMMENT 'BinaryAnnotation.key or Annotation.value if type == -1',
	  `a_value` BLOB COMMENT 'BinaryAnnotation.value(), which must be smaller than 64KB',
	  `a_type` INT NOT NULL COMMENT 'BinaryAnnotation.type() or -1 if Annotation',
	  `a_timestamp` BIGINT COMMENT 'Used to implement TTL; Annotation.timestamp or zipkin_spans.timestamp',
	  `endpoint_ipv4` INT COMMENT 'Null when Binary/Annotation.endpoint is null',
	  `endpoint_ipv6` BINARY(16) COMMENT 'Null when Binary/Annotation.endpoint is null, or no IPv6 address',
	  `endpoint_port` SMALLINT COMMENT 'Null when Binary/Annotation.endpoint is null',
	  `endpoint_service_name` VARCHAR(255) COMMENT 'Null when Binary/Annotation.endpoint is null'
	) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
	
	ALTER TABLE zipkin_annotations ADD UNIQUE KEY(`trace_id_high`, `trace_id`, `span_id`, `a_key`, `a_timestamp`) COMMENT 'Ignore insert on duplicate';
	ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`, `span_id`) COMMENT 'for joining with zipkin_spans';
	ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTraces/ByIds';
	ALTER TABLE zipkin_annotations ADD INDEX(`endpoint_service_name`) COMMENT 'for getTraces and getServiceNames';
	ALTER TABLE zipkin_annotations ADD INDEX(`a_type`) COMMENT 'for getTraces';
	ALTER TABLE zipkin_annotations ADD INDEX(`a_key`) COMMENT 'for getTraces';
	ALTER TABLE zipkin_annotations ADD INDEX(`trace_id`, `span_id`, `a_key`) COMMENT 'for dependencies job';
	
	CREATE TABLE IF NOT EXISTS zipkin_dependencies (
	  `day` DATE NOT NULL,
	  `parent` VARCHAR(255) NOT NULL,
	  `child` VARCHAR(255) NOT NULL,
	  `call_count` BIGINT,
	  `error_count` BIGINT
	) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
	
	ALTER TABLE zipkin_dependencies ADD UNIQUE KEY(`day`, `parent`, `child`);

## 将链路数据存储在ElasticSearch

使用Mysql存储链路数据，在并发高的情况下，显然不合理，这时可以选择使用ElasticSearch存储。读者需要自行安装ElasticSearch、Kibana（下一小节使用），下载地址为https://www.elastic.co/products/elasticsearch。安装完成后并启动它们，其中ElasticSearch的默认端口为9200，Kibana的端口为5601。


首先在pom文件，加上zipkin的依赖和zipkin-autoconfigure-storage-elasticsearch-http的依赖：


        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin</artifactId>
            <version>1.28.0</version>
        </dependency>
        <dependency>
            <groupId>io.zipkin.java</groupId>
            <artifactId>zipkin-autoconfigure-storage-elasticsearch-http</artifactId>
            <version>1.28.0</version>
        </dependency>

在application.yml文件加上Zipkin的配置，配置了zipkin的存储类型为elasticsearch，使用的StorageComponent为elasticsearch。然后需要配置elasticsearch，包括hosts，可以配置多个，用“，”隔开；index为zipkin等，具体配置如下：

	
	zipkin:
	  storage:
	    type: elasticsearch
	    StorageComponent: elasticsearch
	    elasticsearch:
	      cluster: elasticsearch
	      max-requests: 30
	      index: zipkin
	      index-shards: 3
	      index-replicas: 1
	      hosts: localhost:9200

在kibana上展示

上一小节讲述了如何将链路数据存储在ElasticSearch，ElasticSearch可以和Kibana结合，将链路数据展示在 Kibana上。安装完Kibana，并启动，它默认会向本地的9200端口的ElasticSearch读取数据，它默认的端口为5601。访问http://localhost:5601，显示的界面如下：

image.png

在上述的界面点击”Management”按钮，然后点击“Add New”，添加一个index，在上节我们在ElasticSearch中写入链路数据的index配置为“zipkin”,那么在界面填写为“zipkin-*”，点击“Create”按钮。

image.png

创建完index之后，点击Discover，就可以在界面上展示链路数据了。

image.png

参考：

http://blog.csdn.net/forezp/article/details/70148833

https://gitee.com/zhou666/spring-cloud-7simple