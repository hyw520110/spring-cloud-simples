# 消息总线(Spring-Cloud-Bus)

Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控

需要下载安装rabbitMq

改造config-client增加依赖：
	 
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
在配置文件application.properties中加上RabbitMq的配置：

	spring.rabbitmq.host=localhost
	spring.rabbitmq.port=5672
	# spring.rabbitmq.username=
	# spring.rabbitmq.password=

如果rabbitmq有用户名密码，输入即可。

依次启动eureka-server、confg-cserver,启动两个config-service（8881、8882）访问：

http://localhost:8881/config或http://localhost:8882/config

修改git配置中心的相应的参数,如果是传统的做法，需要重启服务，才能达到配置文件的更新。

只需要发送post请求：http://localhost:8881/bus/refresh，再次访问http://localhost:8881/config或http://localhost:8882/config就可以重新读取配置文件

另外，/bus/refresh接口可以指定服务，即使用”destination”参数，比如 “/bus/refresh?destination=config-service:**” 即刷新服务名为customers的所有服务，不管ip。

## 扩展

可以用作自定义的Message Broker,只需要spring-cloud-starter-bus-amqp, 然后再配置文件写上配置即可

Tracing Bus Events： 
需要设置：spring.cloud.bus.trace.enabled=true，如果那样做的话，那么Spring Boot TraceRepository（如果存在）将显示每个服务实例发送的所有事件和所有的ack