# 背景

如何实现分布式id,常见几种方案：

- 使用数据库自增Id
- 使用reids的incr命令
- 使用UUID
- Twitter的snowflake算法
- 利用zookeeper生成唯一ID
- MongoDB的ObjectId

# mongodb实现分布式ID

MongoDB的ObjectId设计成轻量型的，不同的机器都能用全局唯一的同种方法方便地生成它。MongoDB 从一开始就设计用来作为分布式数据库，处理多个节点是一个核心要求。使其在分片环境中要容易生成得多。

它的格式： 

- time 前4个字节是从标准纪元开始的时间戳，单位为秒。时间戳，与随后的5个字节组合起来，提供了秒级别的唯一性。由于时间戳在前，这意味着ObjectId大致会按照插入的顺序排列。这对于某些方面很有用，如将其作为索引提高效率。这4个字节也隐含了文档创建的时间。绝大多数客户端类库都会公开一个方法从ObjectId获取这个信息。

- Machine 接下来的3字节是所在主机的唯一标识符。通常是机器主机名的散列值。这样就可以确保不同主机生成不同的ObjectId，不产生冲突。 
为了确保在同一台机器上并发的多个进程产生的ObjectId 是唯一的，接下来的两字节来自产生ObjectId 的进程标识符（PID）。

- PID 前9字节保证了同一秒钟不同机器不同进程产生的ObjectId 是唯一的。

- INC 后3字节就是一个自动增加的计数器，确保相同进程同一秒产生的ObjectId 也是不一样的。同一秒钟最多允许每个进程拥有2563（16777216）个不同的ObjectId。

## 增加依赖

	<!-- 开启web -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!--mongodb -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-mongodb</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>