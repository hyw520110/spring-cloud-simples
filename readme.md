# 简介：

Spring Cloud：微服务工具包，为开发者提供了在分布式系统的配置管理、服务发现、断路器、智能路由、微代理、控制总线、一次性令牌，全局锁，领导选举，分布式 会话，群集状态等开发工具包。

Spring Boot:旨在简化创建产品级的 Spring 应用和服务，简化了配置文件，使用嵌入式web服务器，含有诸多开箱即用微服务功能,可以和spring cloud联合部署。

Spring Cloud专注于为经典用例和扩展机制提供良好的开箱即用

- 分布式/版本配置
- 服务注册与发现
- 路由选择
- 服务调用
- 负载均衡
- 熔断机制
- 全局锁
- 领导人选举和集群状态
- 分布式消息

# SpringCloud子项目介绍

- Spring Cloud Config：配置管理开发工具包，可以让你把配置放到远程服务器，目前支持本地存储、Git以及Subversion。
- Spring Cloud Bus：事件、消息总线，用于在集群（例如，配置变化事件）中传播状态变化，可与Spring Cloud Config联合实现热部署。
- Spring Cloud Netflix：针对多种Netflix组件提供的开发工具包，其中包括Eureka、Hystrix、Zuul、Archaius等。
- Netflix Eureka：云端负载均衡，一个基于 REST 的服务，用于定位服务，以实现云端的负载均衡和中间层服务器的故障转移。
- Netflix Hystrix：容错管理工具，旨在通过控制服务和第三方库的节点,从而对延迟和故障提供更强大的容错能力。
- Netflix Zuul：边缘服务工具，是提供动态路由，监控，弹性，安全等的边缘服务。
- Netflix Archaius：配置管理API，包含一系列配置管理API，提供动态类型化属性、线程安全配置操作、轮询框架、回调机制等功能。
- Spring Cloud for Cloud Foundry：通过Oauth2协议绑定服务到CloudFoundry，CloudFoundry是VMware推出的开源PaaS云平台。
- Spring Cloud Sleuth：日志收集工具包，封装了Dapper,Zipkin和HTrace操作。
- Spring Cloud Data Flow：大数据操作工具，通过命令行方式操作数据流。
- Spring Cloud Security：安全工具包，为你的应用程序添加安全控制，主要是指OAuth2。
- Spring Cloud Consul：封装了Consul操作，consul是一个服务发现与配置工具，与Docker容器可以无缝集成。
- Spring Cloud Zookeeper：操作Zookeeper的工具包，用于使用zookeeper方式的服务注册和发现。
- Spring Cloud Stream：数据流操作开发包，封装了与Redis,Rabbit、Kafka等发送接收消息。
- Spring Cloud CLI：基于 Spring Boot CLI，可以让你以命令行方式快速建立云组件。

# 微服务开发要素

1、Codebase：从一个代码库部署到多个环境。

2、Dependencies：使用显式的声明隔离依赖，即模块单独运行，并可以显式管理依赖。

3、Config：在系统外部存储配置信息。

4、Backing Services：把支持性服务看做是资源，支持性服务包括数据库、消息队列、缓冲服务器等。

5、Build, release, run：严格的划分编译、构建、运行阶段，每个阶段由工具进行管理。

6、Processes：应用作为无状态执行。

7、Port binding：经由端口绑定导出服务，优先选择 HTTP API 作为通用的集成框架。

8、Concurrency：并发性使用水平扩展实现，对于web就是水平扩展web应用实现。

9、Disposability：服务可处置性，任何服务可以随意终止或启动。

10、Dev/prod parity：开发和生产环境保持高度一致，一键式部署。

11、Logs：将日志看做是事件流来管理，所有参与的服务均使用该方式处理日志。

12、Admin processes：管理任务作为一次性的过程运行（使用脚本管理服务启动和停止）。

# Spring Cloud版本

## 版本命名

Spring Cloud是一个拥有诸多子项目的大型综合项目，原则上其子项目也都维护着自己的发布版本号。那么每一个Spring Cloud的版本都会包含不同的子项目版本，为了要管理每个版本的子项目清单，避免版本名与子项目的发布号混淆，所以没有采用版本号的方式，而是通过命名的方式。

这些版本名字采用了伦敦地铁站的名字，根据字母表的顺序来对应版本时间顺序，比如：最早的Release版本：Angel，第二个Release版本：Brixton，以此类推……

## 版本号

经过上面的解释，不难猜出，之前所提到的Angel.SR6，Brixton.SR5中的SR6、SR5就是版本号了。

当一个版本的Spring Cloud项目的发布内容积累到临界点或者一个严重bug解决可用后，就会发布一个“service releases”版本，简称SRX版本，其中X是一个递增数字。

## 当前版本

通过下表，我们可以快速查阅当前各版本所包含的子项目，以及各子项目的版本号，通过此来决定需要选择怎么样的版本。

