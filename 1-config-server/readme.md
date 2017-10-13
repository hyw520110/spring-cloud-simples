# 分布式配置管理

## 背景: 

在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。

分布式配置框架有：百度的[disconf](https://github.com/knightliao/disconf),阿里的[diamand](https://github.com/takeseem/diamond),spring cloud config

在传统开发中我们往往需要自己开发配置管理服务器:可以使用redis、ldap、zookeeper、db等来存放统一配置信息，然后开发一个管理界面来进行管理。

传统的做法没什么问题，和spring cloud所提供的配置管理方案相比，就是前者需要自己开发，而后者直接简单使用现成的组件即可。当然还有很重要的一点，spring 配置管理模块由于是spring boot核心来实现的，因此做了大量的工作，可以把一些启动参数进行外部配置，这在传统的方案中是很难办到的，因为涉及到要改写第三方组件的问题，难度很大。比如web应用的绑定端口，传统应用只能在tomcat配置文件里改，而spring cloud却可以放到远程，类似的还有数据库连接、安全框架配置等。

在Spring Cloud中，有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。在spring cloud config 组件中，分两个角色，一是config server，二是config client。

要使用spring cloud分布式配置文件总体上分为3个大的步骤
- 首选你需要创建存放配置文件的仓库
- 然后创建一个配置文件服务器，该服务器将配置文件信息转化为rest接口数据
- 然后创建一个应用服务，使用分布式配置文件信息。



## 创建配置文件存放仓库

Spring cloud使用git或svn存放配置文件，默认情况下使用git，因此你需要安装git私服或者直接使用互联网上的github或者git.oschina，创建工程config-repo，此工程再创建一个文件夹config来存放配置文件。然后创建两个配置文件：

- config-dev.properties
- config-test.properties

这两个文件分别对应开发环境和测试环境所需要的配置信息，配置信息如下：

	　　mysqldb.datasource.url=jdbc\:mysql\://10.0.12.170\:3306/test?useUnicode\=true&characterEncoding\=utf-8	
	　　mysqldb.datasource.username=root	
	　　mysqldb.datasource.password=123456	
	　　logging.level.org.springframework.web:DEBUG

配置信息提供了数据库连接参数等，这是因为后面的应用服务中使用到了数据库。

## 创建spring cloud配置服务器(config server)

配置文件仓库创建好了后，就需要将配置文件转换为rest接口服务。这个服务器的功能也是spring cloud提供的，所以我们只需要引入相关jar包,设置一下即可。

创建配置工程config-server,增加依赖：

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
创建启动类：

	@SpringBootApplication
	@EnableConfigServer
	@EnableEurekaClient
	public class ConfigServerApplication {  
	    public static void main(String[] args) {        
	        SpringApplication.run(ConfigServerApplication.class, args);     
	    }       
	}
@EnableConfigServer激活该应用为配置文件服务器，该应用启动后就会读取远程配置文件，转换为rest接口服务

当然，需要配置远程配置文件读取路径，在application.properties中：

	server.port=8888
	spring.application.name=config-server
	spring.cloud.config.server.git.uri=https://github.com/hyw520110/spring-cloud-simples.git
	spring.cloud.config.server.git.searchPaths=0-config-repo
	#spring.cloud.config.label=master
	#spring.cloud.config.server.git.username=username
	#spring.cloud.config.server.git.password=password

git.uri指定配置文件所在的git工程路径，searchPaths表示将搜索该文件夹下的配置文件

http请求地址和资源文件映射如下:

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

启动程序：访问：

http://localhost:8888/config/dev

http://localhost:8888/config-dev.yml

http://localhost:8888/config-dev.properties

可以看到配置信息证明配置服务中心可以从远程程序获取配置信息。

注意：

如查看yml格式配置时，配置文件中有类似配置：
	
	logging.level.org.springframework=INFO
	logging.level.org.springframework.web=DEBUG
会报错，原因是不符合yml数据格式:配置项不能同时有值和子项(springframework有值有子项)

## 配置中心集群化(高可用分布式配置)

当服务实例很多时，都从配置中心读取文件，这时可以考虑将配置中心做成一个微服务，将其集群化，从而达到高可用
### 创建服务注册中心

创建eureka或zookeeper或consul注册中心服务

### config-server

增加EurekaClient的起步依赖spring-cloud-starter-eureka

	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-config-server</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-eureka</artifactId>
	</dependency>

配置（增加eureka配置）：
	
	server.port=8888
	spring.application.name=config-server
	spring.cloud.config.server.git.uri=https://github.com/hyw520110/spring-cloud-simples.git
	spring.cloud.config.server.git.searchPaths=0-config-repo
	#spring.cloud.config.label=master
	#spring.cloud.config.server.git.username=username
	#spring.cloud.config.server.git.password=password
	
	#配置中心集群化
	eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/

启动类上加上@EnableEurekaClient的注解

启动注册中心服务(enureka-server)服务，启动多个配置服务config-server，然后需创建服务使用远程配置,即应用远程配置


# 配置加密解密

配置文件中的密码等敏感信息，明文存储是不安全的，需要加密存储，使用配置服务时需要解密

加密解密需要依赖 java Cryptography Extension (jce) 

[java6 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html)

[java7 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)

[java8 JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

下载jce后解压，替换jdk/jre下的jre/lib/security/目录中的两个jar:

	local_policy.jar
	US_export_policy.jar	  
### 对称加密

config-server的配置文件中需配置的密钥

	encrypt.key=682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
加密：

	curl localhost:8888/encrypt -d abc
如提示：

	Unable to initialize due to invalid secret ke
需安装jce替换jar包，重启config-server服务

解密：

	curl localhost:8888/decrypt -d 55dc12bb9588cdce5ce16a09245e13dbcce60b8e4d01c7ae517170abf31f0542
### 非对称加密

生成证书

cmd进入工程resource目录下执行(或任意目录执行,之后将server.jks文件复制到工程classpath下)

	keytool -genkeypair -alias test -keyalg RSA -dname "CN=china,OU=Unit,O=zhejiang,L=hangzhou,S=State,C=US" -keypass 123456 -keystore server.jks -storepass abcdefg


config-server的配置文件中配置：
	
	#非对称加密
	encrypt.key-store.location=server.jks
	encrypt.key-store.password=abcdefg
	encrypt.key-store.alias=test
	encrypt.key-store.secret=123456
加密：

	curl localhost:8888/encrypt -d abcdefg
解密：

	curl localhost:8888/decrypt -d AQBf98ev1o3DP1l24JBOsMM+Cc3zpPwbaJNye1g8kgDj0FYOdjplcIq+lxZoSzVrfQ5ezyI2a2ObO/8xWW5kEdiACU0kaytDD+RR/LAacSYiAAKyYOjZMpGb0i/64FOv5MpooWz85S177Aecu27lw9vyUWHuh0wGvLXC6Nf5P75Mom9q7mhcWh63HwT5UREHcy2WxcFjQ4PzoIZxJLWvzVyCFYE5E0XpkiUqvkq+wgrloi5aPEkUCbHtxdafOTHowsbD78/Yrh3N9ZMJazJLO+UDfOu6UXZG9t4VqC5AIGha1Ygcbvw3+lHKFQwzCI+davIaR0eYRTczsLampPHGB/Xjz7kLoH/GUTOkicwVPBY4m74+WKzn0ttQAqNehBDhDVo=

### 存储加密配置

使用{cipher}密文的形式存储
	
	spring.datasource.password={cipher}55dc12bb9588cdce5ce16a09245e13dbcce60b8e4d01c7ae517170abf31f0542
	#spring.datasource.password={cipher}AQBf98ev1o3DP1l24JBOsMM+Cc3zpPwbaJNye1g8kgDj0FYOdjplcIq+lxZoSzVrfQ5ezyI2a2ObO/8xWW5kEdiACU0kaytDD+RR/LAacSYiAAKyYOjZMpGb0i/64FOv5MpooWz85S177Aecu27lw9vyUWHuh0wGvLXC6Nf5P75Mom9q7mhcWh63HwT5UREHcy2WxcFjQ4PzoIZxJLWvzVyCFYE5E0XpkiUqvkq+wgrloi5aPEkUCbHtxdafOTHowsbD78/Yrh3N9ZMJazJLO+UDfOu6UXZG9t4VqC5AIGha1Ygcbvw3+lHKFQwzCI+davIaR0eYRTczsLampPHGB/Xjz7kLoH/GUTOkicwVPBY4m74+WKzn0ttQAqNehBDhDVo=
其中{cipher}是标识，表示后面的字符串是加密密文，使用时需要被解密