<table>
<thead>
<tr>
<th>Component</th>
<th>Angel.SR6</th>
<th>Brixton.SR5</th>
<th>Camden.M1</th>
<th>Camden.BUILD-SNAPSHOT</th>
</tr>
</thead>
<tbody>
<tr>
<td>spring-cloud-aws</td>
<td>1.0.4.RELEASE</td>
<td>1.1.1.RELEASE</td>
<td>1.1.1.RELEASE</td>
<td>1.1.2.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-bus</td>
<td>1.0.3.RELEASE</td>
<td>1.1.1.RELEASE</td>
<td>1.2.0.M1</td>
<td>1.2.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-cli</td>
<td>1.0.6.RELEASE</td>
<td>1.1.5.RELEASE</td>
<td>1.2.0.M1</td>
<td>1.2.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-commons</td>
<td>1.0.5.RELEASE</td>
<td>1.1.1.RELEASE</td>
<td>1.1.1.RELEASE</td>
<td>1.1.2.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-contract</td>
<td></td>
<td></td>
<td>1.0.0.M2</td>
<td>1.0.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-config</td>
<td>1.0.4.RELEASE</td>
<td>1.1.3.RELEASE</td>
<td>1.2.0.M1</td>
<td>1.2.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-netflix</td>
<td>1.0.7.RELEASE</td>
<td>1.1.5.RELEASE</td>
<td>1.2.0.M1</td>
<td>1.2.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-security</td>
<td>1.0.3.RELEASE</td>
<td>1.1.2.RELEASE</td>
<td>1.1.2.RELEASE</td>
<td>1.1.3.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-starters</td>
<td>1.0.6.RELEASE</td>
<td></td>
<td></td>
<td></td>
</tr>
<tr>
<td>spring-cloud-cloudfoundry</td>
<td></td>
<td>1.0.0.RELEASE</td>
<td>1.0.0.RELEASE</td>
<td>1.0.1.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-cluster</td>
<td></td>
<td>1.0.1.RELEASE</td>
<td></td>
<td></td>
</tr>
<tr>
<td>spring-cloud-consul</td>
<td></td>
<td>1.0.2.RELEASE</td>
<td>1.1.0.M1</td>
<td>1.1.0.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-sleuth</td>
<td></td>
<td>1.0.6.RELEASE</td>
<td>1.0.6.RELEASE</td>
<td>1.0.7.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-stream</td>
<td></td>
<td>1.0.2.RELEASE</td>
<td>Brooklyn.M1</td>
<td>Brooklyn.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-cloud-zookeeper</td>
<td></td>
<td>1.0.2.RELEASE</td>
<td>1.0.2.RELEASE</td>
<td>1.0.3.BUILD-SNAPSHOT</td>
</tr>
<tr>
<td>spring-boot</td>
<td>1.2.8.RELEASE</td>
<td>1.3.7.RELEASE</td>
<td>1.4.0.RELEASE</td>
<td>1.4.0.RELEASE</td>
</tr>
<tr>
<td>spring-cloud-task</td>
<td></td>
<td>1.0.2.RELEASE</td>
<td>1.0.2.RELEASE</td>
<td>1.0.3.BUILD-SNAPSHOT</td>
</tr>
</tbody>
</table>

最初的Angel版本相对来说拥有的子项目较少，Brixton、Camden则拥有更全的子项目，所提供跟多的组件支持。Brixton发布的子项目更稳定，Camden则更具前瞻性


# 优雅安全地停止SpringBoot应用服务


主要有两种方式：通过HTTP发送shutdown信号，或者通过service stop的方式

## http方式

该方式主要依赖Spring Boot Actuator的endpoint特性

	<dependency>
	  <groupId>org.springframework.boot</groupId>
	  <artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>

开启shutdown endpoint

	
	#启用shutdown
	endpoints.shutdown.enabled=true
	#禁用密码验证
	endpoints.shutdown.sensitive=false
需要停止服务时,post请求host:port/shutdown即可:

	curl -X POST host:port/shutdown

### 安全设置

正式使用时，必须对该请求进行必要的安全设置，比如借助spring-boot-starter-security进行身份认证：

	<dependency>
	  <groupId>org.springframework.boot</groupId>
	  <artifactId>spring-boot-starter-security</artifactId>
	</dependency>

开启安全验证
	
	
	#开启shutdown的安全验证
	endpoints.shutdown.sensitive=true
	#验证用户名
	security.user.name=admin
	#验证密码
	security.user.password=secret
	#角色
	management.security.role=SUPERUSER

	#指定shutdown endpoint的路径
	endpoints.shutdown.path=/stop
	#也可以统一指定所有endpoints的路径`management.context-path=/manage`
	#指定管理端口和IP
	management.port=8081
	management.address=127.0.0.1

## 部署服务

该方式主要借助官方的spring-boot-maven-plugin创建"Fully executable" jar ，这中jar包内置一个shell脚本，可以方便的将该应用设置为Unix/Linux的系统服务(init.d service),官方对该功能在CentOS和Ubuntu进行了测试，对于OS X和FreeBSD,可能需要自定义。

### 加入maven插件

	<plugin>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-maven-plugin</artifactId>
	</plugin>

### 设置为系统服务

	sudo ln -s /var/app/user.jar /etc/init.d/user
	chmod u+x user.jar
	sudo service user start|stop

默认PID文件路径：/var/run/user/user.pid

默认日志文件路径：/var/log/user.log

### 自定义参数

可以使用自定义的.conf文件来变更默认配置

在jar包相同路径下创建一个与.jar的名称相同conf文件，如user.conf

	JAVA_HOME=/usr/local/jdk
	JAVA_OPTS=-Xmx1024M
	LOG_FOLDER=/custom/log

### 安全设置

作为应用服务，安全性是一个不能忽略的问题，基础设置参考：

- 为服务创建一个独立的用户，同时最好将该用户的shell绑定为/usr/sbin/nologin
- 赋予最小范围权限：chmod 500 user.jar
- 阻止修改：sudo chattr +i user.jar
- 设置权限所属：

		chmod 400 user.conf
		chown root:root user.conf

# 自定义的销毁方法

- 实现接口：DisposableBean, ExitCodeGenerator

- 使用注解@PreDestroy


spring cloud中文社区

https://springcloud.cc/








